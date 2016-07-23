package com.dogzz.pim.uihandlers;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.dogzz.pim.MainActivity;
import com.dogzz.pim.R;
import com.dogzz.pim.dataobject.ArticleHeader;
import it.sephiroth.android.library.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

/**
 * Adapter for loading data from List of headers to recycle view
 * Created by dogzz on 07.07.2016.
 */
public class MyRecyclerAdapter  extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> implements Serializable {
    private List<ArticleHeader> articlesHeaders;
    private Context mContext;
    private int selectedPosition = -1;

    public MyRecyclerAdapter(Context context, List<ArticleHeader> articlesHeaders) {
        this.articlesHeaders = articlesHeaders;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.headers_list_row, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        ArticleHeader header = articlesHeaders.get(position);
        try {
            if (!header.getArticleImageUrl().isEmpty()) {
                customViewHolder.articleImage.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(header.getArticleImageUrl())
                        .resizeDimen(R.dimen.image_view_width,R.dimen.image_view_height)
                        .error(R.drawable.ic_menu_gallery)
                        .placeholder(R.drawable.ic_menu_gallery)
                        .into(customViewHolder.articleImage);
            } else {
                customViewHolder.articleImage.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("ImageLoad", e.getMessage());
        }
        //Setting text view title
        customViewHolder.articleTitle.setText(header.getTitle());
        if (header.isOffline()) {
            customViewHolder.isSavedImage.setVisibility(View.VISIBLE);
        } else {
            customViewHolder.isSavedImage.setVisibility(View.GONE);
        }
        boolean showDescription = mContext.getResources().getBoolean(R.bool.show_description);

        if (header.getType() == 1 || showDescription) {
            customViewHolder.articleSubTitle.setVisibility(View.VISIBLE);
            customViewHolder.articleSubTitle.setText(header.getSubTitle());
        } else {
            customViewHolder.articleSubTitle.setVisibility(View.GONE);
            customViewHolder.articleSubTitle.setText("");
        }

        if(selectedPosition == position){
//            customViewHolder.articleTitle.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
//            customViewHolder.articleTitle.setTextColor(Color.WHITE);
//            customViewHolder.articleSubTitle.setTextColor(Color.WHITE);
            customViewHolder.articleCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
        }else{
//            customViewHolder.articleTitle.setBackgroundColor(Color.TRANSPARENT);
            customViewHolder.articleCard.setCardBackgroundColor(mContext.getResources().getColor(R.color.cardview_light_background));
            if (header.isRead()) {
                customViewHolder.articleTitle.setTextColor(mContext.getResources().getColor(R.color.textColorGrayed));
                customViewHolder.articleSubTitle.setTextColor(mContext.getResources().getColor(R.color.textColorGrayed));
            } else {
                customViewHolder.articleTitle.setTextColor(mContext.getResources().getColor(R.color.textColor));
                customViewHolder.articleSubTitle.setTextColor(mContext.getResources().getColor(R.color.textColor));
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != articlesHeaders ? articlesHeaders.size() : 0);
    }

    public void selectItem(int position) {
        notifyItemChanged(selectedPosition);
        selectedPosition = position;
        notifyItemChanged(selectedPosition);
    }

    public void unselectAllItems() {
        notifyItemChanged(selectedPosition);
        selectedPosition = -1;
        notifyItemChanged(selectedPosition);
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView articleImage;
        ImageView isSavedImage;
        TextView articleTitle;
        TextView articleSubTitle;
        CardView articleCard;

        CustomViewHolder(View view) {
            super(view);
            this.articleCard = (CardView) view.findViewById(R.id.articlecard);
            this.articleImage = (ImageView) view.findViewById(R.id.articleimage);
            this.isSavedImage = (ImageView) view.findViewById(R.id.issaved);
            this.articleTitle = (TextView) view.findViewById(R.id.articletitle);
            this.articleSubTitle = (TextView) view.findViewById(R.id.articlesubtitle);
        }
    }
}
