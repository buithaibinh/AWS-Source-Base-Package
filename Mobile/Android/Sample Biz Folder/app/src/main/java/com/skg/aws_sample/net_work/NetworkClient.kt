package com.skg.aws_sample.net_work

import java.util.*


class NetworkClient private constructor() {
    val requestQueue: Queue<String> = LinkedList()
    var service: ApiService = NetworkHelper.retrofit("https://ecoius1zkj.execute-api.ap-southeast-2.amazonaws.com/dev/")
        .build()
        .create(ApiService::class.java)

    companion object {
        val instance: NetworkClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkClient()
        }
    }

    fun add(tag: String?) {
        tag ?: return
        instance.requestQueue.add(tag)
    }
    fun remove(tag: String?){
        tag?: return
        instance.requestQueue.remove(tag)
    }
    fun contains(tag: String?): Boolean{
        return instance.requestQueue.contains(tag)
    }
}