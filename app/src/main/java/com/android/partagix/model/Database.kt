package com.android.partagix.model

import android.location.Location
import android.util.Log
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Database(database: FirebaseFirestore = Firebase.firestore) {

  private val db = database
  private val users = db.collection("users")
  private val items = db.collection("items")
  private val loan = db.collection("loan")
  private val categories = db.collection("categories")

  init {} // kept for easier testing purposes

  fun getUser(idUser: String, onNoUser: () -> Unit = {}, onSuccess: (User) -> Unit) {
    users
        .get()
        .addOnSuccessListener { result ->
          var found = false
          for (document in result) {
            if (document.data["id"] as String == idUser) {
              found = true
              getUserInventory(idUser) { inventory ->
                val user =
                    User(
                        document.data["id"] as String,
                        document.data["name"] as String,
                        document.data["addr"] as String,
                        document.data["rank"] as String,
                        inventory)
                onSuccess(user)
              }
            }
          }
          if (!found) {
            onNoUser()
          }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting user", it) }
  }

  fun getItems(onSuccess: (List<Item>) -> Unit) {
    items
        .get()
        .addOnSuccessListener { result ->
          categories
              .get()
              .addOnSuccessListener { result2 ->
                val categories = mutableMapOf<String, Category>()
                for (document in result2) {
                  categories[document.data["id"] as String] =
                      Category(document.data["id"] as String, document.data["name"] as String)
                }

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
                      )
                  ret.add(item)
                }
                onSuccess(ret)
              }
              .addOnFailureListener { Log.e(TAG, "Error getting categories", it) }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting items", it) }
  }

  private fun toLocation(locationMap: HashMap<*, *>): Location {
    val latitude = locationMap["latitude"] as Double
    val longitude = locationMap["longitude"] as Double

    val location = Location("")
    location.latitude = latitude
    location.longitude = longitude
    return location
  }

  fun locationToMap(location: Location): Map<String, Any?> {
    val locationMap = mutableMapOf<String, Any?>()
    locationMap["latitude"] = location.latitude
    locationMap["longitude"] = location.longitude
    // Add other relevant properties if needed

    return locationMap
  }

  fun getUserInventory(userId: String, onSuccess: (Inventory) -> Unit) {
    getItems { items ->
      val listItems = mutableListOf<Item>()
      for (item in items) {
        if (item.idUser == userId) {
          listItems.add(item)
        }
      }
      onSuccess(Inventory(userId, listItems))
    }
  }

  fun getLoans(onSuccess: (List<Loan>) -> Unit) {
    loan
        .get()
        .addOnSuccessListener { result ->
          val ret = mutableListOf<Loan>()
          for (document in result) {
            val start_date: Timestamp = document.data["start_date"] as Timestamp
            val end_date: Timestamp = document.data["end_date"] as Timestamp
            val loan_state: LoanState = LoanState.valueOf(document.data["loan_state"] as String)

            val loan =
                Loan(
                    document.id,
                    document.data["id_owner"] as String,
                    document.data["id_loaner"] as String,
                    document.data["id_item"] as String,
                    start_date.toDate(),
                    end_date.toDate(),
                    document.data["review_owner"] as String,
                    document.data["review_loaner"] as String,
                    document.data["comment_owner"] as String,
                    document.data["comment_loaner"] as String,
                    loan_state,
                )
            ret.add(loan)
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
  fun getNewUid(collection: CollectionReference): String {
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
            "id_owner" to idUser,
            "id_loaner" to idUser,
            "id_item" to idItem,
            "start_date" to Date(),
            "end_date" to Date(),
            "review_owner" to "Review",
            "review_loaner" to "Review",
            "comment_owner" to "Comment",
            "comment_loaner" to "Comment",
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
            "location" to locationToMap(newItem.location))
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
            userId)
    onSuccess(new)
  }

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
        )
    items.document(newItem.id).set(data)
  }

  fun setLoan(newLoan: Loan) {
    val data5 =
        hashMapOf(
            "id_owner" to newLoan.idOwner,
            "id_loaner" to newLoan.idLoaner,
            "id_item" to newLoan.idItem,
            "start_date" to newLoan.startDate,
            "end_date" to newLoan.endDate,
            "review_owner" to newLoan.reviewOwner,
            "review_loaner" to newLoan.reviewLoaner,
            "comment_owner" to newLoan.commentOwner,
            "comment_loaner" to newLoan.commentLoaner,
            "loan_state" to newLoan.state.toString())
    loan.document(newLoan.id).set(data5)
  }

  fun getItem(id: String, onSuccess: (Item) -> Unit) {
    getItems { items ->
      for (item in items) {
        if (item.id == id) {
          onSuccess(item)
        }
      }
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
          for (document in result) {
            if ((document.data["name"] as String).equals(nameCategory, ignoreCase = true)) {
              onSuccess(document.data["id"] as String)
            }
          }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting idCategory", it) }
  }

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
        )
    users.document(user.id).set(data)
    onSuccess(user)
  }

  /**
   * Retrieve all comments that a user has received, both as a owner than as an loaner
   *
   * @param userId the user's id
   * @param onSuccess the function to return a List containing pairs (comment's author name,
   *   comment)
   */
  fun getComments(userId: String, onSuccess: (List<Pair<String, String>>) -> Unit) {
    val ret = mutableListOf<Pair<String, String>>()

    // Function to add a comment with the author's name to the list
    fun addComment(userId: String, comment: String) {
      var authorName = "" // name of the author of the comment, i.e. the owner
      getUser(userId) { user -> authorName = user.name }

      ret.add(Pair(authorName, comment))
    }

    loan
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {

            if (document.data["loan_state"] as String ==
                LoanState.FINISHED.toString() // only finished loans
            &&
                document.data["id_owner"] as String == userId // if the user is the owner,
                &&
                document.data["review_owner"] as Double != 0.0 // that has been reviewed,
                &&
                document.data["comment_owner"] as String != "") { // and received a comment

              addComment(
                  document.data["id_loaner"] as String, document.data["comment_owner"] as String)
            } else if (document.data["loan_state"] as String ==
                LoanState.FINISHED.toString() // only finished loans
            &&
                document.data["id_loaner"] as String == userId // if the user is the loaner,
                &&
                (document.data["review_loaner"] as String).toDouble() !=
                    0.0 // that has been reviewed
                &&
                document.data["comment_loaner"] as String != "") { // and received a comment

              addComment(
                  document.data["id_owner"] as String, document.data["comment_loaner"] as String)
            }
          }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting loans", it) }

    onSuccess(ret)
  }

  /**
   * Retrieve all ranks that a user has received, both as a owner than as an loaner, compute the
   * average rank, and store it in the user's rank
   *
   * @param idUser the user's id
   */
  fun newAverageRank(idUser: String) {
    loan
        .get()
        .addOnSuccessListener { result ->
          val rankSum = mutableDoubleStateOf(0.0)
          val rankCount = mutableLongStateOf(0)
          for (document in result) {

            if (document.data["loan_state"] as String ==
                LoanState.FINISHED.toString() // only finished loans
            &&
                document.data["id_owner"] as String == idUser // if the user is the owner,
                &&
                (document.data["review_owner"] as String).toDouble() !=
                    0.0) { // that has been reviewed

              rankSum.doubleValue += (document.data["review_owner"] as String).toDouble()
              rankCount.longValue++
            } else if (document.data["loan_state"] as String ==
                LoanState.FINISHED.toString() // only finished loans
            &&
                document.data["id_loaner"] as String == idUser // if the user is the loaner,
                &&
                (document.data["review_loaner"] as String).toDouble() !=
                    0.0) { // that has been reviewed

              rankSum.doubleValue += (document.data["review_loaner"] as String).toDouble()
              rankCount.longValue++
            }
          }

          val averageRank = rankSum.doubleValue / rankCount.longValue
          users.document(idUser).update("rank", averageRank.toString())
        }
        .addOnFailureListener { Log.e(TAG, "Error getting loans", it) }
  }

  /**
   * Set a review for a loan, i.e a rank and an optional comment
   *
   * @param loanId the loan's id
   * @param userId the reviewed user's id
   * @param rank the rank to be set, must be between 0.5 and 5
   * @param comment an optional comment to be set
   * @param onInvalidRank the function to call when the rank is invalid
   */
  fun setReview(
      loanId: String,
      userId: String,
      rank: Double,
      comment: String,
      onInvalidRank: () -> Unit
  ) {
    if (rank < 0.0 || rank > 5) {
      Log.e(TAG, "Error setting review: rank must be between 0.5 and 5")
      onInvalidRank()
    }

    loan
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            if (document.data["loan_state"] as String ==
                LoanState.FINISHED.toString() // only finished loans
            && document.id == loanId && document.data["id_owner"] as String == userId) {

              loan.document(loanId).update("review_owner", rank.toString())
              if (comment != "") loan.document(loanId).update("comment_owner", comment)
            } else if (document.data["loan_state"] as String ==
                LoanState.FINISHED.toString() // only finished loans
            && document.id == loanId && document.data["id_loaner"] as String == userId) {

              loan.document(loanId).update("review_loaner", rank.toString())
              if (comment != "") loan.document(loanId).update("comment_loaner", comment)
            }
          }
        }
        .addOnFailureListener { Log.e(TAG, "Error getting loans", it) }
  }

  companion object {
    private const val TAG = "Database"
  }
}
