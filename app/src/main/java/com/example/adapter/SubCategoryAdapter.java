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
import java.util.Locale;


public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ItemRowHolder> {

    private ArrayList<ItemCategory> dataList,mDataList;
    private Context mContext;


    public SubCategoryAdapter(Context context, ArrayList<ItemCategory> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        mDataList = new ArrayList<>();
        mDataList.addAll(dataList);
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sub_cat_item, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(final ItemRowHolder holder, final int position) {
        final ItemCategory singleItem = dataList.get(position);

        Picasso.get().load(singleItem.getCategoryImageBig()).placeholder(R.drawable.place_holder_medium).into(holder.image);
        holder.text_cat.setText(singleItem.getCategoryName());

     }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        private TextView text_cat;
        private RelativeLayout lyt_parent;

        private ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            text_cat=itemView.findViewById(R.id.text_cat);

        }
    }
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        dataList.clear();
        if (charText.length() == 0) {
            dataList.addAll(mDataList);
        } else {
            for (ItemCategory wp : mDataList) {
                if (wp.getCategoryName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    dataList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
