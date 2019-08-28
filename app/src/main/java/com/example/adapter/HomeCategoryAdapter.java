package com.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.item.ItemCategory;
import com.squareup.picasso.Picasso;
import com.viaviweb.recipebox.R;

import java.util.ArrayList;


public class HomeCategoryAdapter extends RecyclerView.Adapter<HomeCategoryAdapter.ItemRowHolder> {

    private ArrayList<ItemCategory> dataList;
    private Context mContext;


    public HomeCategoryAdapter(Context context, ArrayList<ItemCategory> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home_cat_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemCategory singleItem = dataList.get(position);

        Picasso.get().load(singleItem.getCategoryImageBig()).placeholder(R.drawable.place_holder_small).into(holder.image);
        holder.text_cat.setText(singleItem.getCategoryName());

     }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image, image_thumb;
        private TextView text_cat;
        private RelativeLayout lyt_parent;

        private ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.recipe_image);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
             text_cat=itemView.findViewById(R.id.text_cat);

        }
    }
}
