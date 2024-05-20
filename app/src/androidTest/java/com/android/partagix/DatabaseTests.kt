package com.android.partagix

import android.location.Location
import com.android.partagix.model.Database
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.emptyConst.emptyUser
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.util.Executors
import com.google.firebase.firestore.util.Util.voidErrorTransformer
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import getImageFromFirebaseStorage
import getImagesFromFirebaseStorage
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.spyk
import io.mockk.unmockkStatic
import io.mockk.verify
import java.io.File
import java.sql.Date
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Test

class DatabaseTests {
  val path = "rentals"
  val documentId = "2507"
  val document =
      mutableMapOf<String, Any>(
          "Id" to documentId,
          "Name" to "905 Maple Drive",
          "CreatedDt" to now(),
          "OwnerName" to "Jim Smith")

  /**
   * Mocks the simplest behaviour of a task so .await() can return task or throw exception See more
   * on _await_ and inside of that on awaitImpl
   */
  inline fun <reified T> mockTask(result: T?, exception: Exception? = null): Task<T> {
    val task: Task<T> = mockk(relaxed = true)
    every { task.isComplete } returns true
    every { task.exception } returns exception
    every { task.isCanceled } returns false
    val relaxedT: T = mockk(relaxed = true)
    every { task.result } returns result
    return task
  }

  @Test
  fun testCreateDocument() {

    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    val mockDb: FirebaseFirestore = mockk {}

    every { mockDb.collection(any()) } returns mockCollection

    val database = spyk(Database(mockDb), recordPrivateCalls = true)

    val item =
        Item(
            documentId,
            Category("id", "House"),
            "Jim Smith",
            "Jim Smith",
            Visibility.PUBLIC,
            1234,
            Location(""),
        )

    runBlocking {
      database.createItem(documentId, item)

      coVerify(exactly = 1) { database.createItem(documentId, item) }
    }

    //  Don't forget to unmock.
    unmockkStatic(::now)
  }

  @Test
  fun testSetItem() {
    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    val mockDb: FirebaseFirestore = mockk {}

    every { mockDb.collection(any()) } returns mockCollection

    val database = spyk(Database(mockDb), recordPrivateCalls = true)

    val item =
        Item(
            documentId,
            Category("id", "House"),
            "Jim Smith",
            "Jim Smith",
            Visibility.PUBLIC,
            1234,
            Location(""),
        )

    runBlocking {
      database.setItem(item)

      coVerify(exactly = 1) { database.setItem(item) }
    }

    //  Don't forget to unmock.
    unmockkStatic(::now)
  }

  @Test
  fun testGetItem() {
    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val firebaseStorage = mockk<FirebaseStorage>()
    val storageReference = mockk<StorageReference>()
    val downloadTask = mockk<FileDownloadTask>()

    every { firebaseStorage.reference } returns storageReference
    every { storageReference.child("imageId") } returns storageReference
    every { downloadTask.addOnSuccessListener(any()) } answers
        {
          val listener = arg<OnSuccessListener<File>>(0)
          listener.onSuccess(File("imageId"))
          downloadTask
        }
    every { downloadTask.addOnFailureListener(any()) } returns downloadTask

    every { storageReference.getFile(any<File>()) } answers { downloadTask }

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    val documentSnapshot =
        mockk<DocumentSnapshot> {
          every { id } returns documentId
          every { data } returns document
        }

    val mockDb: FirebaseFirestore = mockk {
      every { document("$path/$documentId").get() } returns
          mockTask<DocumentSnapshot>(documentSnapshot)
    }

    every { mockDb.collection(any()) } returns mockCollection

    val mockQuerySnapshot = mockk<QuerySnapshot>()

    // Wrap the QuerySnapshot into a Task object
    val task = mockk<Task<QuerySnapshot>>()
    every { task.result } returns mockQuerySnapshot

    // Define the behavior for the `get()` function call
    every { mockCollection.get() } returns task

    // Define the behavior for the `addOnSuccessListener` function call
    every { task.addOnSuccessListener(any()) } returns task
    every { task.addOnFailureListener(any()) } returns task

    val firebaseCrud = spyk(Database(mockDb), recordPrivateCalls = true)

    val item =
        Item(
            documentId,
            Category("id", "House"),
            "Jim Smith",
            "Jim Smith",
            Visibility.PUBLIC,
            1234,
            Location(""),
        )

    runBlocking {
      firebaseCrud.getItem(documentId) { assertEquals(document.get("OwnerName"), item) }
    }
  }

  @Test
  fun testGetItems() {
    // Mock current timestamp
    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    mockkStatic(::getImagesFromFirebaseStorage)
    every { getImagesFromFirebaseStorage(any(), any()) } answers
        {
          val onSuccess = arg<(List<File>) -> Unit>(4)
          onSuccess(listOf(File("imageId")))
        }

    // Mock Firestore objects
    val mockDb: FirebaseFirestore = mockk()
    val mockItemsCollection = mockk<CollectionReference>()
    val mockCategoriesCollection = mockk<CollectionReference>()
    val mockUsersCollection = mockk<CollectionReference>()
    val mockLoanCollection = mockk<CollectionReference>()
    val mockItemLoanCollection = mockk<CollectionReference>()
    val mockItemsTask = mockk<Task<QuerySnapshot>>()
    val mockCategoriesTask = mockk<Task<QuerySnapshot>>()

    val categoryId = "catId"
    val categoryName = "catName"

    val userId = "userId"

    val item =
        Item(
            "itemId",
            Category(categoryId, categoryName),
            "itemName",
            "itemDescription",
            Visibility.PUBLIC,
            1234,
            Location(""),
        )

    every { mockDb.collection(any()) } returns mockItemsCollection

    // Define behavior for Firestore mocks
    every { mockDb.collection("items") } returns mockItemsCollection
    every { mockItemsCollection.get() } returns mockItemsTask
    every { mockItemsTask.addOnSuccessListener(any<OnSuccessListener<QuerySnapshot>>()) } answers
        {
          val listener = arg<OnSuccessListener<QuerySnapshot>>(0)
          val mockSnapshot = mockk<QuerySnapshot>()
          val mockDocument = mockk<DocumentSnapshot>()
          val mockQueryDocument = mockk<QueryDocumentSnapshot>()
          every { mockQueryDocument.data } returns
              mapOf(
                  "id" to item.id,
                  "id_category" to categoryId,
                  "name" to item.name,
                  "description" to item.description,
                  "quantity" to item.quantity,
                  "visibility" to item.visibility.ordinal.toLong(),
                  "location" to Database(mockDb).locationToMap(item.location),
                  "id_user" to userId,
                  "id_image" to "imageId")
          every { mockSnapshot.iterator().next() } returns mockQueryDocument

          every { mockSnapshot.iterator().next() } returns mockQueryDocument
          every { mockSnapshot.iterator() } returns mockk()
          every { mockSnapshot.iterator().hasNext() } returns
              true andThen
              false andThen
              true andThen
              false
          every { mockSnapshot.documents.iterator().hasNext() } returns
              true andThen
              false andThen
              true andThen
              false
          every {
            mockDocument.data?.iterator()?.hasNext() ?: mockSnapshot.iterator().hasNext()
          } returns true andThen false andThen true andThen false

          listener.onSuccess(mockSnapshot)
          mockItemsTask
        }
    every { mockItemsTask.addOnFailureListener(any()) } returns mockItemsTask

    every { mockDb.collection("categories") } returns mockCategoriesCollection
    every { mockCategoriesCollection.get() } returns mockCategoriesTask
    every {
      mockCategoriesTask.addOnSuccessListener(any<OnSuccessListener<QuerySnapshot>>())
    } answers
        {
          val listener = arg<OnSuccessListener<QuerySnapshot>>(0)
          val mockSnapshot = mockk<QuerySnapshot>()
          val mockDocument = mockk<DocumentSnapshot>()
          val mockQueryDocument = mockk<QueryDocumentSnapshot>()

          every { mockQueryDocument.data } returns mapOf("id" to categoryId, "name" to categoryName)
          every { mockSnapshot.documents } returns listOf(mockDocument)

          every { mockSnapshot.iterator().next() } returns mockQueryDocument
          every { mockSnapshot.iterator() } returns mockk()
          every { mockSnapshot.iterator().hasNext() } returns true andThen false
          every { mockSnapshot.documents.iterator().hasNext() } returns true andThen false
          every {
            mockDocument.data?.iterator()?.hasNext() ?: mockSnapshot.iterator().hasNext()
          } returns true andThen false

          listener.onSuccess(mockSnapshot)
          mockCategoriesTask
        }
    every { mockCategoriesTask.addOnFailureListener(any()) } returns mockCategoriesTask

    every { mockDb.collection("users") } returns mockUsersCollection
    every { mockDb.collection("loan") } returns mockLoanCollection
    every { mockDb.collection("item_loan") } returns mockItemLoanCollection

    val firebaseStorage = mockk<FirebaseStorage>()
    val storageReference = mockk<StorageReference>()
    val uploadTask = mockk<UploadTask>()
    val downloadTask = mockk<FileDownloadTask>()

    every { firebaseStorage.reference } returns storageReference
    every { storageReference.child("imageId") } returns storageReference
    every { uploadTask.addOnSuccessListener(any()) } returns uploadTask
    every { uploadTask.addOnFailureListener(any()) } returns uploadTask

    // Create Database instance
    val database = Database(mockDb)

    // Perform the function call
    val onSuccessCallback: (List<Item>) -> Unit = { items ->
      // Assert on the returned list of items
      assertNotNull(items)
      assertEquals(1, items.size)
      // Add your assertions here based on the expected behavior
    }
    database.getItems(onSuccessCallback)

    database.getItemsWithImages(onSuccessCallback)

    // Verify that the Firestore collections were accessed correctly
    verify(exactly = 2) { mockItemsCollection.get() }

    verify(exactly = 2) { mockCategoriesCollection.get() }
    // Unmock static function
    unmockkStatic(::now)
  }

  @Test
  fun testCreateUser() {
    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    val mockDb: FirebaseFirestore = mockk {}

    every { mockDb.collection(any()) } returns mockCollection

    val database = spyk(Database(mockDb), recordPrivateCalls = true)

    val user =
        User(
            documentId,
            "",
            "",
            "",
            Inventory("id", listOf()),
        )

    runBlocking {
      database.createUser(user)

      coVerify(exactly = 1) { database.createUser(user) }
    }

    //  Don't forget to unmock.
    unmockkStatic(::now)
  }

  @Test
  fun testGetUser() {
    // Mock current timestamp
    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    mockkStatic(::getImageFromFirebaseStorage)
    every { getImageFromFirebaseStorage("users/userId", any(), any(), any()) } answers
        {
          val onSuccess = arg<(File) -> Unit>(3)
          onSuccess((File("imageId")))
        }

    every { getImageFromFirebaseStorage("users/userId2", any(), any(), any()) } answers
        {
          val onFailure = arg<(java.lang.Exception) -> Unit>(2)
          onFailure(Exception("Error"))
        }

    // Mock Firestore objects

    val mockDb: FirebaseFirestore = mockk()
    val mockUsersCollection = mockk<CollectionReference>()
    val mockUsersTask = mockk<Task<DocumentSnapshot>>()
    val mockUsersTask2 = mockk<Task<DocumentSnapshot>>()

    val mockDocumentReference = mockk<DocumentReference>()
    val mockDocumentReference2 = mockk<DocumentReference>()

    val userId = "userId"
    val userId2 = "userId2"

    // This user is returned by Firestore
    val user =
        User(
            userId,
            "userName",
            "userAddress",
            "rank",
            Inventory("id", listOf()),
            imageId = File("imageId"),
        )
    // This user should be returned by getUser and the other one for getUserWithImage
    val userWithoutImage =
        User(
            userId,
            "userName",
            "userAddress",
            "rank",
            Inventory("id", listOf()),
            imageId = File("noImage"),
        )
    val user2 =
        User(
            userId2,
            "userName",
            "userAddress",
            "rank",
            Inventory("id", listOf()),
            imageId = File("noImage"),
        )
    // Define behavior for Firestore mocks
    every { mockDb.collection(any()) } returns mockUsersCollection

    every { mockDb.collection("users") } returns mockUsersCollection
    every { mockUsersCollection.document("userId") } returns mockDocumentReference
    every { mockUsersCollection.document("userId2") } returns mockDocumentReference2

    every { mockDocumentReference.get() } returns mockUsersTask
    every { mockDocumentReference2.get() } returns mockUsersTask2

    every { mockUsersTask.addOnSuccessListener(any<OnSuccessListener<DocumentSnapshot>>()) } answers
        {
          val listener = arg<OnSuccessListener<DocumentSnapshot>>(0)
          val mockDocument = mockk<DocumentSnapshot>()
          every { mockDocument.data } returns
              mapOf(
                  "id" to user.id,
                  "name" to user.name,
                  "addr" to user.address,
                  "rank" to user.rank,
              )

          listener.onSuccess(mockDocument)
          mockUsersTask
        }
    every {
      mockUsersTask2.addOnSuccessListener(any<OnSuccessListener<DocumentSnapshot>>())
    } answers
        {
          val listener = arg<OnSuccessListener<DocumentSnapshot>>(0)
          val mockDocument = mockk<DocumentSnapshot>()
          every { mockDocument.data } returns
              mapOf(
                  "id" to user2.id,
                  "name" to user2.name,
                  "addr" to user2.address,
                  "rank" to user2.rank,
              )

          listener.onSuccess(mockDocument)
          mockUsersTask
        }

    every { mockUsersTask.addOnFailureListener(any<OnFailureListener>()) } answers
        {
          val listener = arg<OnFailureListener>(0)
          listener.onFailure(Exception("Error"))
          mockUsersTask
        }

    // Perform the function call
    val onSuccessNoImageCallback: (User) -> Unit = { res -> assertEquals(userWithoutImage, res) }
    val onSuccessCallback: (User) -> Unit = { res -> assertEquals(user, res) }
    val onSuccessInvalidImageCallback: (User) -> Unit = { res -> assertEquals(user2, res) }

    val onNoUserCallback: () -> Unit = { assert(false) }
    val database = spyk(Database(mockDb), recordPrivateCalls = true)

    every { database.getUserInventory(any(), any()) } answers
        {
          val onSuccess = arg<(Inventory) -> Unit>(1)
          onSuccess(user.inventory)
        }

    runBlocking { database.getUser(userId, onNoUserCallback, onSuccessNoImageCallback) }

    runBlocking { database.getUserWithImage(userId, onNoUserCallback, onSuccessCallback) }

    runBlocking {
      database.getUserWithImage(userId2, onNoUserCallback, onSuccessInvalidImageCallback)
    }
    //  Don't forget to unmock.
    unmockkStatic(::now)
  }

  @Test
  fun testCreateLoan() {
    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    val mockDb: FirebaseFirestore = mockk {}

    every { mockDb.collection(any()) } returns mockCollection

    val database = spyk(Database(mockDb), recordPrivateCalls = true)

    every { database.getAvailableItems(any(), any()) } just runs

    val loan =
        Loan(
            "id",
            "id_owner",
            "id_loaner",
            "id_item",
            Date(0),
            Date(0),
            "0.0",
            "0.0",
            "c",
            "c",
            LoanState.PENDING)

    runBlocking {
      database.createLoan(loan)

      coVerify(exactly = 1) { database.createLoan(loan) }
      coVerify(exactly = 1) { database.getAvailableItems(any(), any()) }
    }
  }

  @Test
  fun testGetAvailableItems() {
    mockkObject(Authentication)

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val mockDb: FirebaseFirestore = mockk {}

    val mockuser = mockk<FirebaseUser>()

    every { mockuser.uid } returns "myUserId"

    every { mockDb.collection(any()) } returns mockCollection

    every { Authentication.getUser() } returns mockuser

    val database = spyk(Database(mockDb), recordPrivateCalls = true)

    val availableItemBasic =
        Item(
            "availableItemBasic",
            Category("id", "name"),
            "owner",
            "description",
            Visibility.PUBLIC,
            1,
            Location("location"))

    val availableItemInsideCancelLoan =
        Item(
            "availableItemInsideCancelLoan",
            Category("id", "name"),
            "owner",
            "description",
            Visibility.PUBLIC,
            1,
            Location("location"))

    val availableItemInsideFinishedLoan =
        Item(
            "availableItemInsideFinishedLoan",
            Category("id", "name"),
            "owner",
            "description",
            Visibility.PUBLIC,
            1,
            Location("location"))

    val availableItemInsidePendingLoan =
        Item(
            "availableItemInsidePendingLoan",
            Category("id", "name"),
            "owner",
            "description",
            Visibility.PUBLIC,
            1,
            Location("location"))

    val unAvailableItemPrivate =
        Item(
            "unAvailableItemPrivate",
            Category("id", "name"),
            "owner",
            "description",
            Visibility.PRIVATE,
            1,
            Location("location"))
    val unAvailableItemBelongToCurrentUser =
        Item(
            "unAvailableItemBelongToCurrentUser",
            Category("id", "name"),
            "owner",
            "description",
            Visibility.PUBLIC,
            1,
            Location("location"),
            "myUserId")

    val unAvailableItemInsideAcceptedLoan =
        Item(
            "unAvailableItemInsideAcceptedLoan",
            Category("id", "name"),
            "owner",
            "description",
            Visibility.PUBLIC,
            1,
            Location("location"),
            "")

    val unAvailableItemInsideOnGoingLoan =
        Item(
            "unAvailableItemInsideOnGoingLoan",
            Category("id", "name"),
            "owner",
            "description",
            Visibility.PUBLIC,
            1,
            Location("location"))

    val unAvailableItemInsidePendingLoan =
        Item(
            "unAvailableItemInsidePendingLoan",
            Category("id", "name"),
            "owner",
            "description",
            Visibility.PUBLIC,
            1,
            Location("location"),
            "myUserId")

    val items =
        listOf(
            availableItemBasic,
            availableItemInsideCancelLoan,
            availableItemInsideFinishedLoan,
            availableItemInsidePendingLoan,
            unAvailableItemPrivate,
            unAvailableItemBelongToCurrentUser,
            unAvailableItemInsideAcceptedLoan,
            unAvailableItemInsideOnGoingLoan,
            unAvailableItemInsidePendingLoan)

    every { database.getItems(any()) } answers { firstArg<(List<Item>) -> Unit>().invoke(items) }

    every { database.getItemsWithImages(any()) } answers
        {
          firstArg<(List<Item>) -> Unit>().invoke(items)
        }

    val loan1 =
        Loan(
            "loan1",
            "id_owner",
            "id_loaner",
            "availableItemInsideCancelLoan",
            Date(0),
            Date(0),
            "0.0",
            "0.0",
            "c",
            "c",
            LoanState.CANCELLED)

    val loan2 =
        Loan(
            "loan2",
            "id_owner",
            "id_loaner",
            "availableItemInsideFinishedLoan",
            Date(0),
            Date(0),
            "0.0",
            "0.0",
            "c",
            "c",
            LoanState.FINISHED)

    val loan3 =
        Loan(
            "loan3",
            "id_owner",
            "id_loaner",
            "availableItemInsidePendingLoan",
            Date(0),
            Date(0),
            "0.0",
            "0.0",
            "c",
            "c",
            LoanState.PENDING)

    val loan4 =
        Loan(
            "loan4",
            "id_owner",
            "id_loaner",
            "unAvailableItemInsideAcceptedLoan",
            Date(0),
            Date(0),
            "0.0",
            "0.0",
            "c",
            "c",
            LoanState.ACCEPTED)

    val loan5 =
        Loan(
            "loan5",
            "id_owner",
            "id_loaner",
            "unAvailableItemInsideOnGoingLoan",
            Date(0),
            Date(0),
            "0.0",
            "0.0",
            "c",
            "c",
            LoanState.ONGOING)

    val loan6 =
        Loan(
            "loan6",
            "id_owner",
            "myUserId",
            "unAvailableItemInsidePendingLoan",
            Date(0),
            Date(0),
            "0.0",
            "0.0",
            "c",
            "c",
            LoanState.PENDING)

    val loan7 =
        Loan(
            "loan7",
            "myUserId",
            "id_loaner",
            "unAvailableItemBelongToCurrentUser",
            Date(0),
            Date(0),
            "0.0",
            "0.0",
            "c",
            "c",
            LoanState.CANCELLED)

    val loans = listOf(loan1, loan2, loan3, loan4, loan5, loan6, loan7)
    every { database.getLoans(any()) } answers { firstArg<(List<Loan>) -> Unit>().invoke(loans) }

    runBlocking {
      database.getAvailableItems(false) { items ->
        for (item in items) {
          println("name: ${item.id}")
        }
        assertEquals(
            listOf(
                availableItemBasic,
                availableItemInsideCancelLoan,
                availableItemInsideFinishedLoan,
                availableItemInsidePendingLoan),
            items)
      }
    }
  }

  @Test
  fun testGetComments() {
    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    val mockDb: FirebaseFirestore = mockk {}

    every { mockDb.collection(any()) } returns mockCollection

    val database = spyk(Database(mockDb))

    val user1 =
        User(
            "8WuTkKJZLTAr6zs5L7rH", "user1", "", "", Inventory("8WuTkKJZLTAr6zs5L7rH", emptyList()))
    val user2 =
        User(
            "2WuTkKJZLTAr6zs5L7rH", "user2", "", "", Inventory("2WuTkKJZLTAr6zs5L7rH", emptyList()))

    val idLoan1 = "1"
    val idLoan2 = "2"
    val idLoan3 = "3"

    val loan1 =
        Loan(
            id = idLoan1,
            commentBorrower = "just ok",
            commentLender = "banger",
            endDate = java.util.Date(),
            idItem = "item1",
            idLender = user1.id,
            idBorrower = user2.id,
            reviewBorrower = "4",
            reviewLender = "2",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)
    val loan2 =
        Loan(
            id = idLoan2,
            commentBorrower = "sympathetic",
            commentLender = "ungrateful lender",
            endDate = java.util.Date(),
            idItem = "item1",
            idLender = user2.id,
            idBorrower = user1.id,
            reviewBorrower = "1",
            reviewLender = "3",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)

    val loan3 =
        Loan(
            id = idLoan3,
            commentBorrower = "hello world",
            commentLender = "unefficient",
            endDate = java.util.Date(),
            idItem = "item1",
            idLender = user1.id,
            idBorrower = user2.id,
            reviewBorrower = "5",
            reviewLender = "4",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)

    every { database.getLoans(any()) } answers
        {
          firstArg<(List<Loan>) -> Unit>().invoke(listOf(loan1, loan2, loan3))
        }

    every { database.getUser(user1.id, any(), any()) } answers
        {
          thirdArg<(User) -> Unit>().invoke(user1)
        }

    every { database.getUser(user2.id, any(), any()) } answers
        {
          thirdArg<(User) -> Unit>().invoke(user2)
        }
    every { database.getUsers(any()) } answers
        {
          firstArg<(List<User>) -> Unit>().invoke(listOf(user1, user2))
        }

    runBlocking {
      database.getComments(user1.id) {
        assertEquals(
            listOf(
                Pair(user2.name, "sympathetic"),
                Pair(user2.name, "banger"),
                Pair(user2.name, "unefficient")),
            it)
      }
    }

    //  Don't forget to unmock.
    unmockkStatic(::now)
  }

  @Test
  fun testNewAverageRank() {
    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    every { mockDocument.update(any<String>(), any<Double>()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    val user1 =
        User(
            "8WuTkKJZLTAr6zs5L7rH", "user1", "", "", Inventory("8WuTkKJZLTAr6zs5L7rH", emptyList()))
    val user2 =
        User(
            "2WuTkKJZLTAr6zs5L7rH", "user2", "", "", Inventory("2WuTkKJZLTAr6zs5L7rH", emptyList()))

    val idLoan1 = "1"
    val idLoan2 = "2"
    val idLoan3 = "3"

    val loan1 =
        Loan(
            id = idLoan1,
            commentBorrower = "",
            commentLender = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idLender = user1.id,
            idBorrower = user2.id,
            reviewBorrower = "4",
            reviewLender = "3",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)
    val loan2 =
        Loan(
            id = idLoan2,
            commentBorrower = "",
            commentLender = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idLender = user2.id,
            idBorrower = user1.id,
            reviewBorrower = "2",
            reviewLender = "3",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)

    val loan3 =
        Loan(
            id = idLoan3,
            commentBorrower = "",
            commentLender = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idLender = user1.id,
            idBorrower = user2.id,
            reviewBorrower = "5",
            reviewLender = "4",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)

    val mockDb: FirebaseFirestore = mockk {}

    every {
      mockDb.collection("users").document(user1.id).update("rank", (9.0 / 3.0).toString())
    } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    every { mockDb.collection(any()) } returns mockCollection // case other call than the one tested

    val database = spyk(Database(mockDb))

    every { database.getLoans(any()) } answers
        {
          firstArg<(List<Loan>) -> Unit>().invoke(listOf(loan1, loan2, loan3))
        }

    runBlocking {
      database.newAverageRank(user1.id)

      coVerify(exactly = 1) { database.newAverageRank(user1.id) }
    }

    //  Don't forget to unmock.
    unmockkStatic(::now)
  }

  @Test
  fun testSetReview() {

    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    every { mockDocument.update(any<String>(), any<Double>()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    val user1 =
        User(
            "8WuTkKJZLTAr6zs5L7rH",
            "user1",
            "",
            "0.0",
            Inventory("8WuTkKJZLTAr6zs5L7rH", emptyList()))
    val user2 =
        User(
            "2WuTkKJZLTAr6zs5L7rH",
            "user2",
            "",
            "0.0",
            Inventory("2WuTkKJZLTAr6zs5L7rH", emptyList()))

    val idLoan1 = "1"
    val idLoan2 = "2"
    val idLoan3 = "3"

    val loan1 =
        Loan(
            id = idLoan1,
            commentBorrower = "",
            commentLender = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idLender = user1.id,
            idBorrower = user2.id,
            reviewBorrower = "",
            reviewLender = "",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)

    val loan2 =
        Loan(
            id = idLoan2,
            commentBorrower = "",
            commentLender = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idLender = user1.id,
            idBorrower = user2.id,
            reviewBorrower = "",
            reviewLender = "",
            startDate = java.util.Date(),
            state = LoanState.PENDING)

    val loan3 =
        Loan(
            id = idLoan3,
            commentBorrower = "",
            commentLender = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idLender = user1.id,
            idBorrower = user2.id,
            reviewBorrower = "",
            reviewLender = "",
            startDate = java.util.Date(),
            state = LoanState.PENDING)

    val mockDb: FirebaseFirestore = mockk {}

    every {
      mockDb
          .collection(any())
          .document(idLoan1)
          .update(any<String>(), any<Double>(), any<List<Any>>())
    } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    every {
      mockDb.collection(any()).document(any()).update(any<FieldPath>(), any(), any())
    } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    every { mockDb.collection(any()).document(any()).update(any<String>(), any(), any()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    every { mockDb.collection(any()) } returns mockCollection // case other call than the one tested

    val database = spyk(Database(mockDb))

    every { database.getLoans(any()) } answers
        {
          firstArg<(List<Loan>) -> Unit>().invoke(listOf(loan1, loan2, loan3))
        }

    every { database.newAverageRank(user1.id) } answers
        {
          // do nothing
        }
    every { database.newAverageRank(user2.id) } answers
        {
          // do nothing
        }

    runBlocking {
      database.setReview(idLoan1, user1.id, 3.5, "ok, nothing more")
      database.setReview(idLoan1, user2.id, 1.5, "awful")

      coVerify {
        database.setReview(idLoan1, user1.id, 3.5, "ok, nothing more")
        database.setReview(idLoan1, user2.id, 1.5, "awful")
      }
    }

    //  Don't forget to unmock.
    unmockkStatic(::now)
  }

  @Test
  fun testUpdateFCMToken() {

    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    every { mockDocument.update(any<String>(), any<String>()) } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    val mockDb: FirebaseFirestore = mockk {}

    every { mockDb.collection(any()) } returns mockCollection

    val database = spyk(Database(mockDb))

    val user = emptyUser.copy(id = "1", fcmToken = "old_token")
    val newToken = "new_token"

    runBlocking {
      database.updateFCMToken(user.id, newToken)
      coVerify(exactly = 1) { database.updateFCMToken(user.id, newToken) }
      coVerify { mockDocument.update("fcmToken", newToken) }
    }
  }
}
