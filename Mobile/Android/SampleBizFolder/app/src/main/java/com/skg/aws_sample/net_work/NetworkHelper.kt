package com.skg.aws_sample.net_work

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkHelper {
    fun retrofit(url: String) : Retrofit.Builder{
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
    }
    fun getClient() :OkHttpClient.Builder{
        return OkHttpClient.Builder()
            .connectTimeout(20,TimeUnit.SECONDS)
            .readTimeout(20,TimeUnit.SECONDS)
            .writeTimeout(20,TimeUnit.SECONDS)
            .callTimeout(20,TimeUnit.SECONDS)
    }
    fun getLoggingInterceptor(enable : Boolean): Interceptor{
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (enable) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        return interceptor
    }
    fun getHeaderInterceptor(token: String?) : Interceptor{
        return Interceptor {
            val request = it.request().newBuilder()
            request.addHeader("Content-Type","application/json")
            if(token!= null){
                request.addHeader("Authorization",token)
            }
            it.proceed(request.build())
        }
    }

}