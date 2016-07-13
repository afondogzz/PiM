package com.dogzz.pim.uihandlers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dogzz.pim.R;
import com.dogzz.pim.dataobject.ArticleHeader;
import it.sephiroth.android.library.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

/**
 * Adapter for loading data from List of headers to recycle view
 * Created by afon on 07.07.2016.
 */
public class MyRecyclerAdapter  extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> implements Serializable {
    private List<ArticleHeader> articlesHeaders;
    private Context mContext;

    public MyRecyclerAdapter(Context context, List<ArticleHeader> articlesHeaders) {
        this.articlesHeaders = articlesHeaders;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.headers_list_row, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        ArticleHeader header = articlesHeaders.get(i);

        //Download image using picasso library
        try {
            if (!header.getArticleImageUrl().isEmpty()) {
                Picasso.with(mContext).load(header.getArticleImageUrl())
                        .error(R.drawable.ic_menu_gallery)
                        .placeholder(R.drawable.ic_menu_gallery)
                        .into(customViewHolder.imageView);
            }
        } catch (Exception e) {
            Log.e("ImageLoad", e.getMessage());
        }
        //Setting text view title
        customViewHolder.textView.setText(header.getTitle());
    }

    @Override
    public int getItemCount() {
        return (null != articlesHeaders ? articlesHeaders.size() : 0);
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;

        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.articleimage);
            this.textView = (TextView) view.findViewById(R.id.articletitle);
        }
    }
}
