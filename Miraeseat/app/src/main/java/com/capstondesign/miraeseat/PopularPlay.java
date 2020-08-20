package com.capstondesign.miraeseat;

import android.content.Context;
import android.util.Log;

import com.capstondesign.miraeseat.search.PlayClass;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class PopularPlay {
    final static private String KOPIS_key= "***REMOVED***";
    ArrayList<PlayClass> datas;

    static public ArrayList<PlayClass> popularPlay() {
        ArrayList<PlayClass> datas = new ArrayList<PlayClass>();
        int count=5;

        //이번 주의 인기 공연. 서울 한정.
        String PopularPlayURL = "http://kopis.or.kr/openApi/restful/boxoffice?service="+KOPIS_key+"&ststype=week&date="+PlayClass.getToday()+"&area=11";

        String[] index = {"prfnm", "poster"};
        boolean[] index_check = new boolean[index.length];
        String[] index_data = new String[index.length];


        try {
            URL url = new URL(PopularPlayURL);
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

                        if(tag.equals(index[0])){
                            index_check[0] = true;
                            break;
                        }
                        else if(tag.equals(index[1])) {
                            index_check[1] = true;
                            break;
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

                        if (xpp.getName().equals("boxof")) {
                            datas.add(new PlayClass(index_data[0], index_data[1]));

                            count-=1;

                            if(count==0) return datas;
                        }
                        break;

                }
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.d("Error(PopularPlay):", e.toString());
        }
        return null;
    }
}
