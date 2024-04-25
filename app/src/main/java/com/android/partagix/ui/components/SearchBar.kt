package com.android.partagix.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

private const val TAG = "TopSearchBar"

/**
 * TopSearchBar is a composable that displays a search bar at the top of the screen.
 *
 * @param filter a lambda function to filter the items.
 * @param query a string to filter the items.
 * @param modifier a Modifier to apply to this layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(filter: (String) -> Unit, modifier: Modifier = Modifier, query: String? = null) {
  val keyboardController = LocalSoftwareKeyboardController.current
  var active by remember { mutableStateOf(false) }
  var optionalQuery by remember { mutableStateOf("") }

  if (query != null) {
    optionalQuery = query
  }

  Log.d(TAG, "TopSearchBar: optionalQuery: $optionalQuery, active: $active, query: $query")

  SearchBar(
      query = optionalQuery,
      onQueryChange = {
        filter(it)
        optionalQuery = it
      },
      onSearch = {
        filter(it)
        optionalQuery = it
        active = false
      },
      active = false,
      onActiveChange = { active = it },
      modifier = modifier.fillMaxWidth().padding(20.dp),
      placeholder = { Text("Search an Item") },
      leadingIcon = {
        if (!active) {
          Icon(Icons.Default.Menu, contentDescription = "Search")
        } else {
          Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back",
              modifier =
                  Modifier.clickable {
                        active = !active
                        optionalQuery = ""
                        filter(optionalQuery)
                        keyboardController?.hide()
                        Log.d(TAG, "clickable: active: $active, optionalQuery: $optionalQuery")
                      }
                      .testTag("SearchBarBack"))
        }
      },
      trailingIcon = {
        Icon(
            Icons.Default.Search,
            contentDescription = "Search",
            modifier =
                Modifier.clickable {
                      active = true
                      keyboardController?.hide()
                    }
                    .testTag("SearchBarSearch"))
      }) {
        Text("Search an Item", modifier = Modifier.testTag("bar").clickable { active = true })
      }
}
