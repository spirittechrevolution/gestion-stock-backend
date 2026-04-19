# Samba POS — Backend API

> Système de gestion de stock et point de vente (POS) pour les boutiques et commerces en Afrique.

| Élément | Description |
|---------|-------------|
| **Application** | Samba POS |
| **Phase** | MVP Phase 1 |
| **Version** | 1.1 — 2025 |
| **Backend** | Spring Boot (Java) |
| **Mobile** | React Native + Expo |
| **Base de données** | PostgreSQL + SQLite (offline) |
| **Cible** | Commerces sénégalais |

---

## 1. Présentation du projet

### 1.1 Contexte

**Samba** est une application de gestion interne destinée aux commerçants sénégalais (magasins d'électronique, superettes, supermarchés). Elle permet de gérer le catalogue produits, encaisser les ventes, suivre le stock et imprimer les reçus. Un backend Spring Boot centralise les données et un dashboard admin permet de piloter les boutiques clientes.

L'application mobile (React Native) fonctionne en **mode offline-first** et se synchronise avec ce backend lorsqu'une connexion est disponible.

### 1.2 Objectifs du MVP

- Permettre à un vendeur d'encaisser une vente en **moins de 30 secondes**
- Générer et scanner des **QR codes produits** depuis l'app mobile
- Imprimer un **reçu thermique 58mm** via Bluetooth
- **Synchroniser** les données avec le backend Spring Boot
- Permettre à l'équipe Samba de suivre les boutiques actives depuis un **dashboard admin**
- Permettre à un propriétaire de gérer **plusieurs boutiques** depuis la même application

### 1.3 Commerces cibles

| Type | Description |
|------|-------------|
| Magasin électronique | Téléphones, accessoires, électroménager. Produits référencés par numéro de série. |
| Superette | Produits du quotidien, forte rotation, alertes stock. |
| Supermarché | Grand catalogue, multi-caisses, plusieurs vendeurs, rapports consolidés. |

### 1.4 Plans tarifaires

| Plan | Prix/mois | Produits | Boutiques | Fonctionnalités |
|------|-----------|----------|-----------|-----------------|
| **STARTER** | 5 000 FCFA | 100 | 1 | Ventes, stock de base |
| **PRO** | 12 000 FCFA | 500 | 3 | + Alertes, rapports, multi-boutique |
| **BUSINESS** | 25 000 FCFA | Illimité | 10 | + API, export, support prioritaire |

---

## 2. Fonctionnalités MVP Phase 1

### 2.1 Catalogue produits (saisie manuelle)

Le vendeur ou gérant enregistre ses produits manuellement. Chaque produit reçoit un QR code généré par le backend.

| Champ | Obligatoire | Détail |
|-------|-------------|--------|
| Nom du produit | Oui | Texte libre. Recherche tolérante. |
| Catégorie | Oui | Liste configurable par le gérant. |
| Prix de vente (FCFA) | Oui | Arrondi auto. Affiché sur le QR. |
| Prix d'achat (FCFA) | Non | Pour calcul de marge dans les rapports (Phase 2). |
| Stock initial | Non | Défaut 0. Décrémenté à chaque vente. |
| Stock minimum | Non | Seuil d'alerte rupture configurable. |
| Photo produit | Non | Caméra ou galerie. Compressée auto. |
| Numéro de série | Non | Pour électronique. Unique par produit. |

### 2.2 Génération QR codes

Le backend Spring Boot génère deux types de QR codes :

| Type | Généré par | Contenu | Usage |
|------|------------|---------|-------|
| QR produit | Backend Spring Boot | ID produit + ID boutique + prix | Scan à la caisse → ajout panier |
| QR boutique | Dashboard admin Samba | ID boutique + token activation | Onboarding vendeur au 1er lancement |

- **Bibliothèque Java** : `com.google.zxing` (ZXing)
- **Format image** : PNG 300×300px, fond blanc
- **Endpoint API** : `GET /api/products/{id}/qrcode` — base64 ou URL S3
- **Étiquette** : Nom produit + prix FCFA + QR code (30×20mm)
- **Scanner mobile** : `expo-camera` + `expo-barcode-scanner` (React Native)

### 2.3 Caisse simple

Écran principal du vendeur : scan ou recherche produit → panier → paiement → validation.

| Action | Comportement | Détail |
|--------|--------------|--------|
| Scan QR produit | Ajout instantané au panier | 1er scan = qté 1 · 2e scan même produit = qté 2 |
| Recherche par nom | Filtre temps réel | 3 lettres suffisent. Tolérance aux fautes. |
| Ajustement quantité | Boutons + / − | Modifiable avant validation. |
| Total | Calcul temps réel | En FCFA. Mis à jour à chaque modification. |
| Paiement | Espèces / Wave / Orange Money | Sélection du mode avant validation. |
| Monnaie à rendre | Calcul auto | Après saisie du montant reçu. |
| Validation | Décrémente stock local | Sync backend au prochain appel réseau. |
| Annulation ligne | Swipe gauche | Suppression d'un article avant validation. |


### 2.4 Impression reçu

Après validation d'une vente, le reçu est proposé en impression Bluetooth (thermique 58mm) ou partage WhatsApp/SMS.

| Zone du reçu | Contenu | Configurable |
|--------------|---------|--------------|
| En-tête | Logo + Nom boutique + Adresse + Tél. | Oui |
| Référence | Auto-incrémenté ex: `VTE-2025-00312` | Format configurable |
| Date / heure | `18/04/2025 — 14:32` | Non |
| Vendeur | Prénom du caissier connecté | Activable / désactivable |
| Détail articles | Nom · Qté · Prix unit. · Total ligne | Non |
| Total TTC | Montant final en grand | Non |
| Mode paiement | Espèces / Wave / Orange Money | Non |
| Monnaie rendue | Si paiement espèces | Non |
| Pied de page | Message personnalisé boutique | Oui |

| Mode d'envoi | Technologie |
|--------------|-------------|
| Impression Bluetooth | `react-native-bluetooth-escpos` + imprimante thermique 58mm |
| Partage WhatsApp | Image du reçu générée + `expo-sharing` |
| SMS | Montant total + référence envoyés au numéro saisi |
| Sans reçu | Option « Passer » pour accélérer la caisse |

### 2.5 Ventes du jour

Résumé journalier consultable à tout moment. Données SQLite local, consolidées sur le backend après sync.

| Indicateur | Source | Détail |
|------------|--------|--------|
| Nombre de transactions | SQLite local | Ventes validées du jour |
| Chiffre d'affaires total | SQLite local | Somme des totaux en FCFA |
| Répartition paiement | SQLite local | Espèces vs Wave vs Orange Money |
| Top 3 produits | SQLite local | Les plus vendus en quantité |
| Liste des ventes | SQLite local | Heure + montant + mode de paiement |

### 2.6 Stock en temps réel

Le stock est décrémenté automatiquement à chaque vente validée. Alertes visuelles pour les produits en rupture ou sous seuil.

| Alerte | Déclencheur | Affichage |
|--------|-------------|-----------|
| 🔴 Rupture | Stock = 0 | Badge rouge sur la fiche produit |
| 🟠 Stock faible | Stock ≤ seuil minimum | Badge orange sur la fiche produit |
| 🟢 Normal | Stock > seuil minimum | Barre verte proportionnelle |

- Correction manuelle du stock possible (entrée de marchandise)
- Historique des mouvements sauvegardé côté backend

### 2.7 Multi-utilisateurs + PIN

Chaque utilisateur se connecte avec un code PIN 4 chiffres. Droits selon le rôle :

| Action | Propriétaire | Gérant | Vendeur |
|--------|:------------:|:------:|:-------:|
| Effectuer une vente | ✅ | ✅ | ✅ |
| Modifier le prix | ✅ | ✅ | ❌ |
| Annuler une vente | ✅ | ✅ | ❌ |
| Ajouter un produit | ✅ | ✅ | ❌ |
| Modifier le stock | ✅ | ✅ | ❌ |
| Voir tous les rapports | ✅ | ✅ | Son bilan uniquement |
| Gérer les utilisateurs | ✅ | ❌ | ❌ |
| Paramètres boutique | ✅ | ❌ | ❌ |

### 2.8 Multi-boutiques

Un même propriétaire peut gérer plusieurs boutiques distinctes depuis la même application mobile.

| Capacité | Description |
|----------|-------------|
| Nombre de boutiques | Illimité (selon plan d'abonnement) |
| Changement de boutique | Sélection depuis un menu déroulant (en-tête de l'app) |
| Données isolées | Chaque boutique a son propre catalogue, ses ventes, ses utilisateurs |
| Synchronisation | Indépendante par boutique |
| Rapports consolidés | Vue globale du propriétaire : CA toutes boutiques, top produits |

**Flux d'ajout d'une nouvelle boutique :**

1. Propriétaire connecté → Menu "Mes boutiques"
2. Bouton "Ajouter une boutique"
3. Scan du QR code onboarding (généré par dashboard admin Samba)
4. La nouvelle boutique apparaît dans la liste
5. Basculement instantané entre les boutiques

```
┌─────────────────────────────────────────┐
│  [Samba POS]    [Boutique actuelle ▼]   │
│                  ├─ Dakar - Liberté     │
│                  ├─ Thiès - Centre      │
│                  └─ Mbour - Plage       │
├─────────────────────────────────────────┤
│  CA du jour : 125 000 FCFA             │
│  Ventes     : 23                        │
│  Stock alerte : 3 produits              │
└─────────────────────────────────────────┘
```

---

## 3. Backend — API REST

Le backend expose une API REST sécurisée qui gère :

- **Produits** — CRUD, catégorisation, génération de QR codes (ZXing)
- **Ventes** — Enregistrement des ventes, calcul automatique, historique journalier
- **Stock** — Décrémentation automatique à la vente, corrections manuelles, alertes de rupture
- **Boutiques** — Gestion multi-boutiques, activation, plans d'abonnement
- **Synchronisation offline** — File de synchronisation (SyncQueue) pour le mode hors-ligne
- **Authentification** — OAuth2/OIDC via Keycloak, gestion des rôles (VENDEUR, GERANT, ADMIN)
- **Stockage fichiers** — Upload direct vers MinIO via URLs présignées (photos produits, logos, avatars)
- **Rapports** — Chiffre d'affaires journalier, top produits, répartition par mode de paiement

### 3.1 Stack technique

| Composant | Technologie |
|-----------|-------------|
| Framework | Spring Boot 4.0.5 / Java 21 |
| Base de données | PostgreSQL 17 + Flyway |
| Authentification | Keycloak 26.1.5 (OAuth2 / OIDC) |
| Stockage objet | MinIO (S3-compatible) |
| Documentation API | springdoc-openapi 2.8.6 (Swagger UI) |
| Monitoring | Prometheus + Grafana + Micrometer |
| Conteneurisation | Docker Compose (9 services) |

### 3.2 Infrastructure Docker

```bash
cd docker && docker compose up -d    # Démarrer tous les services
```

| Service | Port | URL |
|---------|------|-----|
| PostgreSQL | 5433 | `jdbc:postgresql://localhost:5433` |
| pgAdmin | 5050 | http://localhost:5050 |
| Keycloak | 8180 | http://localhost:8180 |
| MinIO Console | 9001 | http://localhost:9001 |
| Prometheus | 9090 | http://localhost:9090 |
| Grafana | 3001 | http://localhost:3001 |
| Dozzle (logs) | 8888 | http://localhost:8888 |
| Backend API | 9090 | http://localhost:9090/api |

### 3.3 Structure du projet

```
src/main/java/com/africa/samba/
├── codeLists/         # Enums métier (TypeVente, ModePaiement, StatutVente, ...)
├── common/
│   ├── base/          # Entité de base (audit fields)
│   ├── config/        # Security, CORS, MinIO, Keycloak, Swagger
│   ├── constants/     # Constantes globales et messages de réponse
│   ├── exception/     # Gestion centralisée des erreurs
│   └── util/          # Utilitaires (CustomResponse, RoleGuard, ...)
├── controllers/       # Endpoints REST
├── dto/               # Request / Response DTOs
├── entity/            # Entités JPA (User, Produit, Vente, Stock, Boutique, ...)
├── mapper/            # MapStruct mappers
├── repository/        # Spring Data JPA repositories
└── services/          # Logique métier (interfaces + implémentations)
```

---

## 4. Démarrage rapide

### Prérequis

- Java 21+
- Docker & Docker Compose
- Maven 3.9+

### Lancement

```bash
# 1. Copier et remplir les variables d'environnement
cp docker/.env.prod.example docker/.env
# Éditer docker/.env avec vos valeurs

# 2. Créer le fichier .env racine
cp docker/.env .env
# Ajuster les valeurs (ports, credentials)

# 3. Démarrer l'infrastructure
cd docker && docker compose up -d

# 4. Lancer le backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

### Swagger UI

Une fois le backend démarré : http://localhost:9090/api/swagger-ui.html

---

## 5. Boutiques compatibles MVP Phase 1

> Types de commerces entièrement supportés par le MVP Samba Phase 1 sans adaptation nécessaire.

### 5.1 Supérette

Commerce de proximité avec forte rotation de produits du quotidien. C'est le **cas cible principal** du MVP — alertes de rupture de stock, caisse rapide, multi-vendeurs et rapports journaliers couvrent l'intégralité des besoins opérationnels.

**Fonctionnalités** : Catalogue, caisse rapide, alertes stock, multi-vendeurs, rapports journaliers

### 5.2 Magasin électronique (téléphones, accessoires)

Cas cible explicitement documenté dans le cahier des charges Samba. Le champ **numéro de série** est intégré nativement dans le modèle produit, ce qui couvre parfaitement la traçabilité des appareils.

**Fonctionnalités** : Numéro de série, QR code par produit, prix achat/vente, calcul de marge

### 5.3 Boutique informatique

Logique identique au magasin électronique — produits référencés par numéro de série, catalogue structuré, calcul de marge via prix achat/vente. Aucune adaptation nécessaire.

**Fonctionnalités** : Numéro de série, catalogue, marge produit, QR étiquette

### 5.4 Magasin d'électroménager

Articles vendus à l'unité, prix élevés, numéros de série pour la traçabilité. La faible rotation correspond bien au modèle de stock de Samba, et les étiquettes QR sont particulièrement utiles pour des produits encombrants.

**Fonctionnalités** : Numéro de série, faible rotation stock, QR étiquette, photo produit

### 5.5 Librairie / Papeterie

Catalogue standard sans complexité particulière. La recherche tolérante par nom (3 lettres suffisent) est idéale pour retrouver rapidement un article parmi un grand catalogue de références.

**Fonctionnalités** : Catalogue, recherche tolérante, QR produit, catégories configurables

### 5.6 Boutique cadeaux / Décoration

Produits unitaires à prix fixes, souvent accompagnés de photos. Le champ photo produit et les QR codes sur étiquettes s'intègrent naturellement dans ce type de commerce.

**Fonctionnalités** : Photo produit, QR étiquette, catalogue, caisse simple

### 5.7 Boutique de sport (équipements)

Équipements à référence unique, logique similaire à l'électronique. Les catégories configurables permettent d'organiser le catalogue par discipline ou type d'article.

**Fonctionnalités** : Catalogue, alertes stock, catégories, QR produit

### 5.8 Magasin de jouets

Catalogue standard, produits à prix fixes. Les alertes de stock faible sont particulièrement utiles pour anticiper les périodes de forte demande (rentrée scolaire, fêtes).

**Fonctionnalités** : Catalogue, alertes stock, catégories, rapports journaliers

### 5.9 Animalerie (accessoires)

Accessoires et produits standards sans contrainte sanitaire au niveau MVP. Les alertes stock et la caisse rapide couvrent bien les besoins opérationnels quotidiens.

**Fonctionnalités** : Catalogue, alertes stock, caisse rapide, catégories

---

## Licence

Projet privé — © Spirit Tech Revolution
