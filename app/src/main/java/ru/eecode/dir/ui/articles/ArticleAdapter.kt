package ru.eecode.dir.ui.articles

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.eecode.dir.R
import ru.eecode.dir.repository.db.articles.ArticleListItem
import javax.inject.Inject

class ArticleAdapter @Inject constructor() :
    PagedListAdapter<ArticleListItem, ArticleAdapter.ArticleHolder>(diffCallback) {

    var onItemClickListener: OnItemClickListener? = null

    var onDataChangedListener: OnDataChangedListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: ArticleListItem)
    }

    interface OnDataChangedListener {
        fun onDataChanged()
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
        onDataChangedListener?.onDataChanged()
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
        RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false)) {

        var textViewTitle: TextView = itemView.findViewById(R.id.articleTitle)
        var textViewDescription: TextView = itemView.findViewById(R.id.articleDescription)
        var article: ArticleListItem? = null

        fun bindTo(article: ArticleListItem?) {
            this.article = article
            textViewTitle.text = article?.title

            if (article?.description == null || article.description.isEmpty()) {
                textViewDescription.visibility = View.GONE
            } else {
                textViewDescription.visibility = View.VISIBLE
                textViewDescription.text = HtmlCompat.fromHtml(article.description, HtmlCompat.FROM_HTML_MODE_COMPACT)
            }
        }
    }

}