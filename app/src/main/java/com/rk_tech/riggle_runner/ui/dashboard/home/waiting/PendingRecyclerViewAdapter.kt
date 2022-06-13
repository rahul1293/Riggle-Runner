package com.rk_tech.riggle_runner.ui.dashboard.home.waiting

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.response.PendingOrdersBean
import com.rk_tech.riggle_runner.databinding.HolderPendingBinding
import com.zerobranch.layout.SwipeLayout
import java.util.ArrayList

class PendingRecyclerViewAdapter(val mcallback: ProductCallback) :
    RecyclerView.Adapter<PendingRecyclerViewAdapter.RequestHolder>() {
    private var moreBeans: List<PendingOrdersBean> = ArrayList()
    open var swipeLayout: SwipeLayout? = null

    private var loading = false

    interface ProductCallback {
        fun onItemClick(v: View?, m: PendingOrdersBean?, pos: Int?)
    }

    class RequestHolder(val layoutTodoListBinding: HolderPendingBinding) :
        RecyclerView.ViewHolder(layoutTodoListBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestHolder {
        val layoutTodoListBinding: HolderPendingBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.holder_pending, parent, false
        )
        layoutTodoListBinding.setVariable(BR.callback, mcallback)
        return RequestHolder(layoutTodoListBinding)
    }

    override fun onBindViewHolder(
        holder: RequestHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        if (!loading && position < moreBeans.size) {
            holder.layoutTodoListBinding.setVariable(BR.pos, position)
            holder.layoutTodoListBinding.setVariable(BR.bean, moreBeans.get(position))
            holder.layoutTodoListBinding.ivCancel.setOnClickListener {
                holder.layoutTodoListBinding.clReject.visibility = View.VISIBLE
                holder.layoutTodoListBinding.clReject.animate().translationX(0f)
                    .setDuration(100).setInterpolator(AccelerateDecelerateInterpolator())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            Handler(Looper.getMainLooper()).postDelayed({
                                mcallback.onItemClick(it, moreBeans.get(position), position)
                                holder.layoutTodoListBinding.clReject.visibility = View.GONE
                                notifyItemRemoved(position)
                            }, 200)
                        }
                    })
            }
        } else {
            holder.layoutTodoListBinding.setVariable(BR.pos, position)
            holder.layoutTodoListBinding.setVariable(BR.bean, null)
        }

        holder.layoutTodoListBinding.swipeLayout.setOnActionsListener(object :
            SwipeLayout.SwipeActionsListener {
            override fun onOpen(direction: Int, isContinuous: Boolean) {
                if (direction == SwipeLayout.LEFT) {
                    if (swipeLayout != null && swipeLayout !== holder.layoutTodoListBinding.swipeLayout) {
                        swipeLayout?.close(true)
                    }
                    swipeLayout = holder.layoutTodoListBinding.swipeLayout
                }
            }

            override fun onClose() {
                // the main view has returned to the default state
                if (swipeLayout === holder.layoutTodoListBinding.swipeLayout) swipeLayout = null
            }
        })

    }

    override fun getItemCount(): Int {
        return if (loading) 10 else {
            if (moreBeans == null)
                return 0
            else
                return moreBeans.size
        }
    }

    fun setList(moreBeans: List<PendingOrdersBean>) {
        this.moreBeans = moreBeans
        notifyDataSetChanged()
    }

    fun clear() {
        moreBeans.isEmpty()
        notifyDataSetChanged()
    }

    fun getList(): List<PendingOrdersBean> {
        return moreBeans
    }

    fun showShimmerLoading(frameLayout: ShimmerFrameLayout) {
        if (!loading) {
            loading = true
            notifyDataSetChanged()
            frameLayout.startShimmer()
        }
    }

    fun hideShimmerLoading(frameLayout: ShimmerFrameLayout) {
        if (loading) {
            loading = false
            notifyDataSetChanged()
            frameLayout.stopShimmer()
        }
    }

}