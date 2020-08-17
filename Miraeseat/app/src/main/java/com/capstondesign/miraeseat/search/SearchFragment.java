package com.capstondesign.miraeseat.search;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchFragment extends Fragment {
    final static String TAG = "SearchFragment";

    final String SEARCH_WORD = "search_word";
    String word;
    ViewGroup rootView = null;

    FirebaseFirestore db;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        word = getArguments().getString(SEARCH_WORD).trim();

        Thread_hall_result thr = new Thread_hall_result(word);

        try {

            thr.start();
            thr.join();

        } catch (InterruptedException e) {
            Log.d("Error(SearchFragment):", e.toString());
        }


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
        ArrayList<TheaterClass> arrayList;
        ArrayList<HallDetailedClass> finalList;
        String word;

        public Thread_hall_result(String word) {
            this.word = word;
            finalList = new ArrayList<HallDetailedClass>();
        }

        @Override
        public void run() {
            db = FirebaseFirestore.getInstance();

            // 1. API를 이용해 공연시설명과 ID 목록 획득
            arrayList = SearchAPI.GetResult_Theater(word);

            // 2. DB를 검색하여 공연시설명+공연장명의 리스트 반환
            int count = arrayList.size();

            for(int i = 0; i < count; i++) {

                final TheaterClass currentItem = arrayList.get(i);

                db.collection("TheaterInfo").whereEqualTo("theaterCode", currentItem.getTheater_ID()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();

                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        // arrayList의 내용과 각 문서의 내용을 finalList에 추가
                                        finalList.add(new HallDetailedClass(currentItem.getTheater_name()+" "+document.getString("hallName"),
                                                currentItem.getTheater_ID()+"-"+document.getString("hallCode"),
                                                document.getString("hallImage"), document.getBoolean("isSeatplan")));

                                        Log.d(TAG, "new HallDetailedClass: "+currentItem.getTheater_name()+" "+document.getString("hallName")+"/"+
                                                currentItem.getTheater_ID()+"-"+document.getString("hallCode")+"/"+
                                                document.getString("hallImage")+"/"+document.getBoolean("isSeatplan"));
                                    }
                                }
                                else {
                                    Log.d(TAG, "Error reading DB", task.getException());
                                }
                            }
                        });
            }
        }

        public ArrayList<HallDetailedClass> getResult() {
            return finalList;
        }
    }

}
