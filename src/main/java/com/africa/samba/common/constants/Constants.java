package com.africa.samba.common.constants;

public class Constants {

  private Constants() {}

  public static final String X_JWT_ASSERTION = "X-JWT-Assertion";
  public static final String AUTHORIZATION = "Authorization";

  /** Préfixe des rôles Samba dans le realm Keycloak (ex: {@code SAMBA_ADMIN}). */
  public static final String KEYCLOAK_ROLE_PREFIX = "SAMBA_";

  public static class Message {

    private Message() {}

    public static final String SUCCESS_BODY = "SUCCESS";
    public static final String SERVER_ERROR_BODY = "INTERNAL_SERVER_ERROR";
    public static final String UNAUTHORIZED_BODY = "UNAUTHORIZED";
    public static final String CONFLICT_BODY = "CONFLICT";
    public static final String BAD_REQUEST_BODY = "BAD_REQUEST";
    public static final String NOT_FOUND_BODY = "NOT_FOUND";
  }

  public static class Status {

    private Status() {}

    public static final int OK = 200;
    public static final int CREATED = 201;
    public static final int NO_CONTENT = 204;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int CONFLICT = 409;
    public static final int BAD_REQUEST = 400;
    public static final int NOT_FOUND = 404;
  }

  /** Valeurs des code lists — miroir des valeurs persistées dans {@code la_code_list}. */
  public static class CodeList {

    private CodeList() {}

    public static class TypeVente {
      private TypeVente() {}

      public static final String COMPTANT = "COMPTANT";
      public static final String GROS = "GROS";
      public static final String DEVIS = "DEVIS";
      public static final String COMMANDE_DISTANCE = "COMMANDE_DISTANCE";
    }

    public static class ModePaiement {
      private ModePaiement() {}

      public static final String ESPECES = "ESPECES";
      public static final String WAVE = "WAVE";
      public static final String ORANGE_MONEY = "ORANGE_MONEY";
      public static final String FREE_MONEY = "FREE_MONEY";
      public static final String MIXTE = "MIXTE";
    }

    public static class StatutVente {
      private StatutVente() {}

      public static final String BROUILLON = "BROUILLON";
      public static final String EN_ATTENTE = "EN_ATTENTE";
      public static final String VALIDEE = "VALIDEE";
      public static final String ANNULEE = "ANNULEE";
      public static final String REMBOURSEE = "REMBOURSEE";
    }

    public static class TypeMouvement {
      private TypeMouvement() {}

      public static final String ENTREE = "ENTREE";
      public static final String SORTIE_VENTE = "SORTIE_VENTE";
      public static final String CORRECTION_POS = "CORRECTION_POS";
      public static final String CORRECTION_NEG = "CORRECTION_NEG";
      public static final String RETOUR_CLIENT = "RETOUR_CLIENT";
      public static final String TRANSFERT_IN = "TRANSFERT_IN";
      public static final String TRANSFERT_OUT = "TRANSFERT_OUT";
    }

    public static class NiveauAlerte {
      private NiveauAlerte() {}

      public static final String NORMAL = "NORMAL";
      public static final String FAIBLE = "FAIBLE";
      public static final String RUPTURE = "RUPTURE";
    }

    public static class StatutBoutique {
      private StatutBoutique() {}

      public static final String EN_ATTENTE = "EN_ATTENTE";
      public static final String ACTIVE = "ACTIVE";
      public static final String SUSPENDUE = "SUSPENDUE";
      public static final String RESILIEE = "RESILIEE";
    }

    public static class SubscriptionPlan {
      private SubscriptionPlan() {}

      public static final String STARTER = "STARTER";
      public static final String PRO = "PRO";
      public static final String BUSINESS = "BUSINESS";
    }

    public static class TypeQRCode {
      private TypeQRCode() {}

      public static final String PRODUIT = "PRODUIT";
      public static final String BOUTIQUE = "BOUTIQUE";
    }
  }
}
