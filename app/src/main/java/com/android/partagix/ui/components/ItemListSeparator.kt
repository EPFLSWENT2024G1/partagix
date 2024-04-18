package com.android.partagix.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.user.User

/**
 * ItemListColumn is a composable function that displays a list of items in a column. which is the
 * function that will be called when we want to see a list of items.
 *
 * @param List a list of items.
 * @param Title a string that represents the title of the column.
 * @param corner a string that represents the corner of the column.
 * @param onClick a function that takes an item and returns a Unit.
 * @param onClickCorner a function that returns a Unit.
 * @param modifier a Modifier.
 */
@Composable
fun ItemListColumn(
    List: List<Item>,
    users: List<User>,
    loan: List<Loan>,
    Title: String,
    corner: String,
    isClickable: Boolean,
    onClick: (Item) -> Unit,
    onClickCorner: () -> Unit,
    modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
      Text(
          text = Title,
          style =
              TextStyle(
                  fontSize = 18.sp,
                  fontWeight = FontWeight(1000),
                  color = Color(0xFF000000),
              ),
          modifier = Modifier.fillMaxWidth(0.5f).padding(horizontal = 10.dp))
      Text(
          text = corner,
          textAlign = TextAlign.Right,
          modifier =
              if (isClickable) {
                Modifier.fillMaxWidth().padding(end = 10.dp).clickable { onClickCorner() }
              } else {
                Modifier.fillMaxWidth().padding(end = 10.dp)
              })
    }

    ItemList(
        itemList = List,
        users = users,
        loan = loan,
        onClick = onClick,
        modifier = Modifier.fillMaxSize())
  }
}
