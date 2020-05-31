package com.kunal.chineaseappuninstaller.api

import com.kunal.chineaseappuninstaller.MyApplication
import io.reactivex.Observable
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

interface NetworkApi {


    companion object {

        private const val cacheSize = (5 * 1024 * 1024).toLong()
        private var httpClient: OkHttpClient =
            OkHttpClient.Builder().cache(Cache(MyApplication.getAppContext()!!.cacheDir, cacheSize))
                .build()


        fun create(): NetworkApi {

            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://raw.githubusercontent.com/")
                .client(httpClient)
                .build()

            return retrofit.create(NetworkApi::class.java)
        }
    }

    @GET
    fun downloadFileWithDynamicUrlSync(@Url fileUrl: String? = "kunal52/chinese-app-list/master/app-list.csv"): Observable<Response<ResponseBody>>

}