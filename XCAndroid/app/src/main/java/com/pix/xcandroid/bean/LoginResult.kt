package com.pix.xcandroid.bean

/**
 * Created by pixboly on  2018/10/24
 *
 * @version 2.0.0
 * @description
 * @modify
 */
class LoginResult(var uid: String,
                  var name: String,
                  var email: String,
                  var level: Int,
                  var token: String,
                  var sex:Int) {
    override fun toString(): String {
        return "LoginResult(uid='$uid', name='$name', email='$email', level=$level, token='$token', sex=$sex)"
    }
}