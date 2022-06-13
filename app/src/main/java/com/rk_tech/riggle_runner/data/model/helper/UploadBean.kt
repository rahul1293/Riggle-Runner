package com.rk_tech.riggle_runner.data.model.helper

class UploadBean {
    var bytesWritten: Long = 0
    var contentLength: Long = 0
    var isUploaded = false
    var data: Any? = null
    val progress: Int
        get() = (100 * (1.0 * bytesWritten / contentLength)).toInt()
}