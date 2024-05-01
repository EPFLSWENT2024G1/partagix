package com.android.partagix.model.filtering

import android.location.Location
import com.android.partagix.model.item.Item

/** Filtering class to filter items based on different criteria. */
open class Filtering {
  /**
   * Filter items based on the query.
   *
   * @param list the list of items to filter
   * @param query the query to filter the items
   * @return the list of items that match the query
   */
  fun filterItems(list: List<Item>, query: String): List<Item> {
    return list.filter {
      it.name.contains(query, ignoreCase = true) ||
          it.description.contains(query, ignoreCase = true) ||
          it.category.toString().contains(query, ignoreCase = true) ||
          it.visibility.toString().contains(query, ignoreCase = true) ||
          it.quantity.toString().contains(query, ignoreCase = true)
    }
  }

  /**
   * Filter items based on their quantity, at least a certain quantity.
   *
   * @param list the list of items to filter
   * @param atLeastQuantity the minimum quantity of items
   * @return the list of items that match the quantity
   */
  fun filterItems(list: List<Item>, atLeastQuantity: Int): List<Item> {
    return list.filter { it.quantity >= atLeastQuantity }
  }

  /**
   * Filter items based on the current position and the radius.
   *
   * @param currentPosition the current position of the user
   * @param radius the radius to filter the items (in kms)
   * @return the list of items within the radius of the current position.
   */
  fun filterItems(list: List<Item>, currentPosition: Location, radius: Double): List<Item> {
    return list.filter { it.location.distanceTo(currentPosition) <= (radius * 1000) }
  }
}
