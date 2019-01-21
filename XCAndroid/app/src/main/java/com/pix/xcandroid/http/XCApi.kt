package com.pix.xcandroid.http

import com.pix.http.BaseModel
import com.pix.xcandroid.bean.LoginResult

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by pixboly on  2018/10/18
 *
 * @version 2.0.0
 * @description
 * @modify
 */
interface XCApi {

    @GET("user/login")
    fun login(@Query("loginparam")loginparam:String
                    ,@Query("password")password:String
                   ):Observable<BaseModel<LoginResult>>

    companion object {
        const val DOMAN: String = "http://192.168.1.109:8080/"
    }

}
