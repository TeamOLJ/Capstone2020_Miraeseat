//package com.capstondesign.miraeseat;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.IBinder;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//
//import com.bumptech.glide.Glide;
//import com.capstondesign.miraeseat.hall.HallInfo;
//import com.capstondesign.miraeseat.search.PlayClass;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserFactory;
//
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URL;
//import java.util.ArrayList;
//
//public class Main_PopularPlay extends Service {
//    final static private String KOPIS_key= "***REMOVED***";
//    Context ctx;
//    ArrayList<PlayClass> datas;
//
//    Main_PopularPlay(Context ctx) {
//        this.ctx=ctx;
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }   //서비스로 만드려는 시도..
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//         datas = PopularPlay();
//        CreateButton(((MainActivity)ctx).findViewById(R.id.view_theater));
//    }
//
//    static public ArrayList<PlayClass> PopularPlay() {
//        ArrayList<PlayClass> datas = new ArrayList<PlayClass>();
//        int count=5;
//
//        //이번 주의 인기 공연. 서울 한정.
//        String PopularPlayURL = "http://kopis.or.kr/openApi/restful/boxoffice?service="+KOPIS_key+"&ststype=week&date="+PlayClass.getToday()+"&area=11";
//
//        String[] index = {"prfplcnm", "poster"};
//        boolean[] index_check = new boolean[index.length];
//        String[] index_data = new String[index.length];
//
//        try {
//            URL url = new URL(PopularPlayURL);
//            InputStream IS = url.openStream();
//
//            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//            XmlPullParser xpp = factory.newPullParser();
//            xpp.setInput(new InputStreamReader(IS, "UTF-8"));
//
//
//            xpp.next();
//
//            String tag;
//
//            int eventType = xpp.getEventType(); //태그 구분용
//
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                switch (eventType) {
//
//                    case XmlPullParser.START_TAG:
//                        tag = xpp.getName();
//
//                        for (int i = 0; i < index.length; ++i) {
//                            if (tag.equals(index[i])) {
//                                index_check[i] = true;
//                                break;
//                            }
//                        }
//
//
//                    case XmlPullParser.TEXT:
//                        tag = xpp.getText();
//
//                        for (int i = 0; i < index.length; ++i) {
//                            if (index_check[i]) {
//                                index_data[i] = tag;
//                                index_check[i] = false;
//                                break;
//                            }
//                        }
//
//                        break;
//
//
//                    case XmlPullParser.END_TAG:
//
//                        if (xpp.getName().equals("db")) {
//                            datas.add(new PlayClass(index_data[0], index_data[1]));
//                            count-=1;
//
//                            if(count==0) return datas;
//                        }
//                        break;
//
//                }
//                eventType = xpp.next();
//            }
//
//        } catch (Exception e) {
//            Log.d("Error(PopularPlay):", e.toString());
//        }
//        return null;
//    }
//
//    private void CreateButton(View view) {
//        LinearLayout view_theater = (LinearLayout)view;
//        int width = (int) getResources().getDimension(R.dimen.poster_width);
//        int height = (int) getResources().getDimension(R.dimen.poster_height);
//        int margin = (int) getResources().getDimension(R.dimen.poster_margin);
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
//        params.setMargins(margin, margin, margin, margin);
//
//        for (int i = 0; i < 5; ++i) {
//            ImageButton imageButton = new ImageButton(this);
//
//            Glide.with(this).load(datas.get(i).getPoster()).into(imageButton);
//            //imageButton.setImageResource();
//            imageButton.setLayoutParams(params);
//            imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                imageButton.setElevation(1);
//            }
//            imageButton.setOnClickListener(PosterOnClickListner);  //다른 버튼 클릭 리스너
//            view_theater.addView(imageButton);
//        }
//    }
//
//    public View.OnClickListener PosterOnClickListner = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            //int selected_item = (int) v.getId();
//            //Intent act_information = new Intent(getApplicationContext(), HallInfo.class);
//            //startActivity(act_information);
//            Toast.makeText(ctx, "Click", Toast.LENGTH_SHORT).show();
//        }
//    };
//}
