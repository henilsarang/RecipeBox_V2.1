package com.example.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.adapter.SubCategoryAdapter;
import com.example.item.ItemCategory;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.example.util.RecyclerTouchListener;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.viaviweb.recipebox.ActivityMain;
import com.viaviweb.recipebox.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SubCategoryFragment extends Fragment {

    ArrayList<ItemCategory> mListItem;
    public RecyclerView recyclerView;
    SubCategoryAdapter subCategoryAdapter;
    private ProgressBar progressBar;
    private LinearLayout lyt_not_found;
    String Id, Name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);

        if (getArguments() != null) {
            Id = getArguments().getString("Id");
            Name = getArguments().getString("name");
        }
        mListItem = new ArrayList<>();

        lyt_not_found = rootView.findViewById(R.id.lyt_not_found);
        progressBar = rootView.findViewById(R.id.progressBar);
        recyclerView = rootView.findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", "get_sub_cat");
        jsObj.addProperty("cat_id", Id);
        if (JsonUtils.isNetworkAvailable(requireActivity())) {
            new getLatest(API.toBase64(jsObj.toString())).execute(Constant.API_URL);
        }

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                if (Constant.SAVE_ADS_FULL_ON_OFF.equals("true")) {

                    Constant.AD_COUNT++;
                    if (Constant.AD_COUNT == Integer.parseInt(Constant.SAVE_ADS_CLICK)) {
                        Constant.AD_COUNT = 0;
                        final InterstitialAd mInterstitial = new InterstitialAd(requireActivity());
                        mInterstitial.setAdUnitId(Constant.SAVE_ADS_FULL_ID);
                        AdRequest adRequest;
                        if (JsonUtils.personalization_ad) {
                            adRequest = new AdRequest.Builder()
                                    .build();
                        } else {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            adRequest = new AdRequest.Builder()
                                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                    .build();
                        }
                        mInterstitial.loadAd(adRequest);
                        mInterstitial.setAdListener(new AdListener() {
                            @Override
                            public void onAdLoaded() {
                                // TODO Auto-generated method stub
                                super.onAdLoaded();
                                if (mInterstitial.isLoaded()) {
                                    mInterstitial.show();
                                }
                            }

                            public void onAdClosed() {
                                String categoryName = mListItem.get(position).getCategoryName();
                                Bundle bundle = new Bundle();
                                bundle.putString("name", categoryName);
                                bundle.putString("Id", mListItem.get(position).getCategoryId());

                                FragmentManager fm = getFragmentManager();
                                CategoryListFragment categoryListFragment = new CategoryListFragment();
                                categoryListFragment.setArguments(bundle);
                                assert fm != null;
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.hide(SubCategoryFragment.this);
                                ft.add(R.id.fragment1, categoryListFragment, categoryName);
                                ft.addToBackStack(categoryName);
                                ft.commit();
                                ((ActivityMain) requireActivity()).setToolbarTitle(categoryName);
                            }

                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                String categoryName = mListItem.get(position).getCategoryName();
                                Bundle bundle = new Bundle();
                                bundle.putString("name", categoryName);
                                bundle.putString("Id", mListItem.get(position).getCategoryId());

                                FragmentManager fm = getFragmentManager();
                                CategoryListFragment categoryListFragment = new CategoryListFragment();
                                categoryListFragment.setArguments(bundle);
                                assert fm != null;
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.hide(SubCategoryFragment.this);
                                ft.add(R.id.fragment1, categoryListFragment, categoryName);
                                ft.addToBackStack(categoryName);
                                ft.commit();
                                ((ActivityMain) requireActivity()).setToolbarTitle(categoryName);
                            }
                        });
                    } else {
                        String categoryName = mListItem.get(position).getCategoryName();
                        Bundle bundle = new Bundle();
                        bundle.putString("name", categoryName);
                        bundle.putString("Id", mListItem.get(position).getCategoryId());

                        FragmentManager fm = getFragmentManager();
                        CategoryListFragment categoryListFragment = new CategoryListFragment();
                        categoryListFragment.setArguments(bundle);
                        assert fm != null;
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.hide(SubCategoryFragment.this);
                        ft.add(R.id.fragment1, categoryListFragment, categoryName);
                        ft.addToBackStack(categoryName);
                        ft.commit();
                        ((ActivityMain) requireActivity()).setToolbarTitle(categoryName);
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        setHasOptionsMenu(true);
        return rootView;
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
                        ItemCategory objItem = new ItemCategory();
                        objItem.setCategoryId(objJson.getString(Constant.SUB_CATEGORY_ID));
                        objItem.setCategoryName(objJson.getString(Constant.SUB_CATEGORY_NAME));
                        objItem.setCategoryImageBig(objJson.getString(Constant.SUB_CATEGORY_IMAGE));

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
        if (getActivity() != null) {
            subCategoryAdapter = new SubCategoryAdapter(getActivity(), mListItem);
            recyclerView.setAdapter(subCategoryAdapter);

            if (subCategoryAdapter.getItemCount() == 0) {
                lyt_not_found.setVisibility(View.VISIBLE);
            } else {
                lyt_not_found.setVisibility(View.GONE);
            }
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

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();

        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                if (!hasFocus) {
                    searchMenuItem.collapseActionView();
                    searchView.setQuery("", false);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                if (subCategoryAdapter != null) {
                    subCategoryAdapter.filter(newText);
                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }
        });

    }
}
