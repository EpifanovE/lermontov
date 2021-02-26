package ru.eecode.lermontov.repository.db.articles

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleListItem (
    @PrimaryKey(autoGenerate = true) @ColumnInfo( name = "id") val id: Int,
    @ColumnInfo( name = "title") val title: String,
    @ColumnInfo( name = "excerpt") val excerpt: String?,
    @ColumnInfo( name = "description") val description: String?,
    @ColumnInfo( name = "rank") val rank: Int,
    @ColumnInfo( name = "is_favorite") val isFavorite: Boolean
)