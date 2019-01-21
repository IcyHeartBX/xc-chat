package com.pix.retrofitrxjavahttp.http

import com.pix.http.BaseBack
import com.pix.http.BaseModel
import com.pix.http.Rest
import com.pix.http.RxSubscribe
import com.pix.xcandroid.bean.LoginResult
import com.pix.xcandroid.http.XCApi

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call

/**
 * Created by pixboly on  2018/10/18
 *
 * @version 2.0.0
 * @description
 * @modify
 */
object XCRequest {

    /**
     * 第三方登录
     */
    fun login(loginparam:String,password:String,subscribe:RxSubscribe<LoginResult>) {
        Rest.getRestApi<XCApi>(XCApi::class.java,XCApi.DOMAN).login(loginparam,password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscribe)
    }

}
