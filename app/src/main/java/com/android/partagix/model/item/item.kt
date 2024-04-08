package com.android.partagix.model.item

import android.location.Location
import com.android.partagix.model.category.Category

data class Item(
    val id: String,
    val category: Category,
    val name: String,
    val description: String,
    val author: String,
    val visibility: Int,
    val quantity: Int,
    val location: Location,
)
