package krunal.com.example.acer.booksserachingapp;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class MainActivity extends AppCompatActivity {

    private static final String TAB = MainActivity.class.getSimpleName();
    private static final String BASE_URL = "https://www.googleapis.com/books/";
    @SuppressLint("StaticFieldLeak")
    private static RecyclerView mRecycleView;
    private EditText mSearchEditText;
    @SuppressLint("StaticFieldLeak")
    private static RecycleAdapter mRecycleAdapter;
    @SuppressLint("StaticFieldLeak")
    private static ProgressBar mProgressBar;
    private Button mSearchButton;
    private static ArrayList<Books> books = new ArrayList<>();
    private TextView mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecycleView = findViewById(R.id.recyclerView);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setHasFixedSize(true);
        mSearchButton = findViewById(R.id.Searchbutton);
        mSearchEditText = findViewById(R.id.SearcheditText);
        mConnection = findViewById(R.id.textView);
        mConnection.setVisibility(View.INVISIBLE);
        // Check for Internet Connection.
        if (CheckInterConnection(getApplication())) {
            // search button onclick.
            mSearchButton.setOnClickListener((View Button) -> {

                // get the data from EditText
                String getsearchname = mSearchEditText.getText().toString().trim();
                //  replace space by +
                getsearchname.replace(" ", "+");
                // null check for editText.
                if (!getsearchname.matches("")) {
                    //check for books ArrayList.
                    //if books ArrayList is not null then delete all element from the array.
                    if (books != null) {
                        books.clear();
                    }
                    // AsyncTask start.
                    new BooklistAsyncTask(getsearchname, getApplication()).execute();
                    // hide the keyboard.
                    hidekeyboard();
                } else {
                    // if there is no text in text in EditText Display this massage.
                    Toast.makeText(getApplication(), "No Name Found", Toast.LENGTH_LONG).show();
                }
            });

        }else {
            mConnection.setVisibility(View.VISIBLE);
            mRecycleView.setVisibility(View.INVISIBLE);
            mSearchEditText.setVisibility(View.INVISIBLE);
            mSearchButton.setVisibility(View.INVISIBLE);
            mRecycleAdapter =null;
            mSearchEditText =null;
            mProgressBar = null;
            mRecycleView =null;
            mSearchButton = null;
        }
    }


    class BooklistAsyncTask extends AsyncTask<ArrayList<Books>, Void, ArrayList<Books>> {

        // get text from EditText and store in text var.
        private String text;

        @SuppressLint("StaticFieldLeak")
        private Context context;

        BooklistAsyncTask(String name, Context context) {
            this.text = name;
            this.context = context;

        }

        //onpreExcute method it is execute before doInBackground.
        @Override
        protected void onPreExecute() {
            // progressbar gets visible.
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setMax(100);
            super.onPreExecute();
        }

        @SafeVarargs
        @Override
        protected final ArrayList<Books> doInBackground(ArrayList<Books>... voids) {
            // Retrofit object.
            Retrofit sent = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiInterface apiInterface = sent.create(ApiInterface.class);
            // get response body.
            Call<ResponseBody> getresponse = apiInterface.getlist(text);
            getresponse.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {


                        // get jsonObject.
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getInt("totalItems") == 0) {
                            return;
                        }
                        // get jsonArray items from jsonObject.
                        JSONArray itemsArray = jsonObject.getJSONArray("items");

                        for (int i = 0; i < itemsArray.length(); i++) {

                            JSONObject currentObject = itemsArray.getJSONObject(i);
                            // get json object from current object.
                            JSONObject volumeInfoObject = currentObject.getJSONObject("volumeInfo");

                            // get title from volumeInfoObject. And Store in title var.
                            String title = volumeInfoObject.getString("title");

                            String[] author;
                            // get author from volumeInfoObject. And store in author String Array.
                            JSONArray authorArray = volumeInfoObject.optJSONArray("authors");

                            if (authorArray != null) {
                                ArrayList<String> list = new ArrayList<>();
                                for (int a = 0; a < authorArray.length(); a++) {
                                    list.add(authorArray.get(a).toString());
                                }
                                author = list.toArray(new String[list.size()]);
                            } else {
                                // continue if author do not exits.
                                continue;
                            }

                            String description = "";
                            // get description if exits.
                            if (volumeInfoObject.optString("description") != null) {
                                description = volumeInfoObject.optString("description");
                            }
                            String infoLink = "";
                            // get infoLink if exits.
                            if (volumeInfoObject.optString("infoLink") != null) {
                                infoLink = volumeInfoObject.optString("infoLink");
                            }
                            // Add all object to books Arraylist.
                            books.add(new Books(title, author, description, infoLink));
                            // this is log message if need to check.
                            Log.i(TAB, title + Arrays.toString(author) + description + infoLink + i);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.i(TAB, t.toString());
                }
            });
            // return books ArrayList.
            return books;
        }


        // get books ArrayList And Update the UI.
        // This method runs on Main Thread.
        @Override
        protected void onPostExecute(ArrayList<Books> list) {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRecycleAdapter = new RecycleAdapter(context, list);
            mRecycleView.setAdapter(mRecycleAdapter);
            mRecycleAdapter.notifyDataSetChanged();
            // set onclick listener to recycle Adapter
            mRecycleAdapter.SetOnItemClickListener(books -> {
                // get current book Link.
                String link = books.getInfoLink();
                // start intent.
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(link));
                startActivity(i);

            });
            super.onPostExecute(list);
        }
    }

    /**
     * method to hide the keyboard.
     */
    void hidekeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for internet connection.
     * @param context
     * @return boolean
     */
    private boolean CheckInterConnection(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}