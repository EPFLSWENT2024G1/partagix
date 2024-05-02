package com.android.partagix.model

import android.location.Location
import com.android.partagix.model.category.Category
import com.android.partagix.model.filtering.Filtering
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test

class FilteringTest {
  private lateinit var filtering: Filtering
  private lateinit var items: List<Item>

  private lateinit var currentPosition: Location

  @Before
  fun setUp() {
    filtering = Filtering()

    items =
        listOf(
            Item(
                "0",
                Category("0", "Category 0"),
                "Item0",
                "description-0",
                Visibility.PUBLIC,
                1,
                Location("").apply {
                  // Renens Aqua Park
                  latitude = 46.5297788
                  longitude = 6.5831585
                },
                "user0",
            ),
            Item(
                "1",
                Category("1", "Category 1"),
                "Item1",
                "description-1",
                Visibility.PUBLIC,
                5,
                Location("").apply {
                  // Renens
                  latitude = 46.534633
                  longitude = 6.588432
                },
                "user1",
            ),
        )

    currentPosition =
        Location("").apply {
          latitude = 46.5294513
          longitude = 6.5864534
        }
  }

  @Test
  fun testFilterItemsByQuery() {
    // name
    val result = filtering.filterItems(items, "Item1")
    assertEquals(1, result.size)
    assertEquals("Item1", result[0].name)
    assertEquals("1", result[0].id)

    // description
    val result2 = filtering.filterItems(items, "description-1")
    assertEquals(1, result2.size)
    assertEquals("Item1", result2[0].name)

    // category
    val result3 = filtering.filterItems(items, "Category 0")
    assertEquals(1, result3.size)
    assertEquals("Item0", result3[0].name)

    // visibility
    val result4 = filtering.filterItems(items, "PUBLIC")
    assertEquals(2, result4.size)
    assertEquals("Item0", result4[0].name)
    assertEquals("Item1", result4[1].name)

    // quantity
    val result5 = filtering.filterItems(items, "5")
    assertEquals(1, result5.size)
    assertEquals("Item1", result5[0].name)

    // no match
    val result6 = filtering.filterItems(items, "Item3")
    assertEquals(0, result6.size)
  }

  @Test
  fun testFilterItemsByQuantity() {
    val result = filtering.filterItems(items, 2)
    assertEquals(1, result.size)
    assertEquals("Item1", result[0].name)
  }

  @Test
  fun testFilterItemsByLocation() {
    var result = filtering.filterItems(items, currentPosition, 0.1)
    assertEquals(0, result.size)

    // true distance = 255m
    result = filtering.filterItems(items, currentPosition, 0.26)
    assertEquals(1, result.size)
    assertEquals("Item0", result[0].name)

    // true distance = 595m
    result = filtering.filterItems(items, currentPosition, 0.6)
    assertEquals(2, result.size)
    assertEquals("Item0", result[0].name)
    assertEquals("Item1", result[1].name)
  }
}
