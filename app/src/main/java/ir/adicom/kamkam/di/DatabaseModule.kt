package ir.adicom.kamkam.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.adicom.kamkam.data.dao.CravingLogDao
import ir.adicom.kamkam.data.dao.ReductionPlanDao
import ir.adicom.kamkam.data.dao.SmokeLogDao
import ir.adicom.kamkam.data.dao.TriggerDao
import ir.adicom.kamkam.data.database.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "kamkam.db"
        ).addCallback(SeedCallback)
            .build()
    }

    @Provides
    @Singleton
    fun provideSmokeLogDao(database: AppDatabase): SmokeLogDao =
        database.smokeLogDao()

    @Provides
    @Singleton
    fun provideCravingLogDao(database: AppDatabase): CravingLogDao =
        database.cravingLogDao()

    @Provides
    @Singleton
    fun provideTriggerDao(database: AppDatabase): TriggerDao =
        database.triggerDao()

    @Provides
    @Singleton
    fun provideReductionPlanDao(database: AppDatabase): ReductionPlanDao =
        database.reductionPlanDao()
}

private object SeedCallback : RoomDatabase.Callback() {
    private val defaultKeys = listOf(
        "stress", "coffee", "after_meal", "social", "boredom", "driving", "waking_up",
    )

    override fun onCreate(db: SupportSQLiteDatabase) {
        defaultKeys.forEach { key ->
            db.execSQL(
                "INSERT INTO triggers (name, defaultKey, isArchived) VALUES (NULL, '$key', 0)"
            )
        }
    }
}