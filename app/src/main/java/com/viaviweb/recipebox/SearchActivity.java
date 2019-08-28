package com.viaviweb.recipebox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.adapter.CategoryListAdapter;
import com.example.item.ItemLatest;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SearchActivity extends AppCompatActivity {

    ArrayList<ItemLatest> mListItem;
    public RecyclerView recyclerView;
    CategoryListAdapter categoryListAdapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    String search;
    LinearLayout adLayout;
    JsonUtils jsonUtils;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        JsonUtils.setStatusBarGradiant(SearchActivity.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.search);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        Intent intent = getIntent();
        search = intent.getStringExtra("search");

        mListItem = new ArrayList<>();

        lyt_not_found = findViewById(R.id.lyt_not_found);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(SearchActivity.this, 1));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        adLayout = findViewById(R.id.adview);
        JsonUtils.ShowBannerAds(SearchActivity.this, adLayout);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_search_recipe");
        jsObj.addProperty("search_text", search);
        if (JsonUtils.isNetworkAvailable(SearchActivity.this)) {
            new getLatest(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class getLatest extends AsyncTask<String, Void, String> {

        String base64;

        private getLatest(String base64) {
            this.base64 = base64;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0], base64);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            if (null == result || result.length() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if(objJson.has("status")){
                            lyt_not_found.setVisibility(View.VISIBLE);
                        }else {
                        ItemLatest objItem = new ItemLatest();
                        objItem.setRecipeId(objJson.getString(Constant.LATEST_RECIPE_ID));
                        objItem.setRecipeName(objJson.getString(Constant.LATEST_RECIPE_NAME));
                        objItem.setRecipeType(objJson.getString(Constant.LATEST_RECIPE_TYPE));
                        objItem.setRecipePlayId(objJson.getString(Constant.LATEST_RECIPE_VIDEO_PLAY));
                        objItem.setRecipeImageSmall(objJson.getString(Constant.LATEST_RECIPE_IMAGE_SMALL));
                        objItem.setRecipeImageBig(objJson.getString(Constant.LATEST_RECIPE_IMAGE_BIG));
                        objItem.setRecipeViews(objJson.getString(Constant.LATEST_RECIPE_VIEW));
                        objItem.setRecipeTime(objJson.getString(Constant.LATEST_RECIPE_TIME));
                        objItem.setRecipeCategoryName(objJson.getString(Constant.LATEST_RECIPE_CAT_NAME));
                        objItem.setRecipeTotalRate(objJson.getString(Constant.LATEST_RECIPE_TOTAL_RATE));
                        objItem.setRecipeAvgRate(objJson.getString(Constant.LATEST_RECIPE_AVR_RATE));
                        objItem.setRecipeDirection(objJson.getString(Constant.LATEST_RECIPE_DIRE));
                        objItem.setRecipeIngredient(objJson.getString(Constant.LATEST_RECIPE_INGREDIENT));

                        mListItem.add(objItem);
                    }}
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }
        }
    }

    private void displayData() {

        categoryListAdapter = new CategoryListAdapter(SearchActivity.this, mListItem);
        recyclerView.setAdapter(categoryListAdapter);

        if (categoryListAdapter.getItemCount() == 0) {
            lyt_not_found.setVisibility(View.VISIBLE);
        } else {
            lyt_not_found.setVisibility(View.GONE);
        }

    }

    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}
