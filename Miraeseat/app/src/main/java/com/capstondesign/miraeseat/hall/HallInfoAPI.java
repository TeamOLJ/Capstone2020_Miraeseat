package com.capstondesign.miraeseat.hall;

import android.util.Log;

import com.capstondesign.miraeseat.search.HallClass;
import com.capstondesign.miraeseat.search.PlayClass;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class HallInfoAPI {
    final static private String KOPIS_key = "***REMOVED***";
    /*
    공연시설 : tag(공연시설 ID)로 GetDetails_Hall 구하기 / GetDetails_Hall의 공연시설명으로 Get_Play
    공연 : tag(공연 ID)로 공연시설 ID 얻기 / 얻은 공연 시설 ID로 GetDetails_Hall 구하기 / GetDetails_Hall의 공연시설명으로 Get_Play
     */

    //[공연시설 ID]로 공연시설명, 위도, 경도 얻기
    static public HallClass GetDetails_Hall(String id) {
        HallClass data = new HallClass();

        String Play_DetailURL = "http://www.kopis.or.kr/openApi/restful/prfplc/" + id + "?service=" + KOPIS_key;


        //시설명, 위도, 경도
        boolean inFcltynm = false, inLa = false, inLo = false;


        try {
            URL url = new URL(Play_DetailURL);
            InputStream IS = url.openStream();


            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(IS, "UTF-8"));

            xpp.next();
            int eventType = xpp.getEventType();

            String tag;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        tag = xpp.getName();

                        if (tag.equals("fcltynm")) {
                            inFcltynm = true;
                        } else if (tag.equals("la")) {
                            inLa = true;
                        } else if (tag.equals("lo")) {
                            inLo = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        tag = xpp.getText();

                        if (inFcltynm) {
                            data.setHall_name(tag);
                            inFcltynm = false;
                        } else if (inLa) {
                            data.setLatitude(Double.parseDouble(tag));
                            inLa = false;
                        } else if (inLo) {
                            data.setLongitude(Double.parseDouble(tag));
                            inLo = false;
                        }
                        break;


                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();

                        if (tag.equals("db")) {
                            return data;
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.d("Error(GetDetails_Hall):", e.toString());
        }

        return null;
    }


    //[공연시설명]으로 공연 중인 공연 정보들 얻기
    static public ArrayList<PlayClass> Get_Play(String hall_name) {
        ArrayList<PlayClass> datas = new ArrayList<PlayClass>();
        String[] date = PlayClass.getThreeMonthDate();

        String shprfnmfct = "&shprfnmfct=" + hall_name;


        String PlayURL = "http://www.kopis.or.kr/openApi/restful/pblprfr?service=" + KOPIS_key + "&stdate=" + date[0] + "&eddate=" + date[1] + "&cpage=1&rows=10" + shprfnmfct;

        //공연명, 시작 날짜, 종료 날짜, 포스터, 공연 상태
        String[] index = {"prfnm", "prfpdfrom", "prfpdto", "poster", "prfstate"};
        boolean[] index_check = new boolean[index.length];
        String[] index_data = new String[index.length];


        try {

            URL url = new URL(PlayURL);
            InputStream IS = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(IS, "UTF-8"));

            xpp.next();
            int eventType = xpp.getEventType();

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
                            datas.add(new PlayClass(index_data[0], (index_data[1] + "-" + index_data[2]), index_data[3], index_data[4]));
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.d("Error(Get_Play):", e.toString());
        }
        return datas;
    }
}