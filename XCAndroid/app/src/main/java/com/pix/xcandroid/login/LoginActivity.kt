package com.pix.xcandroid.login

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.pix.xcandroid.R

class LoginActivity : AppCompatActivity(),ILoginView {
    var presenter:LoginPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xc_activity_login)
        presenter = LoginPresenter(this)
        presenter?.login()
    }
}
