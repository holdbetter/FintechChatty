package com.holdbetter.fintechchatproject.domain.retrofit

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class ServiceProvider {
    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val CHAT_API_URL = "https://tinkoff-android-fall21.zulipchat.com/api/v1/"
        private val basicAuth by lazy {
            Credentials.basic(TinkoffZulipCredentials.MAIL, TinkoffZulipCredentials.API_KEY)
        }

        private val retrofit: Retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(CHAT_API_URL)
                .addConverterFactory(MoshiConverterFactory.create(Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(authClient)
                .build()
        }

        private val authInterceptor: Interceptor by lazy {
            Interceptor { chain ->
                val original = chain.request()

                val shouldBeAuthorized = original.headers()["isAuthRequired"] != "false"

                val request = original.newBuilder().removeHeader("isAuthRequired")

                if (shouldBeAuthorized) {
                    request.addHeader(AUTH_HEADER, basicAuth)
                }

                chain.proceed(request.build())
            }
        }

        private val authClient: OkHttpClient by lazy {
            OkHttpClient.Builder().addInterceptor(authInterceptor).build()
        }

        fun getApi(): TinkoffZulipApi = retrofit.create()
    }
}