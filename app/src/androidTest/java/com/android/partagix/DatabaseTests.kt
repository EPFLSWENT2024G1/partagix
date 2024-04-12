package com.github.se.assocify.model.database

import android.location.Location
import android.net.Uri
import com.android.partagix.model.Database
import com.android.partagix.model.category.Category
import com.android.partagix.model.item.Item
import com.android.partagix.model.visibility.Visibility

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import io.mockk.verify
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@MockKExtension.ConfirmVerification
class DatabaseTests {
  @get:Rule val mockkRule = MockKRule(this)

  @MockK lateinit var storage: FirebaseStorage

  @MockK lateinit var storageReference: StorageReference

  @MockK lateinit var firestore: FirebaseFirestore

  @MockK lateinit var collectionReference: CollectionReference

  private lateinit var api: Database

  private val successfulItem =
    Item(
      "successful_rid",
      Category("1", "Successful Category"),
      "successful_name",
      "successful_description",
      Visibility.PUBLIC,
      1,
      Location(""),
      "successful_idUser")

  private val failingItem =
    Item(
      "Failing_rid",
      Category("1", "Failing Category"),
      "Failing_name",
      "Failing_description",
      Visibility.PUBLIC,
      1,
      Location(""),
      "Failing_idUser")

  @Before
  fun setUp() {

    every { storage.getReference("uid/receipts") }.returns(storageReference)
    every { firestore.collection("aid/receipts/uid/list") }.returns(collectionReference)

    mockkStatic(Uri::class)
    every { Uri.parse(any()) }.returns(mockk())

    api =
      spyk<Database>(Database(), recordPrivateCalls = true)

    every { api["parseReceiptList"](any<QuerySnapshot>()) } returns
        listOf(successfulItem, failingItem)
  }

  @Test
  fun uploadItem() {
    every { storageReference.child("successful_rid") }.returns(storageReference)


    val successMock = mockk<() -> Unit>(relaxed = true)
    api.createItem("uid", successfulItem )

    verify { successMock.invoke() }

  }

}

