package com.rk_tech.riggle_runner.ui.main.neworder.create_retailer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.github.drjacky.imagepicker.ImagePicker
import com.google.gson.Gson
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.StoreType
import com.rk_tech.riggle_runner.databinding.ActivityCreateRetailerBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.location.LocationHandler
import com.rk_tech.riggle_runner.ui.base.location.LocationResultListener
import com.rk_tech.riggle_runner.ui.base.permission.PermissionHandler
import com.rk_tech.riggle_runner.ui.base.permission.Permissions
import com.rk_tech.riggle_runner.utils.extension.hideKeyboard
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateRetailerActivity : BaseActivity<ActivityCreateRetailerBinding>(),
    LocationResultListener {
    val viewModel: CreateRetailerActivityVM by viewModels()

    private var locationHandler: LocationHandler? = null
    private var mCurrentLocation: Location? = null
    private var dataList = ArrayList<String>()

    companion object {
        fun newIntent(activity: Activity): Intent {
            val intent = Intent(activity, CreateRetailerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }

    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_create_retailer
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        binding.header.tvTitle.text = getString(R.string.create_new_store)
        binding.btnSubmit.isClickable = true
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressed()
                }
                R.id.btnSubmit -> {
                    if (isEmptyField()) {
                        val data = HashMap<String, String>()
                        data["name"] = binding.etStoreName.text.toString().trim()
                        data["mobile"] = binding.etPhoneNumber.text.toString().trim()
                        //data["gstin"] = binding.etGstn.text.toString().trim()
                        //data["store_type"] = selectedStoreType
                        data["pincode"] = binding.etPinCode.text.toString().trim()
                        //data["address"] = binding.etAddress.text.toString().trim()
                        //data["landmark"] = binding.etLandMark.text.toString().trim()
                        try {
                            if (binding.cbAtStore.isChecked) {
                                if (mCurrentLocation != null) {
                                    mCurrentLocation?.let { location ->
                                        data["store_location"] =
                                            location.latitude.toString() + "," + location.longitude.toString()
                                    }
                                } else {
                                    data["store_location"] = ""
                                }
                            } else {
                                data["store_location"] = ""
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            data["store_location"] = ""//lat,lon
                        }
                        viewModel.createNewRetailer(getAuthorization(), data)
                    }
                }
                R.id.ivRetailer -> {
                    launcher.launch(
                        ImagePicker.with(this)
                            .cropSquare()
                            .cameraOnly() // or galleryOnly()
                            .createIntent()
                    )
                }
                R.id.etPinCode -> {
                    setPopupMenu(dataList, binding.etPinCode)
                }
            }
        }

        viewModel.obrRetailer.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    val intent = Intent()
                    intent.putExtra("data", Gson().toJson(it.data))
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
                Status.WARN -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
            }
        })

        viewModel.obrPinCodes.observe(this, Observer {
            when (it.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    it.data?.let { data ->
                        dataList = data as ArrayList<String>
                    }
                }
                Status.WARN -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
                Status.ERROR -> {
                    showHideLoader(false)
                    showErrorToast(it.message)
                }
            }
        })

        binding.cbAtStore.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkLocation()
            }
        })

        setUpSpinner()

        details?.user?.service_hub?.id?.let {
            viewModel.getPinCodes(getAuthorization(), it)
        }

    }

    private fun setPopupMenu(data: List<String>, etPinCode: EditText) {
        val popup = PopupMenu(this, etPinCode)
        for (index in data) {
            popup.menu.add(index)
        }
        popup.setOnMenuItemClickListener { menuItem ->
            etPinCode.setText(menuItem.title.toString())
            true
        }
        popup.show()
    }

    private fun isEmptyField(): Boolean {
        if (TextUtils.isEmpty(binding.etPhoneNumber.text.toString().trim())) {
            binding.etPhoneNumber.error = "Enter valid phone number"
            return false
        }
        if (binding.etPhoneNumber.text.toString().trim().length < 10) {
            binding.etPhoneNumber.error = "Enter valid phone number"
            return false
        }
        if (TextUtils.isEmpty(binding.etPinCode.text.toString().trim())) {
            binding.etPinCode.error = "Enter valid pin code"
            return false
        }
        if (binding.etPinCode.text.toString().trim().length < 6) {
            binding.etPinCode.error = "Enter valid pin code"
            return false
        }
        if (TextUtils.isEmpty(binding.etStoreName.text.toString().trim())) {
            binding.etStoreName.error = "Enter store name"
            return false
        }
        /*if (TextUtils.isEmpty(binding.etAddress.text.toString().trim())) {
            binding.etAddress.error = "Enter store address"
            return false
        }
        if (selectedStoreKey == 0) {
            showErrorToast("Select store type")
            return false
        }*/

        return true
    }

    private var storeList = arrayListOf<StoreType>()
    private var adapter: StoreTypeAdapter? = null
    private var selectedStoreKey = 0
    private var selectedStoreType = ""

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpSpinner() {
        //val hint = StoreType(getString(R.string.store_type), 0)
        storeList?.apply {
            add(0, StoreType(getString(R.string.store_type), 0))
            add(StoreType("General Store", 1))
            add(StoreType("Dairy", 1))
            add(StoreType("Medical Store", 1))
            add(StoreType("Supermarket", 1))
            add(StoreType("QSR / Food Joint", 1))
            add(StoreType("24 hr convenience store", 1))
            add(StoreType("Other", 1))
        }

        adapter = StoreTypeAdapter(this, storeList)
        binding.storeTypeSpinner.adapter = adapter
        binding.storeTypeSpinner.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) hideKeyboard()
            false
        }
        binding.storeTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    val (_, key) = adapterView.getItemAtPosition(i) as StoreType
                    selectedStoreKey = key
                    selectedStoreType = storeList[i].store_type
                    //checkValidation()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                binding.ivRetailer.setImageURI(uri)
            }
        }

    override fun getLocation(location: Location) {
        this.mCurrentLocation = location
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

                override fun onDenied(
                    context: Context?,
                    deniedPermissions: ArrayList<String>?
                ) {
                    super.onDenied(context, deniedPermissions)
                    binding.cbAtStore.isChecked = false
                    showErrorToast("Need to enable the location permission")
                }
            })
    }

    private fun createLocationHandler() {
        locationHandler = LocationHandler(this, this)
        locationHandler?.getUserLocation()
        locationHandler?.removeLocationUpdates()
    }

}