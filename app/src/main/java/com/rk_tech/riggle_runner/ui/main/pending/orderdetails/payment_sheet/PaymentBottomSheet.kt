package com.rk_tech.riggle_runner.ui.main.pending.orderdetails.payment_sheet

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.MenuBean
import com.rk_tech.riggle_runner.data.model.helper.Status
import com.rk_tech.riggle_runner.data.model.request_v2.RevisitRequest
import com.rk_tech.riggle_runner.data.model.response_v2.PaymentRescheduleReason
import com.rk_tech.riggle_runner.data.model.response_v2.UserLoginResponse
import com.rk_tech.riggle_runner.databinding.BottomSheetPaymentBinding
import com.rk_tech.riggle_runner.databinding.ItemImageListBinding
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.ui.base.location.LocationHandler
import com.rk_tech.riggle_runner.ui.base.location.LocationResultListener
import com.rk_tech.riggle_runner.ui.base.permission.PermissionHandler
import com.rk_tech.riggle_runner.ui.base.permission.Permissions
import com.rk_tech.riggle_runner.ui.main.main.MainActivity
import com.rk_tech.riggle_runner.ui.main.pending.orderdetails.CallBackBlurry
import com.rk_tech.riggle_runner.utils.BackStackManager
import com.rk_tech.riggle_runner.utils.FileUtil
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import com.rk_tech.riggle_runner.utils.extension.showErrorToast
import com.rk_tech.riggle_runner.utils.extension.showInfoToast
import com.rk_tech.riggle_runner.utils.view.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_payment.*
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
class PaymentBottomSheet : BottomSheetDialogFragment(), LocationResultListener {
    val viewModel: PaymentBottomSheetVM by viewModels()

    lateinit var binding: BottomSheetPaymentBinding
    private var uploadFile: File? = null
    private var isImageSelected = ""
    private var mlistener: CallBackBlurry? = null

    private var loaderDialog: LoadingDialog? = null

    private var locationHandler: LocationHandler? = null
    private var mCurrentLocation: Location? = null
    var scheduleReason = ArrayList<PaymentRescheduleReason>()
    private var store_name = ""
    private var payment_type = "Cash"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetPaymentBinding.inflate(inflater)
        return binding.root/*inflater.inflate(R.layout.bottom_sheet_payment, container, false)*/
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mlistener?.isExpand(false)
                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            view.viewTreeObserver.addOnGlobalLayoutListener {
                val dialog = dialog as BottomSheetDialog?
                val bottomSheet =
                    dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

                bottomSheet?.let {
                    val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.addBottomSheetCallback(mBottomSheetBehaviorCallback)
                    behavior.isDraggable = false
                    behavior.peekHeight = 0
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        loaderDialog = LoadingDialog(requireContext())
        binding.type = 3
        checkLocation()
        arguments?.getString("pending_amount")?.let { pending ->
            binding.tvAmount.text = pending
            binding.etPayableAmount.setText(pending)
        }
        arguments?.getString("retailer_name")?.let { name ->
            store_name = name
        }

        ivCross.setOnClickListener {
            mlistener?.isExpand(false)
            dismiss()
        }

        tvUpi.setOnClickListener {
            binding.type = 1
            payment_type = "UPI"
        }
        tvCheck.setOnClickListener {
            binding.type = 2
            payment_type = "Cheque"
        }
        tvCash.setOnClickListener {
            binding.type = 3
            payment_type = "Cash"
        }
        tvCredit.setOnClickListener {
            binding.type = 4
            payment_type = "Credit"
        }

        btnContinue.setOnClickListener {
            clCenter.visibility = View.GONE
            clSuccess.visibility = View.GONE
            clConfirm.visibility = View.VISIBLE
            cvTop.visibility = View.GONE
            clRevisit.visibility = View.GONE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.tvDetails.text = Html.fromHtml(
                    "You are about to collect <b>" + binding.etPayableAmount.text.toString()
                        .trim() + "</b> \n through <b>" + payment_type + "</b> from <b>" + store_name + "</b>",
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                binding.tvDetails.text = Html.fromHtml(
                    "You are about to collect <b>" + binding.etPayableAmount.text.toString()
                        .trim() + "</b> \n through <b>" + payment_type + "</b> from <b>" + store_name + "</b>"
                )
            }
        }

        btnDone.setOnClickListener {
;           BackStackManager.getInstance(requireActivity()).clear()
            val intent = MainActivity.newIntent(requireActivity())
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            clCenter.visibility = View.VISIBLE
            clSuccess.visibility = View.GONE
            clConfirm.visibility = View.GONE
            cvTop.visibility = View.VISIBLE
            clRevisit.visibility = View.GONE
        }

        btnConfirm.setOnClickListener {
            arguments?.getInt("order_id", 0)?.let { id ->
                SharedPrefManager.getSavedUser()?.let { user ->
                    donePayments(user, id)
                }
            }
        }

        tvRevisitDate.setOnClickListener {
            showDatePicker()
        }

        tvReasonType.setOnClickListener { view ->
            val menu = PopupMenu(requireContext(), view)
            if (scheduleReason.isNotEmpty()) {
                for (index in scheduleReason) {
                    menu.menu.add(index.value)
                }
            }
            menu.setOnMenuItemClickListener {
                tvReasonType.text = it.title.toString()
                true
            }
            menu.show()
        }

        btnConfirmOne.setOnClickListener {
            if (isEmptyField()) {
                SharedPrefManager.getSavedUser()?.let { user ->
                    arguments?.getInt("order_id", 0)?.let { id ->
                        viewModel.setRevisitDate(
                            user.session_id,
                            id,
                            RevisitRequest(
                                tvReasonType.text.toString().trim(),
                                tvRevisitDate.text.toString().trim()
                            )
                        )
                    }
                }
            }
        }

        setUpRecyclerView()

        viewModel.obrOrderDelivery.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    if (it.data?.pending_amount != null && it.data?.pending_amount > 0) {
                        clCenter.visibility = View.GONE
                        clSuccess.visibility = View.VISIBLE
                        clConfirm.visibility = View.GONE
                        cvTop.visibility = View.VISIBLE
                        clRevisit.visibility = View.VISIBLE
                    } else {
                        clCenter.visibility = View.GONE
                        clSuccess.visibility = View.VISIBLE
                        clConfirm.visibility = View.GONE
                        cvTop.visibility = View.VISIBLE
                        clRevisit.visibility = View.GONE
                    }
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

        viewModel.obrRevisit.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {
                    showHideLoader(true)
                }
                Status.SUCCESS -> {
                    showHideLoader(false)
                    clCenter.visibility = View.GONE
                    clSuccess.visibility = View.VISIBLE
                    clConfirm.visibility = View.GONE
                    cvTop.visibility = View.VISIBLE
                    clRevisit.visibility = View.GONE
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

        viewModel.obrReason.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.LOADING -> {

                }
                Status.SUCCESS -> {
                    it?.data?.let { list ->
                        scheduleReason = list as ArrayList<PaymentRescheduleReason>
                    }
                }
                Status.WARN -> {
                    showErrorToast(it.message)
                }
                Status.ERROR -> {
                    showErrorToast(it.message)
                }
            }
        })

        SharedPrefManager.getSavedUser()?.let { user ->
            viewModel.getCancellationReason(user.session_id)
        }

    }

    private fun isEmptyField(): Boolean {
        if (TextUtils.isEmpty(tvReasonType.text.toString().trim())) {
            tvReasonType.error = "select reason"
            return false
        }
        if (TextUtils.isEmpty(tvRevisitDate.text.toString().trim())) {
            tvRevisitDate.error = "select date"
            return false
        }
        return true
    }

    private fun checkLocation() {
        Permissions.check(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION,
            0,
            object : PermissionHandler() {
                override fun onGranted() {
                    createLocationHandler()
                }

                override fun onDenied(
                    context: Context?,
                    deniedPermissions: java.util.ArrayList<String>?
                ) {
                    super.onDenied(context, deniedPermissions)
                    showErrorToast("Need to enable the location permission")
                }
            })
    }

    private fun createLocationHandler() {
        locationHandler = LocationHandler(requireActivity(), this)
        locationHandler?.getUserLocation()
        locationHandler?.removeLocationUpdates()
    }

    fun showHideLoader(state: Boolean) {
        if (loaderDialog != null) if (state) loaderDialog?.show() else loaderDialog?.dismiss()
    }

    private fun donePayments(user: UserLoginResponse, orderId: Int) {
        val requestBody: RequestBody
        var body: MultipartBody.Part? = null
        val dataRequest = HashMap<String, RequestBody>()
        uploadFile?.let { file ->
            requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("file", file.name, requestBody)
        }
        dataRequest["payment_mode"] =
            "cash".toRequestBody("text/plain".toMediaType())
        dataRequest["amount"] = binding.etPayableAmount.text.toString().trim()
            .toRequestBody("text/plain".toMediaType())
        if (mCurrentLocation != null) {
            dataRequest["location"] =
                (mCurrentLocation?.latitude.toString() + ":" + mCurrentLocation?.longitude.toString()).toRequestBody(
                    "text/plain".toMediaType()
                )
        } else {
            dataRequest["location"] =
                "0.0:0.0".toRequestBody("text/plain".toMediaType())
        }
        viewModel.collectPayment(user.session_id, orderId, dataRequest, body)
    }

    var adapterImages: SimpleRecyclerViewAdapter<MenuBean, ItemImageListBinding>? = null
    private fun setUpRecyclerView() {
        adapterImages = SimpleRecyclerViewAdapter<MenuBean, ItemImageListBinding>(
            R.layout.item_image_list, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.sivLoad, R.id.llSelect -> {
                    launcher.launch(
                        ImagePicker.with(requireActivity())
                            .cropSquare()
                            .cameraOnly() // or galleryOnly()
                            .createIntent()
                    )
                }
                R.id.ivCross -> {
                    adapterImages?.list = getList()
                }
            }
        }
        val layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
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

    fun setListener(listener: CallBackBlurry) {
        this.mlistener = listener
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                uploadFile = FileUtil.from(requireContext(), uri)
                val data = getList()/*apply {
                    add(MenuBean(1, uploadFile?.absolutePath.toString(), 1, false))
                }*/
                data.clear()
                data.add(MenuBean(1, uploadFile?.absolutePath.toString(), 1, false))
                isImageSelected = uploadFile?.absolutePath.toString()
                adapterImages?.list = data
                //activateBtn()
            }
        }

    override fun getLocation(location: Location) {
        this.mCurrentLocation = location
    }


    private fun showDatePicker() {
        var datePicker = DatePickerDialog(
            requireActivity(), dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        /*datePicker.datePicker.setMinDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000 - 1000)
        datePicker.datePicker.setMaxDate(System.currentTimeMillis() + 24 * 60 * 60 * 1000 * 7 - 1000)*/
        datePicker.datePicker.minDate = today.timeInMillis
        datePicker.show()

    }

    private var cal = Calendar.getInstance()
    private var today = Calendar.getInstance()
    private val dateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "yyyy-MM-dd" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            binding.tvRevisitDate.text = sdf.format(cal.time)
        }

}