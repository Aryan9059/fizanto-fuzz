package com.youtube.fizantofuzz.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.youtube.fizantofuzz.YouTube.Item;
import com.youtube.fizantofuzz.YouTube.VideoDetails;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Retrofit.GetDataService;
import com.youtube.fizantofuzz.Retrofit.RetrofitInstance;
import com.youtube.fizantofuzz.Activity.HomeActivity;
import com.youtube.fizantofuzz.Adapter.VideoDetailsAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import xyz.peridy.shimmerlayout.ShimmerLayout;

public class VideosFragment extends Fragment {

    private RecyclerView recyclerView;
    ShimmerLayout shimmer;
    CardView youtube_bar;
    private VideoDetailsAdapter videoDetailsAdapter;
    EditText search;
    private final String TAG = HomeActivity.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_videos, container, false);
        recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(false);
        shimmer = root.findViewById(R.id.shimmer_video);

        search = root.findViewById(R.id.search_videos);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getData();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        youtube_bar = root.findViewById(R.id.youtube_bar);
        youtube_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                youtube_bar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/@FizantoFuzz"));
                startActivity(intent);
            }
        });

        getData();

        return root;
    }

    private void getData() {
        GetDataService dataService = RetrofitInstance.getRetrofit().create(GetDataService.class);

        Call<VideoDetails> videoDetailsRequest = dataService.getVideoData("snippet", "UCjM0QZmYT0RKooirSf9qq5Q", 200, "video","AIzaSyAzXyjw8hhgvecFzTfGosS72Bkor457wAs", "date");
        videoDetailsRequest.enqueue(new Callback<VideoDetails>() {
            @Override
            public void onResponse(Call<VideoDetails> call, Response<VideoDetails> response) {
                if(response.isSuccessful()){
                    if(response.body()!=null){

                        List <Item> items = new ArrayList<>();

                        if (!search.getText().equals("")){
                            int i = 0;
                            while (i < response.body().getItems().size()){
                                String title = response.body().getItems().get(i).getSnippet().getTitle();
                                if (title.toLowerCase().contains(search.getText().toString().toLowerCase())){
                                    items.add(response.body().getItems().get(i));
                                }
                                setUpRecyclerView(items);
                                i++;
                            }

                            Log.e(TAG, "Response Successful");
                        }
                        else {
                            setUpRecyclerView(response.body().getItems());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VideoDetails> call, Throwable t) {
                Log.e(TAG.concat(" API REQUEST FAILED"), t.getMessage());
            }
        });
    }

    private void setUpRecyclerView(List<Item> items) {
        videoDetailsAdapter = new VideoDetailsAdapter(getContext(), items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        shimmer.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(videoDetailsAdapter);
    }

}