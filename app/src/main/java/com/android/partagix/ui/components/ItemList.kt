package com.android.partagix.ui.components

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import java.util.ArrayList

@Composable
fun ItemList(itemList: List<Item>, onClick: (Item) -> Unit, modifier: Modifier = Modifier) {
  LazyColumn(modifier = modifier.fillMaxSize()) {
    items(itemList.size) { index ->
      val item = itemList[index]
      Box(modifier = Modifier.fillMaxSize().clickable { onClick(item) }) { ItemUi(item) }
    }
  }
}

@Preview(device = "spec:width=1080px,height=1270px,dpi=440")
@Composable
fun ItemListPreview() {
  val itemList = ArrayList<Item>()

  for (i in 0..3) {
    itemList.add(Item(i.toString(), Category("1", "name"), "name $i", "description"))
  }

  ItemList(itemList = itemList, onClick = { Log.d(ContentValues.TAG, "Item clicked") })
}