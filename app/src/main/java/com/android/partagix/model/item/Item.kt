package com.android.partagix.model.item

import com.android.partagix.model.category.Category

data class Item(
    val id: String,
    val category: Category,
    val name: String,
    val description: String,
    // TODO: Image ?
    // TODO: Location ?
    // TODO: Quantity ?
)
