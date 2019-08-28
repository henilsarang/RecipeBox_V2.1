package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.favorite.DatabaseHelper;
import com.example.item.ItemLatest;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.example.util.PopUpAds;
import com.github.ornolfr.ratingview.RatingView;
import com.squareup.picasso.Picasso;
import com.viaviweb.recipebox.ActivityDetail;
import com.viaviweb.recipebox.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class LatestAdapter extends RecyclerView.Adapter<LatestAdapter.ItemRowHolder> {

    private ArrayList<ItemLatest> dataList;
    private Context mContext;
    private DatabaseHelper databaseHelper;
    private String s_title, s_image, s_ing,s_dir, s_type, s_play_id;

    public LatestAdapter(Context context, ArrayList<ItemLatest> dataList) {
        this.dataList = dataList;
        this.mContext = context;
        databaseHelper = new DatabaseHelper(mContext);
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

        holder.image_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_title=singleItem.getRecipeName();
                s_image=singleItem.getRecipeImageBig();
                s_ing=singleItem.getRecipeIngredient();
                s_dir=singleItem.getRecipeDirection();
                s_type=singleItem.getRecipeType();
                s_play_id=singleItem.getRecipePlayId();

                (new SaveTask(mContext)).execute(s_image);
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

    @SuppressLint("StaticFieldLeak")
    public class SaveTask extends AsyncTask<String, String, String> {
        private Context context;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        private SaveTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub

            try {

                myFileUrl = new URL(args[0]);

                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + Constant.DOWNLOAD_FOLDER_PATH);
                dir.mkdirs();
                String fileName = idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String args) {
            // TODO Auto-generated method stub

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            if (s_type.equals("video")) {
                share.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_recipe_title)+s_title + "\n" + mContext.getString(R.string.share_recipe_ingredient)+Html.fromHtml(s_ing)+ "\n" +mContext.getString(R.string.share_recipe_direction)+Html.fromHtml(s_dir) + "\n" + mContext.getString(R.string.share_recipe_video) + "\n" + "https://www.youtube.com/watch?v=" + s_play_id + "\n" + mContext.getString(R.string.share_message) + "\n" + "https://play.google.com/store/apps/details?id=" + mContext.getPackageName());
            } else {
                share.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.share_recipe_title)+s_title + "\n" + mContext.getString(R.string.share_recipe_ingredient)+Html.fromHtml(s_ing) + "\n" +mContext.getString(R.string.share_recipe_direction)+Html.fromHtml(s_dir)+ "\n" + mContext.getString(R.string.share_message) + "\n" + "https://play.google.com/store/apps/details?id=" + mContext.getPackageName());
            }
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
            mContext.startActivity(Intent.createChooser(share, "Share Image"));
        }
    }
}
