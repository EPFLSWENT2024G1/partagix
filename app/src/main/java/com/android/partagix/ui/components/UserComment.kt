package com.android.partagix.ui.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.partagix.model.user.User

private const val TAG = "UserComment"

@Composable
fun UserComment(
    author: User,
    comment: String,
    onAuthorClick: (User) -> Unit = {},
) {
  Surface(
      shape = RoundedCornerShape(12.dp),
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface),
      modifier = Modifier.padding(8.dp).fillMaxWidth().testTag("userComment_${author.id}")) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.Start) {
          Text(
              text = author.name,
              style = MaterialTheme.typography.bodyLarge,
              modifier =
                  Modifier.padding(bottom = 8.dp)
                      .clickable {
                        Log.d(TAG, "UserComment: $author")
                        onAuthorClick(author)
                      }
                      .testTag("userCommentAuthor_${author.id}"))
          Text(
              text = comment,
              style = MaterialTheme.typography.bodyMedium,
              minLines = 3,
              maxLines = 5,
              overflow = TextOverflow.Ellipsis)
        }
      }
}
