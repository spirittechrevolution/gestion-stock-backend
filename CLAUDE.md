# CLAUDE.md — Samba Backend

## Vue d'ensemble

**Samba** est une API REST multi-supérettes pour la gestion de stock, développée pour les supérettes africaines. Chaque supérette a ses propres prix et stocks, partagés sur un catalogue global commun.

- **Stack** : Spring Boot 4.0.5 / Java 21 / PostgreSQL 17 / Flyway
- **Auth** : Keycloak (OAuth2 / OIDC)
- **Package racine** : `com.africa.samba`
- **Port applicatif** : 8080 (API préfixée `/api/v1/`)
- **Swagger UI** : http://localhost:9090/api/swagger-ui.html

---

## Commandes essentielles

```bash
# Démarrer l'infrastructure (PostgreSQL, Keycloak, MinIO, Prometheus, Grafana)
cd docker && docker compose up -d

# Lancer le backend (profil local)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Build complet
./mvnw clean package -DskipTests

# Tests
./mvnw test
```

### Ports Docker

| Service    | Port | URL                           |
|------------|------|-------------------------------|
| PostgreSQL | 5433 | `jdbc:postgresql://localhost:5433` |
| pgAdmin    | 5050 | http://localhost:5050         |
| Keycloak   | 8180 | http://localhost:8180         |
| MinIO      | 9001 | http://localhost:9001         |
| Prometheus | 9090 | http://localhost:9090         |
| Grafana    | 3001 | http://localhost:3001         |

---

## Architecture du projet

```
src/main/java/com/africa/samba/
├── codeLists/         # Enums métier (Role, BarcodeType, ProductStatus, StoreMemberRole, ...)
├── common/
│   ├── base/          # BaseEntity (UUID, createdAt, updatedAt via Hibernate)
│   ├── config/        # SecurityConfig, MinioConfig, KeycloakProperties, OpenApiConfig, ...
│   ├── constants/     # Constants (status strings, HTTP codes)
│   ├── exception/     # Exceptions métier + ApiExceptionHandler (@RestControllerAdvice)
│   └── util/          # CustomResponse, BarcodeGenerator, RoleGuard, Utils, ...
├── controllers/       # Endpoints REST
├── dto/
│   ├── request/       # DTOs entrants (validation Bean)
│   └── response/      # DTOs sortants
├── entity/            # Entités JPA (héritent de BaseEntity)
├── mapper/            # MapStruct mappers (interface + implémentation générée)
├── repository/        # Spring Data JPA repositories
└── services/
    ├── interfaces/    # Contrats de service
    └── impl/          # Implémentations
```

### Migrations Flyway

```
src/main/resources/db/migration/
  V1.0.0__init_schema_products.sql
  V1.1.0__create_table_barcodes.sql
  V1.2.0__create_table_users.sql
  V1.3.0__create_table_stores.sql
  V1.4.0__create_table_store_products.sql
  V1.5.0__alter_tables_add_foreign_keys.sql
  V1.6.0__create_table_store_members.sql
  V1.7.0__alter_products_add_status.sql
  V1.8.0__alter_store_products_add_cost_price.sql
  V1.9.0__create_cash_registers.sql
  V1.10.0__create_cash_register_sessions.sql
  V1.11.0__create_sales.sql
  V1.12.0__create_audit_log.sql
```

Nommage : `V{majeur}.{mineur}.{patch}__description_snake_case.sql`. Ne jamais modifier une migration existante.

---

## Modèle de données (tables clés)

| Table            | Rôle                                               |
|------------------|----------------------------------------------------|
| `products`       | Catalogue global — **sans prix**                   |
| `barcodes`       | Codes EAN/INTERNAL liés aux produits (UNIQUE)      |
| `stores`         | Supérettes avec `owner_id`                         |
| `store_products` | Prix, stock, cost_price par (store, product)       |
| `store_members`  | Rôles locaux MANAGER/EMPLOYEE par supérette        |

**Règle critique** : Le prix n'est **jamais** dans `products`. Il est **toujours** dans `store_products`.

---

## Conventions de code

### Réponse API unifiée

Toujours retourner `CustomResponse` (`common/util/CustomResponse.java`) :

```java
new CustomResponse(Constants.Message.SUCCESS_BODY, Constants.Status.OK, "message", data)
```

Pour les pages : passer directement l'objet `Page<?>` dans `data` — `CustomResponse` le convertit automatiquement avec les métadonnées de pagination.

### Exceptions métier

Utiliser les exceptions dédiées (interceptées par `ApiExceptionHandler`) :

| Exception          | HTTP   |
|--------------------|--------|
| `NotFoundException`| 404    |
| `ConflictException`| 409    |
| `BadRequestException` | 400 |
| `UnAuthorizedException` | 401 |
| `StorageException` | 500    |
| `CustomException`  | dynamique selon `.getException()` |

Ne jamais lancer `RuntimeException` directement dans les services.

### Entités JPA

Toutes les entités étendent `BaseEntity` qui fournit :
- `id` : UUID généré par Hibernate (`@UuidGenerator`)
- `createdAt` : `@CreationTimestamp`
- `updatedAt` : `@UpdateTimestamp`

Utiliser `@SuperBuilder` + `@NoArgsConstructor` sur les entités filles.

### Mappers

Utiliser MapStruct. Chaque entité a un mapper dédié dans `mapper/`. Ne pas mapper manuellement dans les services.

### Sécurité / Rôles

**Rôles globaux Keycloak** :
- `ADMIN` — accès total plateforme
- `OWNER` — gestion de ses supérettes

**Rôles locaux** (`store_members.role`) :
- `MANAGER` — approuve produits, gère employés
- `EMPLOYEE` — caisse, création rapide

Le `OWNER` d'une supérette est identifié via `stores.owner_id` — il n'a pas besoin d'être dans `store_members`.

Utiliser `RoleGuard` (`common/util/RoleGuard.java`) pour les vérifications d'accès dans les services.

### Documentation Swagger

Chaque endpoint **doit** indiquer le rôle requis dans `@Operation(description = "Rôle requis : X.")`. Les DTOs de requête/réponse doivent avoir un Javadoc mentionnant le rôle.

---

## Règles métier importantes

- **Codes-barres internes** : format `2XXXXXXXXXXX` (13 chiffres, préfixe `2`). Générés par `BarcodeGenerator`.
- **Produit PENDING** : créé par un employé, visible uniquement dans sa supérette. Passe à `APPROVED` via l'endpoint `/approve`.
- **Marge** : `price − cost_price`. Calculée à la volée dans `StoreProductResponse`, pas stockée en base.
- **Stock faible** : `stock <= stock_min`. Rupture : `stock = 0`.
- **Contrainte unicité** : `UNIQUE(store_id, product_id)` dans `store_products` ; `UNIQUE(store_id, user_id)` dans `store_members`.

---

## Checklist pour une nouvelle fonctionnalité

1. Migration Flyway si changement de schéma (nouveau fichier `V{n}__...sql`)
2. Entité JPA étendant `BaseEntity`
3. Repository Spring Data
4. Interface service + implémentation dans `services/impl/`
5. Mapper MapStruct
6. DTOs request (avec `@Valid`) + response
7. Controller avec `@Operation(description = "Rôle requis : X.")`
8. Retourner `CustomResponse` dans tous les endpoints
9. Lever les exceptions métier appropriées (jamais `RuntimeException`)
