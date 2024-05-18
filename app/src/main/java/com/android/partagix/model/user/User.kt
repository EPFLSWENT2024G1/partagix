package com.android.partagix.model.user

import com.android.partagix.model.inventory.Inventory
import java.io.File

data class User(
    val id: String,
    val name: String,
    val address: String,
    val rank: String,
    val inventory: Inventory,
    val imageId: File = File.createTempFile("default_image", null)

    // Can be null if the user has disabled notifications
    val fcmToken: String? = null,
)
