package com.skg.aws_sample.net_work


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException
import java.util.concurrent.TimeoutException

class Responsitory<T> {
    @SerializedName("status")
    var status: Int = 500
    @SerializedName("message")
    @Expose
    var message: String = "Something went wrong :( !"
    @SerializedName("body")
    @Expose
    var body: T? = null

    constructor(throwable: Throwable?) {
        if (throwable == null) {
            return
        }
        if (throwable.isNetWorkError()) {
            message = "Network error !"
            return
        }
        throwable.message?.apply { message = this }
    }

    constructor(response: Response<T>?) {
        response ?: return
        status = response.code()
        if (response.isSuccessful) {
            message = response.message()
            body = response.body()
            return
        }
        response.errorBody()?.string()?.apply {
            message = this
        }
    }

    private fun Throwable.isNetWorkError(): Boolean {
        return this is SocketException || this is TimeoutException || this is SocketTimeoutException || this is UnknownHostException || this is UnknownServiceException
    }

    val isSuccessful: Boolean get() = status in 200..299 && body != null
}