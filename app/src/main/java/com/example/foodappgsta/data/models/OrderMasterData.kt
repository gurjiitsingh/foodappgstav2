package com.it10x.foodappgstav2.data.models

import com.google.firebase.Timestamp

/**
 * OrderMasterData
 *
 * - Legacy fields are kept for backward compatibility
 * - New clean fields are OPTIONAL (nullable)
 * - Android should prefer NEW fields when available
 */
data class OrderMasterData(

    // --------------------------------------------------
    // BASIC IDENTIFIERS
    // --------------------------------------------------
    var id: String = "",
    var customerName: String = "",
    var email: String = "",
    var userId: String = "",
    var addressId: String = "",

    // --------------------------------------------------
    // ORDER META
    // --------------------------------------------------
    var srno: Int = 0,
    var timeId: String = "",
    var time: String = "",
    var paymentType: String = "",

    /**
     * LEGACY STATUS
     * (used earlier for payment state like COMPLETED / PENDING)
     */
    var status: String = "",

    // --------------------------------------------------
    // LEGACY TOTALS (DO NOT REMOVE)
    // --------------------------------------------------
    var itemTotal: Double = 0.0,                 // before discount & tax
    var endTotalG: Double = 0.0,                  // legacy final total
    var finalGrandTotal: Double = 0.0,            // legacy final total

    var deliveryCost: Double = 0.0,
    var totalDiscountG: Double = 0.0,
    var flatDiscount: Double = 0.0,
    var calculatedPickUpDiscountL: Double = 0.0,
    var calCouponDiscount: Double = 0.0,

    var couponDiscountPercentL: Double = 0.0,
    var pickUpDiscountPercentL: Double = 0.0,
    var couponCode: String? = null,

    /**
     * LEGACY TAX (raw / before discount)
     */
    var totalTax: Double = 0.0,

    // --------------------------------------------------
    // ✅ NEW CLEAN TOTAL FIELDS (PREFERRED)
    // --------------------------------------------------

    /**
     * Total of ALL discounts combined
     */
    var discountTotal: Double? = null,

    /**
     * Tax BEFORE discount
     */
    var taxBeforeDiscount: Double? = null,

    /**
     * Tax AFTER discount (correct tax)
     */
    var taxAfterDiscount: Double? = null,

    /**
     * Subtotal AFTER discount, BEFORE tax
     */
    var subTotal: Double? = null,

    /**
     * Delivery fee (clean naming)
     */
    var deliveryFee: Double? = null,

    /**
     * Final payable amount (correct)
     */
    var grandTotal: Double? = null,

    // --------------------------------------------------
    // ✅ ORDER SOURCE & FLOW (NEW SYSTEM)
    // --------------------------------------------------
    var source: String? = null,          // WEB | POS | APP
    var orderStatus: String? = null,     // NEW | ACCEPTED | COMPLETED | CANCELLED
    var paymentStatus: String? = null,   // PAID | UNPAID | FAILED

    // --------------------------------------------------
    // AUTOMATION FLAGS
    // --------------------------------------------------
    var printed: Boolean? = null,        // auto-print handled
    var acknowledged: Boolean? = null,   // sound acknowledged

    // --------------------------------------------------
    // TIMESTAMPS
    // --------------------------------------------------
    var createdAt: Timestamp? = null,    // Firestore server timestamp
    var createdAtUTC: String? = null     // ISO UTC string
)
