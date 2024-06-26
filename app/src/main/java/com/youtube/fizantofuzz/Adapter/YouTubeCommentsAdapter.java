package com.youtube.fizantofuzz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.youtube.fizantofuzz.YouTube.Comments.Comment;
import com.youtube.fizantofuzz.R;
import java.util.List;

public class YouTubeCommentsAdapter extends RecyclerView.Adapter<YouTubeCommentsAdapter.CommentsViewHolder> {

    private Context context;
    private List<Comment.Item> commentsList;

    public YouTubeCommentsAdapter(Context context, List<Comment.Item> videoDetailsList) {
        this.context = context;
        this.commentsList = videoDetailsList;
    }

    @NonNull
    @Override
    public YouTubeCommentsAdapter.CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_youtube_comment, parent, false);
        return new YouTubeCommentsAdapter.CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull YouTubeCommentsAdapter.CommentsViewHolder holder, int position){
        holder.username.setText(commentsList.get(position).getSnippet().getTopLevelComment().getSnippet().getAuthorDisplayName());
        holder.comment.setText(commentsList.get(position).getSnippet().getTopLevelComment().getSnippet().getTextOriginal());
        Glide.with(context)
                .load(commentsList.get(position).getSnippet().getTopLevelComment().getSnippet().getAuthorProfileImageUrl())
                .into(holder.user_thumbnail);
    }

    @Override
    public int getItemCount(){
        return commentsList.size()==0 ? 0: commentsList.size();
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder{

        ImageView user_thumbnail;
        TextView username, comment;

        public CommentsViewHolder(View itemView) {
            super(itemView);

            user_thumbnail = itemView.findViewById(R.id.user_thumbnail);
            username = itemView.findViewById(R.id.user_username);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
