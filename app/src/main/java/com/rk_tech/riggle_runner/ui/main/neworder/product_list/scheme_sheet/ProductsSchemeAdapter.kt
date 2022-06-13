package com.rk_tech.riggle_runner.ui.main.neworder.product_list.scheme_sheet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.response.Schemes
import java.util.ArrayList

class ProductsSchemeAdapter // data is passed into the constructor
internal constructor(
    private val mContext: Context,
    private val productsData: ArrayList<Schemes>,
    private val product_name: String
) : RecyclerView.Adapter<ProductsSchemeAdapter.ViewHolder>() {

    private var listener: HomeProductsListener? = null

    fun setListener(listener: HomeProductsListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.list_scheme_products, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each cell
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvQuantity.text =
            productsData[position].min_quantity.toString() + " - " + productsData[position].max_quantity
        holder.tvRate.text = "₹ " + String.format("%.2f", productsData[position].rate) + "/pc"
        holder.llQuantProfit.setOnClickListener {
            listener?.variantAdded(
                position,
                productsData.size
            )
        }
        if (productsData[position].free_product != null) {
            holder.tvFreeProductName.visibility = View.VISIBLE
            if (productsData[position].free_quantity != null) {
                holder.tvFreeProductName.text = /*productsData[position].free_product.toString() +*/
                    productsData[position].free_quantity.toString() + " Free," + product_name
            } else {
                holder.tvFreeProductName.text = "Free," + product_name
            }
        } else {
            holder.tvFreeProductName.visibility = View.GONE
        }
    }

    // total number of cells
    override fun getItemCount(): Int {
        return productsData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        var tvRate: TextView = itemView.findViewById(R.id.tvRate)
        var tvFreeProductName: TextView = itemView.findViewById(R.id.tvFreeProductName)
        var llQuantProfit: LinearLayout = itemView.findViewById(R.id.llQuantProfit)
    }

    interface HomeProductsListener {
        fun variantAdded(product_id: Int, quantity: Int)
    }

}