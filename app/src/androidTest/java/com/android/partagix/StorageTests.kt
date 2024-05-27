package com.android.partagix

import android.net.Uri
import com.android.partagix.model.Database
import com.android.partagix.model.FinishedLoansViewModel
import com.android.partagix.model.StorageV2
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.emptyConst.emptyItem
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Test
import java.util.UUID

class StorageTests {

    @Before
    fun setUp() {
        // Mock FirebaseStorage and StorageReference
        val firebaseStorage = mockk<FirebaseStorage>()
        val storageReference = mockk<StorageReference>()
        val uploadTask = mockk<UploadTask>()
        // Mock storageReference.child("images") to return the mock StorageReference

        // Alternatively, you can construct a URI with components
        val builtUri =
            Uri.Builder()
                .scheme("https") // Set the scheme (e.g., "https")
                .authority("example.com") // Set the authority (e.g., "example.com")
                .appendPath("path") // Append a path segment
                .appendQueryParameter("key", "value") // Add query parameters if needed
                .build()

        val imageName = UUID.randomUUID().toString()

        every { firebaseStorage.reference } returns storageReference
        every { storageReference.child("images/$imageName.jpg") } returns storageReference
        every { storageReference.putFile(builtUri) } returns uploadTask

        every { uploadTask.addOnSuccessListener(any()) } returns uploadTask
        every { uploadTask.addOnFailureListener(any()) } returns uploadTask
    }

    @Test
    fun uploadTest() {
        // Load the default image into the cache
        val storageV2 = StorageV2()
        storageV2.uploadImageToFirebaseStorage(
            imageUri = Uri.parse("android.resource://com.android.partagix/drawable/default_image"),
            imageName = "default-image",
            onSuccess = { files ->
                assert(files.isNotEmpty())
            }
        )
    }
}