package com.viaviweb.recipebox;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.ReviewAdapter;
import com.example.favorite.DatabaseHelper;
import com.example.fragment.IngredientFragment;
import com.example.item.ItemLatest;
import com.example.item.ItemReview;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.example.youtube.YoutubePlay;
import com.github.ornolfr.ratingview.RatingView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class ActivityDetail extends AppCompatActivity {

    LinearLayout adLayout;
    boolean isShow = false;
    int scrollRange = -1;
    ImageView imageView, img_play, img_rate, img_share;
    String Id;
    RatingView ratingView;
    TextView text_view, text_recipe_name, text_time;
    FragmentManager fragmentManager;
    WebView webView_details;
    CoordinatorLayout main_content;
    LinearLayout lyt_not_found;
    ProgressBar mProgressBar;
    ItemLatest objBean;
    ArrayList<String> mIngredient;
    ArrayList<ItemReview> mListReview;
    ReviewAdapter reviewAdapter;
    Menu menu;
    JsonUtils jsonUtils;
    String rateMsg;
    DatabaseHelper databaseHelper;
    boolean iswhichscreen;
    MyApplication myApplication;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());
        databaseHelper = new DatabaseHelper(ActivityDetail.this);
        objBean = new ItemLatest();
        mIngredient = new ArrayList<>();
        mListReview = new ArrayList<>();
        myApplication = MyApplication.getAppInstance();

        Intent i = getIntent();
        Id = i.getStringExtra("Id");

        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        adLayout = findViewById(R.id.ad_view);
        Intent intent2 = getIntent();
        iswhichscreen = intent2.getBooleanExtra("isNotification", false);
        if (!iswhichscreen) {
            JsonUtils.ShowBannerAds(ActivityDetail.this, adLayout);
        }

        collapsingToolbar.setTitle(" ");

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;

                }
            }
        });

        imageView = findViewById(R.id.backdrop);
        img_play = findViewById(R.id.image_play);
        ratingView = findViewById(R.id.ratingView);
        img_rate = findViewById(R.id.img_rate);
        text_view = findViewById(R.id.text_view);
        img_share = findViewById(R.id.img_share);
        text_recipe_name = findViewById(R.id.text_recipe_name);
        text_time = findViewById(R.id.text_time);
        fragmentManager = getSupportFragmentManager();
        webView_details = findViewById(R.id.webView_details);
        main_content = findViewById(R.id.main_content);
        lyt_not_found = findViewById(R.id.lyt_not_found);
        mProgressBar = findViewById(R.id.progressBar);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_single_recipe");
        jsObj.addProperty("recipe_id", Id);
        if (JsonUtils.isNetworkAvailable(ActivityDetail.this)) {
            new getDetail(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class getDetail extends AsyncTask<String, Void, String> {

        String base64;

        private getDetail(String base64) {
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
                        if (objJson.has("status")) {
                            lyt_not_found.setVisibility(View.VISIBLE);
                        } else {
                            objBean.setRecipeId(objJson.getString(Constant.LATEST_RECIPE_ID));
                            objBean.setRecipeName(objJson.getString(Constant.LATEST_RECIPE_NAME));
                            objBean.setRecipeType(objJson.getString(Constant.LATEST_RECIPE_TYPE));
                            objBean.setRecipeTime(objJson.getString(Constant.LATEST_RECIPE_TIME));
                            objBean.setRecipeIngredient(objJson.getString(Constant.LATEST_RECIPE_INGREDIENT));
                            objBean.setRecipeDirection(objJson.getString(Constant.LATEST_RECIPE_DIRE));
                            objBean.setRecipeImageBig(objJson.getString(Constant.LATEST_RECIPE_IMAGE_BIG));
                            objBean.setRecipePlayId(objJson.getString(Constant.LATEST_RECIPE_VIDEO_PLAY));
                            objBean.setRecipeUrl(objJson.getString(Constant.LATEST_RECIPE_URL));
                            objBean.setRecipeViews(objJson.getString(Constant.LATEST_RECIPE_VIEW));
                            objBean.setRecipeAvgRate(objJson.getString(Constant.LATEST_RECIPE_AVR_RATE));
                            objBean.setRecipeTotalRate(objJson.getString(Constant.LATEST_RECIPE_TOTAL_RATE));
                            objBean.setRecipeCategoryName(objJson.getString(Constant.LATEST_RECIPE_CAT_NAME));

                            JSONArray jsonArrayChild = objJson.getJSONArray(Constant.ARRAY_NAME_REVIEW);
                            if (jsonArrayChild.length() > 0 && !jsonArrayChild.get(0).equals("")) {
                                for (int j = 0; j < jsonArrayChild.length(); j++) {
                                    JSONObject objChild = jsonArrayChild.getJSONObject(j);
                                    ItemReview item = new ItemReview();
                                    item.setReviewName(objChild.getString(Constant.REVIEW_NAME));
                                    item.setReviewRate(objChild.getString(Constant.REVIEW_RATE));
                                    item.setReviewMessage(objChild.getString(Constant.REVIEW_MESSAGE));
                                    mListReview.add(item);
                                }
                            }
                            displayData();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void displayData() {

        text_view.setText(JsonUtils.Format(Integer.parseInt(objBean.getRecipeViews())));
        text_recipe_name.setText(objBean.getRecipeName());
        text_time.setText(objBean.getRecipeTime());
        ratingView.setRating(Float.parseFloat(objBean.getRecipeAvgRate()));
        WebSettings webSettings = webView_details.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String text = "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/myfonts/Montserrat-Regular.ttf\")}body,* {font-family: MyFont; color:#666666; font-size: 13px;line-height:1.2}img{max-width:100%;height:auto; border-radius: 3px;}</style>";
        webView_details.loadDataWithBaseURL("", text + "<div>" + objBean.getRecipeDirection() + "</div>", "text/html", "utf-8", null);

        Picasso.get().load(objBean.getRecipeImageBig()).placeholder(R.mipmap.app_icon).into(imageView);

        if (objBean.getRecipeType().equals("video")) {
            img_play.setVisibility(View.VISIBLE);
        } else {
            img_play.setVisibility(View.GONE);
        }

        img_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityDetail.this, YoutubePlay.class);
                i.putExtra("id", objBean.getRecipePlayId());
                startActivity(i);
            }
        });

        img_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.LATEST_RECIPE_IDD = objBean.getRecipeId();
                if (myApplication.getIsLogin()) {
                    showRateDialog();
                } else {
                    final PrettyDialog dialog = new PrettyDialog(ActivityDetail.this);
                    dialog.setTitle(getString(R.string.dialog_warning))
                            .setTitleColor(R.color.dialog_text)
                            .setMessage(getString(R.string.login_require))
                            .setMessageColor(R.color.dialog_text)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_close, R.color.dialog_color, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            })
                            .addButton(getString(R.string.dialog_ok), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                    Intent intent_login = new Intent(ActivityDetail.this, SignInActivity.class);
                                    intent_login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent_login.putExtra("isfromdetail", true);
                                    intent_login.putExtra("isid", Constant.LATEST_RECIPE_IDD);
                                    startActivity(intent_login);
                                }
                            })
                            .addButton(getString(R.string.dialog_no), R.color.dialog_white_text, R.color.dialog_color, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });

        img_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new SaveTask(ActivityDetail.this)).execute(objBean.getRecipeImageBig());
            }
        });

        if (!objBean.getRecipeIngredient().isEmpty())
            mIngredient = new ArrayList<>(Arrays.asList(objBean.getRecipeIngredient().split(",")));

        if (!objBean.getRecipeIngredient().isEmpty()) {
            IngredientFragment ingredientFragment = IngredientFragment.newInstance(mIngredient);
            fragmentManager.beginTransaction().replace(R.id.ContainerIngredient, ingredientFragment).commit();
        }

    }


    private void showProgress(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            main_content.setVisibility(View.GONE);
            lyt_not_found.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            main_content.setVisibility(View.VISIBLE);
        }
    }


    private void showAllReview() {
        final Dialog mDialog = new Dialog(ActivityDetail.this, R.style.Theme_AppCompat_Translucent);
        mDialog.setContentView(R.layout.review_all_dialog);
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());
        RecyclerView recyclerView = mDialog.findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(ActivityDetail.this, 1));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        TextView textView_no = mDialog.findViewById(R.id.no_fav);
        TextView text_dialog_review = mDialog.findViewById(R.id.text_dialog_review);
        ImageView image_close_dialog = mDialog.findViewById(R.id.image_close_dialog);

        text_dialog_review.setText(objBean.getRecipeAvgRate());
        image_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        reviewAdapter = new ReviewAdapter(ActivityDetail.this, mListReview);
        recyclerView.setAdapter(reviewAdapter);

        if (reviewAdapter.getItemCount() == 0) {
            textView_no.setVisibility(View.VISIBLE);
        } else {
            textView_no.setVisibility(View.GONE);
        }
        mDialog.show();
    }

    private void showRateDialog() {
        final String deviceId;
        final Dialog mDialog = new Dialog(ActivityDetail.this, R.style.Theme_AppCompat_Translucent);
        mDialog.setContentView(R.layout.rate_dialog);
        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());
        deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        final RatingView ratingView = mDialog.findViewById(R.id.ratingView);
        ImageView image_rate_close = mDialog.findViewById(R.id.image_close);
        final EditText editTextReview = mDialog.findViewById(R.id.edt_d_review);
        ratingView.setRating(0);
        Button button = mDialog.findViewById(R.id.btn_submit);

        image_rate_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextReview.getText().length() == 0) {
                    Toast.makeText(ActivityDetail.this, getString(R.string.require_review), Toast.LENGTH_SHORT).show();
                } else {
                    JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                    jsObj.addProperty("method_name", "recipe_rate");
                    jsObj.addProperty("device_id", deviceId);
                    jsObj.addProperty("post_id", objBean.getRecipeId());
                    jsObj.addProperty("user_id", myApplication.getUserId());
                    jsObj.addProperty("rate", ratingView.getRating());
                    jsObj.addProperty("message", editTextReview.getText().toString());
                    if (JsonUtils.isNetworkAvailable(ActivityDetail.this)) {
                        new SentRating(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
                    }

                    mDialog.dismiss();
                }
            }
        });
        mDialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class SentRating extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;
        String Rate;

        String base64;

        private SentRating(String base64) {
            this.base64 = base64;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ActivityDetail.this);
            pDialog.setMessage(getString(R.string.loading));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0], base64);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.no_data));

            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        rateMsg = objJson.getString("MSG");
                        if (objJson.has(Constant.LATEST_RECIPE_AVR_RATE)) {
                            Rate = objJson.getString(Constant.LATEST_RECIPE_AVR_RATE);
                        } else {
                            Rate = "";
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setRate();
            }

        }

        private void setRate() {
            showToast(rateMsg);
            if (!Rate.isEmpty())
                ratingView.setRating(Float.parseFloat(Rate));
        }
    }

    public void showToast(String msg) {
        Toast.makeText(ActivityDetail.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        this.menu = menu;
        isFavourite();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                if (!iswhichscreen) {
                    super.onBackPressed();
                } else {
                    Intent intent = new Intent(ActivityDetail.this, ActivityMain.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.menu_fav:
                if (objBean.getRecipeId() != null) {
                    ContentValues fav = new ContentValues();
                    if (databaseHelper.getFavouriteById(objBean.getRecipeId())) {
                        databaseHelper.removeFavouriteById(objBean.getRecipeId());
                        menu.getItem(0).setIcon(R.drawable.fav);
                        Toast.makeText(ActivityDetail.this, getString(R.string.favourite_remove), Toast.LENGTH_SHORT).show();
                    } else {
                        fav.put(DatabaseHelper.KEY_ID, objBean.getRecipeId());
                        fav.put(DatabaseHelper.KEY_TITLE, objBean.getRecipeName());
                        fav.put(DatabaseHelper.KEY_IMAGE, objBean.getRecipeImageBig());
                        fav.put(DatabaseHelper.KEY_TIME, objBean.getRecipeTime());
                        fav.put(DatabaseHelper.KEY_CAT, objBean.getRecipeCategoryName());
                        databaseHelper.addFavourite(DatabaseHelper.TABLE_FAVOURITE_NAME, fav, null);
                        menu.getItem(0).setIcon(R.drawable.fave_hov);
                        Toast.makeText(ActivityDetail.this, getString(R.string.favourite_add), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.menu_rating:
                showAllReview();
                break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void isFavourite() {
        if (databaseHelper.getFavouriteById(Id)) {
            menu.getItem(0).setIcon(R.drawable.fave_hov);
        } else {
            menu.getItem(0).setIcon(R.drawable.fav);
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
            if (objBean.getRecipeType().equals("video")) {
                share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_recipe_title) + objBean.getRecipeName() + "\n" + getString(R.string.share_recipe_ingredient) + Html.fromHtml(objBean.getRecipeIngredient()) + "\n" + getString(R.string.share_recipe_direction) + Html.fromHtml(objBean.getRecipeDirection()) + "\n" + getString(R.string.share_recipe_video) + "\n" + "https://www.youtube.com/watch?v=" + objBean.getRecipePlayId() + "\n" + getString(R.string.share_message) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
            } else {
                share.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_recipe_title) + objBean.getRecipeName() + "\n" + getString(R.string.share_recipe_ingredient) + Html.fromHtml(objBean.getRecipeIngredient()) + "\n" + getString(R.string.share_recipe_direction) + Html.fromHtml(objBean.getRecipeDirection()) + "\n" + getString(R.string.share_message) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
            }
            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
            startActivity(Intent.createChooser(share, "Share Image"));
        }
    }
}
