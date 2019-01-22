package com.pix.xcandroid.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.guagua.gofun.http.MD5
import com.pix.xcandroid.MainActivity
import com.pix.xcandroid.R
import kotlinx.android.synthetic.main.xc_activity_login.*

class LoginActivity : AppCompatActivity(),ILoginView, View.OnClickListener {
    var presenter:LoginPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xc_activity_login)
        presenter = LoginPresenter(this)
        btn_login.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v) {
            btn_login -> {  // 登录
                if(et_account.text.isEmpty()) {
                    showToast("请输入用户名!")
                    return
                }
                if(et_password.text.isEmpty()) {
                    showToast("请输入密码!");
                    return
                }
                presenter?.login(et_account.text.toString(),MD5.getMD5(et_password.text.toString()))
            }
        }
    }
    override fun loginSuccess() {
       startActivity(Intent(this,MainActivity::class.java))
    }

    override fun showToast(tMsg: String) {
        Toast.makeText(this,tMsg,Toast.LENGTH_SHORT).show()
    }
}
