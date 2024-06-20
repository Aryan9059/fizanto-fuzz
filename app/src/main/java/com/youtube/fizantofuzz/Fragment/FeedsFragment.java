package com.youtube.fizantofuzz.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.youtube.fizantofuzz.Dialog.AddStoryFeedDialog;
import com.youtube.fizantofuzz.Dialog.AddTextFeedDialog;
import com.youtube.fizantofuzz.Lists.FeedStoryList;
import com.youtube.fizantofuzz.R;
import com.youtube.fizantofuzz.Adapter.FeedTextAdapter;
import com.youtube.fizantofuzz.Adapter.FeedStoryAdapter;
import com.youtube.fizantofuzz.Lists.FeedTextList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xyz.peridy.shimmerlayout.ShimmerLayout;

public class FeedsFragment extends Fragment {

    RecyclerView storyRecyclerView, feedTextRecyclerView;
    private List<FeedStoryList> listItems;
    ShimmerLayout shimmerStories, shimmerFeeds;
    private List<FeedTextList> feeditems;
    EditText search_feeds;
    ImageView add_story, add_feed;
    RecyclerView.Adapter storyAdapter;
    RecyclerView.Adapter feedTextAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_feeds, container, false);

        storyRecyclerView = root.findViewById(R.id.feed_stories);
        feedTextRecyclerView = root.findViewById(R.id.feed_text);
        storyRecyclerView.setHasFixedSize(true);
        feedTextRecyclerView.setHasFixedSize(true);
        feedTextRecyclerView.setNestedScrollingEnabled(false);
        storyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        feedTextRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listItems = new ArrayList<>();
        feeditems = new ArrayList<>();

        add_story = root.findViewById(R.id.add_story);
        add_feed = root.findViewById(R.id.add_feed);

        FirebaseDatabase.getInstance().getReference("Admin").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().contains(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                    add_feed.setVisibility(View.VISIBLE);
                    add_story.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_story.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                DialogFragment dialog = new AddStoryFeedDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("feed_inside", false);
                dialog.setArguments(bundle);
                dialog.show(getParentFragmentManager(), "story");
            }
        });

        add_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_feed.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
                DialogFragment dialog = new AddTextFeedDialog();
                Bundle bundle = new Bundle();
                bundle.putBoolean("feed_inside", false);
                dialog.setArguments(bundle);
                dialog.show(getParentFragmentManager(), "feed");
            }
        });

        shimmerStories = root.findViewById(R.id.shimmer_stories);
        shimmerFeeds = root.findViewById(R.id.shimmer_feeds);

        search_feeds = root.findViewById(R.id.search_feeds);

        FirebaseDatabase.getInstance().getReference("Stories").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listItems.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String url = dataSnapshot.child("url").getValue().toString();
                    String title = dataSnapshot.child("title").getValue().toString();
                    String pic = dataSnapshot.child("pic").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String parent = dataSnapshot.child("parent").getValue().toString();

                    FeedStoryList feedStoryList = new FeedStoryList(title, url, pic, date, parent);
                    listItems.add(feedStoryList);
                }

                Collections.reverse(listItems);
                storyAdapter = new FeedStoryAdapter(listItems, getContext());
                storyRecyclerView.setAdapter(storyAdapter);
                storyAdapter.notifyDataSetChanged();
                shimmerStories.setVisibility(View.GONE);
                storyRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        search_feeds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        storyRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                ViewParent vp = rv.getParent();
                int action = e.getAction();
                boolean tolLeft = rv.canScrollHorizontally(-1);
                boolean tolRight = rv.canScrollHorizontally(1);
                if (tolRight || tolLeft) {
                    if (action == MotionEvent.ACTION_MOVE) {
                        int historySize = e.getHistorySize();
                        boolean b = true;
                        if (historySize > 0) {
                            float x2 = e.getX();
                            float y2 = e.getY();
                            float x1 = e.getHistoricalX(0);
                            float y1 = e.getHistoricalY(0);
                            b = Math.abs(x2 - x1) > Math.abs(y2 - y1) &&
                                    (
                                            (tolLeft && (x2 - x1) > 0) ||
                                                    (tolRight && (x2 - x1) < 0)
                                    );
                        }
                        vp.requestDisallowInterceptTouchEvent(b);
                    }
                    return false;
                }else {
                    if (action == MotionEvent.ACTION_MOVE)
                        vp.requestDisallowInterceptTouchEvent(false);
                    rv.removeOnItemTouchListener(this);
                    return true;
                }
            }
        });

        FirebaseDatabase.getInstance().getReference("Feed").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feeditems.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String body = dataSnapshot.child("body").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String topic = dataSnapshot.child("topic").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String parent = dataSnapshot.child("parent").getValue().toString();

                    FeedTextList feedTextList = new FeedTextList(body, date, topic, image, parent);
                    feeditems.add(feedTextList);
                }

                Collections.reverse(feeditems);
                feedTextAdapter = new FeedTextAdapter(feeditems, getContext());
                feedTextRecyclerView.setAdapter(feedTextAdapter);
                feedTextAdapter.notifyDataSetChanged();
                shimmerFeeds.setVisibility(View.GONE);
                feedTextRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
    }

    private void searchUsers(String s) {
        FirebaseDatabase.getInstance().getReference("Feed").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feeditems.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String body = dataSnapshot.child("body").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String topic = dataSnapshot.child("topic").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    String parent = dataSnapshot.child("parent").getValue().toString();

                    if (topic.toLowerCase().contains(s)){
                    FeedTextList feedTextList = new FeedTextList(body, date, topic, image, parent);
                    feeditems.add(feedTextList);}
                }

                Collections.reverse(feeditems);
                feedTextAdapter = new FeedTextAdapter(feeditems, getContext());
                feedTextRecyclerView.setAdapter(feedTextAdapter);
                feedTextAdapter.notifyDataSetChanged();
                feedTextRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}