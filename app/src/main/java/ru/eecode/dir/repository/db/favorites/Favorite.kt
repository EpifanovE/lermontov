package ru.eecode.dir.repository.db.favorites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey() @ColumnInfo(name = "article_id") val articleId: Int
)
