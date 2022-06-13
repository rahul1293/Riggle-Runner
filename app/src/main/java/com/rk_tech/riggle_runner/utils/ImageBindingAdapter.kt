package com.rk_tech.riggle_runner.utils

import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.rk_tech.riggle_runner.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


object ImageBindingAdapter {

    @JvmStatic
    @BindingAdapter(value = ["simpleRecourse"])
    fun setStepGroupIcon(imageView: ImageView, simpleResourse: Int) {
        if (simpleResourse != -1) {
            imageView.setImageResource(simpleResourse)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["local_images"])
    fun setLocalImages(imageView: ImageView, image_url: String?) {
        image_url?.let {
            Glide.with(imageView.context)
                .load(image_url)
                /*.override(imageView.width, imageView.height)*/
                .placeholder(R.drawable.ic_profile_place_holder)
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["image_uri"])
    fun setImageUri(imageView: ImageView, image_url: String?) {
        image_url?.let {
            Glide.with(imageView.context)
                .load(image_url/*.toUri()*/)
                .placeholder(R.drawable.ic_profile_place_holder)
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["logo_images"])
    fun setLogoImages(imageView: ImageView, image_url: String?) {
        image_url?.let {
            Glide.with(imageView.context)
                .load(it)
                /*.override(imageView.width, imageView.height)*/
                .placeholder(R.mipmap.ic_app_launcher)
                .into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["qr_code"])
    fun setQrCode(imageView: ImageView, qr_code: String?) {
        qr_code?.let {
            Glide.with(imageView.context)
                .load(it)
                /*.override(imageView.width, imageView.height)*/
                .placeholder(R.drawable.qr_code)
                .into(imageView)
        }
    }

    /*@JvmStatic
    @BindingAdapter(value = ["change_font"])
    fun changeFont(textView: TextView, empty: String?) {
        val typeface: Typeface? = ResourcesCompat.getFont(textView.context, R.font.font_medium)
        textView.typeface = typeface
    }*/

    @JvmStatic
    @BindingAdapter(value = ["set_date"])
    fun setDate(textView: TextView, date: String?) {
        date?.let {
            var nwDate = ""
            //val oldFormat = "yyyy-MM-dd'T'HH:mm:ssz"
            val oldFormat = "yyyy-MM-dd'T'HH:mm:ss"
            val newFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(oldFormat)
            try {
                val newDate: Date = sdf.parse(date)
                sdf.applyPattern(newFormat)
                nwDate = sdf.format(newDate)
                textView.text = nwDate
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["update_status_color"])
    fun updateStatusColor(textView: TextView, status: String?) {
        /*val typeface: Typeface? = ResourcesCompat.getFont(textView.context, R.font.font_medium)
        textView.typeface = typeface*/
        status?.let {
            when (status.lowercase()) {
                "pending" -> {
                    textView.text = status
                    textView.setTextColor(
                        ContextCompat.getColor(
                            textView.context,
                            R.color.color_15AB87
                        )
                    )
                    textView.backgroundTintList =
                        ContextCompat.getColorStateList(textView.context, R.color.color_15AB87_30)
                }
                "payment", "confirmed" -> {
                    textView.text = "pending delivery"
                    textView.setTextColor(
                        ContextCompat.getColor(
                            textView.context,
                            R.color.profit_color
                        )
                    )
                    textView.backgroundTintList =
                        ContextCompat.getColorStateList(textView.context, R.color.colorSuccess_30)
                }
                "completed", "delivered" -> {
                    textView.text = "Completed"
                    textView.setTextColor(ContextCompat.getColor(textView.context, R.color.color_117AA7))
                    textView.backgroundTintList =
                        ContextCompat.getColorStateList(textView.context, R.color.color_117AA7_30)
                }
                else -> {
                    textView.text = status
                    textView.setTextColor(
                        ContextCompat.getColor(
                            textView.context,
                            R.color.color_15AB87
                        )
                    )
                    textView.backgroundTintList =
                        ContextCompat.getColorStateList(textView.context, R.color.color_15AB87_30)
                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["set_date_name"])
    fun setDateName(textView: TextView, date: String?) {
        date?.let {
            var nwDate = ""
            //val oldFormat = "yyyy-MM-dd'T'HH:mm:ssz"
            val oldFormat = "yyyy-MM-dd'T'HH:mm:ss"
            val newFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(oldFormat)
            try {
                val newDate: Date = sdf.parse(date)
                sdf.applyPattern(newFormat)
                nwDate = sdf.format(newDate)
                textView.text = "Date : $nwDate"
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["set_visibility"])
    fun setVisibility(view: View, empty: String?) {
        if (Constants.isDeliver) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["set_visibility_two"])
    fun setVisibilityTwo(view: View, empty: String?) {
        if (Constants.isDeliver) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["set_alpha"])
    fun setAlpha(view: View, data: String?) {
        if (data == null || data.isNullOrEmpty()) {
            view.alpha = 0.2f
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.color_C7C7C7))
        } else {
            view.alpha = 1f
            view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.orange))
        }
    }


}