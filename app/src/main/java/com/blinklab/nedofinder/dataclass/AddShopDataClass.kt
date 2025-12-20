package com.blinklab.nedofinder.dataclass


data class AddShopDataClass(
    val shopId: String? = null,
    val ownerId: String? = null,

    val shopName: String? = null,
    val category: String? = null,
    val shopImage: String? = null,

    val address: String? = null,
    val city: String? = null,
    val phone: String? = null,

    val ownerName: String? = null,
    val shopDescription: String? = null,
    val ownerImage: String? = null,

    val status: String? = "pending",
    val createdAt: Long? = System.currentTimeMillis(),

    //admin use only not required in db
    var _uidKey: String? = null,
    var _shopKey: String? = null,

)
