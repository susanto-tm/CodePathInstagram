package com.example.instagram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.databinding.FeedFragmentBinding;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {

    RecyclerView rvFeedContainer;
    FeedFragmentBinding binding;
    public static final String TAG = "FeedFragment";
    protected FeedAdapter adapter;
    protected List<Post> allPosts;
    private SwipeRefreshLayout swipeRefreshConatiner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.feed_fragment, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFeedContainer = binding.rvFeedContainer;
        swipeRefreshConatiner = binding.swipeRefreshLayoutContainer;

        swipeRefreshConatiner.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts();
            }
        });

        swipeRefreshConatiner.setColorSchemeResources(R.color.design_default_color_primary,
                R.color.design_default_color_on_primary,
                R.color.design_default_color_secondary,
                R.color.design_default_color_on_secondary);

        allPosts = new ArrayList<>();

        adapter = new FeedAdapter(getContext(), allPosts);

        rvFeedContainer.setAdapter(adapter);
        rvFeedContainer.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPosts();

    }

    protected void queryPosts() {
        adapter.clear();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts:", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Posts: " + post.getDescription() + " username: " + post.getUser().getUsername());
                }
                adapter.addAll(posts);
                swipeRefreshConatiner.setRefreshing(false);
            }
        });
    }
}
