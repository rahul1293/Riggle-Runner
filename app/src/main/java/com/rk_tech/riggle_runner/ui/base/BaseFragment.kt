package com.rk_tech.riggle_runner.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.data.model.response.LoginResponseDetails
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.hideKeyboard
import com.rk_tech.riggle_runner.utils.view.LoadingDialog

abstract class BaseFragment<Binding : ViewDataBinding> : Fragment() {
    lateinit var binding: Binding
    val parentActivity: BaseActivity<*>?
        get() = activity as? BaseActivity<*>

    private var loaderDialog: LoadingDialog? = null
    var details: LoginResponseDetails? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreateView(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.inflate(layoutInflater, layout, container, false)
        binding.setVariable(BR.vm, getViewModel())
        loaderDialog = LoadingDialog(requireContext())
        SharedPrefManager.getSavedUser()?.let {
            details = it
        }
        return binding.root
    }

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView(view: View)
    override fun onPause() {
        super.onPause()
        hideKeyboard()
    }

    fun showLoading(s: String?) {
        parentActivity?.showLoading(s)
    }

    fun showLoading(s: Int) {
        parentActivity?.showLoading(getString(s))
    }

    fun hideLoading() {
        parentActivity?.hideLoading()
    }

    fun showHideLoader(state: Boolean) {
        if (loaderDialog != null) if (state) loaderDialog?.show() else loaderDialog?.dismiss()
    }

    fun getAuthorization(): String {
        var authorization = ""
        details?.let {
            authorization = it.session_id
        }
        return authorization
    }

}