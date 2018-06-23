package app.com.sampleapp;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    JSONObject jsonObjectFeed;
    JSONArray jsonArrayAuthor;
    ArrayList<String> arrayList;
    CustomAdapter customAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String URL = "https://itunes.apple.com/us/rss/topalbums/limit=10/json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        arrayList = new ArrayList<>();
        customAdapter = new CustomAdapter(this, arrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(customAdapter);
        swipeRefreshLayout.setOnRefreshListener(this);
        getData();
    }

    public void getData() {
        swipeRefreshLayout.setRefreshing(true);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                try {
                    jsonObjectFeed = new JSONObject(response.body().string()).getJSONObject("feed");
                    jsonArrayAuthor = jsonObjectFeed.getJSONArray("entry");
                    for (int i = 0; i < jsonArrayAuthor.length(); i++) {
                        JSONObject jsonObject = jsonArrayAuthor.getJSONObject(i).getJSONObject("title");
                        arrayList.add(jsonObject.getString("label"));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            customAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        customAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);


    }
}
