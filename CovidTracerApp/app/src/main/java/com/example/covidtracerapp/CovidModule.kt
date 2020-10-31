package com.example.covidtracerapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val covidModule = module{

    single {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("https://radiant-beyond-24923.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    single{
        get<Retrofit>().create<CovidApi>(CovidApi::class.java)
    }

    single<Repository> {
        MainRepository(
            covidApi = get()
        )
    }

    viewModel<LoginViewModel> {
        LoginViewModel(
            repository = get()
        )
    }

    viewModel<ShowBeaconsViewModel>{
        ShowBeaconsViewModel(
            repository = get()
        )
    }



}