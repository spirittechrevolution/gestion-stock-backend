# Samba POS — Backend API

> Système de gestion de stock et point de vente (POS) pour les boutiques et commerces en Afrique.

---

## Objectif

**Samba** est une plateforme SaaS de gestion de stock et de point de vente conçue pour les petits et moyens commerces africains. L'application mobile (React Native) fonctionne en **mode offline-first** et se synchronise avec ce backend lorsqu'une connexion est disponible.

Le backend expose une API REST sécurisée qui gère :
- **Produits** — CRUD, catégorisation, génération de QR codes (ZXing)
- **Ventes** — Enregistrement des ventes, calcul automatique, historique journalier
- **Stock** — Décrémentation automatique à la vente, corrections manuelles, alertes de rupture
- **Boutiques** — Gestion multi-boutiques, activation, plans d'abonnement
- **Synchronisation offline** — File de synchronisation (SyncQueue) pour le mode hors-ligne
- **Authentification** — OAuth2/OIDC via Keycloak, gestion des rôles (VENDEUR, GERANT, ADMIN)
- **Stockage fichiers** — Upload direct vers MinIO via URLs présignées (photos produits, logos, avatars)
- **Rapports** — Chiffre d'affaires journalier, top produits, répartition par mode de paiement

## Plans tarifaires

| Plan | Prix/mois | Produits | Boutiques | Fonctionnalités |
|------|-----------|----------|-----------|-----------------|
| **STARTER** | 5 000 FCFA | 100 | 1 | Ventes, stock de base |
| **PRO** | 12 000 FCFA | 500 | 3 | + Alertes, rapports, multi-boutique |
| **BUSINESS** | 25 000 FCFA | Illimité | 10 | + API, export, support prioritaire |

## Stack technique

| Composant | Technologie |
|-----------|-------------|
| Framework | Spring Boot 4.0.5 / Java 21 |
| Base de données | PostgreSQL 17 + Flyway |
| Authentification | Keycloak 26.1.5 (OAuth2 / OIDC) |
| Stockage objet | MinIO (S3-compatible) |
| Documentation API | springdoc-openapi 2.8.6 (Swagger UI) |
| Monitoring | Prometheus + Grafana + Micrometer |
| Conteneurisation | Docker Compose (9 services) |

## Infrastructure Docker

```
docker compose up -d        # Démarrer tous les services
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

## Démarrage rapide

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

## Structure du projet

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

## Licence

Projet privé — © Spirit Tech Revolution
