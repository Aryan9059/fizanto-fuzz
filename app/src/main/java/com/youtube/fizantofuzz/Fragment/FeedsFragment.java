package com.youtube.fizantofuzz.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.fizantofuzz.Lists.Member;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Adapter.FeedTextAdapter;
import com.youtube.fizantofuzz.Adapter.Stories_Adapter;
import com.youtube.fizantofuzz.Lists.FeedTextList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import xyz.peridy.shimmerlayout.ShimmerLayout;

public class FeedsFragment extends Fragment {

    RecyclerView storyRecyclerView, feedtextrecyclerview;
    private List<Member> listItems;
    ShimmerLayout shimmer1, shimmer2;
    private List<FeedTextList> feeditems;
    RecyclerView.Adapter storyAdapter;
    RecyclerView.Adapter feedtextAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_feeds, container, false);

        storyRecyclerView = root.findViewById(R.id.feed_stories);
        feedtextrecyclerview = root.findViewById(R.id.feed_text);
        storyRecyclerView.setHasFixedSize(true);
        feedtextrecyclerview.setHasFixedSize(true);
        shimmer1 = root.findViewById(R.id.shimmer1);
        shimmer2 = root.findViewById(R.id.shimmer2);
        storyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        feedtextrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        listItems = new ArrayList<>();
        feeditems = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String url = dataSnapshot.child("url").getValue().toString();
                    String title = dataSnapshot.child("title").getValue().toString();
                    String pic = dataSnapshot.child("pic").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();

                    Member member = new Member(title, url, pic, date);
                    listItems.add(member);
                }

                Collections.reverse(listItems);
                storyAdapter = new Stories_Adapter(listItems, getContext());
                storyRecyclerView.setAdapter(storyAdapter);
                storyAdapter.notifyDataSetChanged();
                shimmer1.setVisibility(View.GONE);
                storyRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseDatabase.getInstance().getReference("Feed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feeditems.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String body = dataSnapshot.child("body").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String topic = dataSnapshot.child("topic").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();

                    FeedTextList feedTextList = new FeedTextList(body, date, topic, image);
                    feeditems.add(feedTextList);
                }

                Collections.reverse(feeditems);
                feedtextAdapter = new FeedTextAdapter(feeditems, getContext());
                feedtextrecyclerview.setAdapter(feedtextAdapter);
                feedtextAdapter.notifyDataSetChanged();
                shimmer2.setVisibility(View.GONE);
                feedtextrecyclerview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }
}