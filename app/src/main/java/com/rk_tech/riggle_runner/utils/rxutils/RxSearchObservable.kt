package com.rk_tech.riggle_runner.utils.rxutils

import android.widget.EditText
import io.reactivex.subjects.PublishSubject
import android.text.TextWatcher
import android.text.Editable
import io.reactivex.Observable

object RxSearchObservable {
    var DEFAULT_WAIT:Long = 500
    fun from(editText: EditText): Observable<String> {
        val subject = PublishSubject.create<String>()
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                subject.onNext(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })
        return subject
    }
}