package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ArameshDao
import com.example.data.local.entity.BadgeEntity
import com.example.data.local.entity.BreathingEntity
import com.example.data.local.entity.CheckInEntity
import com.example.data.local.entity.EsteemItemEntity
import com.example.data.local.entity.EsteemLogEntity
import com.example.data.local.entity.EsteemWinEntity
import com.example.data.local.entity.GratitudeEntity
import com.example.data.local.entity.JournalEntity
import com.example.data.local.entity.LetterEntity
import com.example.data.local.entity.MeditationEntity
import com.example.data.local.entity.MindfulnessEntity
import com.example.data.local.entity.SelfKnowItemEntity
import com.example.data.local.entity.SelfKnowQuestionEntity
import com.example.data.local.entity.SelfLoveEntity
import com.example.data.local.entity.ServerMediaEntity

@Database(
    entities = [
        GratitudeEntity::class,
        CheckInEntity::class,
        BreathingEntity::class,
        MeditationEntity::class,
        MindfulnessEntity::class,
        LetterEntity::class,
        SelfLoveEntity::class,
        SelfKnowQuestionEntity::class,
        SelfKnowItemEntity::class,
        EsteemWinEntity::class,
        EsteemItemEntity::class,
        EsteemLogEntity::class,
        JournalEntity::class,
        BadgeEntity::class,
        ServerMediaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ArameshDatabase : RoomDatabase() {
    abstract fun arameshDao(): ArameshDao

    companion object {
        @Volatile
        private var INSTANCE: ArameshDatabase? = null

        fun getInstance(context: Context): ArameshDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArameshDatabase::class.java,
                    "aramesh_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
