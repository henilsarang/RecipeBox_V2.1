package com.example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.viaviweb.recipebox.R;

import java.util.ArrayList;


public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ItemRowHolder> {

    private ArrayList<String> dataList;
    private Context mContext;

    public IngredientAdapter(Context context, ArrayList<String> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ingredient, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemRowHolder holder, final int position) {
        holder.text.setText(Html.fromHtml(dataList.get(position)));
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public class ItemRowHolder extends RecyclerView.ViewHolder {
        public TextView text;
        private ItemRowHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textIngredient);
        }
    }
}
