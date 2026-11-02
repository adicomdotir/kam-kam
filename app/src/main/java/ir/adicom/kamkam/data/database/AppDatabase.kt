package ir.adicom.kamkam.data.database


import androidx.room.Database
import androidx.room.RoomDatabase
import ir.adicom.kamkam.data.dao.CravingLogDao
import ir.adicom.kamkam.data.dao.ReductionPlanDao
import ir.adicom.kamkam.data.dao.SmokeLogDao
import ir.adicom.kamkam.data.dao.TriggerDao
import ir.adicom.kamkam.data.entity.CravingLog
import ir.adicom.kamkam.data.entity.ReductionPlan
import ir.adicom.kamkam.data.entity.SmokeLog
import ir.adicom.kamkam.data.entity.Trigger

@Database(
    entities = [CravingLog::class, ReductionPlan::class, SmokeLog::class, Trigger::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun smokeLogDao(): SmokeLogDao
    abstract fun cravingLogDao(): CravingLogDao
    abstract fun triggerDao(): TriggerDao
    abstract fun reductionPlanDao(): ReductionPlanDao

}
