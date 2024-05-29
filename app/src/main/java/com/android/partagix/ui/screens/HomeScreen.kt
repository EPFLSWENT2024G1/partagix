package com.android.partagix.ui.screens

import BarcodeAnalyzer
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.partagix.model.HomeViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.ui.components.BottomNavigationBar
import com.android.partagix.ui.components.ItemListColumn
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route

private const val quickAccessText = "Quick access"
private const val findItemButtonName = "Find item to borrow"
private const val quickScanButtonName = "Quick scan"
private const val findItemInventoryName = "Find item in inventory"
private const val incomingRequestsText = "Incoming request"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    manageLoanViewModel: ManageLoanViewModel,
    navigationActions: NavigationActions,
    onQrScanned: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
  val uiState by manageLoanViewModel.uiState.collectAsStateWithLifecycle()
  val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
  var cameraOpen by remember { mutableStateOf(false) }
  Scaffold(
      modifier = modifier.testTag("homeScreen"),
      topBar = {
        TopAppBar(
            modifier = Modifier.testTag("homeScreenTopAppBar"),
            title = { Text(text = "Welcome back, ${homeUiState.user.name}") },
            actions = {
              IconButton(onClick = { /* TODO go to notification screen */}) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications")
              }
            })
      },
      bottomBar = {
        BottomNavigationBar(
            selectedDestination = Route.HOME,
            navigateToTopLevelDestination = navigationActions::navigateTo,
            modifier = Modifier.testTag("homeScreenBottomNavBar"))
      }) { innerPadding ->
        Column(
            modifier =
                Modifier.fillMaxSize().padding(innerPadding).testTag("homeScreenMainContent")) {
              HorizontalDivider(modifier = Modifier.fillMaxWidth())
              Text(
                  text = quickAccessText,
                  modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 16.dp),
                  style = MaterialTheme.typography.titleLarge)
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                  modifier =
                      Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween) {
                    BigButton(
                        logo = Icons.Default.PersonSearch,
                        text = findItemButtonName,
                        onClick = { navigationActions.navigateTo(Route.LOAN) },
                        modifier = Modifier.weight(1f).testTag("homeScreenFirstBigButton"))
                    Spacer(modifier = Modifier.width(8.dp))
                    CameraToggleButton(
                        cameraOpen = cameraOpen,
                        onCameraToggle = { cameraOpen = it },
                        modifier = Modifier.weight(1f).testTag("homeScreenSecondBigButton"))
                    Spacer(modifier = Modifier.width(8.dp))
                    BigButton(
                        logo = Icons.Default.ImageSearch,
                        text = findItemInventoryName,
                        onClick = { navigationActions.navigateTo(Route.INVENTORY) },
                        modifier = Modifier.weight(1f).testTag("homeScreenThirdBigButton"))
                  }
              if (cameraOpen) {
                Spacer(modifier = Modifier.height(50.dp))
                CameraScreen(onQrScanned)
              }

              Box(modifier = modifier.padding(top = 8.dp)) {
                Text(
                    text = incomingRequestsText,
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                    style = MaterialTheme.typography.titleLarge)
                ItemListColumn(
                    list = uiState.items,
                    users = uiState.users,
                    loan = uiState.loans,
                    title = "",
                    corner = "See All",
                    isCornerClickable = true,
                    onClickCorner = { navigationActions.navigateTo(Route.MANAGE_LOAN_REQUEST) },
                    onUserClick = { /* todo */},
                    isExpandable = true,
                    isOutgoing = false,
                    wasExpanded = uiState.expanded,
                    manageLoanViewModel = manageLoanViewModel,
                    navigationActions = navigationActions,
                    isClickable = true,
                    modifier =
                        Modifier.padding(start = 10.dp, end = 10.dp, top = 12.dp)
                            .testTag("homeScreenItemList"))
              }
            }
      }
}

@Composable
fun BigButton(logo: ImageVector, text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
  Box(
      modifier =
          modifier
              .aspectRatio(1f)
              .size(70.dp)
              .background(MaterialTheme.colorScheme.onPrimary)
              .border(
                  width = 1.dp,
                  color = MaterialTheme.colorScheme.outlineVariant,
                  shape = RoundedCornerShape(8.dp)) // Add a rounded border to the button
              .clickable(onClick = onClick),
      contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Spacer(modifier = Modifier.fillMaxHeight(0.15f))
          Icon(imageVector = logo, contentDescription = null, modifier = Modifier.size(32.dp))
          Spacer(modifier = Modifier.fillMaxHeight(0.1f))
          Text(
              text = text,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              textAlign = TextAlign.Center)
          // Spacers added to insure all icons are at the same height
          Spacer(modifier = Modifier.fillMaxHeight())
        }
      }
}

@Composable
fun CameraToggleButton(
    cameraOpen: Boolean,
    onCameraToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val activity = context as? Activity

  // State to remember if the permission has been granted
  var hasCameraPermission by remember { mutableStateOf(false) }

  // Create a permission launcher
  val permissionLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
          onCameraToggle(!cameraOpen)
        } else {
          Toast.makeText(
                  context, "Camera permission is required to use the camera.", Toast.LENGTH_SHORT)
              .show()
        }
      }

  BigButton(
      logo = Icons.Default.QrCodeScanner,
      text = quickScanButtonName,
      onClick = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED) {
          hasCameraPermission = true
          onCameraToggle(!cameraOpen)
        } else {
          // Request the permission
          permissionLauncher.launch(Manifest.permission.CAMERA)
        }
      },
      modifier = modifier)
}

@Composable
fun CameraScreen(onQrScanned: (String, String) -> Unit) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
  val cameraProvider = cameraProviderFuture.get()

  DisposableEffect(Unit) {
    onDispose {
      // Unbind all use cases to avoid attempting to bind too many use cases
      cameraProvider.unbindAll()
    }
  }

  AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { ctx ->
        val previewView = PreviewView(ctx)
        val preview = Preview.Builder().build()
        val selector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder().build()
        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(ctx), BarcodeAnalyzer(ctx, onQrScanned))

        try {
          cameraProvider.unbindAll()
          cameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, imageAnalysis)
        } catch (exc: Exception) {
          Log.e("CAMERA", "Camera bind error ${exc.localizedMessage}", exc)
        }

        previewView
      })
}
