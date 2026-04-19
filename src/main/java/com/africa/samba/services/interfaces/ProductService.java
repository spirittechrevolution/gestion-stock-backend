package com.africa.samba.services.interfaces;

import com.africa.samba.common.exception.CustomException;
import com.africa.samba.dto.request.CreateBarcodeRequest;
import com.africa.samba.dto.request.CreateProductRequest;
import com.africa.samba.dto.request.QuickCreateProductRequest;
import com.africa.samba.dto.request.UpdateProductRequest;
import com.africa.samba.dto.response.ProductResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Gestion du catalogue global de produits partagé entre toutes les supérettes. */
public interface ProductService {

  /**
   * Crée un produit dans le catalogue global (statut {@code APPROVED}).
   *
   * @param request données du produit à créer
   * @return le produit créé
   * @throws CustomException 409 si un produit avec le même code-barres existe déjà
   */
  ProductResponse create(CreateProductRequest request) throws CustomException;

  /**
   * Crée rapidement un produit en statut {@code PENDING} pour une supérette donnée.
   *
   * <p>Utilisé par un employé en caisse lorsqu'un produit est introuvable dans le catalogue.
   * Le produit n'est visible que dans la supérette créatrice jusqu'à validation.
   *
   * @param storeId identifiant de la supérette
   * @param request données minimales du produit (nom, catégorie, prix, stock)
   * @return le produit créé en statut {@code PENDING}
   * @throws CustomException 404 si la supérette est introuvable
   */
  ProductResponse quickCreate(UUID storeId, QuickCreateProductRequest request)
      throws CustomException;

  /**
   * Approuve un produit en statut {@code PENDING} et le rend visible dans le catalogue global.
   *
   * @param productId identifiant du produit à approuver
   * @return le produit mis à jour avec le statut {@code APPROVED}
   * @throws CustomException 404 si le produit est introuvable
   */
  ProductResponse approve(UUID productId) throws CustomException;

  /**
   * Retourne un produit par son identifiant.
   *
   * @param id UUID du produit
   * @return le produit trouvé
   * @throws CustomException 404 si le produit est introuvable
   */
  ProductResponse getById(UUID id) throws CustomException;

  /**
   * Retourne la liste paginée des produits du catalogue global (statut {@code APPROVED}).
   *
   * @param pageable paramètres de pagination et de tri
   * @return page de produits
   */
  Page<ProductResponse> list(Pageable pageable);

  /**
   * Retourne les produits en attente de validation ({@code PENDING}) pour une supérette.
   *
   * @param storeId identifiant de la supérette
   * @param pageable paramètres de pagination
   * @return page de produits en statut {@code PENDING}
   */
  Page<ProductResponse> listPending(UUID storeId, Pageable pageable);

  /**
   * Recherche des produits par mot-clé (nom, marque ou catégorie).
   *
   * @param keyword terme de recherche
   * @param pageable paramètres de pagination
   * @return page de produits correspondant au mot-clé
   */
  Page<ProductResponse> search(String keyword, Pageable pageable);

  /**
   * Retourne les produits d'une catégorie donnée.
   *
   * @param category nom de la catégorie
   * @param pageable paramètres de pagination
   * @return page de produits de la catégorie
   */
  Page<ProductResponse> listByCategory(String category, Pageable pageable);

  /**
   * Met à jour les informations d'un produit du catalogue global.
   *
   * @param id UUID du produit
   * @param request nouvelles valeurs
   * @return le produit mis à jour
   * @throws CustomException 404 si le produit est introuvable
   */
  ProductResponse update(UUID id, UpdateProductRequest request) throws CustomException;

  /**
   * Désactive un produit du catalogue (soft delete).
   *
   * @param id UUID du produit
   * @throws CustomException 404 si le produit est introuvable
   */
  void delete(UUID id) throws CustomException;

  /**
   * Associe un code-barres EAN officiel à un produit existant.
   *
   * @param productId identifiant du produit
   * @param request code-barres à associer
   * @return le produit mis à jour avec le nouveau code-barres
   * @throws CustomException 404 si le produit est introuvable, 409 si le code-barres est déjà utilisé
   */
  ProductResponse addBarcode(UUID productId, CreateBarcodeRequest request) throws CustomException;

  /**
   * Génère et associe un code-barres interne au format {@code 2XXXXXXXXXXXX} (préfixe 2, 13 chiffres).
   *
   * @param productId identifiant du produit
   * @return le produit mis à jour avec le code-barres généré
   * @throws CustomException 404 si le produit est introuvable
   */
  ProductResponse generateInternalBarcode(UUID productId) throws CustomException;

  /**
   * Recherche un produit par code-barres (EAN ou interne).
   *
   * @param code valeur du code-barres
   * @return le code-barres et le produit associé
   * @throws CustomException 404 si le code-barres est introuvable
   */
  ProductResponse.BarcodeResponse lookupBarcode(String code) throws CustomException;
}
