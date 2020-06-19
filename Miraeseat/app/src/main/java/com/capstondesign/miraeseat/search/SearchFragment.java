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
    final String SEARCH_WORD = "search_word";
    String word;
    ViewGroup rootView = null;

    ArrayList<HallClass> hall_result;
    ArrayList<PlayClass> play_result;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        word = getArguments().getString(SEARCH_WORD).trim();

        hall_result = new ArrayList<HallClass>();
        play_result = new ArrayList<PlayClass>();

        Thread_hall_result thr = new Thread_hall_result(word);
        Thread_play_result tpr = new Thread_play_result(word);

        try {
            thr.start();
            thr.join();

            tpr.start();
            tpr.join();

        } catch (InterruptedException e) {
            Log.d("Error(SearchFragment):", e.toString());
        }


        //SearchedItem searchedItem = new SearchedItem(getContext(), inflater, container, tpr.getResult());
        SearchedItem searchedItem = new SearchedItem(getContext(), inflater, container, thr.getResult());

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                hall_result = SearchAPI.GetResult_Hall(word);
//                play_result = SearchAPI.GetResult_Play(null, word);
//            }
//        }).start();


        //SearchedItem searchedItem = new SearchedItem(getContext(), inflater, container, hall_result);

        rootView = searchedItem.createRootView();
        return rootView;
    }

    class Thread_hall_result extends Thread {
        ArrayList<HallClass> arrayList;
        String word;

        public Thread_hall_result(String word) {
            this.word = word;
            arrayList = new ArrayList<HallClass>();
        }

        @Override
        public void run() {
            arrayList = SearchAPI.GetResult_Hall(word);
        }

        public ArrayList<HallClass> getResult() {
            return arrayList;
        }
    }


    class Thread_play_result extends Thread {
        ArrayList<PlayClass> arrayList;
        String word;

        public Thread_play_result(String word) {
            this.word = word;
            arrayList = new ArrayList<PlayClass>();
        }

        @Override
        public void run() {
            arrayList = SearchAPI.GetResult_Play(word, null);
        }

        public ArrayList<PlayClass> getResult() {
            return arrayList;
        }
    }
}
