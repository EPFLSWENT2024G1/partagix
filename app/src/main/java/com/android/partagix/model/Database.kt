package com.android.partagix.model

import android.location.Location
import android.util.Log
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
            try {
              val startDate: Timestamp = document.data["start_date"] as Timestamp
              val endDate: Timestamp = document.data["end_date"] as Timestamp
              val loanState: LoanState = LoanState.valueOf(document.data["loan_state"] as String)

              val loan =
                  Loan(
                      document.id,
                      document.data["id_owner"] as String,
                      document.data["id_loaner"] as String,
                      document.data["id_item"] as String,
                      startDate.toDate(),
                      endDate.toDate(),
                      document.data["review_owner"] as String,
                      document.data["review_loaner"] as String,
                      document.data["comment_owner"] as String,
                      document.data["comment_loaner"] as String,
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

    getLoans { loans ->
      for (loan in loans) {

        if (loan.state == LoanState.FINISHED // only finished loans
        &&
            loan.idOwner == userId // if the user is the owner,
            &&
            loan.reviewOwner.toDouble() != 0.0 // that has been reviewed,
            &&
            loan.commentOwner != "") { // and received a comment

          getUser(loan.idLoaner, onNoUser = {}) { user ->
            ret.add(Pair(user.name, loan.commentOwner))
          }
        } else if (loan.state == LoanState.FINISHED // only finished loans
        &&
            loan.idLoaner == userId // if the user is the loaner,
            &&
            loan.reviewLoaner.toDouble() != 0.0 // that has been reviewed
            &&
            loan.commentLoaner != "") { // and received a comment

          getUser(loan.idOwner, onNoUser = {}) { user ->
            ret.add(Pair(user.name, loan.commentLoaner))
          }
        }
      }
    }

    onSuccess(ret)
  }

  /**
   * Retrieve all ranks that a user has received, both as a owner than as an loaner, compute the
   * average rank, and store it in the user's rank
   *
   * @param idUser the user's id
   */
  fun newAverageRank(idUser: String) {

    var rankSum = 0.0
    var rankCount = 0L

    getLoans { loans ->
      for (loan in loans) {

        if (loan.state == LoanState.FINISHED // only finished loans
        &&
            loan.idOwner == idUser // if the user is the owner,
            &&
            loan.reviewOwner.toDouble() != 0.0) { // that has been reviewed

          rankSum += loan.reviewOwner.toDouble()
          rankCount++
        } else if (loan.state == LoanState.FINISHED // only finished loans
        &&
            loan.idLoaner == idUser // if the user is the loaner,
            &&
            loan.reviewLoaner.toDouble() != 0.0) { // that has been reviewed

          rankSum += loan.reviewLoaner.toDouble()
          rankCount++
        }
      }
      val averageRank = rankSum / rankCount.toDouble()
      users.document(idUser).update("rank", averageRank.toString())
    }
  }

  /**
   * Set a review for a loan, i.e a rank and an optional comment
   *
   * @param loanId the loan's id
   * @param userId the reviewed user's id
   * @param rank the rank to be set, must be between 0.5 and 5
   * @param comment an optional comment to be set
   */
  fun setReview(
      loanId: String,
      userId: String,
      rank: Double,
      comment: String,
  ) {
    getLoans { loans ->
      for (loan in loans) {

        if (loan.state == LoanState.FINISHED // only finished loans
        && loan.id == loanId && loan.idOwner == userId) {

          this.loan.document(loanId).update("review_owner", rank.toString())
          if (comment != "") this.loan.document(loanId).update("comment_owner", comment)
        } else if (loan.state == LoanState.FINISHED // only finished loans
        && loan.id == loanId && loan.idLoaner == userId) {

          this.loan.document(loanId).update("review_loaner", rank.toString())
          if (comment != "") this.loan.document(loanId).update("comment_loaner", comment)
        }
      }
    }
  }

  companion object {
    private const val TAG = "Database"
  }
}
