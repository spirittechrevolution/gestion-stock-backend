package com.africa.samba.common.constants;

/**
 * Constantes de messages de réponse retournés dans le champ {@code message} de {@link
 * com.africa.samba.common.util.CustomResponse}.
 *
 * <p>Convention : {@code <DOMAINE>_<OPERATION>_<SUCCES|FAILURE>[_RAISON]}
 */
public class ResponseMessageConstants {

  private ResponseMessageConstants() {}

  // ── AUTH ────────────────────────────────────────────────────────
  public static final String USER_LOGIN_SUCCESS = "USER_LOGIN_SUCCESS";
  public static final String USER_LOGOUT_SUCCESS = "USER_LOGOUT_SUCCESS";
  public static final String USER_FORGOT_PASSWORD_SUCCESS = "USER_FORGOT_PASSWORD_SUCCESS";
  public static final String USER_INVALID_CREDENTIALS = "USER_INVALID_CREDENTIALS";
  public static final String USER_ROLE_ASSIGN_SUCCESS = "USER_ROLE_ASSIGN_SUCCESS";
  public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
  public static final String USER_UNAUTHORIZED = "USER_UNAUTHORIZED";
  public static final String USER_FORBIDDEN = "USER_FORBIDDEN";

  // ── OTP ─────────────────────────────────────────────────────────
  public static final String OTP_SENT_SUCCESS = "OTP_SENT_SUCCESS";
  public static final String OTP_VERIFIED_SUCCESS = "OTP_VERIFIED_SUCCESS";
  public static final String OTP_INVALID = "OTP_INVALID";
  public static final String OTP_NOT_FOUND = "OTP_NOT_FOUND";

  // ── ADMIN ───────────────────────────────────────────────────────
  public static final String ADMIN_CREATE_SUCCESS = "ADMIN_CREATE_SUCCESS";
  public static final String ADMIN_ROLE_ASSIGN_SUCCESS = "ADMIN_ROLE_ASSIGN_SUCCESS";
  public static final String ADMIN_DELETE_SUCCESS = "ADMIN_DELETE_SUCCESS";

  // ── USER ────────────────────────────────────────────────────────
  public static final String USER_GET_SUCCESS = "USER_GET_SUCCESS";
  public static final String USER_GET_LIST_SUCCESS = "USER_GET_LIST_SUCCESS";
  public static final String USER_CREATE_SUCCESS = "USER_CREATE_SUCCESS";
  public static final String USER_UPDATE_SUCCESS = "USER_UPDATE_SUCCESS";
  public static final String USER_DELETE_SUCCESS = "USER_DELETE_SUCCESS";
  public static final String USER_GET_FAILURE = "USER_GET_FAILURE";
  public static final String USER_GET_FAILURE_NOT_FOUND = "USER_GET_FAILURE_NOT_FOUND";
  public static final String USER_CREATE_FAILURE = "USER_CREATE_FAILURE";
  public static final String USER_CREATE_FAILURE_ALREADY_EXISTS =
      "USER_CREATE_FAILURE_ALREADY_EXISTS";
  public static final String USER_UPDATE_FAILURE = "USER_UPDATE_FAILURE";
  public static final String USER_DELETE_FAILURE = "USER_DELETE_FAILURE";

  // ── PRODUIT ─────────────────────────────────────────────────────
  public static final String PRODUIT_GET_SUCCESS = "PRODUIT_GET_SUCCESS";
  public static final String PRODUIT_GET_LIST_SUCCESS = "PRODUIT_GET_LIST_SUCCESS";
  public static final String PRODUIT_CREATE_SUCCESS = "PRODUIT_CREATE_SUCCESS";
  public static final String PRODUIT_UPDATE_SUCCESS = "PRODUIT_UPDATE_SUCCESS";
  public static final String PRODUIT_DELETE_SUCCESS = "PRODUIT_DELETE_SUCCESS";
  public static final String PRODUIT_GET_FAILURE_NOT_FOUND = "PRODUIT_GET_FAILURE_NOT_FOUND";
  public static final String PRODUIT_CREATE_FAILURE = "PRODUIT_CREATE_FAILURE";
  public static final String PRODUIT_CREATE_FAILURE_BAD_REQUEST =
      "PRODUIT_CREATE_FAILURE_BAD_REQUEST";
  public static final String PRODUIT_UPDATE_FAILURE = "PRODUIT_UPDATE_FAILURE";
  public static final String PRODUIT_UPDATE_FAILURE_NOT_FOUND =
      "PRODUIT_UPDATE_FAILURE_NOT_FOUND";

  // ── VENTE ───────────────────────────────────────────────────────
  public static final String VENTE_GET_SUCCESS = "VENTE_GET_SUCCESS";
  public static final String VENTE_GET_LIST_SUCCESS = "VENTE_GET_LIST_SUCCESS";
  public static final String VENTE_CREATE_SUCCESS = "VENTE_CREATE_SUCCESS";
  public static final String VENTE_CREATE_FAILURE = "VENTE_CREATE_FAILURE";
  public static final String VENTE_CREATE_FAILURE_BAD_REQUEST =
      "VENTE_CREATE_FAILURE_BAD_REQUEST";
  public static final String VENTE_GET_FAILURE_NOT_FOUND = "VENTE_GET_FAILURE_NOT_FOUND";
  public static final String VENTE_ANNULEE_SUCCESS = "VENTE_ANNULEE_SUCCESS";

  // ── STOCK ───────────────────────────────────────────────────────
  public static final String STOCK_MOUVEMENT_SUCCESS = "STOCK_MOUVEMENT_SUCCESS";
  public static final String STOCK_MOUVEMENT_FAILURE = "STOCK_MOUVEMENT_FAILURE";
  public static final String STOCK_ALERTE_LIST_SUCCESS = "STOCK_ALERTE_LIST_SUCCESS";

  // ── BOUTIQUE ────────────────────────────────────────────────────
  public static final String BOUTIQUE_GET_SUCCESS = "BOUTIQUE_GET_SUCCESS";
  public static final String BOUTIQUE_GET_LIST_SUCCESS = "BOUTIQUE_GET_LIST_SUCCESS";
  public static final String BOUTIQUE_CREATE_SUCCESS = "BOUTIQUE_CREATE_SUCCESS";
  public static final String BOUTIQUE_UPDATE_SUCCESS = "BOUTIQUE_UPDATE_SUCCESS";
  public static final String BOUTIQUE_GET_FAILURE_NOT_FOUND = "BOUTIQUE_GET_FAILURE_NOT_FOUND";

  // ── CATEGORIE ───────────────────────────────────────────────────
  public static final String CATEGORIE_GET_SUCCESS = "CATEGORIE_GET_SUCCESS";
  public static final String CATEGORIE_GET_LIST_SUCCESS = "CATEGORIE_GET_LIST_SUCCESS";
  public static final String CATEGORIE_CREATE_SUCCESS = "CATEGORIE_CREATE_SUCCESS";
  public static final String CATEGORIE_UPDATE_SUCCESS = "CATEGORIE_UPDATE_SUCCESS";
  public static final String CATEGORIE_GET_FAILURE_NOT_FOUND = "CATEGORIE_GET_FAILURE_NOT_FOUND";

  // ── QR CODE ─────────────────────────────────────────────────────
  public static final String QRCODE_GENERATE_SUCCESS = "QRCODE_GENERATE_SUCCESS";
  public static final String QRCODE_GENERATE_FAILURE = "QRCODE_GENERATE_FAILURE";

  // ── SYNC ────────────────────────────────────────────────────────
  public static final String SYNC_SUCCESS = "SYNC_SUCCESS";
  public static final String SYNC_FAILURE = "SYNC_FAILURE";

  // ── STORAGE ─────────────────────────────────────────────────────
  public static final String STORAGE_UPLOAD_SUCCESS = "STORAGE_UPLOAD_SUCCESS";
  public static final String STORAGE_UPLOAD_FAILURE = "STORAGE_UPLOAD_FAILURE";
  public static final String STORAGE_DELETE_SUCCESS = "STORAGE_DELETE_SUCCESS";

  // ── CODE LIST ───────────────────────────────────────────────────
  public static final String CODELIST_GET_SUCCESS = "CODELIST_GET_SUCCESS";
  public static final String CODELIST_POST_SUCCESS = "CODELIST_POST_SUCCESS";
  public static final String CODELIST_PUT_SUCCESS = "CODELIST_PUT_SUCCESS";
  public static final String CODELIST_GET_FAILURE_NOT_FOUND = "CODELIST_GET_FAILURE_NOT_FOUND";
  public static final String CODELIST_GET_FAILURE_BAD_REQUEST = "CODELIST_GET_FAILURE_BAD_REQUEST";
  public static final String CODELIST_POST_FAILURE = "CODELIST_POST_FAILURE";
  public static final String CODELIST_POST_DUPLICATE = "CODELIST_POST_DUPLICATE";
  public static final String CODELIST_PUT_FAILURE = "CODELIST_PUT_FAILURE";
  public static final String CODELIST_PUT_FAILURE_BAD_REQUEST = "CODELIST_PUT_FAILURE_BAD_REQUEST";
}
