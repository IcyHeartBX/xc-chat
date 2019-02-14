package com.pix.xcandroid.login

import android.util.Log
import com.pix.http.RxSubscribe
import com.pix.retrofitrxjavahttp.http.XCRequest
import com.pix.xcandroid.bean.LoginResult
import com.pix.xcandroid.manager.UserManager

class LoginPresenter(val view:ILoginView) {
    val TAG = "LoginPresenter"
    fun login(account:String,password:String) {
        XCRequest.login(account,password,object : RxSubscribe<LoginResult>() {
            override fun onSuccess(t: LoginResult?) {
                Log.d(TAG,"onSuccess(),${t.toString()}")
                UserManager.uid = t?.uid.toString()
                UserManager.email = t!!.email
                UserManager.level = t?.level
                UserManager.name = t!!.name
                UserManager.sex = t?.sex
                UserManager.token = t!!.token
                view.loginSuccess()
            }

            override fun onError(t: Throwable) {
                super.onError(t)
                Log.d(TAG,"onError()")
                view.showToast(t.toString())
            }
            override fun onFailed(code: Int, msg: String?) {
                view.showToast("错误码:$code,msg:$msg")
            }

        })
    }
}