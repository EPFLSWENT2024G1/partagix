package com.android.partagix.model.user

import com.android.partagix.model.inventory.Inventory

data class User(
    val id: String,
    val name: String,
    val address: String,
    val rank: String,
    val inventory: Inventory,

    // Can be null if the user has disabled notifications
    val fcmToken: String? = null,
)
