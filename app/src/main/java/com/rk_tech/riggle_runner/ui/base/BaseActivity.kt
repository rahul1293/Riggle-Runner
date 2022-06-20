package com.rk_tech.riggle_runner.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.rk_tech.riggle_runner.App
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.data.model.response_v2.UserLoginResponse
import com.rk_tech.riggle_runner.databinding.ViewProgressSheetBinding
import com.rk_tech.riggle_runner.ui.base.connectivity.ConnectivityProvider
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.event.NoInternetSheet
import com.rk_tech.riggle_runner.utils.extension.UnAuthUser
import com.rk_tech.riggle_runner.utils.extension.hideKeyboard
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.rxutils.EventBus
import com.rk_tech.riggle_runner.utils.view.LoadingDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject


abstract class BaseActivity<Binding : ViewDataBinding> : AppCompatActivity(),
    ConnectivityProvider.ConnectivityStateListener {
    private var progressSheet: ProgressSheet? = null
    open val onRetry: (() -> Unit)? = null
    lateinit var binding: Binding
    val app: App
        get() = application as App

    var details: UserLoginResponse? = null

    @Inject
    lateinit var connectivityProvider: ConnectivityProvider
    private var noInternetSheet: NoInternetSheet? = null

    private var loaderDialog: LoadingDialog? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout: Int = getLayoutResource()
        binding = DataBindingUtil.setContentView(this, layout)
        binding.setVariable(BR.vm, getViewModel())
        connectivityProvider.addListener(this)
        //loaderDialog = LoadingDialog(this)
        SharedPrefManager.getSavedUser()?.let {
            details = it
        }
        onCreateView()
    }

    protected abstract fun getLayoutResource(): Int
    protected abstract fun getViewModel(): BaseViewModel
    protected abstract fun onCreateView()


    private fun registerEventBus(viewModel: BaseViewModel) {
        viewModel.compositeDisposable.add(
            EventBus.subscribe<UnAuthUser>()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //showUnauthSheet()
                    showErrorToast("Unauthorized")
                }, {
                    Log.e("TAG", it.message.toString())
                })
        )
    }

    fun showToast(msg: String? = "Something went wrong !!") {
        Toast.makeText(this, msg ?: "Showed null value !!", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }

    fun showLoading(s: Int) {
        showLoading(getString(s))
    }

    fun showLoading(s: String?) {
        progressSheet?.dismissAllowingStateLoss()
        progressSheet = ProgressSheet(object : ProgressSheet.BaseCallback {
            override fun onClick(view: View?) {}
            override fun onBind(bind: ViewProgressSheetBinding) {
                progressSheet?.showMessage(s);
            }
        })
        progressSheet?.show(supportFragmentManager, progressSheet?.tag)

    }

    fun hideLoading() {
        progressSheet?.dismissAllowingStateLoss()
    }

    fun showHideLoader(state: Boolean) {
        if (state) {
            loaderDialog = LoadingDialog(this)
            loaderDialog?.show()
        } else {
            loaderDialog?.dismiss()
        }
    }

    override fun onDestroy() {
        progressSheet?.dismissAllowingStateLoss()
        connectivityProvider.removeListener(this)
        super.onDestroy()
    }

    fun getAuthorization(): String {
        var authorization = ""
        details?.let {
            authorization = it.session_id
        }
        return authorization
    }
    /*override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        if (noInternetSheet == null) {
            noInternetSheet = NoInternetSheet()
            noInternetSheet?.isCancelable = false
        }
        if (state.hasInternet()) {
            if (noInternetSheet?.isVisible == true)
                noInternetSheet?.dismiss()
        } else {
            if (noInternetSheet?.isVisible == false)
                noInternetSheet?.show(supportFragmentManager, noInternetSheet?.getTag())
        }
    }*/

    override fun onStateChange(state: ConnectivityProvider.NetworkState) {
        if (noInternetSheet == null) {
            noInternetSheet = NoInternetSheet()
            noInternetSheet?.setCancelable(false)
        }
        if (state.hasInternet()) {
            if (noInternetSheet?.isVisible == true)
                noInternetSheet?.dismiss()
        } else {
            if (noInternetSheet?.isVisible == false)
                noInternetSheet?.show(supportFragmentManager, noInternetSheet?.getTag())
        }
    }

    private fun ConnectivityProvider.NetworkState.hasInternet(): Boolean {
        return (this as? ConnectivityProvider.NetworkState.ConnectedState)?.hasInternet == true
    }

}