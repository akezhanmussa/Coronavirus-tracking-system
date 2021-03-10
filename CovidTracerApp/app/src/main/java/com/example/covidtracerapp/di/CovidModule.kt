package com.example.covidtracerapp.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.room.Room
import com.example.covidtracerapp.api.CovidApi
import com.example.covidtracerapp.presentation.LoginViewModel
import com.example.covidtracerapp.MainRepository
import com.example.covidtracerapp.Repository
import com.example.covidtracerapp.presentation.ShowBeaconsViewModel
import com.example.covidtracerapp.database.AppDatabase
import com.example.covidtracerapp.database.ContactedDAO
import com.example.covidtracerapp.presentation.FirebaseNotificationViewModel
import com.example.covidtracerapp.presentation.MapViewModel
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory



class OAuth2Interceptor(
    val token: String
): Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().header(
            "Authorization", "Bearer $token"
        ).build()
        return chain.proceed(request)
    }
}

val covidModule = module{

    fun getSharedPrefs(context: Context) : SharedPreferences =
        context.getSharedPreferences("default", Context.MODE_PRIVATE)

    single<Retrofit>(named("api")) {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        var token = getSharedPrefs(androidContext()).getString("USER_TOKEN", "")

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl("https://covid--back.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()
    }

    single<CovidApi>{
        get<Retrofit>(named("api")).create<CovidApi>(
            CovidApi::class.java)
    }

    single<Repository> {
        MainRepository(
            authApi = get(),
            covidApi = get(),
            contactedDAO = get(),
            sharedPreferences = getSharedPrefs(androidContext())
        )
    }

    viewModel<LoginViewModel> {
        LoginViewModel(
            repository = get()
        )
    }

    viewModel<MapViewModel>{
        MapViewModel(
            repository = get()
        )
    }

    viewModel<ShowBeaconsViewModel>{
        ShowBeaconsViewModel(
            repository = get()
        )
    }

    viewModel{
        FirebaseNotificationViewModel(
            repository = get()
        )
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "covid-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    fun getContactedDao(database: AppDatabase): ContactedDAO {
        return database.contactedDao()
    }

    single {
        getContactedDao(get())
    }
}