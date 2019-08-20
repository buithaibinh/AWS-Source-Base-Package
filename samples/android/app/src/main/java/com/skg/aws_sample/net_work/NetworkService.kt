package com.skg.aws_sample.net_work

import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.StringReader
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

const val UNKNOWN_ERROR = 998
const val NETWORK_ERROR = 999

interface NetworkListtener<T> {
    fun hasProgress(): Boolean {
        return true
    }

    fun onCompleted(code: Int, message: String, response: T?) {}
    fun onSuccessful(response: T?)
    fun onFailed(code: Int, message: String) {}
}

/**
 * Observable
 */
fun <T> Observable<T>.onNext(
    tag: String? = null,
    progression: Boolean = false,
    block: (Int, String, T?) -> Unit
): Disposable {
    return subscribeWith(object : DisposableObserver<T?>() {
        override fun onNext(value: T?) {
            block(200, "OK", value)
        }

        override fun onStart() {
            onRequestStarted(tag)
        }

        override fun onComplete() {
            onRequestCompleted(tag)
        }


        override fun onError(e: Throwable) {
            onRequestError(e, block)
        }
    })

}

fun <T> Observable<T>.onSuccess(tag: String? = null, progression: Boolean = false, block: (T?) -> Unit): Disposable {
    return subscribeWith(object : DisposableObserver<T>() {
        override fun onStart() {
            onRequestStarted(tag)
        }

        override fun onComplete() {
            onRequestCompleted(tag)
        }

        override fun onNext(response: T) {
            block(response)
        }

        override fun onError(e: Throwable) {
            onRequestError(e)
        }
    })
}

/**
 *  Single
 * */

fun <T> Single<T>.onNext(tag: String? = null, progression: Boolean= false, block: (Int, String, T?) -> Unit) {
    subscribe(object : SingleObserver<T> {
        override fun onSuccess(response: T) {
            onRequestCompleted(tag)
            block(200,"OK",response)
        }

        override fun onSubscribe(d: Disposable) {
            onRequestStarted(tag)
        }

        override fun onError(e: Throwable) {
            onRequestCompleted(tag)
            onRequestError(e,block)
        }
    })
}

fun <T> Single<T>.onSuccess(tag: String? = null, progression: Boolean= false,block: (T?) -> Unit){
    subscribe(object: SingleObserver<T>{
        override fun onSuccess(response: T) {
            onRequestCompleted(tag)
            block(response)
        }

        override fun onSubscribe(d: Disposable) {
            onRequestStarted(tag)
        }

        override fun onError(e: Throwable) {
            onRequestCompleted(tag)
            onRequestError(e)
        }

    })
}

/**
 * Call
 * */
fun <T> Call<T>.onReponse(tag: String? = null, progression: Boolean = false, block: (Int,String,T?) -> Unit) {
    onRequestStarted(tag)
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            onRequestCompleted(tag)
            onRequestError(t,block  )
        }

        override fun onResponse(call: Call<T>, response: Response<T?>) {
            onRequestCompleted(tag)
            onRequestSuccess(response) { status, message, body ->
                block(status,message,body)
            }
        }
    })
}
fun <T> Call<T>.onSuccess(tag: String? = null, progression: Boolean = false, block: (T?) -> Unit) {
    onRequestStarted(tag)
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            onRequestCompleted(tag)
            onRequestError(t)
        }

        override fun onResponse(call: Call<T>, response: Response<T?>) {
            onRequestCompleted(tag)
            onRequestSuccess(response) { _, _, body ->
                block(body)
            }
        }
    })
}

fun <T> onRequestSuccess(response: Response<T?>?, block: (Int, String, T?) -> Unit) {
    if(response == null){
        onRequestError()
        return
    }
    if (response.isSuccessful) {
        block(response.code(), response.message(), response.body())
        return
    }
    if (response.errorBody() == null) {
        onRequestError()
        return
    }
    try {
        val res = response.getErrorResponse() ?: return
        block(res.status, res.message, null)
        return
    } catch (e: java.lang.Exception) {

    }
}

fun onRequestError(throwable: Throwable? = null) {
    when {
        throwable == null -> "Something went  wrong"
        throwable.isNetworkError() -> "Network error"
        throwable is HttpException -> "${throwable.code()} ${throwable.message()}"
        else -> "Something went wrong"
    }
}

fun <T> onRequestError(throwable: Throwable?, block: (Int, String, T?) -> Unit) {
    when {
        throwable == null -> block(UNKNOWN_ERROR, "Something went wrong!", null)
        throwable is HttpException -> block(throwable.code(), throwable.message(), null)
        throwable.isNetworkError() -> block(NETWORK_ERROR, "Network error", null)
    }
}

fun onRequestCompleted(tag: String?) {
    NetworkClient.remove(tag)
    //show progress
}

fun onRequestStarted(tag: String?) {
    NetworkClient.add(tag)
    //hide progress
}

private fun Throwable.isNetworkError(): Boolean {
    return this is SocketException || this is SocketTimeoutException || this is UnknownHostException
}

private fun Response<*>.getErrorResponse(): Responsitory<*>? {
    return this.errorBody()?.string()?.parse(Responsitory::class.java)
}

private val gson = Gson()
fun <T> String.parse(cls: Class<T>): T? {
    if (isNullOrEmpty()) {
        return null
    }
    return try {
        return gson.fromJson(StringReader(this), cls)
    } catch (e: Exception) {
        null
    }
}