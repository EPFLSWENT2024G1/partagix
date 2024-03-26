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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
                     title = { Text("Back to selection" /*TODO: get item name*/)},
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
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally)
        {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    /*TODO: get photo and display it*/

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight()){
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background)/*TODO: get item photo*/,
                            contentDescription = null,
                            alignment = Alignment.BottomCenter
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))

                    Column(){
                        OutlinedTextField(
                            value = "Object name" /*TODO: get item description*/,
                            onValueChange = {},
                            label = { Text("Object name") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )

                        OutlinedTextField(
                            value = "Author" /*TODO: get item description*/,
                            onValueChange = {},
                            label = { Text("Author" /*TODO: get user name*/) },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true
                        )
                    }

                }
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)) {
                OutlinedTextField(
                    value = "Description" /*TODO: get item description*/,
                    onValueChange = {},
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5,
                    readOnly = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                CategoryDropDown()

                Spacer(modifier = Modifier.height(8.dp))

                /*Check pour savoir s'il il existe qqch pr entrer que des nombres*/
                OutlinedTextField(
                    value = "Quantity" /*TODO: get item quantity*/,
                    onValueChange = {},
                    label = { Text("Quantity") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = "Where" /*TODO: get item localisation*/,
                    onValueChange = {},
                    label = { Text("Where") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )

                /*TODO: make it a drop down with visibility*/
                OutlinedTextField(
                    value = "Visibility" /*TODO: get item visibility*/,
                    onValueChange = {},
                    label = { Text("Visibility") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { /*TODO*/ },
                        content = {
                            Text("Download QR code")
                        },
                        modifier = Modifier.fillMaxWidth(0.5f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { /*TODO*/ },
                        content = {
                            Text("Loan requests")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { /*TODO*/ },
                    content = {
                        Text("Edit")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

            }
            }
        }
    }
