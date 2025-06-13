# Diagramme de la sécurité - Backend GroupsMaker

```mermaid
graph TD
    Client[Client (Postman / Frontend)]
    Client -->|Requête avec JWT| FilterChain[Spring Security Filter Chain]

    FilterChain --> JwtFilter[JwtAuthenticationFilter]
    JwtFilter --> JwtService[JwtService]
    JwtService -->|Valide le token JWT| JwtFilter

    JwtFilter --> UtilisateurService[UtilisateurService (UserDetailsService)]
    UtilisateurService --> Database[Base de données Utilisateur]
    Database --> UtilisateurService

    JwtFilter -->|Crée Authentication| SecurityContext[SecurityContextHolder]
    SecurityContext --> FilterChain

    FilterChain -->|Accès autorisé| Controllers[Contrôleurs REST sécurisés]
    FilterChain -->|Sinon accès refusé (401)| Error401[Erreur 401 Unauthorized]

    Client -->|Requête sans JWT| FilterChain
