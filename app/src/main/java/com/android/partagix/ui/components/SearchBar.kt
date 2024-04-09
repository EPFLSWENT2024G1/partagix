package com.android.partagix.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import com.android.partagix.model.InventoryUIState
import com.android.partagix.model.InventoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar (filter : (String) -> Unit, query : String, modifier: Modifier){
    val keyboardController = LocalSoftwareKeyboardController.current
    var active by remember { mutableStateOf(false) }
    SearchBar(
        query = query,
        onQueryChange = { filter (it) },
        onSearch = { filter (it) },
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