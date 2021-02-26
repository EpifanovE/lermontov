package ru.eecode.lermontov.repository.db.articles

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article (
    @PrimaryKey(autoGenerate = true) @ColumnInfo( name = "id") val id: Int,
    @ColumnInfo( name = "title") val title: String,
    @ColumnInfo( name = "excerpt") val excerpt: String?,
    @ColumnInfo( name = "description") val description: String?,
    @ColumnInfo( name = "body") val body: String
)