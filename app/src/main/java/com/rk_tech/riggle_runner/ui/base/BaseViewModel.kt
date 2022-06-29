package com.rk_tech.riggle_runner.ui.base

import android.view.View
import androidx.lifecycle.ViewModel
import com.rk_tech.riggle_runner.utils.event.SingleActionEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import org.json.JSONObject

open class BaseViewModel : ViewModel() {
    open val TAG: String = this.javaClass.simpleName
    var compositeDisposable = CompositeDisposable()

    val onClick: SingleActionEvent<View> = SingleActionEvent()


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun Disposable.addToCompositeDisposable() {
        compositeDisposable.add(this)
    }

    open fun onClick(view: View) {
        onClick.value = view
    }

    public fun getErrorMessage(errorBody: ResponseBody?): String {
        val text: String? = errorBody?.string()
        if (!text.isNullOrEmpty()) {
            return try {
                val obj = JSONObject(text)
                obj.getString("message")
            } catch (e: Exception) {
                return text
            }
        }
        return errorBody.toString()
    }

}