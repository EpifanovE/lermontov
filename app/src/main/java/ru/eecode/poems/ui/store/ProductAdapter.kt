package ru.eecode.poems.ui.store

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails
import ru.eecode.poems.R

import javax.inject.Inject

class ProductAdapter @Inject constructor() : RecyclerView.Adapter<ProductAdapter.ProductHolder>() {

    private var dataSet = ArrayList<SkuDetails>()

    var onItemClickListener: ProductAdapter.OnItemClickListener? = null

    fun setItems(products: List<SkuDetails>) {
        dataSet.clear()
        dataSet.addAll(products)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: SkuDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_list_item, parent, false)

        return ProductHolder(view)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        val skuDetails: SkuDetails = dataSet[position]
        holder.bindTo(dataSet[position])
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(skuDetails) }
    }

    override fun getItemCount(): Int = dataSet.size

    class ProductHolder constructor(view: View) :
        RecyclerView.ViewHolder(view) {

        private val titleView: TextView = itemView.findViewById(R.id.productTitle)
        private val descriptionView: TextView = itemView.findViewById(R.id.productDescription)
        private val priceView: TextView = itemView.findViewById(R.id.productPrice)

        fun bindTo(skuDetails: SkuDetails) {
            titleView.text = skuDetails.title
            descriptionView.text = skuDetails.description
            priceView.text = skuDetails.price
        }
    }
}