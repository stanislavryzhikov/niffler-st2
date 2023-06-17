package guru.qa.niffler.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public abstract class BaseRestClient {
    protected final String baseServiceUrl;
    protected final OkHttpClient httpClient;
    protected final Retrofit retrofit;
    public BaseRestClient(String baseServiceUrl) {
        this.baseServiceUrl = baseServiceUrl;
        this.httpClient = new OkHttpClient.Builder().build();
        this.retrofit = new Retrofit.Builder()
                .client(httpClient)
                .baseUrl(baseServiceUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}