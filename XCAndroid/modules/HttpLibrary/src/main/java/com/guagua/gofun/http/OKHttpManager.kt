package com.pix.http


import android.util.Log
import com.guagua.gofun.http.SignHelper
import okhttp3.*
import java.util.concurrent.TimeUnit

import java.util.*
import kotlin.collections.HashMap


object OKHttpManager {
    private val TAG = "OKHttpManager"
    var okHttpClient: OkHttpClient? = null
    var clientBuild = OkHttpClient.Builder()

    fun setHeaders(headers:Map<String,String>) {
        clientBuild.addInterceptor(AddHeaderAndParamsInterceptor(headers))
        okHttpClient = clientBuild.connectTimeout(60 * 1000, TimeUnit.MILLISECONDS)
        .readTimeout(60 * 1000,TimeUnit.MILLISECONDS)
        .build()
    }

    // 签名拦截器
    class AddHeaderAndParamsInterceptor(var headers:Map<String,String>):Interceptor {
        override fun intercept(chain: Interceptor.Chain?): Response {
            var oldRequest = chain?.request()

            // 签名
            var params = HashMap<String,String>()
            var paramsNames = oldRequest?.url()?.queryParameterNames()
            for (pName in paramsNames!!) {
                params[pName] = oldRequest?.url()?.queryParameter(pName)!!
                Log.d(TAG,"requestParams:key=$pName,value:${params[pName]}")
            }
            var sign = SignHelper.getSignUseHeaders(params,headers)
            // 添加公共参数
            var signUrlBuilder
                    = oldRequest?.url()?.newBuilder()?.scheme(oldRequest?.url()?.scheme())
                    ?.host(oldRequest?.url().host())
                    ?.addQueryParameter("r",OKHttpManager.genRandomString())
                    ?.addQueryParameter("sign",sign)

            var newRequest = oldRequest?.newBuilder()?.method(oldRequest?.method(),oldRequest?.body())
                    ?.url(signUrlBuilder?.build())
            // 增加公共头
            for((k,v) in headers) {
                newRequest?.addHeader(k,v)
            }
            return chain?.proceed(newRequest?.build())!!
        }
    }

    fun cancleRequest() {
        okHttpClient?.dispatcher()?.cancelAll()
    }

    //生成随机字符串
    private fun genRandomString(): String {
        val random = Random(System.currentTimeMillis())
        var rand: Int

        var len = random.nextInt(10)
        if (len < 4) {
            len = 4
        }
        val buf = StringBuilder()
        for (i in 0 until len) {
            rand = random.nextInt(26)
            buf.append((rand + 'a'.toInt()).toChar())
        }
        return buf.toString()
    }
}
