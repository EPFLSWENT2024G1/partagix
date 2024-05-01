package com.android.partagix.model.loan

import java.util.Date

data class Loan(
    val id: String,
    val idOwner: String,
    val idLoaner: String,
    val idItem: String,
    val startDate: Date,
    val endDate: Date,
    val reviewOwner: String,
    val reviewLoaner: String,
    val commentOwner: String,
    val commentLoaner: String,
    val state: LoanState
)
