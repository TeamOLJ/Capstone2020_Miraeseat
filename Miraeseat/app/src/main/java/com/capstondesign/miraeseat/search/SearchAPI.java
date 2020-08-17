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
    //문제점 : 검색결과가 없을 때 에러코드가 안나옴. 어떻게 처리해줘야할지 찾아봐야함.


    /*
    [검색어==공연시설명]으로 공연 시설 찾기.
    일차로 KOPIS_API 기준으로 검색 한 후,
    DB에 공연시설 ID로 접근하여 추가 정보 획득할 예정 (SearchFragment에서)
     */
    static public ArrayList<TheaterClass> GetResult_Theater(String search_word) {
        ArrayList<TheaterClass> datas = new ArrayList<TheaterClass>();

        String shprfnmfct = "&shprfnmfct=" + search_word;

        String HallURL = "http://www.kopis.or.kr/openApi/restful/prfplc?service=" + KOPIS_key + "&cpage=1&rows=9" + shprfnmfct + "&signgucode=11"; //너무 많아서 우선 서울 한정으로 찾게 해놓음.


        //공연시설명, ID
        boolean inFcltynm = false, inMt10id = false;
        String theater_name = null, theater_id = null;


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

                        if (tag.equals("fcltynm")) {
                            inFcltynm = true;

                        } else if (tag.equals("mt10id")) {
                            inMt10id = true;

                        }
                        break;


                    case XmlPullParser.TEXT:
                        tag = xpp.getText();

                        if (inFcltynm) {
                            theater_name = tag;
                            inFcltynm = false;

                        } else if (inMt10id) {
                            theater_id = tag;
                            inMt10id = false;

                        }
                        break;


                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();

                        if (tag.equals("db")) {
                            datas.add(new TheaterClass(theater_name, theater_id));
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.d("Err(GetResult_Theater):", e.toString());
        }

        return datas;
    }

}
