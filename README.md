# Samba — Backend API Supérettes

> Plateforme de gestion multi-supérettes avec catalogue partagé, scan de produits et prix personnalisés par magasin.

| Élément | Description |
|---------|-------------|
| **Application** | Samba |
| **Version** | 2.0 — 2026 |
| **Backend** | Spring Boot 4.0.5 / Java 21 |
| **Base de données** | PostgreSQL 17 + Flyway |
| **Cible** | Supérettes en Afrique |

---

## 1. Présentation du projet

### 1.1 Contexte

**Samba** est une application dédiée à la gestion de supérettes. Elle fournit un **catalogue global partagé** entre tous les magasins, avec des **prix et stocks définis individuellement** par chaque supérette. Le système permet le scan de codes-barres (EAN ou internes) pour identifier les produits en caisse.

### 1.2 Objectifs

- Maintenir un **catalogue global** de produits commun à toutes les supérettes
- Permettre à chaque supérette de **définir ses propres prix et stocks**
- Scanner les **codes-barres** (EAN officiels ou codes internes générés) pour identifier les produits
- Gérer **plusieurs supérettes** depuis un même compte propriétaire
- Garantir l'**unicité des codes-barres** et éviter la duplication des produits

---

## 2. Modèle de données

### 2.1 Vue d'ensemble

```
┌──────────────┐       ┌──────────────┐       ┌──────────────────┐
│   products   │       │   barcodes   │       │     stores       │
│──────────────│       │──────────────│       │──────────────────│
│ id           │◄──┐   │ id           │       │ id               │
│ name         │   └───│ product_id   │       │ name             │
│ brand        │       │ code (UNIQUE)│       │ address          │
│ category     │       │ type         │       │ phone            │
│              │       │ (EAN/INTERNE)│       │ owner_id         │
│ ⚠️ PAS DE    │       └──────────────┘       └────────┬─────────┘
│   PRIX ICI   │                                       │
└──────┬───────┘                                       │
       │              ┌───────────────────────┐        │
       │              │    store_products     │        │
       └──────────────│───────────────────────│────────┘
                      │ store_id (FK)         │
                      │ product_id (FK)       │
                      │ price                 │
                      │ stock                 │
                      │ stock_min             │
                      │ UNIQUE(store_id,      │
                      │        product_id)    │
                      └───────────────────────┘
```

### 2.2 Table `products` — Catalogue global

Produits partagés entre toutes les supérettes. **Aucun prix ici.**

| Champ | Type | Description |
|-------|------|-------------|
| `id` | UUID | Identifiant unique |
| `name` | VARCHAR | Nom du produit |
| `brand` | VARCHAR | Marque (nullable) |
| `category` | VARCHAR | Catégorie du produit |
| `description` | TEXT | Description (nullable) |
| `image_url` | VARCHAR | Photo du produit (nullable) |

### 2.3 Table `barcodes` — Codes-barres

Chaque code est **unique**. Un produit peut avoir **plusieurs codes-barres**.

| Champ | Type | Description |
|-------|------|-------------|
| `id` | UUID | Identifiant unique |
| `code` | VARCHAR | Code-barres (**UNIQUE**) |
| `product_id` | UUID (FK) | Référence vers `products` |
| `type` | ENUM | `EAN` (officiel GS1) ou `INTERNAL` (généré) |

**Règles :**
- 1 code-barres = 1 seul produit
- 1 produit peut avoir N codes-barres
- Les codes internes suivent le format `2XXXXXXXXXXX` (préfixe `2`, 13 chiffres)

### 2.4 Table `stores` — Supérettes

| Champ | Type | Description |
|-------|------|-------------|
| `id` | UUID | Identifiant unique |
| `name` | VARCHAR | Nom de la supérette |
| `address` | VARCHAR | Adresse |
| `phone` | VARCHAR | Téléphone |
| `owner_id` | UUID (FK) | Propriétaire |

### 2.5 Table `store_products` — Catalogue par supérette

Chaque supérette choisit ses produits et définit ses propres prix.

| Champ | Type | Description |
|-------|------|-------------|
| `store_id` | UUID (FK) | Référence vers `stores` |
| `product_id` | UUID (FK) | Référence vers `products` |
| `price` | DECIMAL | Prix de vente dans cette supérette |
| `stock` | INTEGER | Stock disponible |
| `stock_min` | INTEGER | Seuil d'alerte rupture |

**Contrainte :** `UNIQUE(store_id, product_id)` — un produit ne peut apparaître qu'une seule fois par supérette.

---

## 3. Fonctionnement

### 3.1 Ajout d'un produit au catalogue global

```
┌─────────────────────────────────────────────────┐
│  Scan code-barres OU création manuelle          │
│                                                 │
│  ┌─ Code scanné ?                               │
│  │   ├─ OUI → Produit trouvé ? → Afficher       │
│  │   │        Pas trouvé ? → Créer le produit   │
│  │   └─ NON → Créer manuellement               │
│  │            → Générer un code interne          │
│  └──────────────────────────────────────────────┘
```

1. Scanner un code-barres ou saisir les infos manuellement
2. Si le produit n'existe pas → le créer dans `products`
3. Si pas de code-barres → générer un code interne (`2000000000001`, `2000000000002`, ...)
4. Associer le code dans la table `barcodes`

### 3.2 Ajout d'un produit à une supérette

1. La supérette consulte le **catalogue global**
2. Elle clique **"Ajouter à ma supérette"**
3. Elle saisit son **prix de vente** et son **stock initial**
4. Le produit est enregistré dans `store_products`

### 3.3 Scan en caisse

```
┌──────────────────────────────────────────────────┐
│  1. Scanner le code-barres                       │
│  2. Rechercher dans `barcodes` → product_id      │
│  3. Vérifier dans `store_products`               │
│     (store_id + product_id)                      │
│  4. Afficher le prix de la supérette             │
│  5. Ajouter au panier                            │
└──────────────────────────────────────────────────┘
```

### 3.4 Gestion des codes-barres

| Type | Source | Format | Exemple |
|------|--------|--------|---------|
| **EAN** | Fabricant (standard GS1) | 8 ou 13 chiffres | `3017620422003` |
| **INTERNAL** | Généré par Samba | Préfixe `2` + 12 chiffres | `2000000000001` |

Les codes internes sont générés automatiquement pour les produits sans code-barres officiel (produits en vrac, produits locaux, etc.).

---

## 4. API REST

### 4.1 Produits (catalogue global)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/products` | Lister tous les produits du catalogue |
| `GET` | `/api/products/{id}` | Détail d'un produit |
| `POST` | `/api/products` | Créer un produit |
| `PUT` | `/api/products/{id}` | Modifier un produit |
| `DELETE` | `/api/products/{id}` | Supprimer un produit |
| `GET` | `/api/products/search?q=` | Rechercher par nom/marque/catégorie |

### 4.2 Codes-barres

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/barcodes/lookup/{code}` | Trouver un produit par code-barres |
| `POST` | `/api/barcodes` | Associer un code-barres à un produit |
| `POST` | `/api/barcodes/generate/{productId}` | Générer un code interne |
| `GET` | `/api/barcodes/product/{productId}` | Lister les codes d'un produit |

### 4.3 Supérettes

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/stores` | Lister les supérettes |
| `GET` | `/api/stores/{id}` | Détail d'une supérette |
| `POST` | `/api/stores` | Créer une supérette |
| `PUT` | `/api/stores/{id}` | Modifier une supérette |
| `DELETE` | `/api/stores/{id}` | Supprimer une supérette |

### 4.4 Catalogue supérette (store_products)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/api/stores/{storeId}/products` | Catalogue de la supérette |
| `POST` | `/api/stores/{storeId}/products` | Ajouter un produit (avec prix) |
| `PUT` | `/api/stores/{storeId}/products/{productId}` | Modifier prix/stock |
| `DELETE` | `/api/stores/{storeId}/products/{productId}` | Retirer un produit |
| `GET` | `/api/stores/{storeId}/scan/{barcode}` | Scan en caisse : prix du magasin |

---

## 5. Règles métier

### 5.1 Séparation global / local

| Donnée | Scope | Table |
|--------|-------|-------|
| Nom, marque, catégorie | **Global** (partagé) | `products` |
| Code-barres | **Global** (partagé) | `barcodes` |
| Prix, stock | **Local** (par supérette) | `store_products` |

### 5.2 Contraintes

| Règle | Description |
|-------|-------------|
| Code-barres unique | Un code ne peut référencer qu'un seul produit |
| Pas de duplication | Ne jamais créer deux fiches pour le même produit |
| Pas de prix global | Le prix est **toujours** défini par la supérette |
| Unicité store/product | `UNIQUE(store_id, product_id)` dans `store_products` |

### 5.3 Alertes stock

| Niveau | Condition | Action |
|--------|-----------|--------|
| Rupture | `stock = 0` | Alerte rouge, produit marqué indisponible |
| Stock faible | `stock <= stock_min` | Alerte orange, notification au gérant |
| Normal | `stock > stock_min` | Aucune alerte |

---

## 6. Stack technique

| Composant | Technologie |
|-----------|-------------|
| Framework | Spring Boot 4.0.5 / Java 21 |
| Base de données | PostgreSQL 17 + Flyway |
| Authentification | Keycloak (OAuth2 / OIDC) |
| Stockage objet | MinIO (S3-compatible) |
| Documentation API | springdoc-openapi 2.8.6 (Swagger UI) |
| Monitoring | Prometheus + Grafana + Micrometer |
| Conteneurisation | Docker Compose |
| Codes-barres | ZXing (com.google.zxing) |

---

## 7. Structure du projet

```
src/main/java/com/africa/samba/
├── codeLists/         # Enums métier (ModePaiement, TypeMouvement, ...)
├── common/
│   ├── base/          # Entité de base (audit fields)
│   ├── config/        # Security, CORS, MinIO, Keycloak, Swagger
│   ├── constants/     # Constantes globales
│   ├── exception/     # Gestion centralisée des erreurs
│   └── util/          # Utilitaires (CustomResponse, ...)
├── controllers/       # Endpoints REST
├── dto/               # Request / Response DTOs
├── entity/            # Entités JPA (Product, Barcode, Store, StoreProduct, ...)
├── mapper/            # MapStruct mappers
├── repository/        # Spring Data JPA repositories
└── services/          # Logique métier (interfaces + implémentations)
```

---

## 8. Démarrage rapide

### Prérequis

- Java 21+
- Docker & Docker Compose
- Maven 3.9+

### Lancement

```bash
# 1. Démarrer l'infrastructure
cd docker && docker compose up -d

# 2. Lancer le backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Infrastructure Docker

| Service | Port | URL |
|---------|------|-----|
| PostgreSQL | 5433 | `jdbc:postgresql://localhost:5433` |
| pgAdmin | 5050 | http://localhost:5050 |
| Keycloak | 8180 | http://localhost:8180 |
| MinIO Console | 9001 | http://localhost:9001 |
| Prometheus | 9090 | http://localhost:9090 |
| Grafana | 3001 | http://localhost:3001 |

### Swagger UI

http://localhost:9090/api/swagger-ui.html

---

## Licence

Projet privé — © Spirit Tech Revolution
