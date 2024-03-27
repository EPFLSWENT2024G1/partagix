package com.android.partagix.model

import Category
import Inventory
import Item
import Loan
import User
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore
import java.util.Date

private var newId: Long = 0

class Database {

  private val db = Firebase.firestore
  private val users = db.collection("users")
  private val items = db.collection("items")
  private val inventory = db.collection("inventories")
  private val loan = db.collection("loan")
  private val categories = db.collection("categories")

  init {
    //createExampleForDb()
  }

  fun getUsers(onSuccess: (List<User>) -> Unit) {
    users
        .get()
        .addOnSuccessListener { result ->
          val ret = mutableListOf<User>()
          for (document in result) {
            val user =
                User(
                    document.data["id"] as String,
                    document.data["name"] as String,
                    document.data["addr"] as String,
                    document.data["rank"] as String,
                )
            ret.add(user)
          }
          onSuccess(ret)
        }
        .addOnFailureListener() { println("----- error $it") }
  }

  fun getItems(onSuccess: (List<Item>) -> Unit) {
    items
        .get()
        .addOnSuccessListener { result ->
          val ret = mutableListOf<Item>()
          for (document in result) {
            val item = Item(document.data["id"] as String, document.data["id_category"] as String)
            ret.add(item)
          }
          onSuccess(ret)
        }
        .addOnFailureListener() { println("----- error $it") }
  }

  fun getInventories(onSuccess: (List<Inventory>) -> Unit) {
    inventory
        .get()
        .addOnSuccessListener { result ->
          val ret = mutableListOf<Inventory>()
          for (document in result) {
            val inventory =
                Inventory(document.data["id_user"] as String, document.data["id_item"] as String)
            ret.add(inventory)
          }
          onSuccess(ret)
        }
        .addOnFailureListener() { println("----- error $it") }
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
                )
            ret.add(loan)
          }
          onSuccess(ret)
        }
        .addOnFailureListener() { println("----- error $it") }
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
        .addOnFailureListener() { println("----- error $it") }
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

    val data3 = hashMapOf("id" to idItem, "id_category" to idCategory)
    items.document("$idItem").set(data3)

    val idInventory = getNewUid(inventory)
    val data4 = hashMapOf("id_user" to idUser, "id_item" to idItem)
    inventory.document(idInventory).set(data4)

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
        )
    loan.document(idLoan).set(data5)
  }
}
