package com.holdbetter.fintechchatproject.di

import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipCredentials
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier


@Module
class DomainModule {
    @AuthHeader
    @Provides
    fun getAuthHeader(): String = "Authorization"

    @BaseApiUrl
    @Provides
    fun getChatApiBaseUrl(): String = "https://tinkoff-android-fall21.zulipchat.com/api/v1/"

    @ApiCredentials
    @Provides
    @ApplicationScope
    fun getCredentials(): String =
        Credentials.basic(TinkoffZulipCredentials.MAIL, TinkoffZulipCredentials.API_KEY)

    @Provides
    @Reusable
    fun getAuthInterceptor(
        @AuthHeader header: String,
        @ApiCredentials credentials: String
    ): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()

            val shouldBeAuthorized = original.headers()["isAuthRequired"] != "false"

            val request = original.newBuilder().removeHeader("isAuthRequired")

            if (shouldBeAuthorized) {
                request.addHeader(header, credentials)
            }

            chain.proceed(request.build())
        }
    }

    @Provides
    @Reusable
    fun getOkHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Provides
    @ApplicationScope
    fun getRetrofit(
        @BaseApiUrl chatApiBaseUrl: String,
        authClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
        rxJava3CallAdapterFactory: RxJava3CallAdapterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(chatApiBaseUrl)
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(rxJava3CallAdapterFactory)
            .client(authClient)
            .build()
    }

    @Provides
    fun getMoshiFactory(): MoshiConverterFactory {
        return MoshiConverterFactory.create(
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        )
    }

    @Provides
    fun getCallAdapter(): RxJava3CallAdapterFactory {
        return RxJava3CallAdapterFactory.create()
    }

    @Provides
    @ApplicationScope
    fun getApi(retrofit: Retrofit): TinkoffZulipApi {
        return retrofit.create(TinkoffZulipApi::class.java)
    }
}

@Qualifier
annotation class AuthHeader

@Qualifier
annotation class BaseApiUrl

@Qualifier
annotation class ApiCredentials