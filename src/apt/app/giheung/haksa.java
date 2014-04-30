package apt.app.giheung;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jaebeomlee on 2014. 4. 12..
 */

public class haksa extends Activity{

    Date calender = new Date();
    int year;
    int month;
    int date;
    int day;
    int lastday;

    TextView  tv;

    Calendar calendar = Calendar.getInstance();
    String tbody;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temporary);
//        year = calender.getYear()+1900;
//        month = calender.getMonth();
//        date= calender.getDate();
//        day = calender.getDay();
//
//
//        tv = (TextView)findViewById(R.id.tv1);
//
//        new Thread(){
//            @Override
//            public void run() {
//                Document document;
//                try{
//                    String url = "http://giheung.hs.kr/?_page=130&"
//                            + "yy="
//                            + year
//                            + "&mm="
//                            + month;
//                    document = (Document) Jsoup.connect(url).get();
////            Elements td = document.select("td");
////            Elements a = document.select("a");
//
//                    lastday = calendar.getActualMaximum(calendar.DAY_OF_MONTH);
//                    tbody = document.toString();
//                    tbody = tbody.split("<tbody>")[1];
//                    int count = StringUtils.countOccurrences(tbody,"<span class=\"title\">");
//
//
////                    tbody= tbody.split("<div")[3];
//
//                    tbody = tbody.split("<span class=\"title\">")[2];
//                    tbody = tbody.replace("</span></a>","");
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            tv.setText(tbody);
//                        }
//                    });
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }.start();



    }

}
