package com.capstondesign.miraeseat.search;

import android.content.res.Resources;
import android.util.Log;

import com.capstondesign.miraeseat.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class SearchAPI {
    final static private String KOPIS_key= "***REMOVED***";
    //API에서 검색어가 포함된 객체를 찾아 일부 정보만 ArrayList로 반환

    /*
    [검색어]로 공연시설명, 공연시설 ID 찾기.
    포스터 필요
     */
    static public ArrayList<HallClass> GetResult_Hall(String search_word) {
        ArrayList<HallClass> datas = new ArrayList<HallClass>();

        String shprfnmfct = "&shprfnmfct=" + search_word;

        String HallURL = "http://www.kopis.or.kr/openApi/restful/prfplc?service=" + KOPIS_key + "&cpage=1&rows=9" + shprfnmfct + "&signgucode=11"; //너무 많아서 우선 서울 한정으로 찾게 해놓음.


        //공연시설명, ID
        String[] index = {"fcltynm", "mt10id"};
        boolean[] index_check = new boolean[index.length];
        String[] index_data = new String[index.length];


        try {

            URL url = new URL(HallURL);
            InputStream IS = url.openStream();


            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(IS, "UTF-8"));

            xpp.next();
            int eventType = xpp.getEventType(); //태그 구분용

            String tag;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        for (int i = 0; i < index.length; ++i) {
                            if (tag.equals(index[i])) {
                                index_check[i] = true;
                                break;
                            }
                        }

                        break;


                    case XmlPullParser.TEXT:
                        tag = xpp.getText();

                        for (int i = 0; i < index.length; ++i) {
                            if (index_check[i]) {
                                index_data[i] = tag;
                                index_check[i] = false;
                                break;
                            }
                        }

                        break;


                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();

                        if (tag.equals("db")) {
                            datas.add(new HallClass(index_data[0], index_data[1]));
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.d("Error(GetResult_Hall):", e.toString());
        }

        return datas;
    }


    //[검색어]로 공연명, 공연 ID, 공연 포스터 찾기
    static public ArrayList<PlayClass> GetResult_Play(String search_word) {

        ArrayList<PlayClass> datas = new ArrayList<PlayClass>();

        String shprfnm = "&shprfnm=" + search_word;  //공연명

        String[] date = PlayClass.getThreeMonthDate();

        //3개월 내에 공연 이력이 있는 경우 모두 출력
        String PlayURL = "http://www.kopis.or.kr/openApi/restful/pblprfr?service=" + KOPIS_key + "&stdate=" + date[0] + "&eddate=" + date[1] + "&rows=9&cpage=1" + shprfnm + "&signgucode=11"; //서울 한정


        //공연ID, 공연명, 포스터
        String[] index = {"mt20id", "prfnm", "poster"};
        boolean[] index_check = new boolean[index.length];
        String[] index_data = new String[index.length];

        try {
            URL url = new URL(PlayURL);
            InputStream IS = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(IS, "UTF-8"));


            xpp.next();

            String tag;

            int eventType = xpp.getEventType(); //태그 구분용

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        for (int i = 0; i < index.length; ++i) {
                            if (tag.equals(index[i])) {
                                index_check[i] = true;
                                break;
                            }
                        }


                    case XmlPullParser.TEXT:
                        tag = xpp.getText();

                        for (int i = 0; i < index.length; ++i) {
                            if (index_check[i]) {
                                index_data[i] = tag;
                                index_check[i] = false;
                                break;
                            }
                        }

                        break;


                    case XmlPullParser.END_TAG:

                        if (xpp.getName().equals("db")) {

                            datas.add(new PlayClass(index_data[0], index_data[1], index_data[2]));
                        }
                        break;

                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.d("Error(GetResult_Play):", e.toString());
        }

        return datas;
    }

}
