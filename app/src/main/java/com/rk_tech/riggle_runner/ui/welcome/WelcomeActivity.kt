package com.rk_tech.riggle_runner.ui.welcome

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.databinding.ActivityWelcomeBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.location.LocationHandler
import com.rk_tech.riggle_runner.ui.base.location.LocationResultListener
import com.rk_tech.riggle_runner.ui.base.permission.PermissionHandler
import com.rk_tech.riggle_runner.ui.base.permission.Permissions
import com.rk_tech.riggle_runner.ui.login.LoginActivity
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showInfoToast
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class WelcomeActivity : BaseActivity<ActivityWelcomeBinding>(), LocationResultListener {

    private val viewModel: WelcomeActivityVM by viewModels()

    private var locationHandler: LocationHandler? = null
    private var mCurrentLocation: Location? = null

    override fun getLayoutResource(): Int {
        return R.layout.activity_welcome
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        checkLocation()
        viewModel.obrAuthVerify.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    it.data?.let { data ->
                        SharedPrefManager.saveUser(data)
                    }
                    val intent = MainActivity.newIntent(
                        this@WelcomeActivity
                    )
                    startActivity(intent)
                    finish()
                }
                Status.WARN -> {
                    showInfoToast(it.message)
                    val intent = LoginActivity.newIntent(this@WelcomeActivity)
                    startActivity(intent)
                    finish()
                }
                Status.ERROR -> {
                    showErrorToast(it.message)
                    val intent = LoginActivity.newIntent(this@WelcomeActivity)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }

    private fun checkLocation() {
        Permissions.check(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION,
            0,
            object : PermissionHandler() {
                override fun onGranted() {
                    createLocationHandler()
                }

                override fun onDenied(context: Context?, deniedPermissions: ArrayList<String>?) {
                    super.onDenied(context, deniedPermissions)
                    openActivity()
                }
            })
    }

    private fun createLocationHandler() {
        locationHandler = LocationHandler(this, this)
        locationHandler?.getUserLocation()
        locationHandler?.removeLocationUpdates()
    }

    private fun openActivity() {

        if (SharedPrefManager.getSavedUser() != null) {
            viewModel.authPing(getAuthorization())
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = LoginActivity.newIntent(this@WelcomeActivity)
                startActivity(intent)
                finish()
            }, 1000)
        }

    }

    override fun getLocation(location: Location) {
        this.mCurrentLocation = location
        openActivity()
    }

}