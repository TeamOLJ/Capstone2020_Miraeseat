package com.capstondesign.miraeseat.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    final String SEARCH_WORD="search_word";
    String word;
    ViewGroup rootView = null;

    ArrayList<HallClass> hall_result;
    ArrayList<PlayClass> play_result;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        word = getArguments().getString(SEARCH_WORD).trim();

        hall_result = new ArrayList<HallClass>();
        play_result = new ArrayList<PlayClass>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                hall_result = SearchAPI.GetResult_Hall(word);
                play_result = SearchAPI.GetResult_Play(null, word);
            }
        }).start();


        SearchedItem searchedItem = new SearchedItem(getContext(), inflater, container, hall_result);

        rootView = searchedItem.createRootView();
        return rootView;
    }

}
