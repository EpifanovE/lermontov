package ru.eecode.dir.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.eecode.dir.repository.ArticleRepository
import ru.eecode.dir.repository.db.articles.ArticleDao
import ru.eecode.dir.repository.db.AppDatabase
import ru.eecode.dir.repository.db.DbCallback
import ru.eecode.dir.repository.db.articles.ArticlesSeeder
import ru.eecode.dir.repository.db.favorites.FavoriteDao
import ru.eecode.dir.utils.JsonAssetsLoader
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
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

}