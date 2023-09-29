package com.patsurvey.nudge.di

import android.app.Application
import android.content.Context
import android.os.Build
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.patsurvey.nudge.BuildConfig
import com.patsurvey.nudge.data.prefs.PrefRepo
import com.patsurvey.nudge.network.ErrorInterceptor
import com.patsurvey.nudge.network.interfaces.ApiService
import com.patsurvey.nudge.utils.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * created by anil on 18/05/22
 */

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

  @Singleton
  @Provides
  fun provideInterceptors():ArrayList<Interceptor>{
    val interceptors = arrayListOf<Interceptor>()
    if(BuildConfig.DEBUG){
      val loggingInterceptor=CurlLoggingInterceptor()
      interceptors.add(loggingInterceptor)
    }
    return interceptors
  }


  @Provides
  @Singleton
  fun provideApiService(retrofit: Retrofit): ApiService {
    return retrofit.create(ApiService::class.java)
  }

  /**
   * Creates Retrofit object
   *
   * @return
   */
  @Singleton
  @Provides
  fun provideRetrofit(
    interceptors: ArrayList<Interceptor>,
    sharedPref: PrefRepo,
    application: Application
  ): Retrofit {
    val cache = Cache(application.cacheDir, 10 * 1024 * 1024) // 10 MB
    val timeout = 60.toLong()
    val clientBuilder =
      OkHttpClient.Builder()
//    getOkHttpBuilder()
        /*.hostnameVerifier(HostnameVerifier { hostname, session -> true })*/
        .connectTimeout(timeout, TimeUnit.SECONDS)
        .readTimeout(timeout, TimeUnit.SECONDS)
//        .cache(cache)
    clientBuilder.addNetworkInterceptor(getNetworkInterceptor(application.applicationContext))
    clientBuilder.addInterceptor(
      getHeaderInterceptor(
        sharedPref,
        application.applicationContext
      )
    )
    if (interceptors.isNotEmpty()) {
      interceptors.forEach { interceptor ->
        clientBuilder.addInterceptor(interceptor)
      }
    }
    clientBuilder.addInterceptor { chain ->
      val request = chain.request()
      val response = chain.proceed(request)
      response
    }

    val gson = GsonBuilder().setLenient().create()

    return Retrofit.Builder()
      .client(clientBuilder.build())
      .addConverterFactory(GsonConverterFactory.create(gson))
      .baseUrl(BuildConfig.BASE_URL)
      .build()
  }

  private fun getOkHttpBuilder(): OkHttpClient.Builder =
    if (!BuildConfig.DEBUG) {
      OkHttpClient().newBuilder()
    } else {
      // Workaround for the error "Caused by: javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException: Trust anchor for certification path not found.
      getUnsafeOkHttpClient()
    }

  private fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
    try {
      val trustAllCerts: Array<TrustManager> = arrayOf(
        object : X509TrustManager {
          @Throws(CertificateException::class)
          override fun checkClientTrusted(
            chain: Array<out java.security.cert.X509Certificate>?,
            authType: String?
          ) = Unit

          @Throws(CertificateException::class)
          override fun checkServerTrusted(
            chain: Array<out java.security.cert.X509Certificate>?,
            authType: String?
          ) = Unit

          override fun getAcceptedIssuers(): Array<out java.security.cert.X509Certificate>? = arrayOf()
        }
      )
      val sslContext: SSLContext = SSLContext.getInstance("SSL")
      sslContext.init(null, trustAllCerts, SecureRandom())
      // Create an ssl socket factory with our all-trusting manager
      val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
      val builder = OkHttpClient.Builder()
      builder.sslSocketFactory(sslSocketFactory,
        trustAllCerts[0] as X509TrustManager
      )
      builder.hostnameVerifier { _, _ -> true }
      return builder
    } catch (ex: Exception) {
      throw RuntimeException(ex)
    }
  }

  @Singleton
  @Provides
  fun provideGson(): Gson {
    return GsonBuilder()
      .setLenient()
      .create()
  }

  @Singleton
  @Provides
  fun provideOkHttpClient(): OkHttpClient {
    val httpClientBuilder =
      OkHttpClient
        .Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .addInterceptor(ErrorInterceptor())
        .connectionPool(ConnectionPool(0, 1, TimeUnit.NANOSECONDS))

    if (BuildConfig.DEBUG) {
      val logging = HttpLoggingInterceptor()
      logging.level = HttpLoggingInterceptor.Level.BODY
      httpClientBuilder.addInterceptor(logging)
    }
    return httpClientBuilder.build()
  }


  private fun getNetworkInterceptor(context: Context): Interceptor {
    return object : Interceptor {
      @Throws(IOException::class)
      override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val maxAge = 60 //60 Sec
        val cacheHeaderValue =
          if (DeviceInfoUtils.hasNetwork(context))
            "public, max-age=$maxAge"
          else "public, only-if-cached, max-stale=$maxAge"

        //val request = originalRequest.newBuilder().build()
        val request = originalRequest.newBuilder()
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build()
        val response = chain.proceed(request)
        return response.newBuilder()
          .removeHeader("Pragma")
          .removeHeader("Cache-Control")
          .header("Cache-Control", cacheHeaderValue)
          .build()
      }

    }
  }

  /**
   * Header Interceptor For API Call
   */

  private fun getHeaderInterceptor(sharedPref: PrefRepo, context: Context): Interceptor {
    return object : Interceptor {
      @Throws(IOException::class)
      override fun intercept(chain: Interceptor.Chain): Response {
        var headerType = chain.request().headers(KEY_HEADER_TYPE)

        if (headerType.isNullOrEmpty()) {
          headerType = arrayListOf(HEADER_TYPE_NONE)
        }
        val requestBuilder = chain.request().newBuilder()

        for(headers in headerType) {
           when(headers){
             KEY_HEADER_MOBILE->{
               if (sharedPref.getLoginStatus()) {
                   requestBuilder.addHeader(
                   KEY_HEADER_AUTH,
                   "Bearer " + (sharedPref.getAccessToken()!!)
                 )
               }
             }
           }
        }
        requestBuilder.addHeader(KEY_SOURCE_TYPE, KEY_SOURCE_PLATFORM)
        requestBuilder.addHeader(KEY_APP_VERSION,BuildConfig.VERSION_NAME)
        requestBuilder.addHeader(KEY_SDK_INT, Build.VERSION.SDK_INT.toString())
        requestBuilder.addHeader(KEY_DEVICE_DETAILS, Build.BRAND+"|"+Build.DEVICE)
        val method = chain.request().method
        if(method.equals("POST", true)){
          requestBuilder.cacheControl(CacheControl.FORCE_NETWORK)
        }
        requestBuilder.removeHeader(KEY_HEADER_TYPE)
        val request = requestBuilder.build()
        return chain.proceed(request)
      }
    }
  }
}