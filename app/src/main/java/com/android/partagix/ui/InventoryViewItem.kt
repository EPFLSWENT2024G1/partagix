package com.android.partagix.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.partagix.R


@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryViewItem() {
    Scaffold (
        topBar = {
                 TopAppBar(
                     title = { Text("Item" /*TODO: get item name*/)},
                     modifier = Modifier.fillMaxWidth(),
                     navigationIcon = {IconButton(onClick = {/*TODO: navigate back to inventory screen*/}){
                         Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)}
                     }
                 )
        },
        modifier = Modifier.fillMaxWidth()
    ){
        Column(modifier = Modifier
            .padding(it)
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)){
                /*TODO: get photo and display it*/
                Box(){
                    Image(painter = painterResource(id = R.drawable.ic_launcher_background)/*TODO: get item photo*/,
                        contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(value = "Description" /*TODO: get item description*/,
                    onValueChange = {},
                    label = {Text("Description")},
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    readOnly =  true)
            }
            }

    }
}