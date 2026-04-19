# Guide UI/UX pour Designer (Figma)

Ce document synthétise l'organisation fonctionnelle et les flux principaux de l'application Samba, afin d'aider le designer à concevoir les écrans web et mobile.

---

## 1. Vision d'ensemble

Samba est une plateforme de gestion multi-supérettes, avec :
- Un **catalogue global** de produits partagé
- Des **supérettes** (magasins) ayant leur propre stock, prix, membres
- Des **rôles** (ADMIN, OWNER, MANAGER, EMPLOYEE) qui conditionnent l'accès aux écrans et actions
- Un système de **vente en caisse** (scan, panier, paiement)
- Un backoffice pour la gestion, un front mobile pour la caisse

---

## 2. Profils utilisateurs & parcours

### 2.1 Rôles principaux
- **ADMIN** : gestion plateforme, accès total
- **OWNER** : propriétaire de supérette(s), gestion de ses magasins
- **MANAGER** : gérant local, valide produits, gère équipe
- **EMPLOYEE** : vendeur/caissier, scan, vente, création rapide

### 2.2 Parcours clés
- **Connexion** (Keycloak, OAuth2)
- **Navigation** : tableau de bord, catalogue, ventes, stocks, membres, statistiques
- **Scan produit** (mobile) : scan code-barres, ajout au panier, vente
- **Ajout produit** : via scan ou création manuelle (admin/manager/employé)
- **Gestion stock/prix** : par supérette
- **Alertes stock** : notifications visuelles (rupture, stock faible)
- **Gestion membres** : ajout, modification, suppression, rôles
- **Historique/Audit** : consultation des actions

---

## 3. Écrans principaux à prévoir

### 3.1 Web (Backoffice)
- **Login**
- **Dashboard** (stats globales, alertes)
- **Catalogue produits** (liste, recherche, fiche, ajout/modif/suppression)
- **Gestion supérettes** (liste, fiche, création, édition)
- **Catalogue local** (prix, stock, scan, alertes)
- **Gestion membres** (liste, ajout, rôles)
- **Ventes** (historique, stats, détails)
- **Audit log** (historique actions)
- **Paramètres compte** (profil, avatar)

### 3.2 Mobile (Caisse)
- **Login**
- **Accueil caisse** (scan, panier, vente rapide)
- **Scan produit** (caméra, recherche code-barres)
- **Ajout rapide produit** (si non trouvé)
- **Panier** (liste, quantités, total, paiement)
- **Historique ventes**
- **Alertes stock**
- **Profil utilisateur**

---

## 4. Composants & UI patterns
- **Tableaux** (catalogue, membres, ventes)
- **Fiches détaillées** (produit, supérette, membre)
- **Modales** (ajout/édition, confirmation)
- **Badges** (statut produit, rôle, stock)
- **Notifications** (succès, erreur, alertes)
- **Stepper** (création produit, vente)
- **Recherche & filtres** (catalogue, ventes)
- **Scan code-barres** (mobile)

---

## 5. Codes couleur & statuts
- **Stock normal** : vert
- **Stock faible** : orange
- **Rupture** : rouge
- **Produit validé** : badge "APPROVED" (vert)
- **Produit en attente** : badge "PENDING" (jaune)
- **Rôle** : badge (ADMIN, OWNER, MANAGER, EMPLOYEE)

---

## 6. Points d'attention UX
- **Séparation claire** entre catalogue global et local
- **Visibilité immédiate** des alertes stock
- **Actions rapides** (scan, ajout, vente)
- **Gestion des droits** : masquer/inactiver les actions non autorisées selon le rôle
- **Feedback utilisateur** : loading, succès, erreurs explicites
- **Responsive** : web (desktop/tablette), mobile (caisse)

---

## 7. Ressources utiles
- [Swagger API](http://localhost:9090/api/swagger-ui.html) : endpoints, structures, rôles requis
- [README technique](README.md) : modèle de données, règles métier

---

**Contact équipe technique** :
- Pour toute question sur les flux, droits, ou données, contacter le lead dev ou PO.
