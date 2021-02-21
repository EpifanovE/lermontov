package ru.eecode.dir.repository.db.articles

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity

@Entity(tableName = "articles")
data class ArticleWithMatchinfo (
    @Embedded
    val article: ArticleListItem,
    @ColumnInfo(name = "matchinfo")
    val matchInfo: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArticleWithMatchinfo

        if (article != other.article) return false
        if (!matchInfo.contentEquals(other.matchInfo)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = article.hashCode()
        result = 31 * result + matchInfo.contentHashCode()
        return result
    }
}
