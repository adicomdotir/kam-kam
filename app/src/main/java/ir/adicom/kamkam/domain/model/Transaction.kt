package ir.adicom.kamkam.domain.model

data class Transaction(
    val id: Long,
    val title: String,
    val category: String,
    val amount: Double,
    val type: TransactionType,
    val timestamp: Long
)