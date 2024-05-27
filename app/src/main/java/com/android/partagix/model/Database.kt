package com.android.partagix.model

import android.location.Location
import android.util.Log
import androidx.core.os.bundleOf
import com.android.partagix.model.auth.Authentication
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import getImageFromFirebaseStorage
import getImagesFromFirebaseStorage
import java.io.File

class Database(database: FirebaseFirestore = Firebase.firestore) {

  private val db = database
  private val users = db.collection("users")
  private val items = db.collection("items")
  private val loan = db.collection("loan")
  private val categories = db.collection("categories")

  /**
   * Get all users from the database
   *
   * @param onSuccess the function to call with the list of users
   */
  fun getUsers(onSuccess: (List<User>) -> Unit) {
    getItems { items ->
      users
          .get()
          .addOnSuccessListener { result ->
            val ret = mutableListOf<User>()
            for (document in result) {
              val listItems = items.filter { it.idUser == document.data["id"] }
              val user =
                  User(
                      document.data["id"] as String,
                      document.data["name"] as String,
                      document.data["addr"] as String,
                      document.data["rank"] as String,
                      Inventory(document.data["id"] as String, listItems),
                      File("noImage"),
                      document.data["fcmToken"] as String?)
              ret.add(user)
            }
            onSuccess(ret)
          }
          .addOnFailureListener { Log.e(TAG, "Error getting users", it) }
    }
  }

  /**
   * Get one user from the database
   *
   * @param idUser the user's id
   * @param onNoUser the function to call when the user is not found
   * @param onSuccess the function to call with the user
   */
  fun getUser(idUser: String, onNoUser: () -> Unit = {}, onSuccess: (User) -> Unit) {
    users.document(idUser).get().addOnSuccessListener {
      val user = it.data
      if (user != null) {
        Log.d(TAG, user.toString())
        getUserInventory(idUser) { inventory ->
          val name = user["name"] as String
          val addr = user["addr"] as String
          val rank = user["rank"] as String
          val fcmToken = user["fcmToken"] as String?
          onSuccess(User(idUser, name, addr, rank, inventory, File("noImage"), fcmToken))
        }
      } else {
        onNoUser()
      }
    }
  }

  fun getUserWithImage(idUser: String, onNoUser: () -> Unit = {}, onSuccess: (User) -> Unit) {
    users.document(idUser).get().addOnSuccessListener {
      val onSuccessImage = { localFile: File ->
        val user = it.data
        if (user != null) {
          Log.d(TAG, user.toString())
          getUserInventory(idUser) { inventory ->
            val name = user["name"] as String
            val addr = user["addr"] as String
            val rank = user["rank"] as String
            val fcmToken = user["fcmToken"] as String?
            onSuccess(User(idUser, name, addr, rank, inventory, localFile, fcmToken))
          }
        } else {
          onNoUser()
        }
      }
      getImageFromFirebaseStorage(
          "users/$idUser",
          onFailure = {
            Log.w("emptyUserImage", "No image found for user $idUser")
            onSuccessImage(File("noImage"))
          }) { localFile ->
            onSuccessImage(localFile)
          }
    }
  }

  /**
   * Get the current user
   *
   * @param onSuccess the function to call with the current user
   */
  fun getCurrentUser(onSuccess: (User) -> Unit) {
    val user = Firebase.auth.currentUser
    if (user != null) {
      getUser(user.uid, onSuccess = onSuccess)
    } else Log.e(TAG, "No user logged in")
  }

  /**
   * Get all items from the database
   *
   * @param onSuccess the function to call with the list of items
   */
  fun getItemsWithImages(onSuccess: (List<Item>) -> Unit) {
    items
        .get()
        .addOnSuccessListener { result ->
          val paths = mutableListOf<String>()
          for (document in result) {
            if (document.data["image_path"] != null && document.data["image_path"] != "") {
              paths.add(document.data["image_path"] as String)
            } else {
              paths.add("default-image.jpg")
            }
          }
          categories
              .get()
              .addOnSuccessListener { result2 ->
                val categories =
                    result2
                        .map { document ->
                          document.data["id"] as String to
                              Category(
                                  document.data["id"] as String, document.data["name"] as String)
                        }
                        .toMap()

                getImagesFromFirebaseStorage(
                    paths,
                    Firebase.storage,
                    onFailure = { Log.e("error getting images for items", it.toString()) }) {
                        localFiles ->
                      val ret = mutableListOf<Item>()
                      var count = 0
                      for (document in result) {
                        val locationMap = document.data["location"] as HashMap<*, *>
                        val location = toLocation(locationMap)

                        val visibility = (document.data["visibility"] as Long).toInt()
                        val item =
                            Item(
                                document.data["id"] as String,
                                categories[document.data["id_category"] as String]!!,
                                document.data["name"] as String,
                                document.data["description"] as String,
                                Visibility.values()[visibility],
                                document.data["quantity"] as Long,
                                location,
                                document.data["id_user"] as String,
                                localFiles[count++])
                        ret.add(item)
                      }
                      onSuccess(ret)
                    }
              }
              .addOnFailureListener { Log.e(TAG, "Error getting categories", it) }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting items", it) }
  }

  fun getItems(onSuccess: (List<Item>) -> Unit) {
    items
        .get()
        .addOnSuccessListener { result ->
          categories
              .get()
              .addOnSuccessListener { result2 ->
                val categories =
                    result2
                        .map { document ->
                          document.data["id"] as String to
                              Category(
                                  document.data["id"] as String, document.data["name"] as String)
                        }
                        .toMap()

                val ret = mutableListOf<Item>()
                for (document in result) {
                  val locationMap = document.data["location"] as HashMap<*, *>
                  val location = toLocation(locationMap)

                  val visibility = (document.data["visibility"] as Long).toInt()

                  val item =
                      Item(
                          document.data["id"] as String,
                          categories[document.data["id_category"] as String]!!,
                          document.data["name"] as String,
                          document.data["description"] as String,
                          Visibility.values()[visibility],
                          document.data["quantity"] as Long,
                          location,
                          document.data["id_user"] as String,
                          File("noImage"))
                  ret.add(item)
                }
                onSuccess(ret)
              }
              .addOnFailureListener { Log.e(TAG, "Error getting categories", it) }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting items", it) }
  }

  /**
   * Convert a location map to a Location object
   *
   * @param locationMap the map containing the location data
   * @return the Location object
   */
  private fun toLocation(locationMap: HashMap<*, *>): Location {
    val latitude = locationMap["latitude"] as Double
    val longitude = locationMap["longitude"] as Double
    val displayName = locationMap["display_name"] as String?

    val location = Location("")
    location.latitude = latitude
    location.longitude = longitude
    location.extras = bundleOf("display_name" to displayName)
    return location
  }

  /**
   * Convert a Location object to a map
   *
   * @param location the Location object
   * @return the map containing the location data
   */
  fun locationToMap(location: Location): Map<String, Any?> {
    val locationMap = mutableMapOf<String, Any?>()
    locationMap["latitude"] = location.latitude
    locationMap["longitude"] = location.longitude
    locationMap["display_name"] = location.extras?.getString("display_name")
    // Add other relevant properties if needed

    return locationMap
  }

  /**
   * Get the inventory of a user
   *
   * @param userId the user's id
   * @param onSuccess the function to call with the user's inventory
   */
  fun getUserInventory(userId: String, onSuccess: (Inventory) -> Unit) {
    getItems { items ->
      val listItems = items.filter { it.idUser == userId }
      onSuccess(Inventory(userId, listItems))
    }
  }

  /**
   * Get all the loans
   *
   * @param onSuccess the function to call with the list of loans
   */
  fun getLoans(onSuccess: (List<Loan>) -> Unit) {
    loan
        .get()
        .addOnSuccessListener { result ->
          val ret = mutableListOf<Loan>()
          for (document in result) {
            try {
              val startDate: Timestamp = document.data["start_date"] as Timestamp
              val endDate: Timestamp = document.data["end_date"] as Timestamp
              val loanState: LoanState = LoanState.valueOf(document.data["loan_state"] as String)

              val loan =
                  Loan(
                      document.id,
                      document.data["id_lender"] as String,
                      document.data["id_borrower"] as String,
                      document.data["id_item"] as String,
                      startDate.toDate(),
                      endDate.toDate(),
                      document.data["review_lender"] as String,
                      document.data["review_borrower"] as String,
                      document.data["comment_lender"] as String,
                      document.data["comment_borrower"] as String,
                      loanState,
                  )
              ret.add(loan)
            } catch (e: Exception) {
              Log.e(TAG, "Error parsing loan data", e)
            }
          }
          onSuccess(ret)
        }
        .addOnFailureListener { Log.e(TAG, "Error getting loans", it) }
  }
  /*
   fun getCategories(onSuccess: (List<Category>) -> Unit) {
     categories
         .get()
         .addOnSuccessListener { result ->
           val ret = mutableListOf<Category>()
           for (document in result) {
             val category =
                 Category(
                     document.data["id"] as String,
                     document.data["name"] as String,
                 )
             ret.add(category)
           }
           onSuccess(ret)
         }
         .addOnFailureListener { Log.e(TAG, "Error getting categories", it) }
   }
  */

  fun getAvailableItems(isItemWithImage: Boolean = false, onSuccess: (List<Item>) -> Unit) {

    val itemSuccess: (List<Item>) -> Unit = { items ->
      getLoans { loans ->
        val availableItems1 =
            items.filter { item ->
              item.visibility == Visibility.PUBLIC && item.idUser != Authentication.getUser()?.uid
            }

        val availableItems2 =
            availableItems1.filter { item ->
              loans.none { loan ->
                loan.idItem == item.id &&
                    (loan.state == LoanState.ACCEPTED || loan.state == LoanState.ONGOING)
              }
            }
        val availableItems3 =
            availableItems2.filter { item ->
              loans.none { loan ->
                loan.idItem == item.id &&
                    loan.state == LoanState.PENDING &&
                    loan.idBorrower == Authentication.getUser()?.uid
              }
            }
        onSuccess(availableItems3)
      }
    }

    if (isItemWithImage) {
      getItemsWithImages(itemSuccess)
    } else {
      getItems(itemSuccess)
    }
  }

  private fun getNewUid(collection: CollectionReference): String {
    val uidDocument = collection.document()
    return uidDocument.id
  }

  /*
  private fun createExampleForDb(
      users: CollectionReference = this.users,
      items: CollectionReference = this.items,
      loan: CollectionReference = this.loan,
      categories: CollectionReference = this.categories
  ) {
    val idUser = getNewUid(users)
    val data1 =
        hashMapOf(
            "id" to idUser,
            "name" to "name",
            "addr" to "addr",
            "rank" to "rank",
        )
    users.document(idUser).set(data1)

    val idCategory = getNewUid(categories)
    val data2 =
        hashMapOf(
            "id" to idCategory,
            "name" to "name",
        )
    categories.document(idCategory).set(data2)

    val idItem = getNewUid(items)

    val data3 =
        hashMapOf(
            "id" to idItem,
            "id_category" to idCategory,
            "name" to "name",
            "description" to "description",
            "visibility" to 0,
            "quantity" to 1,
            "location" to locationToMap(Location("")),
            "id_user" to idUser,
        )
    items.document(idItem).set(data3)

    val idLoan = getNewUid(loan)
    val data5 =
        hashMapOf(
            "id_lender" to idUser,
            "id_borrower" to idUser,
            "id_item" to idItem,
            "start_date" to Date(),
            "end_date" to Date(),
            "review_lender" to "Review",
            "review_borrower" to "Review",
            "comment_lender" to "Comment",
            "comment_borrower" to "Comment",
            "state" to LoanState.FINISHED.toString())
    loan.document(idLoan).set(data5)

    val idItemLoan = getNewUid(itemLoan)
    val data6 = hashMapOf("id_item" to idItem, "id_loan" to idLoan)
    itemLoan.document(idItemLoan).set(data6)
  }

  private fun resetDB() {
    val latch = CountDownLatch(6)

    users.get().addOnSuccessListener { result ->
      result.forEach { it.reference.delete() }
      latch.countDown()
    }
    items.get().addOnSuccessListener { result ->
      result.forEach { it.reference.delete() }
      latch.countDown()
    }

    loan.get().addOnSuccessListener { result ->
      result.forEach { it.reference.delete() }
      latch.countDown()
    }
    categories.get().addOnSuccessListener { result ->
      result.forEach { it.reference.delete() }
      latch.countDown()
    }
    itemLoan.get().addOnSuccessListener { result ->
      result.forEach { it.reference.delete() }
      latch.countDown()
    }

    latch.await()
  }

   */

  /**
   * Create an item in the database
   *
   * @param userId the user's id
   * @param newItem the item to create
   * @param onSuccess the function to call when the item is created
   */
  fun createItem(userId: String, newItem: Item, onSuccess: (Item) -> Unit = {}) {
    val idItem = getNewUid(items)
    val data =
        hashMapOf(
            "id" to idItem,
            "id_category" to newItem.category.id,
            "name" to newItem.name,
            "description" to newItem.description,
            "id_user" to userId,
            "visibility" to newItem.visibility.ordinal,
            "quantity" to newItem.quantity,
            "location" to locationToMap(newItem.location),
            "image_path" to newItem.imageId.name)
    items.document(idItem).set(data)
    val new =
        Item(
            idItem,
            newItem.category,
            newItem.name,
            newItem.description,
            newItem.visibility,
            newItem.quantity,
            newItem.location,
            userId,
            newItem.imageId)
    onSuccess(new)
  }

  /**
   * Set an item in the database
   *
   * @param newItem the item to set
   */
  fun setItem(newItem: Item) {
    val data =
        hashMapOf(
            "id" to newItem.id,
            "id_category" to newItem.category.id,
            "name" to newItem.name,
            "description" to newItem.description,
            "id_user" to newItem.idUser,
            "visibility" to newItem.visibility.ordinal,
            "quantity" to newItem.quantity,
            "location" to locationToMap(newItem.location),
            "image_path" to newItem.id)
    items.document(newItem.id).set(data)
  }

  /**
   * Set a loan in the database
   *
   * @param newLoan the loan to create
   */
  fun setLoan(newLoan: Loan) {
    val data =
        hashMapOf(
            "id_lender" to newLoan.idLender,
            "id_borrower" to newLoan.idBorrower,
            "id_item" to newLoan.idItem,
            "start_date" to newLoan.startDate,
            "end_date" to newLoan.endDate,
            "review_lender" to newLoan.reviewLender,
            "review_borrower" to newLoan.reviewBorrower,
            "comment_lender" to newLoan.commentLender,
            "comment_borrower" to newLoan.commentBorrower,
            "loan_state" to newLoan.state.toString())

    loan.document(newLoan.id).set(data)
  }

  /**
   * Create a loan in the database
   *
   * @param newLoan the loan to create
   * @param onSuccess the function to call when the loan is created
   */
  fun createLoan(newLoan: Loan, onSuccess: (Loan) -> Unit = {}) {

    getAvailableItems { items ->
      val item = items.firstOrNull { it.id == newLoan.idItem }
      if (item == null) {
        Log.d(TAG, "Item not found")
        return@getAvailableItems
      }

      val idLoan = getNewUid(loan)
      val data =
          hashMapOf(
              "id_lender" to newLoan.idLender,
              "id_borrower" to newLoan.idBorrower,
              "id_item" to newLoan.idItem,
              "start_date" to newLoan.startDate,
              "end_date" to newLoan.endDate,
              "review_lender" to newLoan.reviewLender,
              "review_borrower" to newLoan.reviewBorrower,
              "comment_lender" to newLoan.commentLender,
              "comment_borrower" to newLoan.commentBorrower,
              "loan_state" to newLoan.state.toString())
      loan.document(idLoan).set(data)
      val new =
          Loan(
              idLoan,
              newLoan.idLender,
              newLoan.idBorrower,
              newLoan.idItem,
              newLoan.startDate,
              newLoan.endDate,
              newLoan.reviewLender,
              newLoan.reviewBorrower,
              newLoan.commentLender,
              newLoan.commentBorrower,
              newLoan.state)
      onSuccess(new)
    }
  }

  /**
   * Get an item from the database
   *
   * @param id the item's id
   * @param onSuccess the function to call with the item
   */
  fun getItem(id: String, onSuccess: (Item) -> Unit) {
    getItems { items ->
      val item = items.firstOrNull { it.id == id }
      item?.let { onSuccess(it) }
    }
  }

  /**
   * Get an item from the database
   *
   * @param id the item's id
   * @param onSuccess the function to call with the item
   */
  fun getItemWithImage(id: String, onSuccess: (Item) -> Unit) {
    items.document(id).get().addOnSuccessListener {
      val onSuccessImage = { localFile: File ->
        val item = it.data
        if (item != null) {
          val locationMap = item["location"] as HashMap<*, *>
          val location = toLocation(locationMap)

          val visibility = (item["visibility"] as Long).toInt()

          val newItem =
              Item(
                  item["id"] as String,
                  Category(item["id_category"] as String, ""),
                  item["name"] as String,
                  item["description"] as String,
                  Visibility.values()[visibility],
                  item["quantity"] as Long,
                  location,
                  item["id_user"] as String,
                  localFile)
          onSuccess(newItem)
        }
      }

      val path = it.data?.get("image_path") as String

      getImageFromFirebaseStorage(
          "images/$path",
          onFailure = {
            Log.w("emptyItemImage", "No image found")
            onSuccessImage(File("noImage"))
          }) { localFile ->
            onSuccessImage(localFile)
          }
      // onSuccess(item_)

    }
  }
  /**
   * Get the category id from a category name
   *
   * @param nameCategory the name of the category
   * @param onSuccess the function to call when the id is found
   */
  fun getIdCategory(nameCategory: String, onSuccess: (String) -> Unit) {
    categories
        .get()
        .addOnSuccessListener { result ->
          val doc =
              result.firstOrNull {
                (it.data["name"] as String).equals(nameCategory, ignoreCase = true)
              }
          doc?.let { onSuccess(it["id"] as String) }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting idCategory", it) }
  }

  fun getImageFromPath(path: String, onSuccess: (String) -> Unit) {}

  /**
   * Create a user in the database
   *
   * @param user the user to create
   */
  fun createUser(user: User) {
    val data =
        hashMapOf(
            "id" to user.id,
            "name" to user.name,
            "addr" to user.address,
            "rank" to user.rank,
            "fcmToken" to user.fcmToken,
        )
    users.document(user.id).set(data)
  }

  /**
   * Update a user in the database
   *
   * @param user the user to update (with the new values)
   * @param onSuccess the function to call when the user is updated
   */
  fun updateUser(user: User, onSuccess: (User) -> Unit) {
    val data =
        hashMapOf(
            "id" to user.id,
            "name" to user.name,
            "addr" to user.address,
            "rank" to user.rank,
            "fcmToken" to user.fcmToken,
        )
    users.document(user.id).set(data)
    onSuccess(user)
  }

  /**
   * Get the FCM token of a user
   *
   * @param userId the user's id
   * @param onSuccess the function to call when the token is found (or null if the user does not
   *   exist).
   */
  fun getFCMToken(userId: String, onSuccess: (String?) -> Unit) {
    getUser(userId, onNoUser = { onSuccess(null) }) { user -> onSuccess(user.fcmToken) }
  }

  /**
   * Update the FCM token of a user
   *
   * @param userId the user's id
   * @param fcmToken the new FCM token
   */
  fun updateFCMToken(userId: String, fcmToken: String) {
    users.document(userId).update("fcmToken", fcmToken)
  }

  /**
   * Retrieve all comments that a user has received, both as a lender and as a borrower
   *
   * @param userId the user's id
   * @param onSuccess the function that will be called with the resulting list containing pairs
   *   (comment's author name, comment)
   */
  fun getComments(userId: String, onSuccess: (List<Pair<String, String>>) -> Unit) {
    val ret = mutableListOf<Pair<String, String>>()

    getUsers { users ->
      getLoans { loans ->
        val loanBorrower =
            loans.filter { loan ->
              loan.state == LoanState.FINISHED &&
                  loan.idBorrower == userId &&
                  loan.reviewBorrower.toDouble() != 0.0 &&
                  loan.commentBorrower != ""
            }

        val loanLender =
            loans.filter { loan ->
              loan.state == LoanState.FINISHED &&
                  loan.idLender == userId &&
                  loan.reviewLender.toDouble() != 0.0 &&
                  loan.commentLender != ""
            }

        loanBorrower.forEach { loan ->
          val user = users.first { it.id == loan.idLender }
          ret.add(Pair(user.name, loan.commentBorrower))
        }

        loanLender.forEach { loan ->
          val user = users.first { it.id == loan.idBorrower }
          ret.add(Pair(user.name, loan.commentLender))
        }
        onSuccess(ret)
      }
    }
  }

  /**
   * Retrieve all ranks that a user has received, both as a lender and as a borrower, compute the
   * average rank, and store it in the user's rank
   *
   * @param idUser the user's id
   */
  fun newAverageRank(idUser: String) {

    getLoans { loans ->
      val reviewedLoans =
          loans.filter { loan ->
            loan.state == LoanState.FINISHED && // Only finished loans
                ((loan.idLender == idUser &&
                    loan.reviewLender.toDouble() !=
                        0.0) || // If the user is the lender and the loan has been reviewed
                    (loan.idBorrower == idUser &&
                        loan.reviewBorrower.toDouble() !=
                            0.0)) // Or if the user is the borrower and the loan has been reviewed
          }

      val rankSum =
          reviewedLoans.sumOf { loan ->
            if (loan.idLender == idUser) loan.reviewLender.toDouble()
            else loan.reviewBorrower.toDouble()
          }
      val rankCount = reviewedLoans.size.toLong()

      if (rankCount != 0L) {
        val averageRank = rankSum / rankCount.toDouble()
        users.document(idUser).update("rank", averageRank.toString())
      }
    }
  }

  /**
   * Set a review for a loan, i.e a rank and an optional comment
   *
   * @param loanId the loan's id
   * @param reviewedUserId the reviewed user's id
   * @param rank the rank to be set, must be between 0.5 and 5
   * @param comment an optional comment to be set
   */
  fun setReview(
      loanId: String,
      reviewedUserId: String,
      rank: Double,
      comment: String = "",
  ) {
    getLoans { loans ->
      val filteredLoans =
          loans.filter { loan ->
            loan.state == LoanState.FINISHED && // Only finished loans
                loan.id == loanId && // Matches the loanId
                ((loan.idLender == reviewedUserId) ||
                    (loan.idBorrower ==
                        reviewedUserId)) // Matches the userId as either lender or borrower
          }

      filteredLoans.forEach { loan ->
        if (loan.idLender == reviewedUserId) {
          this.loan.document(loanId).update("review_lender", rank.toString())
          if (comment.isNotBlank()) this.loan.document(loanId).update("comment_lender", comment)
        } else if (loan.idBorrower == reviewedUserId) {
          this.loan.document(loanId).update("review_borrower", rank.toString())
          if (comment.isNotBlank()) this.loan.document(loanId).update("comment_borrower", comment)
        }
      }
      newAverageRank(reviewedUserId)
    }
  }

  /* TODO : uncomment this function is at some point we need to display multiple users with their profile pictures in the app
   fun getUsersWithImages(onSuccess: (List<User>) -> Unit) {
     getItems { items ->
       users
           .get()
           .addOnSuccessListener { result ->
             val ret = mutableListOf<User>()
             for (document in result) {
               val listItems = items.filter { it.idUser == document.data["id"] }
               getImageFromFirebaseStorage(
                   "users/" + (document.data["id"] as String),
                   onFailure = {
                     getImageFromFirebaseStorage("users/default.png") { localFile ->
                       val user =
                           User(
                               document.data["id"] as String,
                               document.data["name"] as String,
                               document.data["addr"] as String,
                               document.data["rank"] as String,
                               Inventory(document.data["id"] as String, listItems),
                               localFile)
                       ret.add(user)
                     }
                   }) { localFile ->
                     val user =
                         User(
                             document.data["id"] as String,
                             document.data["name"] as String,
                             document.data["addr"] as String,
                             document.data["rank"] as String,
                             Inventory(document.data["id"] as String, listItems),
                             localFile)
                     ret.add(user)
                   }
             }
             onSuccess(ret)
           }
           .addOnFailureListener { Log.e(TAG, "Error getting users", it) }
     }
   }

  */

  companion object {
    private const val TAG = "Database"
  }
}
