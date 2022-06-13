package com.rk_tech.riggle_runner.ui.main.neworder.product_list

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.response.ProductList
import kotlin.math.roundToInt

class ShopByBrandsProductsAdapter
internal constructor(
    private val mContext: Context,
) : RecyclerView.Adapter<ShopByBrandsProductsAdapter.ViewHolder>() {

    private var listener: ShopByBrandsProductsListener? = null
    private var productList = ArrayList<ProductList>()

    fun setListener(listener: ShopByBrandsProductsListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_home_products, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each cell
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (productList != null && productList.size > 0) {
            val data: ProductList = productList[position]
            if (position == productList.size - 1)
                holder.view.visibility = View.INVISIBLE
            else
                holder.view.visibility = View.VISIBLE

            Glide.with(mContext)
                .load(data?.banner_image?.image)
                .placeholder(R.mipmap.ic_app_launcher)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC))
                .into(holder.ivImg)
            holder.tvName.text = data.name
            /*productList[position].service_hub?.name?.let {
                holder.tvServiceHub.text = "Serviced By : " + it
            }*/
            holder.tvPrice.text =
                String.format(
                    mContext.getString(R.string.rupees_value),
                    data.company_rate.roundToInt()/*data.retailer_price*/
                )

            holder.tvMRP.text =
                String.format(
                    mContext.getString(R.string.rupees_value),
                    data.base_rate?.toFloat().roundToInt()/*data.retailer_price*/
                )

            val strikePrice: Int? = data.base_rate?.toFloat().roundToInt()
            if (strikePrice != null) {
                holder.tvStrikePrice.text =
                    String.format(
                        mContext.getString(R.string.rupees_value),
                        strikePrice/*data.strikePrice*/
                    )
                holder.tvStrikePrice.paintFlags =
                    holder.tvStrikePrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                holder.tvStrikePrice.visibility = View.GONE //VISIBLE
            } else
                holder.tvStrikePrice.visibility = View.GONE

            /*val discount: Int? = data.discount
            if (discount != null) {
                holder.tvOff.text =
                    String.format(mContext.getString(R.string.value_off), data.discount)
                holder.tvOff.visibility = View.VISIBLE
            } else
                holder.tvOff.visibility = View.GONE*/

            data.quantity?.let {
                if (it > 0) {
                    holder.tvAdd.visibility = View.GONE
                    holder.cvQuant.visibility = View.VISIBLE
                    holder.tvQuantSet.text = it.toString()
                } else {
                    holder.llMOQ.visibility = View.VISIBLE
                    //holder.tvMOQ.text = data.moq.toString()
                    //holder.tvCartCount.visibility = View.GONE
                    holder.tvAdd.visibility = View.VISIBLE
                    holder.cvQuant.visibility = View.GONE
                }
            }

            holder.tvMOQ.text = data.retailer_step.toString()
            holder.tvDeliveryIn.text = "Delivery in " + data.delivery_tat_days.toString() + " days"

            try {
                holder.tvAdd.setOnClickListener(View.OnClickListener {
                    if (data.combo_products != null && data.combo_products.size > 0) {
                        listener?.comboClick(position)
                    } else {
                        addUpdateItem(data, holder)
                    }
                })

                holder.ivPlus.setOnClickListener {
                    if (data.combo_products != null && data.combo_products.size > 0) {
                        listener?.comboClick(position)
                    } else {
                        if (holder.tvQuantSet.text.toString().toInt() % data.retailer_step == 0) {
                            var quantity =
                                holder.tvQuantSet.text.toString().toInt() + (1 * data.retailer_step)
                            //     productsData[holder.adapterPosition].item_cart = holder.tvQuantSet.text.toString().toInt() + (1 * data.moq)
                            //holder.tvQuantSet.text = quantity.toString()
                            productList[position].quantity = quantity
                            notifyItemChanged(position)
                            listener?.singleVariantUpdate(holder.ivPlus,data.id, quantity)
                        } else {
                            listener?.comboClick(position)
                        }
                    }
                }

                holder.ivMinus.setOnClickListener {
                    if (data.combo_products != null && data.combo_products.size > 0) {
                        listener?.comboClick(position)
                    } else {
                        if (holder.tvQuantSet.text.toString().toInt() % data.retailer_step == 0) {
                            if (holder.tvQuantSet.text.toString().toInt() > 0) {
                                var quantity =
                                    holder.tvQuantSet.text.toString().toInt() - (1 * data.retailer_step)
                                if (quantity <= 0) {
                                    //holder.tvQuantSet.text = (0).toString()
                                    //listener?.singleVariantUpdate(holder.ivMinus,data.id, 0)
                                    productList[position].quantity = 0
                                    notifyItemChanged(position)
                                    holder.tvAdd.visibility = View.VISIBLE
                                    holder.cvQuant.visibility = View.GONE
                                } else {
                                    //holder.tvQuantSet.text = (quantity).toString()
                                    //listener?.singleVariantUpdate(holder.ivMinus,data.id, quantity)
                                    productList[position].quantity = quantity
                                    notifyItemChanged(position)
                                }
                            }
                        } else {
                            listener?.comboClick(position)
                        }
                    }
                }

                /*holder.rlProduct.setOnClickListener {
                    ProductListActivity.selected_pos = position
                    ProductDetailPage.start(mContext, data.id)
                }*/
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder.tvCartCount.visibility = View.GONE
            data.schemes?.let {
                if (it.size > 0) {
                    holder.tvCartCount.visibility = View.VISIBLE
                    holder.tvCartCount.text =
                        "Bulk Offer upto ₹" + String.format(
                            "%.2f",
                            it[it.size - 1].rate
                        ) + "/pc"

                    holder.tvPrice.text =
                        "₹" + String.format(
                            "%.2f",
                            it[0].rate
                        ) + ""

                    val profit: Double? = data.base_rate?.toFloat() - it[0].rate
                    if (profit != null) {
                        holder.tvProfit.text =
                            String.format(mContext.getString(R.string.rupees_value_profits), profit)
                        holder.tvProfit.visibility = View.VISIBLE
                    } else {
                        holder.tvProfit.visibility = View.GONE
                    }

                }
            }

            if (data.combo_products != null && data.combo_products.size > 0) {
                holder.tvCombo.visibility = View.VISIBLE
            } else {
                holder.tvCombo.visibility = View.GONE
            }

        }
        holder.tvCartCount.setOnClickListener {
            listener?.singleVariantUpdate(it, productList[position].id, position)
        }

    }

    private fun addUpdateItem(data: ProductList, holder: ViewHolder) {
        /*  if (data.is_variant_key!!)
              listener?.itemClicked(data.siblingID)
          else {*/
        holder.cvQuant.visibility = View.VISIBLE
        //holder.llMOQ.visibility = View.INVISIBLE
        holder.tvAdd.visibility = View.GONE
        holder.tvQuantSet.text = "0"
        holder.ivPlus.performClick()
        //}
    }

    // total number of cells
    override fun getItemCount(): Int {
        return productList.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var view: View = itemView.findViewById(R.id.view)
        var ivImg: AppCompatImageView = itemView.findViewById(R.id.ivImg)

        //var tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        var tvName: TextView = itemView.findViewById(R.id.tvName)
        var tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        var tvStrikePrice: TextView = itemView.findViewById(R.id.tvStrikePrice)
        var tvProfit: TextView = itemView.findViewById(R.id.tvProfit)
        var tvMRP: TextView = itemView.findViewById(R.id.tvMRP)
        var tvOff: TextView = itemView.findViewById(R.id.tvOff)
        var tvMOQ: TextView = itemView.findViewById(R.id.tvMOQ)
        var tvAdd: TextView = itemView.findViewById(R.id.tvAdd)
        var tvCartCount: TextView = itemView.findViewById(R.id.tvCartCount)
        var llMOQ: LinearLayout = itemView.findViewById(R.id.llMOQ)
        var cvQuant: CardView = itemView.findViewById(R.id.cvQuant)
        var tvQuantSet: TextView = itemView.findViewById(R.id.tvQuantSet)
        var ivMinus: AppCompatImageView = itemView.findViewById(R.id.ivMinus)
        var ivPlus: AppCompatImageView = itemView.findViewById(R.id.ivPlus)
        var rlProduct: RelativeLayout = itemView.findViewById(R.id.rlProduct)
        var tvCombo: TextView = itemView.findViewById(R.id.tvCombo)
        var tvServiceHub: TextView = itemView.findViewById(R.id.tvServiceHub)
        var tvDeliveryIn: TextView = itemView.findViewById(R.id.tvDeliveryIn)
    }

    interface ShopByBrandsProductsListener {
        fun itemClicked(product_id: Int)
        fun singleVariantUpdate(view: View, id: Int, position: Int)
        fun comboClick(product_id: Int) {

        }
    }

    fun setList(productList: ArrayList<ProductList>) {
        this.productList = productList
        notifyDataSetChanged()
    }

    fun updateCart(pos: Int, product: ProductList) {
        productList[pos] = product
        notifyItemChanged(pos)
    }

    fun getList(): ArrayList<ProductList> {
        return productList
    }

}