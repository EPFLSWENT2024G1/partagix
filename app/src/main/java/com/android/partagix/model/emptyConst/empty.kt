package com.android.partagix.model.emptyConst

import android.location.Location
import com.android.partagix.model.category.Category
import com.android.partagix.model.inventory.Inventory
import com.android.partagix.model.item.Item
import com.android.partagix.model.loan.Loan
import com.android.partagix.model.loan.LoanState
import com.android.partagix.model.user.User
import com.android.partagix.model.visibility.Visibility
import java.util.Date

// Fill with default fields
val emptyItem =
    Item(
        "",
        Category("", ""),
        "",
        "",
        Visibility.PUBLIC,
        0,
        Location(""),
    )

val emptyLoan = Loan("", "", "", "", Date(), Date(), "", "", "", "", LoanState.PENDING)

val emptyUser = User("", "", "", "", Inventory("", emptyList()))

val emptyInventory = Inventory("", emptyList())
