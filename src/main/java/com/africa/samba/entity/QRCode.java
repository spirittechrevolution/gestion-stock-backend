package com.africa.samba.entity;

import com.africa.samba.codeLists.TypeQRCode;
import com.africa.samba.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * QRCode — trace de chaque QR code généré par le backend ZXing.
 *
 * <p>Deux usages : - PRODUIT : QR collé sur l'étiquette du produit, scanné à la caisse - BOUTIQUE :
 * QR d'onboarding envoyé au client pour configurer l'app mobile
 *
 * <p>Le contenu (payload) est la chaîne encodée dans le QR. L'image PNG est stockée sur minio —
 * urlImage pointe vers elle.
 */
@Entity
@Table(name = "qr_codes", schema = "administrative")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QRCode extends BaseEntity {

  // ── Type et contenu ───────────────────────────────────────────────────

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TypeQRCode type;

  /**
   * Payload encodé dans le QR. Pour PRODUIT : "SAMBA-B{boutiqueId}-P{produitId}-{prix}-{checksum}"
   * Pour BOUTIQUE : "SAMBA-ONBOARD-{tokenActivation}"
   */
  @Column(nullable = false, length = 200)
  private String payload;

  /** URL de l'image PNG stockée sur S3 ou Cloudinary */
  @Column(name = "url_image")
  private String urlImage;

  /** Base64 de l'image PNG — retourné directement par l'API si pas de S3 */
  @Column(name = "base64_image", columnDefinition = "TEXT")
  private String base64Image;

  // ── Dimensions PNG (générées par ZXing) ───────────────────────────────

  @Column(name = "largeur_px")
  @Builder.Default
  private Integer largeurPx = 300;

  @Column(name = "hauteur_px")
  @Builder.Default
  private Integer hauteurPx = 300;

  // ── Relations (nullable car QR boutique n'a pas de produit) ──────────

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "produit_id")
  private Produit produit;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "boutique_id", nullable = false)
  private Boutique boutique;

  // ── Méthodes métier ───────────────────────────────────────────────────

  public boolean hasUrlImage() {
    return urlImage != null && !urlImage.isBlank();
  }

  public String getImageSource() {
    return hasUrlImage() ? urlImage : "data:image/png;base64," + base64Image;
  }
}
