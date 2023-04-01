package com.example.gpt.data.remote

import com.example.gpt.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber


private const val BASE_URL = "https://api.openai.com/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOpenAIApi(): OpenAIApi {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(AuthInterceptor())
            addInterceptor(LoggingInterceptor())
            retryOnConnectionFailure(true)
            connectTimeout(100, TimeUnit.SECONDS)
        }.build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
            .create(OpenAIApi::class.java)
    }
}

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.OPEN_AI_KEY}")
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(request)
    }
}

class LoggingInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val t1 = System.nanoTime()
        Timber.tag("OkHttp").d(
            String.format(
                "--> Sending request %s on %s%n%s",
                request.url,
                chain.connection(),
                request.headers
            )
        )
        val requestBuffer = Buffer()
        request.body?.writeTo(requestBuffer)
        Timber.tag("OkHttp").d(requestBuffer.readUtf8())

        val response: Response = chain.proceed(request)
        val t2 = System.nanoTime()
        Timber.tag("OkHttp").d(
            java.lang.String.format(
                "<-- Received response for %s in %.1fms%n%s",
                response.request.url,
                (t2 - t1) / 1000000.0,
                response.headers
            )
        )
        val contentType: MediaType? = response.body?.contentType()
        val content: String? = response.body?.string()
        if (content != null) {
            Timber.tag("OkHttp").d(content)
        }

        val wrappedBody: ResponseBody? = content?.toResponseBody(contentType)
        return response.newBuilder().body(wrappedBody).build()
    }
}
