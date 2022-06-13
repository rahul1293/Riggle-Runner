package com.rk_tech.riggle_runner.ui.base.adapter;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rk_tech.riggle_runner.BR

open class RVAdapter<M, B : ViewDataBinding>(
    @field:LayoutRes @param:LayoutRes private val layoutResId: Int,
    private val modelVariableId: Int,
    private val callback: Callback<M>
) : RecyclerView.Adapter<RVAdapter.Holder<B>>() {
    private val dataList = ObservableArrayList<M>()

    interface Callback<M> {
        fun onItemClick(v: View, m: M)
        fun onItemClick(v: View, m: M, position: Int) {
        }
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    fun setDataList(newList: List<M>?) {
        dataList.clear()
        newList?.let { dataList.addAll(it) }
        notifyDataSetChanged()
    }

    fun addDataList(newDataList: List<M>?) {
        val positionStart = dataList.size
        newDataList?.let {
            val itemCount = newDataList.size
            dataList.addAll(newDataList)
            notifyItemRangeInserted(positionStart, itemCount)
        }
    }

    fun getDataList(): ObservableArrayList<M> {
        return dataList
    }

    fun getData(index: Int): M? {
        if (index < dataList.size)
            return dataList[index]
        return null
    }

    fun clearList() {
        dataList.clear()
        notifyDataSetChanged()
    }

    fun addData(data: M) {
        val positionStart = dataList.size
        dataList.add(data)
        notifyItemInserted(positionStart)
    }


    class Holder<S : ViewDataBinding>(var binding: S) : RecyclerView.ViewHolder(
        binding.root
    ) {

    }

    override fun onBindViewHolder(holder: Holder<B>, position: Int) {
        onBind(holder.binding, dataList[position], position)
        holder.binding.executePendingBindings()
    }

    open fun onBind(binding: B, bean: M, position: Int) {
        binding.setVariable(modelVariableId, bean)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder<B> {
        val binding: B =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutResId, parent, false)
        val holder = Holder(binding)
        binding.setVariable(BR.callback, callback)
        binding.setVariable(BR.holder, holder)
        return holder
    }
}