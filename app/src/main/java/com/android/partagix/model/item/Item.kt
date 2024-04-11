package com.android.partagix.model.item

import android.location.Location
import com.android.partagix.model.category.Category
import com.android.partagix.model.visibility.Visibility

data class Item(
    val id: String,
    val category: Category,
    val name: String,
    val description: String,
    val visibility: Visibility,
    val quantity: Long,
    val location: Location,
    val idUser: String = "",
)
