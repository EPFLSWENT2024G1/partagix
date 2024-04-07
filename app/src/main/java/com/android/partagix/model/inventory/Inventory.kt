package com.android.partagix.model.inventory

import com.android.partagix.model.item.Item

data class Inventory(
    val idUser: String,
    val items: List<Item>,
)
