package com.rk_tech.riggle_runner.utils.event

import androidx.annotation.MainThread

class SingleActionEvent<T> : SingleLiveEvent<T>() {

    @MainThread
    fun call(v: T) {
        value = v
    }
}