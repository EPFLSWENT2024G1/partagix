package com.android.partagix

import android.location.Location
import com.android.partagix.model.Database
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.util.Executors
import com.google.firebase.firestore.util.Util.voidErrorTransformer
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.unmockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.sql.Date

class DatabaseTests {
  val path = "rentals"
  val documentId = "2507"
  val document = mutableMapOf<String, Any>(
    "Id" to documentId,
    "Name" to "905 Maple Drive",
    "CreatedDt" to Timestamp.now(),
    "OwnerName" to "Jim Smith"
  )
  /**
   * Mocks the simplest behaviour of a task so .await() can return task or throw exception
   * See more on [await] and inside of that on awaitImpl
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
  fun testingCreateDocument() {

    mockkStatic(::now)
    every { now() } returns Timestamp(Date(0))

    val taskCompletionSource = TaskCompletionSource<Void>()

    val mockCollection = mockk<CollectionReference>()

    val mockDocument = mockk<DocumentReference>()

    every { mockCollection.document(any()) } returns mockDocument

    every { mockCollection.document() } returns mockDocument

    val documentId = "wkUYnOmKkNVWlo1K8/59SDD/JtCWCf9MvnAgSYx9BbCN8ZbuNU+uSqPWVDuFnVRB"
    every { mockDocument.id } returns documentId

    every { mockDocument.set(any()) } returns taskCompletionSource.task.continueWith(Executors.DIRECT_EXECUTOR, voidErrorTransformer())

    val mockDb: FirebaseFirestore = mockk {}

    every { mockDb.collection(any()) } returns mockCollection

    val database = spyk(Database(mockDb), recordPrivateCalls = true)

    val item = Item(
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

      coVerify(exactly = 1) {
        database.createItem(documentId, item)
      }

    }

    //  Don't forget to unmock.
    unmockkStatic(::now)
  }
}