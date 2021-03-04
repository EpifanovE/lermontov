package ru.eecode.poems.ui.store

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.eecode.poems.R
import ru.eecode.poems.domain.store.StoreProduct

import javax.inject.Inject

class ProductAdapter @Inject constructor() : RecyclerView.Adapter<ProductAdapter.ProductHolder>() {

    private var dataSet = ArrayList<StoreProduct>()

    var onItemClickListener: ProductAdapter.OnItemClickListener? = null

    fun setItems(products: List<StoreProduct>) {
        dataSet.clear()
        dataSet.addAll(products)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: StoreProduct)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list_item, parent, false)

        return ProductHolder(view)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val product: StoreProduct = dataSet[position]
        holder.bindTo(dataSet[position])
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(product) }
    }

    override fun getItemCount(): Int = dataSet.size

    class ProductHolder constructor(view: View) :
        RecyclerView.ViewHolder(view) {

        private val titleView: TextView = itemView.findViewById(R.id.productTitle)
        private val descriptionView: TextView = itemView.findViewById(R.id.productDescription)
        private val priceView: TextView = itemView.findViewById(R.id.productPrice)
        private val imageView: ImageView = itemView.findViewById(R.id.productImage)

        fun bindTo(product: StoreProduct) {
            titleView.text = product.title ?: product.skuDetails.title
            descriptionView.text = product.skuDetails.description
            priceView.text = product.skuDetails.price

            if (product.image == null) {
                imageView.visibility = View.GONE
            }

            product.image?.let {
                imageView.setImageDrawable(it)
            }
        }
    }
}