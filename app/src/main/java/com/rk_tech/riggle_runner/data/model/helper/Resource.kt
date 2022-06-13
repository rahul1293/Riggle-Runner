package com.rk_tech.riggle_runner.data.model.helper

/**
 * A generic class that holds a value with its loading status.
 *
 * @param <T>
</T> */
class Resource<T> private constructor(var status: Status, val data: T?, val message: String="") {

    override fun toString(): String {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}'
    }

    companion object {
        fun <T> success(data: T?, msg: String=""): Resource<T?> {
            return Resource(Status.SUCCESS, data, msg)
        }

        fun <T> warn(data: T?, msg: String=""): Resource<T?> {
            return Resource(Status.WARN, data, msg)
        }

        fun <T> error(data: T?, errMsg: String=""): Resource<T?> {
            return Resource(Status.ERROR, data, errMsg)
        }


        fun <T> loading(data: T?): Resource<T?> {
            return Resource(Status.LOADING, data)
        }

        fun <T> loading(): Resource<T?> {
            return Resource(Status.LOADING, null)
        }
    }

}