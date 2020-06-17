package com.capstondesign.miraeseat.search;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class SearchAPI {
    final static private String APIkey = "***REMOVED***";

    //문제점 : 검색결과가 없을 때 에러코드가 안나옴. 어떻게 처리해줘야할지 찾아봐야함.



    //[검색어==공연시설명]으로 공연 시설 찾기. page와 row 사용법 모르겠음.
    static public ArrayList<HallClass> GetResult_Hall(String searced_word) {

        ArrayList<HallClass> datas = new ArrayList<HallClass>();

        String shprfnmfct = "&shprfnmfct=" + searced_word;   //공연시설명

        String HallURL = "http://www.kopis.or.kr/openApi/restful/prfplc?service=" + APIkey + "&cpage=1&rows=5" + shprfnmfct;

        boolean inFcltynm = false, inMt10id = false;
        String hall_name = null, hall_id = null;


        //문제점 : 검색결과가 없을 때 에러코드가 안나옴. 어떻게 처리해줘야할지 찾아봐야함.
        try {

            URL url = new URL(HallURL);
            InputStream IS = url.openStream();


            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(IS, "UTF-8"));


            xpp.next();
            int eventType = xpp.getEventType(); //태그 구분용

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {

                    case XmlPullParser.START_TAG:

                        if (xpp.getName().equals("fcltynm")) {
                            inFcltynm = true;

                        } else if (xpp.getName().equals("mt10id")) {
                            inMt10id = true;

                        }
                        break;


                    case XmlPullParser.TEXT:

                        if (inFcltynm) {
                            hall_name = xpp.getText();
                            inFcltynm = false;

                        } else if (inMt10id) {
                            hall_id = xpp.getText();
                            inMt10id = false;

                        }
                        break;


                    case XmlPullParser.END_TAG:

                        if (xpp.getName().equals("db")) {
                            datas.add(new HallClass(hall_name, hall_id));
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return datas;
    }


    //[검색어==공연명 또는 공연시설명]으로 공연 찾기
    static public ArrayList<PlayClass> GetResult_Play(String search_play, String search_hall) {

        ArrayList<PlayClass> datas = new ArrayList<PlayClass>();

        String shprfnmfct = (search_hall != null) ? "&shprfnmfct=" + search_hall : "";   //공연시설명
        String shprfnm = (search_play != null) ? "&shprfnm" + search_play : "";  //공연명

        String[] date = getDate();

        //3개월 내에 공연 이력이 있는 경우 모두 출력
        //prfstate=01(공연예정) 02(공연중) 03(공연완료)
        String PlayURL = "http://www.kopis.or.kr/openApi/restful/pblprfr?service=" + APIkey + "&stdate=" + date[0] + "&eddate=" + date[1] + "&rows=10&cpage=1" + shprfnmfct + shprfnm;

        String play_name = null, play_hall = null, poster = null;
        boolean inPlay_name = false, inPlay_hall = false, inPoster = false;

        try {
            URL url = new URL(PlayURL);
            InputStream IS = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(IS, "UTF-8"));


            xpp.next();
            int eventType = xpp.getEventType(); //태그 구분용

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {

                    case XmlPullParser.START_TAG:

                        if (xpp.getName().equals("prfnm")) {
                            inPlay_name = true;

                        } else if (xpp.getName().equals("fcltynm")) {
                            inPlay_hall = true;

                        } else if (xpp.getName().equals("poster")) {
                            inPoster = true;
                        }


                    case XmlPullParser.TEXT:

                        if (inPlay_name) {
                            play_name = xpp.getText();  //공연명
                            inPlay_name = false;

                        } else if (inPlay_hall) {
                            play_hall = xpp.getText();  //공연시설명
                            inPlay_hall = false;

                        } else if (inPoster) {
                            poster = xpp.getText(); //공연포스터
                            inPoster = false;

                        }
                        break;


                    case XmlPullParser.END_TAG:

                        if (xpp.getName().equals("db")) {
                            datas.add(new PlayClass(play_name, play_hall, poster));
                        }
                        break;

                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return datas;
    }

    //오늘 날짜와 3달 뒤 날짜를 구함
    static public String[] getDate() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String Today = Integer.toString(year) + String.format("%02d", month) + String.format("%02d", day);

        if (month > 9) {
            month -= 9;
            year += 1;
        } else month += 3;

        String AfterThreeMonth = Integer.toString(year) + String.format("%02d", month) + String.format("%02d", day);

        return new String[]{Today, AfterThreeMonth};
    }
}
