package com.example.covidtracerapp.di

import com.example.covidtracerapp.api.AuthApi
import com.example.covidtracerapp.api.CovidApi
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class BasicAuthInterceptor(
    userName: String,
    password: String
): Interceptor {
    private var credentials: String = Credentials.basic(userName, password)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .header("Content-type", "application/x-www-form-urlencoded")
            .header("Authorization", credentials)
            .build()
        return chain.proceed(request)
    }
}

val authModule = module {
    single(named("auth")){
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(BasicAuthInterceptor("covid-client", "covid-secret"))
            .build()

        Retrofit.Builder()
            .baseUrl("https://covid--back.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    single{
        get<Retrofit>(named("auth")).create<AuthApi>(
            AuthApi::class.java)
    }
}