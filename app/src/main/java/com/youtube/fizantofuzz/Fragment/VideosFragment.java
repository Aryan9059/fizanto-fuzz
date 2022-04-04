package com.youtube.fizantofuzz.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.youtube.fizantofuzz.Model.Item;
import com.youtube.fizantofuzz.Model.VideoDetails;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Retrofit.GetDataService;
import com.youtube.fizantofuzz.Retrofit.RetrofitInstance;
import com.youtube.fizantofuzz.Activity.HomeActivity;
import com.youtube.fizantofuzz.Adapter.VideoDetailsAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.peridy.shimmerlayout.ShimmerLayout;

public class VideosFragment extends Fragment {

    private RecyclerView recyclerView;
    TextView subscribe;
    private VideoDetailsAdapter videoDetailsAdapter;
    private final String TAG = HomeActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefresh;
    ShimmerLayout shimmer_video;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_videos, container, false);

        swipeRefresh = root.findViewById(R.id.swipeRefresh);
        recyclerView = root.findViewById(R.id.recyclerview);
        subscribe = root.findViewById(R.id.subscribe);
        shimmer_video = root.findViewById(R.id.shimmer_video);
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/c/fizantofuzz"));
                startActivity(intent);
            }
        });

        setUpRefreshListener();
        getData();

        return root;
    }

    private void setUpRefreshListener() {
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                getData();
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void getData() {
        swipeRefresh.setRefreshing(true);
        GetDataService dataService = RetrofitInstance.getRetrofit().create(GetDataService.class);

        Call<VideoDetails> videoDetailsRequest = dataService.getVideoData("snippet", "UCjM0QZmYT0RKooirSf9qq5Q", 200, "video","AIzaSyAzXyjw8hhgvecFzTfGosS72Bkor457wAs", "date");
        videoDetailsRequest.enqueue(new Callback<VideoDetails>() {
            @Override
            public void onResponse(Call<VideoDetails> call, Response<VideoDetails> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null){
                        Log.e(TAG, "Response Successful");
                        setUpRecyclerView(response.body().getItems());
                        swipeRefresh.setRefreshing(false);
                    }
                    else {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(getContext(), "Something went wrong! Please try again later!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(getContext(), "Something went wrong! Please try again later!", Toast.LENGTH_SHORT).show();                  }
            }

            @Override
            public void onFailure(Call<VideoDetails> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong! Please try again later!", Toast.LENGTH_SHORT).show();
                Log.e(TAG.concat(" API REQUEST FAILED"), t.getMessage());
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void setUpRecyclerView(List<Item> items) {
        videoDetailsAdapter = new VideoDetailsAdapter(getContext(), items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        shimmer_video.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(videoDetailsAdapter);
    }

}