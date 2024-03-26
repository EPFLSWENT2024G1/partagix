import java.util.Date

data class Loan (
    val idOwner: Long,
    val idLoaner: Long,
    val idItem: Long,
    val startDate: Date,
    val endDate: Date,
    val reviewOwner: String,
    val reviewLoaner: String,
    val commentOwner: String,
    val commentLoaner: String,
)