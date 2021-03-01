package ru.eecode.poems.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.eecode.poems.repository.ArticleRepository
import ru.eecode.poems.repository.db.articles.ArticleDao
import ru.eecode.poems.repository.db.AppDatabase
import ru.eecode.poems.repository.db.DbCallback
import ru.eecode.poems.repository.db.articles.ArticlesSeeder
import ru.eecode.poems.repository.db.favorites.FavoriteDao
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
    fun provideArticleRepository(articleDao: ArticleDao, favoriteDao: FavoriteDao, jsonAssetsLoader: JsonAssetsLoader) = ArticleRepository(articleDao, favoriteDao, jsonAssetsLoader)

    @Singleton
    @Provides
    fun provideFirebaseAnalytics(@ApplicationContext appContext: Context) : FirebaseAnalytics = FirebaseAnalytics.getInstance(appContext)
}