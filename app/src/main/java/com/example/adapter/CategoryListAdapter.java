package com.example.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.item.ItemLatest;
import com.example.util.JsonUtils;
import com.example.util.PopUpAds;
import com.github.ornolfr.ratingview.RatingView;
import com.squareup.picasso.Picasso;
import com.viaviweb.recipebox.ActivityDetail;
import com.viaviweb.recipebox.R;

import java.util.ArrayList;


public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ItemRowHolder> {

    private ArrayList<ItemLatest> dataList;
    private Context mContext;


    public CategoryListAdapter(Context context, ArrayList<ItemLatest> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_latest_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemLatest singleItem = dataList.get(position);

        Picasso.get().load(singleItem.getRecipeImageBig()).placeholder(R.drawable.place_holder_big).into(holder.image);
        holder.txt_cat.setText(singleItem.getRecipeCategoryName());
        holder.txt_time.setText(singleItem.getRecipeTime());
        holder.txt_recipe.setText(singleItem.getRecipeName());
        holder.txt_view.setText(JsonUtils.Format(Integer.parseInt(singleItem.getRecipeViews())));
        holder.ratingView.setRating(Float.parseFloat(singleItem.getRecipeAvgRate()));

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpAds.ShowInterstitialAds(mContext,singleItem.getRecipeId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image, image_fav, image_share;
        private TextView txt_cat, txt_recipe, txt_view, txt_time;
        private LinearLayout lyt_parent;
        private RatingView ratingView;
        private ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_recipe);
            image_fav = itemView.findViewById(R.id.img_fav_list);
            image_share = itemView.findViewById(R.id.img_share);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            txt_cat = itemView.findViewById(R.id.text_cat_name);
            txt_recipe = itemView.findViewById(R.id.text_recipe_name);
            txt_view = itemView.findViewById(R.id.text_view);
            txt_time = itemView.findViewById(R.id.text_time);
            ratingView=itemView.findViewById(R.id.ratingView);
        }
    }
}
