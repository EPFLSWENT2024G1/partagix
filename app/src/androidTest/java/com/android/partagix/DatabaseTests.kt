package com.android.partagix

import android.location.Location
import com.android.partagix.model.Database
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.database.Query
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.util.Executors
import com.google.firebase.firestore.util.Util.voidErrorTransformer
import io.mockk.coVerify
import io.mockk.every
import io.mockk.invoke
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkStatic
import io.mockk.verify
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
          "CreatedDt" to Timestamp.now(),
          "OwnerName" to "Jim Smith")

  /**
   * Mocks the simplest behaviour of a task so .await() can return task or throw exception See more
   * on [await] and inside of that on awaitImpl
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

    // Mock Firestore objects
    val mockDb: FirebaseFirestore = mockk()
    val mockItemsCollection = mockk<CollectionReference>()
    val mockCategoriesCollection = mockk<CollectionReference>()
    val mockUsersCollection = mockk<CollectionReference>()
    val mockLoanCollection = mockk<CollectionReference>()
    val mockItemLoanCollection = mockk<CollectionReference>()
    val mockItemsQuery = mockk<Query>()
    val mockCategoriesQuery = mockk<Query>()
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
    // Create Database instance
    val databaseUsedOnlyOnce = Database(mockDb)

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
              )
          every { mockSnapshot.iterator().next() } returns mockQueryDocument

          every { mockSnapshot.iterator().next() } returns mockQueryDocument
          every { mockSnapshot.iterator() } returns mockk()
          every { mockSnapshot.iterator().hasNext() } returns true andThen false
          every { mockSnapshot.documents.iterator().hasNext() } returns true andThen false
          every {
            mockDocument.data?.iterator()?.hasNext() ?: mockSnapshot.iterator().hasNext()
          } returns true andThen false

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
    // every {mockDb.collection(any())} returns mockItemsCollection

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

    // Verify that the Firestore collections were accessed correctly
    verify(exactly = 1) { mockItemsCollection.get() }

    verify(exactly = 1) { mockCategoriesCollection.get() }

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
            commentLoaner = "just ok",
            commentOwner = "banger",
            endDate = java.util.Date(),
            idItem = "item1",
            idOwner = user1.id,
            idLoaner = user2.id,
            reviewLoaner = "4",
            reviewOwner = "2",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)
    val loan2 =
        Loan(
            id = idLoan2,
            commentLoaner = "sympathetic",
            commentOwner = "ungrateful owner",
            endDate = java.util.Date(),
            idItem = "item1",
            idOwner = user2.id,
            idLoaner = user1.id,
            reviewLoaner = "1",
            reviewOwner = "3",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)

    val loan3 =
        Loan(
            id = idLoan3,
            commentLoaner = "hello world",
            commentOwner = "unefficient",
            endDate = java.util.Date(),
            idItem = "item1",
            idOwner = user1.id,
            idLoaner = user2.id,
            reviewLoaner = "5",
            reviewOwner = "4",
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

    runBlocking {
      var commentsOnUser1: List<Pair<String, String>> = emptyList()
      database.getComments(user1.id) { commentsOnUser1 = it }
      assertEquals(
          commentsOnUser1,
          listOf(
              Pair(user2.name, "banger"),
              Pair(user2.name, "sympathetic"),
              Pair(user2.name, "unefficient")))
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
            commentLoaner = "",
            commentOwner = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idOwner = user1.id,
            idLoaner = user2.id,
            reviewLoaner = "4",
            reviewOwner = "3",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)
    val loan2 =
        Loan(
            id = idLoan2,
            commentLoaner = "",
            commentOwner = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idOwner = user2.id,
            idLoaner = user1.id,
            reviewLoaner = "2",
            reviewOwner = "3",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)

    val loan3 =
        Loan(
            id = idLoan3,
            commentLoaner = "",
            commentOwner = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idOwner = user1.id,
            idLoaner = user2.id,
            reviewLoaner = "5",
            reviewOwner = "4",
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
            commentLoaner = "",
            commentOwner = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idOwner = user1.id,
            idLoaner = user2.id,
            reviewLoaner = "",
            reviewOwner = "",
            startDate = java.util.Date(),
            state = LoanState.FINISHED)

    val loan2 =
        Loan(
            id = idLoan2,
            commentLoaner = "",
            commentOwner = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idOwner = user1.id,
            idLoaner = user2.id,
            reviewLoaner = "",
            reviewOwner = "",
            startDate = java.util.Date(),
            state = LoanState.PENDING)

    val loan3 =
        Loan(
            id = idLoan3,
            commentLoaner = "",
            commentOwner = "",
            endDate = java.util.Date(),
            idItem = "item1",
            idOwner = user1.id,
            idLoaner = user2.id,
            reviewLoaner = "",
            reviewOwner = "",
            startDate = java.util.Date(),
            state = LoanState.PENDING)

    val mockDb: FirebaseFirestore = mockk {}

    every {
      mockDb.collection("loan").document(idLoan1).update("comment_owner", "ok, nothing more")
    } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    every { mockDb.collection("loan").document(idLoan1).update("comment_loaner", "awful") } returns
        taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    every { mockDb.collection(any()) } returns mockCollection // case other call than the one tested

    val database = spyk(Database(mockDb))

    every { database.getLoans(any()) } answers
        {
          firstArg<(List<Loan>) -> Unit>().invoke(listOf(loan1, loan2, loan3))
        }

    runBlocking {
      database.setReview(idLoan1, user1.id, 3.5, "ok, nothing more")
      database.setReview(idLoan1, user2.id, 1.5, "awful")

      coVerify(exactly = 2) {
        database.setReview("1", user1.id, 3.5, "ok, nothing more")
        database.setReview(idLoan1, user2.id, 1.5, "awful")
      }
    }

    //  Don't forget to unmock.
    unmockkStatic(::now)
  }
}
