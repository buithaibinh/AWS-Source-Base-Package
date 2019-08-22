package com.skg.aws_sample.net_work

import com.skg.aws_sample.model.Tokens
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    fun registerToken(@Body info: @JvmSuppressWildcards Map<String,Any>) : Call<Tokens>
    @POST("hello")
    fun sendHello(@Body info: @JvmSuppressWildcards Map<String,Any>) : Call<Void>
    @POST("hello")
    fun sendHelloAll(@Body info: @JvmSuppressWildcards Map<String,String>) : Call<Void>
}