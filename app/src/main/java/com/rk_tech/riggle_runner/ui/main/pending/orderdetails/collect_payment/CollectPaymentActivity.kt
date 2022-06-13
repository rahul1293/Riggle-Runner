package com.rk_tech.riggle_runner.ui.main.pending.orderdetails.collect_payment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.drjacky.imagepicker.ImagePicker
import com.google.gson.Gson
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.MenuBean
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.response.OrderDetailsResponse
import com.rk_tech.riggle_runner.data.model.response.StoreType
import com.rk_tech.riggle_runner.databinding.ActivityCollectPaymentBinding
import com.rk_tech.riggle_runner.databinding.DialogFullViewBinding
import com.rk_tech.riggle_runner.databinding.ItemImageListBinding
import com.rk_tech.riggle_runner.ui.base.BaseActivity
import com.rk_tech.riggle_runner.ui.base.BaseCustomDialog
import com.rk_tech.riggle_runner.ui.base.BaseViewModel
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.neworder.create_retailer.StoreTypeAdapter
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.revisit.RevisitActivity
import com.rk_tech.riggle_runner.utils.Constants
import com.rk_tech.riggle_runner.utils.FileUtil
import com.rk_tech.riggle_runner.utils.extension.hideKeyboard
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showSuccessToast
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@AndroidEntryPoint
class CollectPaymentActivity : BaseActivity<ActivityCollectPaymentBinding>() {

    val viewModel: CollectPaymentActivityVM by viewModels()
    var data: OrderDetailsResponse? = null
    private var uploadFile: File? = null
    private var locationData = ""
    private var pendingAmount = 0f
    private var remainAmount = 0f
    private var isImageSelected = ""

    companion object {
        fun newIntent(activity: Activity): Intent {
            val intent = Intent(activity, CollectPaymentActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return intent
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_collect_payment
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        intent.getStringExtra("retailer_name")?.let {
            binding.header.tvTitle.text = it
        }
        binding.show = false
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.ivBack -> {
                    onBackPressed()
                }
                R.id.btnContinue -> {
                    AlertDialog.Builder(this)
                        .setMessage("Are you sure want to proceed payment?")
                        .setPositiveButton(
                            "Yes"
                        ) { dialog, _ ->
                            donePayments()
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setCancelable(false).show()
                }
                R.id.rlQrDetails -> {
                    openFullViewCode(binding.bean?.service_hub?.qr_code_image)
                }
                R.id.tvRevisitValue -> {
                    showDatePicker()
                }
                R.id.tvMode -> {
                    val popup = PopupMenu(this, it)
                    popup.menu.add("Cash")
                    popup.menu.add("UPI")
                    popup.menu.add("Cheque")
                    /*popup.menu.add("SP Credit")*/
                    popup.setOnMenuItemClickListener { menuItem ->
                        binding.tvMode.text = menuItem.title.toString()
                        binding.show = menuItem.title.toString().trim().equals("UPI", true)
                        if (menuItem.title.toString().equals("Cash", true)) {
                            binding.llThree.visibility = View.GONE
                            isImageSelected = ""
                            adapterImages?.list = getList()
                        } else {
                            binding.llThree.visibility = View.VISIBLE
                            uploadFile?.let {
                                val data = getList().apply {
                                    add(MenuBean(1, uploadFile?.absolutePath.toString(), 1, false))
                                }
                                isImageSelected = uploadFile?.absolutePath.toString()
                                adapterImages?.list = data
                            }
                        }
                        activateBtn()
                        true
                    }
                    popup.show()
                }
                R.id.tvChallan -> {
                    data?.challan_file?.let {
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(it)
                        startActivity(i)
                    }
                }
            }
        }

        intent.getStringExtra("order_details")?.let {
            data = Gson().fromJson(
                it, OrderDetailsResponse::class.java
            )
            data?.let { data ->
                pendingAmount = data.pending_amount
            }
            binding.bean = data
        }

        intent.getStringExtra("location_data")?.let {
            locationData = it
        }

        binding.etReason.doOnTextChanged { text, start, before, count ->
            if (text.toString().trim().isNotEmpty()) {
                remainAmount = (pendingAmount - text.toString().toFloat())
                binding.tvPendingValue.text =
                    "₹ " + (pendingAmount - text.toString().toFloat()).toString()
                if (remainAmount > 0) {
                    binding.rlSecond.visibility = View.VISIBLE
                } else {
                    binding.rlSecond.visibility = View.GONE
                }
            } else {
                binding.tvPendingValue.text = "₹ $pendingAmount"
                binding.rlSecond.visibility = View.VISIBLE
            }
        }

        binding.etReasonText.doOnTextChanged { text, start, before, count ->
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

        viewModel.obrOrderDelivery.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    showSuccessToast("Done payment")
                    /*if (remainAmount > 0) {
                        val intent = RevisitActivity.newIntent(this)
                        data?.let {
                            intent.putExtra("order_id", it.id)
                        }
                        intent.putExtra("is_back", false)
                        intent.putExtra("pending_amount", remainAmount)
                        startActivity(intent)
                    } else {*/
                    val intent = MainActivity.newIntent(this)
                    startActivity(intent)
                    finishAffinity()
                    /*}*/
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

        viewModel.obrOrderDetails.observe(this, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    binding.bean = it.data
                    it.data?.let { dta ->
                        data = dta
                        pendingAmount = dta.pending_amount
                        dta.payment_location?.let { location ->
                            locationData = location
                        }
                    }
                    showHideLoader(false)
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

        if (binding.bean == null) {
            intent?.getIntExtra("order_id", 0)?.let {
                viewModel.getOrderDetails(getAuthorization(), it)
            }
        }

        setUpRecyclerView()
        setUpSpinner()
    }

    private fun donePayments() {
        val requestBody: RequestBody
        var body: MultipartBody.Part? = null
        val dataRequest = HashMap<String, RequestBody>()
        uploadFile?.let { file ->
            //requestBody = create("multipart/form-data".toMediaTypeOrNull(), file)
            requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("payment_receipts", file.name, requestBody)
        }
        dataRequest["payment_mode"] =
            binding.tvMode.text.toString().toRequestBody("text/plain".toMediaType())
        dataRequest["amount"] = binding.etReason.text.toString().trim()
            .toRequestBody("text/plain".toMediaType())
        dataRequest["payment_location"] =
            locationData.toRequestBody("text/plain".toMediaType())

        if (remainAmount > 0) {
            dataRequest["payment_reschedule_reason"] =
                selectedStoreType.toRequestBody("text/plain".toMediaType())
            dataRequest["payment_rescheduled_to"] = binding.tvRevisitValue.text.toString().trim()
                .toRequestBody("text/plain".toMediaType())
        }

        data?.id?.let { orderId ->
            viewModel.donePayment(getAuthorization(), orderId, dataRequest, body)
        }
    }

    private var dialogFile: BaseCustomDialog<DialogFullViewBinding>? = null

    private fun openFullViewCode(qrCodeImage: String?) {
        dialogFile = BaseCustomDialog(
            this,
            R.layout.dialog_full_view
        ) { view ->
            if (view != null && view.id != 0) {
                when (view.id) {
                    R.id.ivClose -> {
                        dialogFile?.dismiss()
                    }
                }
            }
        }
        dialogFile?.setCancelable(false)
        //dialogFile?.window?.setBackgroundDrawableResource(R.color.color_17)
        dialogFile?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialogFile?.binding?.qrCode = qrCodeImage
        //dialogFile?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialogFile?.show()
    }

    var adapterImages: SimpleRecyclerViewAdapter<MenuBean, ItemImageListBinding>? = null
    private fun setUpRecyclerView() {
        adapterImages = SimpleRecyclerViewAdapter<MenuBean, ItemImageListBinding>(
            R.layout.item_image_list, BR.bean
        ) { v, m, pos ->
            when (pos) {
                0 -> {
                    launcher.launch(
                        ImagePicker.with(this)
                            .cropSquare()
                            .cameraOnly() // or galleryOnly()
                            .createIntent()
                    )
                }
            }
        }
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImageList.layoutManager = layoutManager
        binding.rvImageList.adapter = adapterImages
        adapterImages?.list = getList()
    }

    private fun getList(): MutableList<MenuBean> {
        val dataList = ArrayList<MenuBean>()
        dataList.apply {
            add(MenuBean(1, "", 0, false))
        }
        return dataList
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                uploadFile = FileUtil.from(this, uri)
                val data = getList().apply {
                    add(MenuBean(1, uploadFile?.absolutePath.toString(), 1, false))
                }
                isImageSelected = uploadFile?.absolutePath.toString()
                adapterImages?.list = data
                activateBtn()
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
                        binding.etReasonText.visibility = View.VISIBLE
                        activateBtn()
                    } else {
                        binding.etReasonText.visibility = View.GONE
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
            binding.tvRevisitValue.text = sdf.format(cal.time)
            activateBtn()
        }

    private fun activateBtn() {
        if (remainAmount <= 0f) {
            if (binding.tvMode.text.toString().equals("Cash", true)) {
                activate()
            } else {
                if (isImageSelected.isNotEmpty()) {
                    activate()
                } else {
                    deActivate()
                }
            }
        } else {
            if (binding.tvMode.text.toString().equals("Cash", true)) {
                if (selectedStoreKey != 0 && !TextUtils.isEmpty(
                        binding.tvRevisitValue.text.toString().trim()
                    ) && selectedStoreType.isNotEmpty()
                ) {
                    activate()
                } else {
                    deActivate()
                }
            } else {
                if (selectedStoreKey != 0 && !TextUtils.isEmpty(
                        binding.tvRevisitValue.text.toString().trim()
                    ) && selectedStoreType.isNotEmpty() && isImageSelected.isNotEmpty()
                ) {
                    activate()
                } else {
                    deActivate()
                }
            }
        }
    }

    private fun activate() {
        binding.btnContinue.alpha = 1f
        binding.btnContinue.isClickable = true
        binding.btnContinue.isEnabled = true
    }

    private fun deActivate() {
        binding.btnContinue.alpha = 0.2f
        binding.btnContinue.isClickable = false
        binding.btnContinue.isEnabled = false
    }

}