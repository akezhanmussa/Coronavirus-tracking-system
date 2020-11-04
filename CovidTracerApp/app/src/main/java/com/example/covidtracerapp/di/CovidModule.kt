package com.example.covidtracerapp.di

import androidx.room.Room
import com.example.covidtracerapp.api.CovidApi
import com.example.covidtracerapp.presentation.LoginViewModel
import com.example.covidtracerapp.MainRepository
import com.example.covidtracerapp.Repository
import com.example.covidtracerapp.presentation.ShowBeaconsViewModel
import com.example.covidtracerapp.database.AppDatabase
import com.example.covidtracerapp.database.ContactedDAO
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
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
        get<Retrofit>().create<CovidApi>(
            CovidApi::class.java)
    }

    single<Repository> {
        MainRepository(
            covidApi = get(),
            contactedDAO = get()
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

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "covid-database")
            .build()
    }
    fun getContactedDao(database: AppDatabase): ContactedDAO {
        return database.contactedDao()
    }
    single {
        getContactedDao(get())
    }
}