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
│ status       │       │ (EAN/INTERNE)│       │ owner_id (FK)    │
│ (APPROVED/   │       └──────────────┘       └────────┬─────────┘
│  PENDING)    │                                       │
│ created_by   │                              ┌────────┴─────────┐
│ _store_id    │                              │  store_members   │
│              │                              │──────────────────│
│ ⚠️ PAS DE    │                              │ store_id (FK)    │
│   PRIX ICI   │                              │ user_id  (FK)    │
└──────┬───────┘                              │ role (MANAGER/   │
       │              ┌───────────────────────┐│   EMPLOYEE)      │
       │              │    store_products     ││ active           │
       └──────────────│───────────────────────│└──────────────────┘
                      │ store_id (FK)         │
                      │ product_id (FK)       │
                      │ price (vente)         │
                      │ cost_price (achat)    │
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
| `status` | ENUM | `APPROVED` (validé) ou `PENDING` (en attente) — défaut `APPROVED` |
| `created_by_store_id` | UUID (FK) | Supérette ayant créé le produit (nullable — renseigné pour les créations rapides) |

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
| `cost_price` | DECIMAL | Prix d'achat fournisseur (nullable) |
| `stock` | INTEGER | Stock disponible |
| `stock_min` | INTEGER | Seuil d'alerte rupture |

> **Marge unitaire** = `price − cost_price`. Ce calcul est exposé automatiquement dans la réponse API (`margin`).

**Contrainte :** `UNIQUE(store_id, product_id)` — un produit ne peut apparaître qu'une seule fois par supérette.

### 2.6 Table `store_members` — Employés / Managers

Associe des utilisateurs à une supérette avec un rôle local.

| Champ | Type | Description |
|-------|------|-------------|
| `id` | UUID | Identifiant unique |
| `store_id` | UUID (FK) | Référence vers `stores` |
| `user_id` | UUID (FK) | Référence vers `users` |
| `role` | ENUM | `MANAGER` ou `EMPLOYEE` |
| `active` | BOOLEAN | Membre actif (soft delete) — défaut `true` |

**Contrainte :** `UNIQUE(store_id, user_id)` — un utilisateur ne peut être membre qu'une fois par supérette.

> **Note :** Le propriétaire (`owner_id` dans `stores`) n'a pas besoin d'être dans `store_members`. Il dispose déjà de tous les droits sur ses supérettes.

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

### 3.2 Création rapide par un employé

Quand un employé ne trouve pas un produit dans le catalogue, il peut le **créer à la volée** :

```
┌──────────────────────────────────────────────────┐
│  1. Scan code-barres → produit introuvable       │
│  2. Créer rapidement :                           │
│     - nom, catégorie, prix, stock                │
│  3. Le produit est créé en statut PENDING        │
│  4. Il est ajouté au catalogue de la supérette   │
│  5. L'employé peut l'utiliser immédiatement      │
│  6. Le propriétaire / manager valide plus tard   │
│     → statut passe à APPROVED                    │
└──────────────────────────────────────────────────┘
```

| Statut | Visibilité | Utilisation |
|--------|-----------|-------------|
| `PENDING` | Uniquement dans la supérette qui l'a créé | Vente possible immédiatement |
| `APPROVED` | Catalogue global (toutes les supérettes) | Pleinement utilisable |

### 3.3 Ajout d'un produit à une supérette

1. La supérette consulte le **catalogue global**
2. Elle clique **"Ajouter à ma supérette"**
3. Elle saisit son **prix de vente** et son **stock initial**
4. Le produit est enregistré dans `store_products`

### 3.4 Scan en caisse

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

### 3.5 Gestion des codes-barres

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
| `GET` | `/v1/products` | Lister tous les produits du catalogue |
| `GET` | `/v1/products/{id}` | Détail d'un produit |
| `POST` | `/v1/products` | Créer un produit (admin) |
| `PUT` | `/v1/products/{id}` | Modifier un produit (admin) |
| `DELETE` | `/v1/products/{id}` | Supprimer un produit (admin) |
| `GET` | `/v1/products/search?keyword=` | Rechercher par nom/marque/catégorie |
| `GET` | `/v1/products/category/{category}` | Lister par catégorie |
| `POST` | `/v1/products/stores/{storeId}/quick-create` | Création rapide par un employé (PENDING) |
| `PUT` | `/v1/products/{id}/approve` | Approuver un produit en attente (admin) |
| `GET` | `/v1/products/stores/{storeId}/pending` | Lister les produits en attente d'une supérette |

### 4.2 Codes-barres

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/products/barcodes/{code}` | Trouver un produit par code-barres |
| `POST` | `/v1/products/{productId}/barcodes` | Associer un code-barres à un produit |

### 4.3 Supérettes

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/stores` | Lister les supérettes |
| `GET` | `/v1/stores/{id}` | Détail d'une supérette |
| `POST` | `/v1/stores` | Créer une supérette |
| `PUT` | `/v1/stores/{id}` | Modifier une supérette |
| `DELETE` | `/v1/stores/{id}` | Supprimer une supérette |

### 4.4 Catalogue supérette (store_products)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/stores/{storeId}/products` | Catalogue de la supérette |
| `POST` | `/v1/stores/{storeId}/products` | Ajouter un produit (avec prix) |
| `PUT` | `/v1/stores/{storeId}/products/{productId}` | Modifier prix/stock |
| `DELETE` | `/v1/stores/{storeId}/products/{productId}` | Retirer un produit |
| `GET` | `/v1/stores/{storeId}/products/scan/{barcode}` | Scan en caisse : prix du magasin |
| `GET` | `/v1/stores/{storeId}/products/low-stock` | Produits en rupture / stock faible |

### 4.5 Membres supérette (store_members)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/stores/{storeId}/members` | Lister les membres de la supérette |
| `POST` | `/v1/stores/{storeId}/members` | Ajouter un membre (MANAGER ou EMPLOYEE) |
| `PUT` | `/v1/stores/{storeId}/members/{memberId}` | Modifier le rôle ou le statut |
| `DELETE` | `/v1/stores/{storeId}/members/{memberId}` | Retirer un membre (soft delete) |

---

## 5. Règles métier

### 5.1 Rôles

Samba distingue deux niveaux de rôles :

**Rôles globaux (plateforme)** — définis dans Keycloak :

| Rôle | Description |
|------|-------------|
| `ADMIN` | Administrateur plateforme — accès total |
| `OWNER` | Propriétaire de supérettes — crée et gère ses magasins |

**Rôles locaux (par supérette)** — définis dans `store_members` :

| Rôle | Description |
|------|-------------|
| `MANAGER` | Gérant d'une supérette — approuve les produits, gère les employés |
| `EMPLOYEE` | Vendeur / caissier — scan, vente, création rapide de produits |

> Le propriétaire (`OWNER`) n'a pas besoin d'être dans `store_members`. Il a automatiquement tous les droits sur ses supérettes via `stores.owner_id`.

### 5.2 Séparation global / local

| Donnée | Scope | Table |
|--------|-------|-------|
| Nom, marque, catégorie | **Global** (partagé) | `products` |
| Code-barres | **Global** (partagé) | `barcodes` |
| Prix de vente, prix d'achat, stock | **Local** (par supérette) | `store_products` |

### 5.3 Métriques de rentabilité

Grâce au champ `cost_price` (prix d'achat) sur `store_products`, les métriques suivantes sont calculables :

| Métrique | Formule | Niveau |
|----------|---------|--------|
| Marge unitaire | `price − cost_price` | Par produit × supérette |
| Bénéfice par vente | `(price − cost_price) × quantité` | Par transaction |
| Marge par catégorie | `Σ marges des produits de la catégorie` | Par catégorie × supérette |
| Taux de marge | `(price − cost_price) / price × 100` | % par produit |

> Le champ `margin` est calculé automatiquement et renvoyé dans la réponse API `StoreProductResponse`.

### 5.4 Contraintes

| Règle | Description |
|-------|-------------|
| Code-barres unique | Un code ne peut référencer qu'un seul produit |
| Pas de duplication | Ne jamais créer deux fiches pour le même produit |
| Pas de prix global | Le prix est **toujours** défini par la supérette |
| Unicité store/product | `UNIQUE(store_id, product_id)` dans `store_products` |

### 5.5 Alertes stock

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
## 9. Sécurité & documentation des rôles d'accès

Chaque endpoint de l'API indique explicitement le ou les rôles requis dans la documentation Swagger (`@Operation(description=...)`).

- **ADMIN** : accès total (création, modification, suppression, validation)
- **OWNER** : gestion de ses propres supérettes, validation, consultation
- **EMPLOYEE** : opérations de caisse, création rapide, consultation

Les DTOs de requête et de réponse sont également annotés (Javadoc) pour préciser les rôles pouvant utiliser ou recevoir chaque structure.

**Exemples :**

- `@Operation(description = "Rôle requis : ADMIN. Seuls les administrateurs peuvent créer un produit.")`
- `/** Rôle requis : ADMIN. Seuls les administrateurs peuvent utiliser ce DTO. */`

Consultez la documentation Swagger (http://localhost:9090/api/swagger-ui.html) pour voir les rôles requis sur chaque endpoint.

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
