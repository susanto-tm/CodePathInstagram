package com.example.instagram;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.databinding.ItemPostBinding;

import java.util.List;
import java.util.zip.Inflater;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public FeedAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        ItemPostBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_post, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Post> addPosts) {
        posts.addAll(addPosts);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemPostBinding binding;
        TextView tvUsername;
        TextView tvDescription;
        ImageView ivUserPost;

        public ViewHolder(@NonNull ItemPostBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
            this.tvUsername = this.binding.tvUsername;
            this.tvDescription = this.binding.tvDescription;
            this.ivUserPost = this.binding.ivUserPost;
        }

        public void bind(Post post) {
            binding.setPost(post);
            binding.executePendingBindings();



        }

    }
}
