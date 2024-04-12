package com.android.partagix.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


/**
 * A top search bar.
 *
 * @param filter a function to filter the items.
 * @param query the query to filter the items.
 * @param modifier Modifier to apply to this layout.
 */
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(filter: (String) -> Unit, query: String, modifier: Modifier) {
  val keyboardController = LocalSoftwareKeyboardController.current
  var active by remember { mutableStateOf(false) }
  SearchBar(
      query = query,
      onQueryChange = { filter(it) },
      onSearch = { filter(it) },
      active = false,
      onActiveChange = { active = it },
      modifier = modifier.fillMaxWidth().padding(20.dp),
      placeholder = { Text("Search an Item") },
      leadingIcon = {
        if (!active) {
          Icon(Icons.Default.Menu, contentDescription = "Search")
        } else {
          Icon(
              Icons.Default.ArrowBack,
              contentDescription = "Search",
              modifier =
                  modifier.clickable {
                    filter("")

                    keyboardController?.hide()
                  })
        }
      },
      trailingIcon = {
        Icon(
            Icons.Default.Search,
            contentDescription = "Search",
            modifier = modifier.clickable { keyboardController?.hide() })
      }) {
        Text("Search an Item")
      }
}


 */
