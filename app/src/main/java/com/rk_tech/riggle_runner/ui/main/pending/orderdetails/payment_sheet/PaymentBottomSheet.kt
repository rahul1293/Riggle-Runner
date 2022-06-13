package com.rk_tech.riggle_runner.ui.main.pending.orderdetails.payment_sheet

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rk_tech.riggle_runner.BR
import com.rk_tech.riggle_runner.R
import com.rk_tech.riggle_runner.data.model.MenuBean
import com.rk_tech.riggle_runner.databinding.BottomSheetPaymentBinding
import com.rk_tech.riggle_runner.databinding.ItemImageListBinding
import com.rk_tech.riggle_runner.ui.base.SimpleRecyclerViewAdapter
import com.rk_tech.riggle_runner.utils.FileUtil
import com.rk_tech.riggle_runner.utils.SharedPrefManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_sheet_payment.*
import java.io.File

@AndroidEntryPoint
class PaymentBottomSheet : BottomSheetDialogFragment() {
    val viewModel: PaymentBottomSheetVM by viewModels()

    lateinit var binding: BottomSheetPaymentBinding
    private var uploadFile: File? = null
    private var isImageSelected = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetPaymentBinding.inflate(inflater)
        return binding.root/*inflater.inflate(R.layout.bottom_sheet_payment, container, false)*/
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
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
                    behavior.isDraggable = true
                    behavior.peekHeight = 0
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.type = 3
        ivCross.setOnClickListener { dismiss() }

        tvUpi.setOnClickListener {
            binding.type = 1
        }
        tvCheck.setOnClickListener {
            binding.type = 2
        }
        tvCash.setOnClickListener {
            binding.type = 3
        }
        tvCredit.setOnClickListener {
            binding.type = 4
        }

        btnContinue.setOnClickListener {
            arguments?.getInt("order_id", 0)?.let { id ->
                SharedPrefManager.getSavedUser()?.let { user ->

                }
            }
            clCenter.visibility = View.GONE
            clSuccess.visibility = View.VISIBLE
        }

        btnDone.setOnClickListener {

        }
        setUpRecyclerView()
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

}