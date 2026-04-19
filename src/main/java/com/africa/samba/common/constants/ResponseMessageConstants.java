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

  // ── PRODUCT (catalogue global) ────────────────────────────────
  public static final String PRODUCT_GET_SUCCESS = "PRODUCT_GET_SUCCESS";
  public static final String PRODUCT_GET_LIST_SUCCESS = "PRODUCT_GET_LIST_SUCCESS";
  public static final String PRODUCT_CREATE_SUCCESS = "PRODUCT_CREATE_SUCCESS";
  public static final String PRODUCT_UPDATE_SUCCESS = "PRODUCT_UPDATE_SUCCESS";
  public static final String PRODUCT_DELETE_SUCCESS = "PRODUCT_DELETE_SUCCESS";
  public static final String PRODUCT_GET_FAILURE_NOT_FOUND = "PRODUCT_GET_FAILURE_NOT_FOUND";
  public static final String PRODUCT_CREATE_FAILURE = "PRODUCT_CREATE_FAILURE";
  public static final String PRODUCT_CREATE_FAILURE_BAD_REQUEST =
      "PRODUCT_CREATE_FAILURE_BAD_REQUEST";
  public static final String PRODUCT_UPDATE_FAILURE = "PRODUCT_UPDATE_FAILURE";
  public static final String PRODUCT_UPDATE_FAILURE_NOT_FOUND =
      "PRODUCT_UPDATE_FAILURE_NOT_FOUND";

  // ── BARCODE ─────────────────────────────────────────────────────
  public static final String BARCODE_LOOKUP_SUCCESS = "BARCODE_LOOKUP_SUCCESS";
  public static final String BARCODE_CREATE_SUCCESS = "BARCODE_CREATE_SUCCESS";
  public static final String BARCODE_GENERATE_SUCCESS = "BARCODE_GENERATE_SUCCESS";
  public static final String BARCODE_NOT_FOUND = "BARCODE_NOT_FOUND";
  public static final String BARCODE_ALREADY_EXISTS = "BARCODE_ALREADY_EXISTS";

  // ── STORE (supérette) ───────────────────────────────────────────
  public static final String STORE_GET_SUCCESS = "STORE_GET_SUCCESS";
  public static final String STORE_GET_LIST_SUCCESS = "STORE_GET_LIST_SUCCESS";
  public static final String STORE_CREATE_SUCCESS = "STORE_CREATE_SUCCESS";
  public static final String STORE_UPDATE_SUCCESS = "STORE_UPDATE_SUCCESS";
  public static final String STORE_DELETE_SUCCESS = "STORE_DELETE_SUCCESS";
  public static final String STORE_GET_FAILURE_NOT_FOUND = "STORE_GET_FAILURE_NOT_FOUND";

  // ── STORE PRODUCT (catalogue supérette) ─────────────────────────
  public static final String STORE_PRODUCT_ADD_SUCCESS = "STORE_PRODUCT_ADD_SUCCESS";
  public static final String STORE_PRODUCT_UPDATE_SUCCESS = "STORE_PRODUCT_UPDATE_SUCCESS";
  public static final String STORE_PRODUCT_REMOVE_SUCCESS = "STORE_PRODUCT_REMOVE_SUCCESS";
  public static final String STORE_PRODUCT_GET_LIST_SUCCESS = "STORE_PRODUCT_GET_LIST_SUCCESS";
  public static final String STORE_PRODUCT_SCAN_SUCCESS = "STORE_PRODUCT_SCAN_SUCCESS";
  public static final String STORE_PRODUCT_NOT_FOUND = "STORE_PRODUCT_NOT_FOUND";
  public static final String STORE_PRODUCT_ALREADY_EXISTS = "STORE_PRODUCT_ALREADY_EXISTS";
  public static final String STORE_PRODUCT_STOCK_INSUFFICIENT =
      "STORE_PRODUCT_STOCK_INSUFFICIENT";

  // ── STORAGE ─────────────────────────────────────────────────────
  public static final String STORAGE_PRESIGN_SUCCESS = "STORAGE_PRESIGN_SUCCESS";
  public static final String STORAGE_PRESIGN_FAILURE = "STORAGE_PRESIGN_FAILURE";
  public static final String STORAGE_PRESIGN_FAILURE_BUCKET_NOT_ALLOWED =
      "STORAGE_PRESIGN_FAILURE_BUCKET_NOT_ALLOWED";

  // ── CODE LIST ───────────────────────────────────────────────────
  public static final String CODELIST_GET_SUCCESS = "CODELIST_GET_SUCCESS";
  public static final String CODELIST_GET_LIST_SUCCESS = "CODELIST_GET_LIST_SUCCESS";
  public static final String CODELIST_GET_FAILURE_NOT_FOUND = "CODELIST_GET_FAILURE_NOT_FOUND";
  public static final String CODELIST_GET_FAILURE_BAD_REQUEST = "CODELIST_GET_FAILURE_BAD_REQUEST";
  public static final String CODELIST_POST_FAILURE = "CODELIST_POST_FAILURE";
  public static final String CODELIST_POST_DUPLICATE = "CODELIST_POST_DUPLICATE";
  public static final String CODELIST_PUT_FAILURE = "CODELIST_PUT_FAILURE";
  public static final String CODELIST_PUT_FAILURE_BAD_REQUEST = "CODELIST_PUT_FAILURE_BAD_REQUEST";
}
