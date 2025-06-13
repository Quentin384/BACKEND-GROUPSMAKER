# 🛠 BACKEND-GROUPSMAKER

API backend développée en Java Spring Boot pour la gestion sécurisée de listes, personnes, tirages et utilisateurs dans le cadre du projet **GroupsMaker**.

---

## 📋 Table des matières

- [Technologies](#technologies)  
- [Installation](#installation)  
- [Configuration](#configuration)  
- [Démarrage](#démarrage)  
- [Architecture et logique métier](#architecture-et-logique-métier)  
- [Endpoints détaillés](#endpoints-détaillés)  
- [Sécurité et authentification](#sécurité-et-authentification)  
- [Exemples d’utilisation avec JWT](#exemples-dutilisation-avec-jwt)  
- [Tests avec Postman](#tests-avec-postman)  
- [Contribuer](#contribuer)  
- [Licence](#licence)

---

## 🔧 Technologies utilisées

- Java 17  
- Spring Boot  
- Spring Data JPA (PostgreSQL)  
- Spring Security (JWT, BCrypt)  
- Maven (build)  

---

## ⚙ Installation

1. Clone le repository backend :

```bash
git clone https://github.com/Quentin384/BACKEND-GROUPSMAKER.git
cd BACKEND-GROUPSMAKER
```

2. Configure ta base PostgreSQL et mets à jour le fichier `src/main/resources/application.properties` avec tes infos (URL, utilisateur, mot de passe).

3. (Optionnel) Pour un développement rapide, active la création automatique des tables :

```properties
spring.jpa.hibernate.ddl-auto=create
```

---

## ▶️ Démarrage de l’application

```bash
./mvnw spring-boot:run
```

L’API sera disponible sur `http://localhost:8080`.

---

## 🧱 Architecture et logique métier

- **Utilisateur** : chaque utilisateur possède un nom d’utilisateur unique et un mot de passe encodé.  
- **Liste** : chaque liste appartient à un utilisateur unique.  
- **Personne** : entité liée à une liste, représentant une personne dans cette liste.  
- **Tirage** : opération liée à une liste, enregistrée avec une date.  

**Contrôle d’accès** :  
- Un utilisateur ne peut accéder qu’aux listes dont il est propriétaire.  
- Toute modification ou ajout dans une liste vérifie que l’utilisateur connecté est bien propriétaire, sinon la requête retourne un `403 Forbidden`.

---

## 🔗 Endpoints détaillés

### Authentification

| Route               | Méthode | Description                     | Auth requise |
|---------------------|---------|--------------------------------|--------------|
| `/api/auth/signup`  | POST    | Inscription d’un utilisateur    | Non          |
| `/api/auth/login`   | POST    | Connexion et récupération JWT   | Non          |

### Gestion des listes, personnes, tirages (sécurisé)

| Route                                        | Méthode | Description                                             | Autorisation                  |
|----------------------------------------------|---------|---------------------------------------------------------|-------------------------------|
| `/api/listes`                                | GET     | Récupère les listes associées à l’utilisateur connecté | `ROLE_USER` (JWT obligatoire) |
| `/api/listes`                                | POST    | Crée une nouvelle liste liée à l’utilisateur connecté  | `ROLE_USER`                   |
| `/api/listes/{idListe}/personnes`            | GET     | Récupère les personnes d’une liste donnée               | `ROLE_USER`, propriétaire     |
| `/api/listes/{idListe}/personnes`            | POST    | Ajoute une personne à une liste                          | `ROLE_USER`, propriétaire     |
| `/api/listes/{idListe}/tirages`               | GET     | Récupère les tirages d’une liste                         | `ROLE_USER`, propriétaire     |
| `/api/listes/{idListe}/tirages`               | POST    | Ajoute un tirage avec date automatique                   | `ROLE_USER`, propriétaire     |

> Toute tentative d’accès à une liste non possédée par l’utilisateur renvoie un HTTP 403.

---

## 🔐 Sécurité et authentification

- Mot de passe stocké encodé via BCrypt.  
- Utilisation de JWT (JSON Web Token) pour les sessions stateless.  
- Configuration Spring Security avec filtre JWT pour protéger les endpoints.  
- Seules les routes `/api/auth/**` sont accessibles sans authentification.  
- Tous les autres endpoints exigent un JWT valide et un rôle `ROLE_USER`.  

Le token JWT est transmis dans l’en-tête HTTP `Authorization` sous la forme :

```
Authorization: Bearer <token>
```

---

## 📖 Exemples d’utilisation

### Inscription

```bash
curl -X POST http://localhost:8080/api/auth/signup -H "Content-Type: application/json" -d '{"username":"quentin","password":"monMotDePasse"}'
```

### Connexion (login)

```bash
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"quentin","password":"monMotDePasse"}'
```

> Réponse : JWT à utiliser dans les requêtes suivantes.

---

### Récupérer les listes de l’utilisateur (token JWT obligatoire)

```bash
curl -H "Authorization: Bearer <TON_TOKEN>" http://localhost:8080/api/listes
```

---

### Créer une liste

```bash
curl -X POST http://localhost:8080/api/listes -H "Authorization: Bearer <TON_TOKEN>" -H "Content-Type: application/json" -d '{"nom":"Liste A"}'
```

---

### Ajouter une personne à une liste

```bash
curl -X POST http://localhost:8080/api/listes/1/personnes -H "Authorization: Bearer <TON_TOKEN>" -H "Content-Type: application/json" -d '{"nom":"Alice", "prenom":"Dupont"}'
```

---

## 🧪 Tests avec Postman

- Crée une collection et importe les endpoints.  
- Configure le token JWT dans la section Authorization (Bearer Token) pour les routes sécurisées.  
- Teste les réponses 401 sans token, 200 avec token valide.  
- Vérifie que tu ne peux accéder qu’aux listes dont tu es propriétaire (403 sinon).  

---

## 🤝 Contribution

Contributions, améliorations et corrections de bugs bienvenues. Forke le projet, fais tes modifications puis crée une pull request.

---

## 📄 Licence

Ce projet est sous licence MIT. Voir [LICENSE](./LICENSE).
