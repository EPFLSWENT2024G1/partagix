package com.android.partagix.model.loan

import java.util.Date

data class Loan(
    val id: String,
    val idLender: String,
    val idBorrower: String,
    val idItem: String,
    val startDate: Date,
    val endDate: Date,
    val reviewLender: String,
    val reviewBorrower: String,
    val commentLender: String,
    val commentBorrower: String,
    val state: LoanState
)
