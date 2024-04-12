package com.android.partagix.ui.components

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import java.util.ArrayList

/**
 * Composable function to display a list of items, in a column.
 *
 * @param itemList a list of items to display.
 * @param onClick a function to handle the click event on an item.
 * @param modifier Modifier to apply to this layout.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemList(
    itemList: List<Item>,
    onClick: (Item) -> Unit,
    modifier: Modifier = Modifier,
    stickyHeader: @Composable() (() -> Unit)? = null,
) {
  LazyColumn(modifier = modifier.fillMaxSize()) {
    if (stickyHeader != null) {
      stickyHeader { stickyHeader() }
    }
    items(itemList.size) { index ->
      val item = itemList[index]
      Box(modifier = Modifier.fillMaxSize().clickable { onClick(item) }.testTag("ItemListItem")) {
        ItemUi(item)
      }
    }
  }
}

@Preview(device = "spec:width=1080px,height=1270px,dpi=440")
@Composable
fun ItemListPreview() {
  val itemList = ArrayList<Item>()

  for (i in 0..3) {
    itemList.add(
        Item(
            i.toString(),
            Category("1", "name"),
            "name $i",
            "description",
            Visibility.PUBLIC,
            1,
            android.location.Location("location")))
  }

  ItemList(itemList = itemList, onClick = { Log.d(ContentValues.TAG, "Item clicked") })
}
