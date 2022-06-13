package com.rk_tech.riggle_runner.utils.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Coroutines {
    fun main(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            work()
        }

    fun io(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.IO).launch {
            work()
        }
}