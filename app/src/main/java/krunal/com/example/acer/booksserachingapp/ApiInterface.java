package krunal.com.example.acer.booksserachingapp;

import okhttp3.ResponseBody;
import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by acer on 20-02-2018.
 */

/**
 * Interface for Querying the Url using Retrofit.
 */
public interface ApiInterface {

    @GET("v1/volumes")
    Call<ResponseBody> getlist(@Query("q") String str);

}
