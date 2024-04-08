package com.android.partagix.model

import android.location.Location
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import java.util.Date

class Database {

  private val db = Firebase.firestore
  private val users = db.collection("users")
  private val items = db.collection("items")
  private val inventory = db.collection("inventories")
  private val loan = db.collection("loan")
  private val categories = db.collection("categories")
  private val item_loan = db.collection("item_loan")

  init {
    // createExampleForDb()
  }

  fun getUser(idUser: String, onSuccess: (User) -> Unit) {
    users
        .get()
        .addOnSuccessListener { result ->
          for (document in result) {
            if (document.data["id"] as String == idUser) {

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
        }
        .addOnFailureListener { println("----- error $it") }
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
                  val item =
                      Item(
                          document.data["id"] as String,
                          categories[document.data["id_category"] as String]!!,
                          document.data["name"] as String,
                          document.data["description"] as String,
                          document.data["author"] as String,
                          document.data["visibility"] as Int,
                          document.data["quantity"] as Int,
                          document.data["location"] as Location,
                      )
                  ret.add(item)
                }
                onSuccess(ret)
              }
              .addOnFailureListener { println("----- error $it") }
        }
        .addOnFailureListener { println("----- error $it") }
  }

  fun getUserInventory(userId: String, onSuccess: (Inventory) -> Unit) {
    inventory
        .get()
        .addOnSuccessListener { result ->
          val inventoryMap =
              result.groupBy(
                  { it.data["id_user"] as String }, // Key selector function
                  { it.data["id_item"] as String } // Value selector function
                  )
          val listIdItem = inventoryMap[userId] ?: emptyList()
          val listItems = mutableListOf<Item>()
          getItems { items ->
            for (item in items) {
              if (listIdItem.contains(item.id)) {
                listItems.add(item)
              }
            }
            onSuccess(Inventory(userId, listItems))
          }
        }
        .addOnFailureListener { println("----- error $it") }
  }

  fun getLoans(onSuccess: (List<Loan>) -> Unit) {
    loan
        .get()
        .addOnSuccessListener { result ->
          val ret = mutableListOf<Loan>()
          for (document in result) {
            val loan =
                Loan(
                    document.data["id_owner"] as String,
                    document.data["id_loaner"] as String,
                    document.data["id_item"] as String,
                    document.data["start_date"] as Date,
                    document.data["end_date"] as Date,
                    document.data["review_owner"] as String,
                    document.data["review_loaner"] as String,
                    document.data["comment_owner"] as String,
                    document.data["comment_loaner"] as String,
                    LoanState.FINISHED)
            ret.add(loan)
          }
          onSuccess(ret)
        }
        .addOnFailureListener { println("----- error $it") }
  }

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
        .addOnFailureListener { println("----- error $it") }
  }

  private fun getNewUid(collection: CollectionReference): String {
    val uidDocument = collection.document()
    return uidDocument.id
  }

  private fun createExampleForDb(
      users: CollectionReference = this.users,
      items: CollectionReference = this.items,
      inventory: CollectionReference = this.inventory,
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
    users.document("$idUser").set(data1)

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
            "author" to "author",
            "visibility" to 0,
            "quantity" to 1,
            "location" to Location(""),
        )
    items.document("$idItem").set(data3)

    val idInventory = getNewUid(inventory)
    val data4 = hashMapOf("id_user" to idUser, "id_item" to idItem)
    inventory.document(idInventory).set(data4)

    val idLoan = getNewUid(loan)
    val data5 =
        hashMapOf(
            "id_owner" to idUser,
            "id_loaner" to idUser,
            "start_date" to Date(),
            "end_date" to Date(),
            "review_owner" to "Review",
            "review_loaner" to "Review",
            "comment_owner" to "Comment",
            "comment_loaner" to "Comment",
            "state" to LoanState.FINISHED.toString())
    loan.document(idLoan).set(data5)

    val idItemLoan = getNewUid(item_loan)
    val data6 = hashMapOf("id_item" to idItem, "id_loan" to idLoan)
    item_loan.document(idItemLoan).set(data6)
  }

  fun createItem(userId: String, newItem: Item) {

    val idItem = getNewUid(items)
    val data3 =
        hashMapOf(
            "id" to idItem,
            "id_category" to newItem.category.id,
            "name" to newItem.name,
            "description" to newItem.description,
            "author" to newItem.author,
            "visibility" to newItem.visibility,
            "quantity" to newItem.quantity,
            "location" to newItem.location,
        )
    items.document(idItem).set(data3)

    val data4 = hashMapOf("id_user" to userId, "id_item" to idItem)
    this.inventory.document(userId).set(data4)
  }

  fun setItem(newItem: Item) {
    val data3 =
        hashMapOf(
            "id" to newItem.id,
            "id_category" to newItem.category.id,
            "name" to newItem.name,
            "description" to newItem.description,
            "author" to newItem.author,
            "visibility" to newItem.visibility,
            "quantity" to newItem.quantity,
            "location" to newItem.location,
        )
    items.document(newItem.id).set(data3)
  }
}
