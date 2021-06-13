package caprita.catalin.cityquest.ui.dagger;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import caprita.catalin.cityquest.ui.R;
import caprita.catalin.cityquest.ui.api.auth.AuthorizationTokenInterceptor;
import caprita.catalin.cityquest.ui.util.Constants;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * dagger.Module Class that is responsible for providing all the application-scoped dependecies
 * for anyone that is going to need them. Anything that will not change for the lifetime
 * of the app :D*/
@Module
public class AppModule {

    /*
    * Provide Request Options for the Glide FrameWork*/
    @Provides
    @Singleton
    public static RequestOptions provideRequestOptions(){
        return RequestOptions.placeholderOf(R.drawable.ic_baseline_map_24)
                .error(R.drawable.ic_launcher_background);
    }

    @Provides
    @Singleton
    public static RequestManager provideGlideInstance(Application application,
                                                      RequestOptions requestOptions){
        return Glide.with(application)
                .applyDefaultRequestOptions(requestOptions);
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(AuthorizationTokenInterceptor interceptor){
        return new OkHttpClient.Builder()
        .addInterceptor(interceptor)
                .callTimeout(5, TimeUnit.MINUTES)
                .readTimeout(100,TimeUnit.SECONDS)
                .connectTimeout(5,TimeUnit.MINUTES)
                .writeTimeout(100, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
        .build();
    }

    @Provides
    @Singleton
    public static Retrofit provideRetrofit(OkHttpClient okHttpClient){
        return new Retrofit.Builder()
                .baseUrl(Constants.API.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }
}
