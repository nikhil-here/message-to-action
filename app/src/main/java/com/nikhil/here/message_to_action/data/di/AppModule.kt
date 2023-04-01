package com.nikhil.here.message_to_action.data.di

import com.google.gson.Gson
import com.mocklets.pluto.PlutoInterceptor
import com.nikhil.here.message_to_action.data.api.ChatGptService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor() = OkHttpClient.Builder()
        .addInterceptor(PlutoInterceptor())
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .baseUrl(ChatGptService.CHAT_GPT_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit) = retrofit.create(ChatGptService::class.java)

    @Provides
    @Singleton
    fun provideGson() = Gson()

}