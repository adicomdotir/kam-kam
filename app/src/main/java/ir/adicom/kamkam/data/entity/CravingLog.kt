package ir.adicom.kamkam.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "craving_logs",
    foreignKeys = [
        ForeignKey(
            entity = Trigger::class,
            parentColumns = ["id"],
            childColumns = ["triggerId"],
            onDelete = ForeignKey.Companion.SET_NULL,
        )
    ],
    indices = [Index("timestamp"), Index("triggerId")],
)
data class CravingLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val resisted: Boolean,
    val triggerId: Long? = null,
    val durationSeconds: Int? = null,
    val deletedAt: Long? = null,
)