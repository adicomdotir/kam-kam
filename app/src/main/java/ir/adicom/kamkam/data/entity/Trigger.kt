package ir.adicom.kamkam.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "triggers")
data class Trigger(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String? = null,
    val defaultKey: String? = null,
    val isArchived: Boolean = false,
) {
    val isDefault: Boolean get() = defaultKey != null
}