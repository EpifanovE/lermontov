package ru.eecode.dir.repository.db.articles

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4(contentEntity = Article::class, tokenizer="unicode61")
@Entity(tableName = "articles_fts")
data class ArticleFts (
    @ColumnInfo( name = "title") val title: String,
    @ColumnInfo( name = "description") val description: String?,
    @ColumnInfo( name = "body") val body: String
)