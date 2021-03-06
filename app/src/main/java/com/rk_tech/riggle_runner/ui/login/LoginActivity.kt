package com.rk_tech.riggle_runner.ui.login

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request_v2.SendOtpRequest
import com.rk_tech.riggle_runner.databinding.ActivityLoginBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.login.otp.EnterOtpActivity
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showInfoToast
import com.rk_tech.riggle_runner.utils.extension.successToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {

    val viewModel: LoginActivityVM by viewModels()

    companion object {
        fun newIntent(activity: Activity): Intent {
            val intent = Intent(activity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_login
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.tvStarted -> {
                    if (isEmptyView()) {
                        /*viewModel.login(
                            LoginRequest(
                                binding.etEmail.text.toString().trim(),
                                binding.etPassword.text.toString().trim()
                            )
                        )*/
                        viewModel.sendOtp(SendOtpRequest(binding.etEmail.text.toString().trim()))
                    }
                }
            }
        }


        viewModel.obrSendOtp.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    successToast(it.message)
                    val intent =
                        EnterOtpActivity.newIntent(this, binding.etEmail.text.toString().trim())
                    startActivity(intent)
                }
                Status.WARN -> {
                    showHideLoader(false)
                    showInfoToast(it.message)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
            }
        })

        viewModel.obrLogin.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
//                    it.data?.let { data ->
//                        SharedPrefManager.saveUser(data)
//                    }
                    val intent = MainActivity.newIntent(
                        this
                    )
                    startActivity(intent)
                    finishAffinity()
                }
                Status.WARN -> {
                    showHideLoader(false)
                    showInfoToast(it.message)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
            }
        })
    }

    private fun isEmptyView(): Boolean {
        if (TextUtils.isEmpty(
                binding.etEmail.text.toString().trim()
            ) || binding.etEmail.text.toString().trim().length < 10
        ) {
            binding.etEmail.error = "Enter Valid Phone Number"
            return false
        }
//        if (TextUtils.isEmpty(binding.etPassword.text.toString().trim())) {
//            binding.etPassword.error = "Password must not be empty"
//            return false
//        }
        return true
    }

}