# Samba — Backend API Supérettes

> Plateforme de gestion multi-supérettes avec catalogue partagé, scan de produits, caisse mobile et prix personnalisés par magasin.

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
- Enregistrer les **ventes en caisse** via des sessions de caisse horodatées
- Stocker les **préférences** de chaque utilisateur (langue, notifications, FCM push)

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
                      │ product_id (FK)       │      ┌─────────────────────┐
                      │ price (vente)         │      │   cash_registers    │
                      │ cost_price (achat)    │      │─────────────────────│
                      │ stock                 │      │ store_id (FK)       │
                      │ stock_min             │      │ number / label      │
                      │ UNIQUE(store_id,      │      └──────────┬──────────┘
                      │        product_id)    │                 │
                      └───────────────────────┘      ┌──────────┴──────────┐
                                                     │  cash_register_     │
                                                     │  sessions           │
                                                     │─────────────────────│
                                                     │ cash_register_id FK │
                                                     │ seller_id (FK)      │
                                                     │ opened_at           │
                                                     │ closed_at (nullable)│
                                                     └──────────┬──────────┘
                                                                │
                                                     ┌──────────┴──────────┐
                                                     │       sales         │
                                                     │─────────────────────│
                                                     │ session_id (FK)     │
                                                     │ store_product_id FK │
                                                     │ quantity / total    │
                                                     └─────────────────────┘
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
| `created_by_store_id` | UUID (FK) | Supérette ayant créé le produit (renseigné pour les créations rapides) |

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

### 2.7 Hiérarchie Caisse → Session → Vente

```
Store (supérette)
 └── CashRegister (caisse)
      └── CashRegisterSession (session ouverte/fermée)
           └── Sale (vente)
```

Chaque niveau valide l'appartenance au niveau supérieur lors des appels API.

### 2.8 Table `user_preferences` — Préférences utilisateur

Préférences UI/UX stockées par utilisateur (relation OneToOne avec `users`).

| Groupe | Champs clés |
|--------|-------------|
| Interface | `langue`, `theme`, `taille_police` |
| Caisse | `son_scan_actif`, `vibration_scan_actif`, `mode_paiement_defaut`, `afficher_recu_auto` |
| Catalogue | `vue_catalogue`, `tri_produits_defaut`, `afficher_produits_rupture` |
| Notifications | `notif_stock_faible`, `notif_rupture_stock`, `notif_peremption`, `notif_bilan_journalier` |
| Session | `timeout_session_minutes` (5–60), `pin_a_chaque_ouverture` |
| Push | `fcm_token` (Firebase Cloud Messaging) |

Les préférences sont **créées automatiquement avec les valeurs par défaut** à l'inscription.

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

**Règle anti-doublon** : la création vérifie l'unicité `(name, brand)` → 409 si doublon.

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

### 3.4 Session de caisse

```
┌──────────────────────────────────────────────────┐
│  1. Ouvrir une session (assigner un vendeur)     │
│  2. Enregistrer les ventes dans la session       │
│  3. Fermer la session → closedAt renseigné       │
│  4. Consulter les stats : CA, marge, nb ventes   │
└──────────────────────────────────────────────────┘
```

Une seule session active à la fois par caisse (`UNIQUE` sur `cash_register_id` sans `closed_at`).

---

## 4. API REST

### 4.1 Authentification

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/v1/auth/login` | Connexion email/mot de passe |
| `POST` | `/v1/auth/login/phone` | Connexion téléphone/mot de passe |
| `POST` | `/v1/auth/logout` | Déconnexion (révocation refresh token) |
| `POST` | `/v1/auth/register/send-otp` | Étape 1 inscription : envoi OTP |
| `POST` | `/v1/auth/register/verify-otp` | Étape 2 : vérification OTP |
| `POST` | `/v1/auth/register/complete` | Étape 3 : finalisation inscription |
| `POST` | `/v1/auth/forgot-password` | Mot de passe oublié |

### 4.2 Produits (catalogue global)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/products` | Lister tous les produits du catalogue |
| `GET` | `/v1/products/{id}` | Détail d'un produit |
| `POST` | `/v1/products` | Créer un produit (admin) — vérifie doublon name+brand |
| `PUT` | `/v1/products/{id}` | Modifier un produit (admin) |
| `DELETE` | `/v1/products/{id}` | Supprimer un produit (admin) |
| `GET` | `/v1/products/search?keyword=` | Rechercher par nom/marque/catégorie |
| `GET` | `/v1/products/category/{category}` | Lister par catégorie |
| `GET` | `/v1/products/{productId}/barcodes` | Lister tous les codes-barres d'un produit |
| `POST` | `/v1/products/{productId}/barcodes` | Associer un code-barres EAN à un produit |
| `POST` | `/v1/products/{productId}/barcodes/generate` | Générer un code-barres interne |
| `GET` | `/v1/products/barcodes/{code}` | Trouver un produit par code-barres |
| `POST` | `/v1/products/stores/{storeId}/quick-create` | Création rapide par un employé (PENDING) |
| `PUT` | `/v1/products/{id}/approve` | Approuver un produit (admin) |
| `GET` | `/v1/products/stores/{storeId}/pending` | Lister les produits en attente |

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
| `GET` | `/v1/stores/{storeId}/products/{storeProductId}/stats` | Statistiques d'un produit |

### 4.5 Membres supérette (store_members)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/stores/{storeId}/members` | Lister les membres (filtre optionnel `?role=MANAGER\|EMPLOYEE`) |
| `POST` | `/v1/stores/{storeId}/members` | Ajouter un membre (MANAGER ou EMPLOYEE) |
| `PUT` | `/v1/stores/{storeId}/members/{memberId}` | Modifier le rôle ou le statut |
| `DELETE` | `/v1/stores/{storeId}/members/{memberId}` | Retirer un membre (soft delete) |

### 4.6 Caisses

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/stores/{storeId}/cash-registers` | Lister les caisses |
| `POST` | `/v1/stores/{storeId}/cash-registers` | Créer une caisse |
| `PUT` | `/v1/stores/{storeId}/cash-registers/{id}` | Modifier une caisse |
| `DELETE` | `/v1/stores/{storeId}/cash-registers/{id}` | Désactiver une caisse |

### 4.7 Sessions de caisse

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/stores/{storeId}/cash-registers/{cashRegisterId}/sessions` | Lister les sessions |
| `POST` | `/v1/stores/{storeId}/cash-registers/{cashRegisterId}/sessions` | Ouvrir une session |
| `PUT` | `/v1/stores/{storeId}/cash-registers/{cashRegisterId}/sessions/{sessionId}/close` | Fermer une session |

### 4.8 Ventes

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/v1/stores/{storeId}/cash-registers/{cashRegisterId}/sessions/{sessionId}/sales` | Enregistrer une vente |
| `GET` | `/v1/stores/{storeId}/cash-registers/{cashRegisterId}/sessions/{sessionId}/sales` | Ventes d'une session |
| `GET` | `/v1/stores/{storeId}/cash-registers/{cashRegisterId}/sessions/{sessionId}/sales/stats` | Stats d'une session |

### 4.9 Utilisateur (moi)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/users/me/preferences` | Consulter mes préférences |
| `PUT` | `/v1/users/me/preferences` | Mettre à jour mes préférences |
| `PATCH` | `/v1/users/me/preferences/fcm-token` | Mettre à jour le token FCM push |
| `POST` | `/v1/users/me/preferences/reset` | Réinitialiser aux valeurs par défaut |
| `GET` | `/v1/users/me/stores` | Mes supérettes (en tant que membre actif) |

### 4.10 Audit log

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/audit/store/{storeId}` | Historique d'une supérette |
| `GET` | `/v1/audit/user/{userId}` | Historique d'un utilisateur |
| `GET` | `/v1/audit/event?type=` | Historique par type d'événement |
| `GET` | `/v1/audit/session/{sessionId}` | Historique d'une session de caisse |

### 4.11 Administration

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/v1/admin/users` | Lister les administrateurs |
| `POST` | `/v1/admin/users` | Créer un administrateur |
| `PUT` | `/v1/admin/users/{userId}/role` | Modifier le rôle d'un administrateur |
| `DELETE` | `/v1/admin/users/{userId}` | Supprimer un administrateur (soft delete) |

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
| Préférences UI/UX | **Par utilisateur** | `user_preferences` |

### 5.3 Métriques de rentabilité

| Métrique | Formule | Niveau |
|----------|---------|--------|
| Marge unitaire | `price − cost_price` | Par produit × supérette |
| Bénéfice par vente | `(price − cost_price) × quantité` | Par transaction |
| Taux de marge | `(price − cost_price) / price × 100` | % par produit |

> Le champ `margin` est calculé automatiquement et renvoyé dans la réponse `StoreProductResponse`.

### 5.4 Contraintes

| Règle | Description |
|-------|-------------|
| Code-barres unique | Un code ne peut référencer qu'un seul produit |
| Pas de doublon produit | `(name, brand)` doit être unique → 409 si collision |
| Pas de prix global | Le prix est **toujours** défini par la supérette |
| Unicité store/product | `UNIQUE(store_id, product_id)` dans `store_products` |
| Session unique par caisse | Une seule session active à la fois par caisse |
| Hiérarchie caisse | Chaque vente valide Store → Caisse → Session |

### 5.5 Alertes stock

| Niveau | Condition | Action |
|--------|-----------|--------|
| Rupture | `stock = 0` | Alerte rouge, produit marqué indisponible |
| Stock faible | `stock <= stock_min` | Alerte orange, notification au gérant |
| Normal | `stock > stock_min` | Aucune alerte |

### 5.6 Notifications push (FCM)

- Token FCM stocké dans `user_preferences.fcm_token`
- Mis à jour à chaque démarrage de l'app mobile via `PATCH /v1/users/me/preferences/fcm-token`
- Notifications ciblées par supérette via jointure sur `store_members` (actifs)

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
| Push notifications | Firebase Cloud Messaging (FCM) |
| Tâches planifiées | `@Scheduled` + `@EnableScheduling` |

---

## 7. Structure du projet

```
src/main/java/com/africa/samba/
├── codeLists/         # Enums métier (Role, BarcodeType, ProductStatus, StoreMemberRole, ...)
├── common/
│   ├── base/          # Entité de base (UUID, createdAt, updatedAt)
│   ├── config/        # Security, CORS, MinIO, Keycloak, Swagger
│   ├── constants/     # Constants, ResponseMessageConstants
│   ├── exception/     # Gestion centralisée des erreurs
│   └── util/          # CustomResponse, BarcodeGenerator, RoleGuard, RequestHeaderParser
├── controllers/       # Endpoints REST
├── dto/               # Request / Response DTOs
├── entity/            # Entités JPA
├── mapper/            # Mappers statiques entity → DTO
├── repository/        # Spring Data JPA repositories
└── services/
    ├── interfaces/    # Contrats de service
    └── impl/          # Implémentations + ScheduledTasksService
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
