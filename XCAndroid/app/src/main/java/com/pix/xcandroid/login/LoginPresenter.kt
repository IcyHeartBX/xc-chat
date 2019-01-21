package com.pix.xcandroid.login

import android.util.Log
import com.pix.http.RxSubscribe
import com.pix.retrofitrxjavahttp.http.XCRequest
import com.pix.xcandroid.bean.LoginResult

class LoginPresenter(val view:ILoginView) {
    val TAG = "LoginPresenter"
    fun login() {
        val loginparam = "pixboly@gmial.com"
        val pwd = "e10adc3949ba59abbe56e057f20f883e"
        XCRequest.login(loginparam,pwd,object : RxSubscribe<LoginResult>() {
            override fun onSuccess(t: LoginResult?) {
                Log.d(TAG,"onSuccess(),${t.toString()}")
                // 取得当前用户的个人信息
            }
            override fun onError(t: Throwable) {
                super.onError(t)
                Log.d(TAG,"onError()")
            }
        })
    }
}