package com.rk_tech.riggle_runner.ui.login.otp

import `in`.aabhasjindal.otptextview.OTPListener
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request_v2.SendOtpRequest
import com.rk_tech.riggle_runner.data.model.request_v2.VerifyOtpRequest
import com.rk_tech.riggle_runner.databinding.ActivityEnterOtpBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showInfoToast
import com.rk_tech.riggle_runner.utils.extension.successToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterOtpActivity : BaseActivity<ActivityEnterOtpBinding>() {
    val viewModel: EnterOtpActivityVM by viewModels()
    private var phoneNo: String = ""

    companion object {
        fun newIntent(activity: Activity, phoneNumber: String): Intent {
            val intent = Intent(activity, EnterOtpActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("phone_number", phoneNumber)
            return intent
        }

    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_enter_otp
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView() {
        phoneNo = intent.getStringExtra("phone_number").toString()
        binding.tvPhone.text = "+91- $phoneNo"
        sendOTP()
        startCountDown()

        viewModel.onClick.observe(this, Observer {
            when (it?.id) {
                R.id.btn_verify -> {
                    viewModel.verifyOtp(VerifyOtpRequest(phoneNo, binding.otpView.otp.toString()))
                }
                R.id.tvResend -> {
                    viewModel.sendOtp(SendOtpRequest(phoneNo))
                }
            }
        })

        viewModel.obrSendOtp.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                }
                Status.SUCCESS -> {
                    successToast(it.message)
                }
                Status.WARN -> {
                    showInfoToast(it.message)
                }
                Status.ERROR -> {
                    showErrorToast(it.message)
                }
            }
        })

        viewModel.obrOtpVerify.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    it.data?.let { data ->
                        SharedPrefManager.saveUser(data)
                    }
                    val intent = MainActivity.newIntent(this)
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

    private fun sendOTP() {
        callOTPApi()
        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                deactivateBtn()
            }

            override fun onOTPComplete(otp: String) {
                activateBtn()
            }
        }
    }

    private fun callOTPApi() {
        viewModel.sendOtp(SendOtpRequest(phoneNo))
    }

    private var countDown: CountDownTimer? = null
    private fun startCountDown() {
        countDown = object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                binding.tvCountDown.text = "Resend OTP in : " + millisUntilFinished / 1000 + " sec"
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                binding.tvResend.visibility = View.VISIBLE
                binding.tvCountDown.visibility = View.GONE
            }
        }.start()
    }

    private fun deactivateBtn() {
        binding.btnVerify.alpha = 0.2f
        binding.btnVerify.isClickable = false
    }

    private fun activateBtn() {
        binding.btnVerify.alpha = 1f
        binding.btnVerify.isClickable = true
    }

}