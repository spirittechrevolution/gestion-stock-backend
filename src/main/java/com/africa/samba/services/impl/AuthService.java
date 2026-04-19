package com.africa.samba.services.impl;

/**
 * AuthService — gestion de l'authentification Samba POS.
 *
 * <p>Flux connexion vendeur (mobile) : 1. L'app envoie boutiqueId + pin 2. On vérifie le PIN
 * (BCrypt) 3. On vérifie que la boutique est ACTIVE 4. On retourne un JWT signé
 *
 * <p>Flux onboarding boutique (1er lancement app) : 1. L'app scanne le QR boutique → extrait le
 * tokenActivation 2. On cherche la boutique par ce token 3. On l'active, on génère le mot de passe
 * admin initial
 */
/*
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService {

    private final BoutiqueRepository boutiqueRepo;
    private final UserRepository UserRepo;
    private final UserPreferencesRepository prefsRepo;
    private final PasswordEncoder          passwordEncoder;
    private final JwtService               jwtService;

    // ── Connexion par PIN ─────────────────────────────────────────────────

    @Transactional
    public LoginResult loginPin(Long boutiqueId, Long UserId, String pin) {
        Boutique boutique = boutiqueRepo.findById(boutiqueId)
                .orElseThrow(() -> ResourceNotFoundException.boutique(boutiqueId));

        if (!boutique.isActive())
            throw AuthenticationException.boutiqueInactive();

        User User = UserRepo.findById(UserId)
                .orElseThrow(() -> new ResourceNotFoundException("User introuvable"));

        if (!User.getActif())
            throw new AuthenticationException("Compte désactivé");

        if (!passwordEncoder.matches(pin, User.getPinHash()))
            throw AuthenticationException.pinInvalide();

        String token = jwtService.genererToken(User);
        User.setTokenSession(token);
        User.setLastLoginAt(LocalDateTime.now());
        UserRepo.save(User);

        log.info("Connexion réussie : User={}, boutique={}", UserId, boutiqueId);
        return LoginResult.builder()
                .token(token)
                .User(User)
                .boutique(boutique)
                .build();
    }

    // ── Onboarding boutique via QR ────────────────────────────────────────

    @Transactional
    public Boutique activerViaScan(String tokenActivation) {
        Boutique boutique = boutiqueRepo.findByTokenActivation(tokenActivation)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Token d'activation invalide : " + tokenActivation
                ));

        if (boutique.getStatut() != StatutBoutique.EN_ATTENTE)
            throw new IllegalStateException("Boutique déjà activée ou suspendue");

        boutique.setStatut(StatutBoutique.ACTIVE);
        boutique.setDateActivation(LocalDateTime.now());
        Boutique saved = boutiqueRepo.save(boutique);

        log.info("Boutique activée via QR scan : id={}, nom={}", boutique.getId(), boutique.getNom());
        return saved;
    }

    // ── Inscription boutique (dashboard admin → nouvelle boutique) ─────────

    @Transactional
    public Boutique inscrireBoutique(Boutique boutique) {
        if (boutiqueRepo.existsByEmail(boutique.getEmail()))
            throw new DuplicateResourceException("Email déjà utilisé : " + boutique.getEmail());

        // Génération du token d'onboarding
        boutique.setTokenActivation(UUID.randomUUID().toString().replace("-", ""));
        boutique.setStatut(StatutBoutique.EN_ATTENTE);
        Boutique saved = boutiqueRepo.save(boutique);

        log.info("Boutique inscrite : id={}, email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    // ── Déconnexion ───────────────────────────────────────────────────────

    @Transactional
    public void deconnecter(Long UserId) {
        UserRepo.clearTokenSession(UserId);
    }

    // ── Création User ──────────────────────────────────────────────

    @Transactional
    public User creerUser(User User, String pinClair, Long boutiqueId) {
        Boutique boutique = boutiqueRepo.findById(boutiqueId)
                .orElseThrow(() -> ResourceNotFoundException.boutique(boutiqueId));

        // Vérification limite plan
        long nbVendeurs = UserRepo.countVendeursActifs(boutiqueId);
        if (!boutique.getPlan().isVendeurAutorise((int) nbVendeurs + 1))
            throw PlanLimiteException.vendeurs(boutique.getPlan().name());

        if (UserRepo.existsByNomAndBoutiqueId(User.getNom(), boutiqueId))
            throw new DuplicateResourceException("Nom déjà utilisé dans cette boutique : " + User.getNom());

        // Hashage du PIN
        if (pinClair == null || pinClair.length() != 4 || !pinClair.matches("\\d{4}"))
            throw new IllegalArgumentException("Le PIN doit être composé de 4 chiffres");

        User.setPinHash(passwordEncoder.encode(pinClair));
        User.setBoutique(boutique);
        User.setActif(true);

        User saved = UserRepo.save(User);

        // Création automatique des préférences avec valeurs par défaut
        if (!prefsRepo.existsByUserId(saved.getId())) {
            prefsRepo.save(UserPreferences.defautPour(saved));
        }

        log.info("User créé : id={}, role={}, boutique={}", saved.getId(), saved.getRole(), boutiqueId);
        return saved;
    }

    // ── DTO résultat connexion ────────────────────────────────────────────

    @Builder
    @Getter
    public static class LoginResult {
        private final String      token;
        private final User User;
        private final Boutique    boutique;
    }
}*/
