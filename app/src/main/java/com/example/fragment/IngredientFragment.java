package com.example.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.adapter.IngredientAdapter;
import com.viaviweb.recipebox.R;

import java.util.ArrayList;

public class IngredientFragment extends Fragment {

    public RecyclerView recyclerView;
    static ArrayList<String> mList;
    IngredientAdapter mAdapter;

    public static IngredientFragment newInstance(ArrayList<String> categoryId) {
        IngredientFragment f = new IngredientFragment();
        mList = categoryId;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ingredient, container, false);
        recyclerView = rootView.findViewById(R.id.vertical_courses_list);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);

        mAdapter = new IngredientAdapter(getActivity(), mList);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

}
