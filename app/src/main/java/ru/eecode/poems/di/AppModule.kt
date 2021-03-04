package ru.eecode.poems.di

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.eecode.poems.R
import ru.eecode.poems.domain.StoreViewModelConfig
import ru.eecode.poems.repository.ArticleRepository
import ru.eecode.poems.repository.db.articles.ArticleDao
import ru.eecode.poems.repository.db.AppDatabase
import ru.eecode.poems.repository.db.DbCallback
import ru.eecode.poems.repository.db.articles.ArticlesSeeder
import ru.eecode.poems.repository.db.favorites.FavoriteDao
import ru.eecode.poems.ui.observers.BillingClientLifecycle
import ru.eecode.poems.utils.JsonAssetsLoader
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideJsonAssetsLoader(@ApplicationContext context: Context): JsonAssetsLoader = JsonAssetsLoader(context)

    @Provides
    fun provideArticlesSeeder(jsonAssetsLoader: JsonAssetsLoader): ArticlesSeeder = ArticlesSeeder(jsonAssetsLoader)

    @Provides
    fun provideDbCallback(articlesSeeder: ArticlesSeeder) = DbCallback(articlesSeeder)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = AppDatabase.getDatabaseClient(appContext)

    @Singleton
    @Provides
    fun provideArticleDao(db: AppDatabase) = db.articleDao()

    @Singleton
    @Provides
    fun provideFavoriteDao(db: AppDatabase) = db.favoriteDao()

    @Singleton
    @Provides
    fun provideArticleRepository(articleDao: ArticleDao, favoriteDao: FavoriteDao, jsonAssetsLoader: JsonAssetsLoader) =
        ArticleRepository(articleDao, favoriteDao, jsonAssetsLoader)

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(@ApplicationContext appContext: Context): FirebaseAnalytics =
        FirebaseAnalytics.getInstance(appContext)

    @Singleton
    @Provides
    fun provideBillingClientLifecycle(@ApplicationContext appContext: Context): BillingClientLifecycle {
        val skuList = ArrayList<String>()
        skuList.addAll(appContext.resources.getStringArray(R.array.products))
        return BillingClientLifecycle(appContext as Application, skuList)
    }

    @Provides
    fun provideStoreViewModelConfig(@ApplicationContext appContext: Context): StoreViewModelConfig {
        return StoreViewModelConfig(
            appContext.resources.getInteger(R.integer.interstitialNumber),
            appContext.resources.getBoolean(R.bool.isPaidVersion)
        )
    }
}