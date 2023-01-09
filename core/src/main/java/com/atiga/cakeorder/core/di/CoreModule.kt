package com.atiga.cakeorder.core.di

import android.app.admin.DnsEvent
import com.atiga.cakeorder.core.domain.repository.CakeRepository
import com.atiga.cakeorder.core.domain.repository.ICakeRepository
import com.atiga.cakeorder.core.network.ApiService
import com.atiga.cakeorder.core.network.RemoteDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

//val BASE_URL: String = "http://10.0.0.29:45455/api/"
val BASE_URL: String = "http://192.168.1.5:45455/api/"
val URL_SPASI: String = "http://192.168.1.12:45455/api/"
val DEVELOPMENT_URL: String = "http://192.168.18.10:45455/api/"

val networkModule = module {
    single {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { RemoteDataSource(get()) }
    single<ICakeRepository> {
        CakeRepository(get())
    }
}