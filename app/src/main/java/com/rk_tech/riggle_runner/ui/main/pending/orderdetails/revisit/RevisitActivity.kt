package com.rk_tech.riggle_runner.ui.main.pending.orderdetails.revisit

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.StoreType
import com.rk_tech.riggle_runner.databinding.ActivityRevisitBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.neworder.create_retailer.StoreTypeAdapter
import com.rk_tech.riggle_runner.utils.extension.hideKeyboard
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showSuccessToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RevisitActivity : BaseActivity<ActivityRevisitBinding>() {
    val viewModel: RevisitActivityVM by viewModels()

    private var orderId = 0
    private var isBack = false
    private var pendingAmount = 0f

    companion object {
        fun newIntent(activity: Activity): Intent {
            val intent = Intent(activity, RevisitActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }

    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_revisit
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView() {
        binding.header.tvTitle.text = "Choose Revisit"
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    if (isBack)
                        onBackPressed()
                    else
                        showErrorToast("You have ₹$pendingAmount net payable amount so please set next revisit date")
                }
                R.id.tvDeliverDate -> {
                    showDatePicker()
                }
                R.id.btn_proceed -> {
                    viewModel.reSchedulePayment(
                        getAuthorization(),
                        orderId,
                        selectedStoreType,
                        binding.tvDeliverDate.text.toString()
                    )
                }
            }
        }
        intent?.getIntExtra("order_id", 0)?.let {
            orderId = it
        }

        intent?.getBooleanExtra("is_back", false)?.let {
            isBack = it
        }

        intent?.getFloatExtra("pending_amount", 0f)?.let {
            pendingAmount = it
            binding.tvPending.text = "Net Pending Amount is ₹$pendingAmount"
        }

        binding.etReason.doOnTextChanged { text, start, before, count ->
            if (text.toString().isNotEmpty()) {
                if (selectedStoreKey == 4) {
                    selectedStoreType = text.toString()
                    activateBtn()
                }
            } else {
                selectedStoreType = ""
                activateBtn()
            }
        }

        viewModel.obrRevisit.observe(this, androidx.lifecycle.Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    showSuccessToast("Rescheduled payment")
                    val intent = MainActivity.newIntent(
                        this
                    )
                    startActivity(intent)
                    finishAffinity()
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

        setUpSpinner()

        if (isBack) {
            showDatePicker()
        } else {
            AlertDialog.Builder(this)
                .setMessage("You have ₹$pendingAmount net pending amount so please set next revisit date of payment")
                .setPositiveButton(
                    "OK"
                ) { dialog, _ ->
                    showDatePicker()
                    dialog.dismiss()
                }
                .setCancelable(false).show()
        }

    }

    private var storeList = arrayListOf<StoreType>()
    private var adapter: StoreTypeAdapter? = null
    private var selectedStoreKey = 0
    private var selectedStoreType = ""

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpSpinner() {
        storeList.apply {
            add(0, StoreType("Reason for Pending Amount", 0))
            add(StoreType("Shop Closed", 1))
            add(StoreType("Owner Not Available", 2))
            add(StoreType("Intent", 3))
            add(StoreType("Other", 4))
        }

        adapter = StoreTypeAdapter(this, storeList)
        binding.storeTypeSpinner.adapter = adapter
        binding.storeTypeSpinner.setOnTouchListener { _, motionEvent ->
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
                    if (key == 4) {
                        selectedStoreType = ""
                        binding.etReason.visibility = View.VISIBLE
                        activateBtn()
                    } else {
                        binding.etReason.visibility = View.GONE
                        activateBtn()
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            }
    }

    private fun showDatePicker() {
        val datePicker = DatePickerDialog(
            this, dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis() + 24 * 60 * 60 * 1000 - 1000
        datePicker.datePicker.maxDate = System.currentTimeMillis() + 24 * 60 * 60 * 1000 * 7 - 1000
        //datePicker.datePicker.maxDate = calendarMax.timeInMillis
        datePicker.setCancelable(false)
        datePicker.show()

    }

    private var cal = Calendar.getInstance()
    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "yyyy-MM-dd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.tvDeliverDate.text = sdf.format(cal.time)
            activateBtn()
        }

    private fun activateBtn() {
        /* btn_proceed?.alpha = 1f
         btn_proceed?.isClickable = true*/
        if (selectedStoreKey != 0 && !TextUtils.isEmpty(
                binding.tvDeliverDate.text.toString().trim()
            ) && selectedStoreType.isNotEmpty()
        ) {
            binding.btnProceed.alpha = 1f
            binding.btnProceed.isClickable = true
        } else {
            binding.btnProceed.alpha = 0.2f
            binding.btnProceed.isClickable = false
        }
    }

    override fun onBackPressed() {
        if (isBack)
            super.onBackPressed()
        else
            showErrorToast("You have ₹$pendingAmount net payable amount so please set next revisit date")
    }

}