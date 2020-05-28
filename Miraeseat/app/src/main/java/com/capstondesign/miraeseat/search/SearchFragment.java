package com.capstondesign.miraeseat.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    final String SEARCH_WORD = "search_word";
    ViewGroup rootView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ArrayList<String> result = new ArrayList<String>();
        String data = getArguments().getString(SEARCH_WORD).trim();

        SearchedItem searchedItem = new SearchedItem(getContext(), inflater, container, data);
        rootView = searchedItem.createRootView();
        return rootView;
    }
}
