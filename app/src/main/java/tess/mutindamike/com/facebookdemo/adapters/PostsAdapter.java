package tess.mutindamike.com.facebookdemo.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import tess.mutindamike.com.facebookdemo.GlideApp;
import tess.mutindamike.com.facebookdemo.R;

import tess.mutindamike.com.facebookdemo.models.Posts;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {
    private Context context;
    private List<Posts> postsList = Collections.emptyList();;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_single_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Posts artist = postsList.get(position);
        holder.title.setText(artist.getPost_title());
        holder.count.setText(artist.getLikes() + " likes");

        // loading album cover using Glide library
        GlideApp
                .with(context).
                load(artist.getPost_image())
                .thumbnail(0.2f)
                .into(holder.thumbnail);

//        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, SingleArtistActivity.class);
//                intent.putExtra("TOP_IMAGE_URL", artist.getThumbNail());
//                context.startActivity(intent);
//            }
//        });
//        holder.overflow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showPopupMenu(holder.overflow);
//            }
//        });
    }

    public PostsAdapter(Context context, List<Posts> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    public PostsAdapter() {
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;
        public LinearLayout linearLayout;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayoutMain);
        }
    }
}
