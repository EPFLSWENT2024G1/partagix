package com.android.partagix.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.user.User
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

@Composable
fun UserComment(
    author: User,
    comment: String,
    userViewModel: UserViewModel,
    navigationActions: NavigationActions
) {
  OutlinedButton(
      shape = RoundedCornerShape(12.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
      onClick = {
        userViewModel.setUser(author)
        navigationActions.navigateTo(Route.OTHER_ACCOUNT)
      },
      modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column(modifier = Modifier.padding(8.dp)) {
          Text(text = author.name, modifier = Modifier.padding(bottom = 8.dp))
          Text(text = comment, minLines = 3, maxLines = 5, overflow = TextOverflow.Ellipsis)
        }
      }
}
