package pl.warszawianin.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.warszawianin.data.local.AppDatabase
import pl.warszawianin.data.local.ReportDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "warszawianin_db"
        ).build()
    }

    @Provides
    fun provideReportDao(database: AppDatabase): ReportDao {
        return database.reportDao()
    }
}
