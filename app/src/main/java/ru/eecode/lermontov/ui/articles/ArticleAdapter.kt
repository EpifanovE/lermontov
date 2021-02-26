package ru.eecode.lermontov.ui.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import ru.eecode.lermontov.R
import ru.eecode.lermontov.repository.db.articles.ArticleListItem
import javax.inject.Inject

class ArticleAdapter @Inject constructor() :
    PagedListAdapter<ArticleListItem, ArticleAdapter.ArticleHolder>(diffCallback) {

    var onItemClickListener: OnItemClickListener? = null

    var onDataChangedListener: OnDataChangedListener? = null

    var onFavoriteClickListener: OnFavoriteClickListener? = null

    /**
     * To prevent scroll a recycler view to 0 position, when an item added/removed from favorites
     */
    var favoriteButtonPressed: Boolean = false

    interface OnItemClickListener {
        fun onItemClick(item: ArticleListItem)
    }

    interface OnDataChangedListener {
        fun onDataChanged()
    }

    interface OnFavoriteClickListener {
        fun onClick(articleId: Int, isFavorite: Boolean)
    }

    override fun onBindViewHolder(holder: ArticleHolder, position: Int) {

        val article: ArticleListItem = getItem(position) ?: return

        holder.bindTo(article)

        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(article) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleHolder =
        ArticleHolder(parent)

    override fun onCurrentListChanged(
        previousList: PagedList<ArticleListItem>?,
        currentList: PagedList<ArticleListItem>?
    ) {
        super.onCurrentListChanged(previousList, currentList)

        if (!favoriteButtonPressed) {
            onDataChangedListener?.onDataChanged()
        }
        favoriteButtonPressed = false
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ArticleListItem>() {
            override fun areItemsTheSame(oldItem: ArticleListItem, newItem: ArticleListItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ArticleListItem, newItem: ArticleListItem): Boolean =
                oldItem == newItem
        }
    }

    inner class ArticleHolder constructor(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.article_index_item, parent, false)) {

        var textViewTitle: TextView = itemView.findViewById(R.id.articleTitle)
        var textViewExcerpt: TextView = itemView.findViewById(R.id.articleExcerpt)
        var favoriteIcon: MaterialButton = itemView.findViewById(R.id.favoriteIcon);
        var article: ArticleListItem? = null

        fun bindTo(article: ArticleListItem?) {
            this.article = article
            textViewTitle.text = article?.title

            if (article?.excerpt == null || article.excerpt.isEmpty()) {
                textViewExcerpt.visibility = View.GONE
            } else {
                textViewExcerpt.visibility = View.VISIBLE
                textViewExcerpt.text = HtmlCompat.fromHtml(article.excerpt, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }

            if (article != null && article.isFavorite) {
                favoriteIcon.setIconTintResource(R.color.favoriteButtonActive)
            } else {
                favoriteIcon.setIconTintResource(R.color.favoriteButtonNonActive)
            }

            if (article != null) {
                favoriteIcon.setOnClickListener {
                    onFavoriteClickListener?.onClick(article.id, article.isFavorite)
                    favoriteButtonPressed = true
                }
            }
        }
    }

}