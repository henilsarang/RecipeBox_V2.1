package com.example.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.favorite.DatabaseHelper;
import com.example.item.ItemLatest;
import com.example.util.PopUpAds;
import com.squareup.picasso.Picasso;
import com.viaviweb.recipebox.ActivityDetail;
import com.viaviweb.recipebox.R;

import java.util.ArrayList;


public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ItemRowHolder> {

    private ArrayList<ItemLatest> dataList;
    private Context mContext;
    private DatabaseHelper databaseHelper;

    public FavoriteAdapter(Context context, ArrayList<ItemLatest> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        databaseHelper = new DatabaseHelper(mContext);
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_fav_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemLatest singleItem = dataList.get(position);

        Picasso.get().load(singleItem.getRecipeImageBig()).placeholder(R.drawable.place_holder_big).into(holder.image);
        holder.txt_cat.setText(singleItem.getRecipeCategoryName());
        holder.txt_time.setText(singleItem.getRecipeTime());
        holder.txt_recipe.setText(singleItem.getRecipeName());

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopUpAds.ShowInterstitialAds(mContext,singleItem.getRecipeId());
            }
        });

        if (databaseHelper.getFavouriteById(singleItem.getRecipeId())) {
            holder.image_fav.setImageResource(R.drawable.fave_hov);
        } else {
            holder.image_fav.setImageResource(R.drawable.fav_list);
        }

        holder.image_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues fav = new ContentValues();
                if (databaseHelper.getFavouriteById(singleItem.getRecipeId())) {
                    databaseHelper.removeFavouriteById(singleItem.getRecipeId());
                    holder.image_fav.setImageResource(R.drawable.fav_list);
                    Toast.makeText(mContext, mContext.getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                } else {
                    fav.put(DatabaseHelper.KEY_ID, singleItem.getRecipeId());
                    fav.put(DatabaseHelper.KEY_TITLE, singleItem.getRecipeName());
                    fav.put(DatabaseHelper.KEY_IMAGE, singleItem.getRecipeImageBig());
                    fav.put(DatabaseHelper.KEY_TIME, singleItem.getRecipeTime());
                    fav.put(DatabaseHelper.KEY_CAT, singleItem.getRecipeCategoryName());

                    databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                    holder.image_fav.setImageResource(R.drawable.fave_hov);
                    Toast.makeText(mContext, mContext.getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image, image_fav;
        private TextView txt_cat, txt_recipe, txt_time;
        private LinearLayout lyt_parent;

        private ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_recipe);
            image_fav = itemView.findViewById(R.id.img_fav_list);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            txt_cat = itemView.findViewById(R.id.text_cat_name);
            txt_recipe = itemView.findViewById(R.id.text_recipe_name);
            txt_time = itemView.findViewById(R.id.text_time);

        }
    }
}
