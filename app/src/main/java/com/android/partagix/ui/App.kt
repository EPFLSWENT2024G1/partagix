package com.android.partagix.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.partagix.MainActivity
import com.android.partagix.model.BorrowViewModel
import com.android.partagix.model.Database
import com.android.partagix.model.EvaluationViewModel
import com.android.partagix.model.FinishedLoansViewModel
import com.android.partagix.model.HomeViewModel
import com.android.partagix.model.InventoryViewModel
import com.android.partagix.model.ItemViewModel
import com.android.partagix.model.LoanViewModel
import com.android.partagix.model.ManageLoanViewModel
import com.android.partagix.model.StampViewModel
import com.android.partagix.model.StartOrEndLoanUIState
import com.android.partagix.model.StartOrEndLoanViewModel
import com.android.partagix.model.StorageV2
import com.android.partagix.model.UserViewModel
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.auth.SignInResultListener
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.notification.FirebaseMessagingService
import com.android.partagix.model.user.User
import com.android.partagix.ui.components.locationPicker.LocationPickerViewModel
import com.android.partagix.ui.navigation.NavigationActions
import com.android.partagix.ui.navigation.Route
import com.android.partagix.ui.screens.BootScreen
import com.android.partagix.ui.screens.BorrowScreen
import com.android.partagix.ui.screens.EditAccount
import com.android.partagix.ui.screens.EndLoanScreen
import com.android.partagix.ui.screens.HomeScreen
import com.android.partagix.ui.screens.InventoryCreateOrEditItem
import com.android.partagix.ui.screens.InventoryScreen
import com.android.partagix.ui.screens.InventoryViewItemScreen
import com.android.partagix.ui.screens.LoanScreen
import com.android.partagix.ui.screens.LoginScreen
import com.android.partagix.ui.screens.ManageLoanRequest
import com.android.partagix.ui.screens.ManageOutgoingLoan
import com.android.partagix.ui.screens.OldLoansScreen
import com.android.partagix.ui.screens.QrScanScreen
import com.android.partagix.ui.screens.StampScreen
import com.android.partagix.ui.screens.StartLoanScreen
import com.android.partagix.ui.screens.ViewAccount
import com.android.partagix.ui.screens.ViewOtherAccount
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import java.io.File
import kotlinx.coroutines.launch

class App(
    private val activity: MainActivity,
    private val auth: Authentication? = null,
    private val imageStorage: StorageV2 = StorageV2(),
    private val db: Database = Database(Firebase.firestore, imageStorage),
    private val notificationManager: FirebaseMessagingService = FirebaseMessagingService(db = db),
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(activity)
) : SignInResultListener {

  private var authentication: Authentication = Authentication(activity, this)

  private var navigationActionsInitialized = false
  lateinit var navigationActions: NavigationActions

  private val inventoryViewModel = InventoryViewModel(db = db)
  private val manageViewModelLoan =
      ManageLoanViewModel(db = db, notificationManager = notificationManager)
  private val manageViewModelIncoming =
      ManageLoanViewModel(db = db, notificationManager = notificationManager)
  private val manageViewModelOutgoing =
      ManageLoanViewModel(db = db, notificationManager = notificationManager)
  private val loanViewModel = LoanViewModel(db = db)
  private val borrowViewModel = BorrowViewModel(db = db, notificationManager = notificationManager)
  private val itemViewModel =
      ItemViewModel(
          db = db,
          imageStorage = imageStorage,
          onItemSaved = { item -> inventoryViewModel.updateItem(item) },
          onItemCreated = { item -> inventoryViewModel.createItem(item) },
      )
  private val userViewModel = UserViewModel(db = db, imageStorage = imageStorage)
  private val otherUserViewModel = UserViewModel(db = db, imageStorage = imageStorage)
  private val evaluationViewModel =
      EvaluationViewModel(db = db, notificationManager = notificationManager)
  private val finishedLoansViewModel = FinishedLoansViewModel(db = db)
  private val startOrEndLoanViewModel =
      StartOrEndLoanViewModel(db = db, notificationManager = notificationManager)
  private val homeViewModel = HomeViewModel(db = db, context = activity)
  private val locationPickerViewModel = LocationPickerViewModel()

  init {
    notificationManager.initPermissions()
  }

  @Composable
  fun Create(
      idItem: String? = null,
      mock: Boolean = false,
      mockNavigationActions: NavigationActions? = null
  ) {
    ComposeNavigationSetup()
    if (mock) {
      navigationActions = mockNavigationActions!!
    }

    val user = Authentication.getUser()

    if (user != null) {
      notificationManager.checkToken(user.uid) {}
      if (idItem != null) {
        onQrScanned(idItem, user.uid)
      } else {
        navigationActions.navigateTo(Route.BOOT)
      }
    } else {
      navigationActions.navigateTo(Route.BOOT)
    }
  }

  private fun onQrScanned(idItem: String, idUser: String) {

    db.getItem(idItem) { itemViewModel.updateUiItem(it) }
    db.getLoans { loans ->
      val loan =
          loans.find {
            it.idItem == idItem && it.state == LoanState.ACCEPTED && it.idBorrower == idUser
          }

      val loan2 =
          loans.find {
            it.idItem == idItem && it.state == LoanState.ONGOING && it.idLender == idUser
          }

      if (loan != null) {
        db.getItem(idItem) { item ->
          db.getUser(loan.idBorrower) { borrower ->
            db.getUser(loan.idLender) { lender ->
              startOrEndLoanViewModel.update(StartOrEndLoanUIState(loan, item, borrower, lender))
              navigationActions.navigateTo(Route.STARTLOAN)
            }
          }
        }
      } else if (loan2 != null) {
        db.getItem(idItem) { item ->
          db.getUser(loan2.idBorrower) { borrower ->
            db.getUser(loan2.idLender) { lender ->
              startOrEndLoanViewModel.update(StartOrEndLoanUIState(loan2, item, borrower, lender))
              navigationActions.navigateTo(Route.ENDLOAN)
            }
          }
        }
      } else {
        db.getItem(idItem) { item ->
          itemViewModel.updateUiItem(item)
          navigationActions.navigateTo(Route.VIEW_ITEM)
        }
      }
    }
  }

  fun navigateForTest(route: String) {
    navigationActions.navigateTo(route)
  }

  fun setNavigationActionsInitialized(value: Boolean) {
    navigationActionsInitialized = value
  }

  override fun onSignInSuccess(user: FirebaseUser?) {
    if (user != null) {
      notificationManager.checkToken(user.uid) { newToken ->
        val newUser =
            User(
                user.uid,
                user.displayName ?: "",
                "Unknown Location",
                "0",
                Inventory(user.uid, emptyList()),
                File("res/drawable/default_image.jpg"),
                newToken,
                user.email ?: "")
        db.getUser(
            user.uid,
            onNoUser = {
              // If the user is not found, create it
              db.createUser(newUser)
            }) {}

        // test that navigationActions has been initialized
        if (navigationActionsInitialized) {
          navigationActions.navigateTo(Route.HOME)
        }
      }
    }

    Log.d(TAG, "onSignInSuccess: user=$user")
  }

  override fun onSignInFailure(errorCode: Int) {
    // Go back to safe state and report error
    navigationActions.navigateTo(Route.BOOT)
    Log.e(TAG, "onSignInFailure: errorCode=$errorCode")
  }

  @Composable
  private fun ComposeNavigationSetup() {
    Log.d(TAG, "onComposeNavigationSetup: called")
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navController = rememberNavController()
    navigationActions = remember(navController) { NavigationActions(navController) }

    globalNavigationActions = navigationActions

    setNavigationActionsInitialized(true)
    val selectedDestination = Route.BOOT // This is not even used
    ComposeMainContent(
        navController = navController,
        selectedDestination = selectedDestination,
    ) {
      scope.launch { drawerState.open() }
    }
  }

  @Composable
  fun ComposeMainContent(
      modifier: Modifier = Modifier,
      navController: NavHostController,
      selectedDestination: String,
      onDrawerClicked: () -> Unit = {},
  ) {
    Row(modifier = modifier.fillMaxSize()) {
      Column(
          modifier =
              Modifier.fillMaxSize().background(MaterialTheme.colorScheme.inverseOnSurface)) {
            ComposeNavigationHost(
                navController = navController,
                modifier = Modifier.weight(1f),
            )
          }
    }
  }

  private fun checkLocationPermissions(retries: Int = 3): Boolean {
    if (retries == 0) {
      return false
    }

    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) !=
        PackageManager.PERMISSION_GRANTED) {
      Log.d(TAG, "checkLocationPermissions: requesting permissions")
      ActivityCompat.requestPermissions(
          activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

      return checkLocationPermissions(retries - 1)
    } else {
      Log.d(TAG, "checkLocationPermissions: permissions granted")
      return true
    }
  }

  @SuppressLint("MissingPermission")
  @Composable
  private fun ComposeNavigationHost(
      navController: NavHostController,
      modifier: Modifier = Modifier
  ) {
    val onQrScan = this::onQrScanned
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Route.HOME,
    ) {
      composable(Route.BOOT) { BootScreen(authentication, navigationActions, modifier) }
      composable(Route.LOGIN) { LoginScreen(authentication, modifier) }
      composable(Route.HOME) {
        manageViewModelIncoming.getLoanRequests(isOutgoing = false)
        homeViewModel.updateUser()

        HomeScreen(
            homeViewModel = homeViewModel,
            manageLoanViewModel = manageViewModelIncoming,
            navigationActions = navigationActions,
            onQrScanned = onQrScan)
      }
      composable(Route.LOAN) {
        if (checkLocationPermissions()) {
          fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
              Log.d(TAG, "onCreate: location=$location")
              userViewModel.updateLocation(location)
            }
          }
          loanViewModel.getAvailableLoans()
          LoanScreen(
              navigationActions = navigationActions,
              loanViewModel = loanViewModel,
              userViewModel = userViewModel,
              itemViewModel = itemViewModel,
              manageLoanViewModel = manageViewModelLoan,
              modifier = modifier)
        } else {
          navigationActions.navigateTo(Route.HOME)
        }
      }
      composable(Route.BORROW) {
        BorrowScreen(
            viewModel = borrowViewModel,
            navigationActions = navigationActions,
            itemViewModel = itemViewModel)
      }
      composable(Route.INVENTORY) {
        inventoryViewModel.getInventory()
        manageViewModelIncoming.getLoanRequests(isOutgoing = false)
        manageViewModelOutgoing.getLoanRequests(isOutgoing = true)
        InventoryScreen(
            inventoryViewModel = inventoryViewModel,
            navigationActions = navigationActions,
            manageLoanViewModelOutgoing = manageViewModelOutgoing,
            manageLoanViewModelIncoming = manageViewModelIncoming,
            itemViewModel = itemViewModel)
      }

      composable(Route.QR_SCAN) { QrScanScreen(navigationActions) }

      composable(
          Route.ACCOUNT,
      ) {
        userViewModel.setUserToCurrent()
        ViewAccount(
            navigationActions = navigationActions,
            userViewModel = userViewModel,
            otherUserViewModel = otherUserViewModel)
      }

      composable(
          Route.OTHER_ACCOUNT + "/{userId}",
          arguments = listOf(navArgument("userId") { type = NavType.StringType })) {
            val userId = it.arguments?.getString("userId")
            if (userId != null) {
              otherUserViewModel.setLoading(true)
              db.getUser(userId) { user ->
                otherUserViewModel.setLoading(false)
                otherUserViewModel.updateUser(user)
              }
            }
            ViewOtherAccount(
                navigationActions = navigationActions,
                userViewModel = userViewModel,
                otherUserViewModel = otherUserViewModel)
          }

      composable(
          Route.EDIT_ACCOUNT,
      ) {
        EditAccount(
            navigationActions = navigationActions,
            userViewModel = userViewModel,
            locationViewModel = locationPickerViewModel)
      }

      composable(Route.VIEW_ITEM) {
        itemViewModel.getUser()
        itemViewModel.getAvailabilityDates()
        InventoryViewItemScreen(
            navigationActions, itemViewModel, borrowViewModel, otherUserViewModel)
      }
      composable(Route.VIEW_OTHERS_ITEM) {
        itemViewModel.getUser()
        itemViewModel.getAvailabilityDates()
        InventoryViewItemScreen(
            navigationActions, itemViewModel, borrowViewModel, otherUserViewModel, true)
      }

      composable(
          Route.VIEW_ITEM + "/{itemId}",
          arguments = listOf(navArgument("itemId") { type = NavType.StringType })) {
            val itemId = it.arguments?.getString("itemId")

            if (itemId != null) {
              db.getItem(itemId) { item ->
                itemViewModel.updateUiItem(item)
                itemViewModel.getUser()
              }
              itemViewModel.getAvailabilityDates()
              InventoryViewItemScreen(
                  navigationActions, itemViewModel, borrowViewModel, otherUserViewModel)
            } else {
              // Fail safe defaults principle
              navigationActions.navigateTo(Route.INVENTORY)
            }
          }

      composable(Route.CREATE_ITEM) {
        itemViewModel.getUser()
        InventoryCreateOrEditItem(
            itemViewModel, navigationActions, locationPickerViewModel, mode = "create")
      }
      composable(Route.EDIT_ITEM) {
        InventoryCreateOrEditItem(
            itemViewModel, navigationActions, locationPickerViewModel, mode = "edit")
      }
      composable(Route.MANAGE_LOAN_REQUEST) {
        // Fetch the new loan requests first
        manageViewModelIncoming.getLoanRequests(isOutgoing = false)
        ManageLoanRequest(
            manageLoanViewModel = manageViewModelIncoming,
            navigationActions = navigationActions,
            itemViewModel = itemViewModel)
      }
      composable(Route.FINISHED_LOANS) {
        finishedLoansViewModel.getFinishedLoan()
        OldLoansScreen(
            finishedLoansViewModel = finishedLoansViewModel,
            itemViewModel = itemViewModel,
            evaluationViewModel = evaluationViewModel,
            navigationActions = navigationActions)
      }
      composable(Route.MANAGE_OUTGOING_LOAN) {
        manageViewModelOutgoing.getLoanRequests(isOutgoing = true)
        ManageOutgoingLoan(
            manageLoanViewModel = manageViewModelOutgoing,
            navigationActions = navigationActions,
            itemViewModel = itemViewModel)
      }
      composable(
          Route.STAMP + "/{itemId}",
          arguments = listOf(navArgument("itemId") { type = NavType.StringType })) {
            StampScreen(
                modifier = modifier,
                stampViewModel = StampViewModel(activity),
                itemID = it.arguments?.getString("itemId") ?: "",
                navigationActions = navigationActions)
          }
      composable(Route.STARTLOAN) {
        StartLoanScreen(
            startOrEndLoanViewModel = startOrEndLoanViewModel,
            navigationActions = navigationActions,
            manageLoanViewModel = manageViewModelLoan,
            itemViewModel = itemViewModel,
            modifier = modifier)
      }
      composable(Route.ENDLOAN) {
        EndLoanScreen(
            startOrEndLoanViewModel = startOrEndLoanViewModel,
            navigationActions = navigationActions,
            itemViewModel = itemViewModel)
      }
    }
  }

  companion object {
    private const val TAG = "App"

    private var globalNavigationActions: NavigationActions? = null

    fun getNavigationActions(): NavigationActions? {
      return globalNavigationActions
    }
  }
}
