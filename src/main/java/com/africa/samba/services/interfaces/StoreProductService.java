package com.africa.samba.services.interfaces;

import com.africa.samba.common.exception.CustomException;
import com.africa.samba.dto.request.AddStoreProductRequest;
import com.africa.samba.dto.request.UpdateStoreProductRequest;
import com.africa.samba.dto.response.StoreProductResponse;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Gestion du catalogue local d'une supérette : ajout de produits du catalogue global,
 * prix de vente, prix d'achat, stock et scan en caisse.
 */
public interface StoreProductService {

  /**
   * Ajoute un produit du catalogue global au catalogue d'une supérette avec son prix et son stock.
   *
   * @param storeId UUID de la supérette
   * @param request données du produit à ajouter (productId, prix, stock)
   * @return le produit ajouté au catalogue de la supérette
   * @throws CustomException 404 si la supérette ou le produit est introuvable,
   *                         409 si le produit est déjà dans le catalogue
   */
  StoreProductResponse add(UUID storeId, AddStoreProductRequest request) throws CustomException;

  /**
   * Met à jour le prix de vente, le prix d'achat ou le stock d'un produit dans une supérette.
   *
   * @param storeId        UUID de la supérette
   * @param storeProductId UUID de l'entrée catalogue supérette
   * @param request        nouvelles valeurs (prix, stock, stock_min)
   * @return le produit mis à jour
   * @throws CustomException 404 si le produit n'est pas dans le catalogue de cette supérette
   */
  StoreProductResponse update(UUID storeId, UUID storeProductId, UpdateStoreProductRequest request)
      throws CustomException;

  /**
   * Retire un produit du catalogue d'une supérette (soft delete).
   *
   * @param storeId        UUID de la supérette
   * @param storeProductId UUID de l'entrée catalogue supérette
   * @throws CustomException 404 si le produit n'est pas dans le catalogue de cette supérette
   */
  void remove(UUID storeId, UUID storeProductId) throws CustomException;

  /**
   * Retourne la liste paginée des produits du catalogue d'une supérette.
   *
   * @param storeId  UUID de la supérette
   * @param pageable paramètres de pagination et de tri
   * @return page de produits du catalogue
   */
  Page<StoreProductResponse> listByStore(UUID storeId, Pageable pageable);

  /**
   * Recherche un produit par code-barres dans le catalogue d'une supérette (scan en caisse).
   *
   * <p>Retourne le produit avec le prix défini par la supérette.
   *
   * @param storeId UUID de la supérette
   * @param barcode valeur du code-barres (EAN ou interne)
   * @return le produit avec le prix de la supérette
   * @throws CustomException 404 si le code-barres est introuvable dans cette supérette
   */
  StoreProductResponse scan(UUID storeId, String barcode) throws CustomException;

  /**
   * Retourne les produits en stock bas ({@code stock <= stock_min}) d'une supérette.
   *
   * @param storeId  UUID de la supérette
   * @param pageable paramètres de pagination
   * @return page de produits en stock insuffisant
   */
  Page<StoreProductResponse> lowStock(UUID storeId, Pageable pageable);
}
