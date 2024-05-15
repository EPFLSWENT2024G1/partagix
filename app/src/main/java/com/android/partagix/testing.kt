import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.emptyConst.emptyLoan
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.ui.components.ItemUi

@Composable
@Preview(showBackground = true)
fun Testing(modifier: Modifier = Modifier) {
  Dialog(
      onDismissRequest = { /*TODO: other things?*/},
      properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier.fillMaxWidth().testTag("popup")) {
              Column(
                  modifier =
                      modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = modifier.fillMaxWidth()) {
                          Text(
                              text = "End loan :",
                              fontSize = 20.sp,
                              modifier =
                                  modifier
                                      .padding(
                                          start = 10.dp, end = 26.dp, top = 16.dp, bottom = 16.dp)
                                      .testTag("title"))
                          IconButton(onClick = {}, modifier = modifier.testTag("closeButton")) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "")
                          }
                        }
                    Column(modifier = modifier.padding(8.dp, 0.dp, 8.dp, 8.dp).fillMaxWidth()) {
                      Box(
                          modifier =
                              modifier
                                  .fillMaxWidth()
                                  .padding(bottom = 35.dp)
                                  .testTag("item")
                                  .clickable(onClick = {})) {
                            ItemUi(item = emptyItem, user = emptyUser, loan = emptyLoan)
                          }

                      Button(
                          modifier = modifier.fillMaxWidth().testTag("endLoanButton"),
                          colors =
                              ButtonColors(
                                  containerColor = MaterialTheme.colorScheme.error,
                                  contentColor = MaterialTheme.colorScheme.onError,
                                  disabledContainerColor = MaterialTheme.colorScheme.error,
                                  disabledContentColor = MaterialTheme.colorScheme.onError),
                          onClick = {}) {
                            Text(text = "End Loan")
                          }
                      Spacer(modifier = modifier.height(6.dp))
                    }
                  }
            }
      }
}
