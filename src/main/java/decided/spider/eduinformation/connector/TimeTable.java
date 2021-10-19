package decided.spider.eduinformation.connector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TimeTable {

    private final DataContainer data;
    private JSONArray courses;

    public TimeTable(Login login) {
        data = login.data;
    }

    public TimeTable getTimeTable(int year,int term) throws Exception {

        data.connection.url(data.url + "/jwglxt/kbcx/xskbcx_cxXsKb.html?gnmkdm=N2151&su=" + data.stuNum);
        data.connection.data("xnm",String.valueOf(year))
                .data("xqm", String.valueOf(term*term));
        Connection.Response response = data.connection.method(Connection.Method.POST).execute();
        JSONObject json = JSON.parseObject(response.body());
        analysis(JSON.parseArray(json.getString("kbList")));

        return this;
    }

    /*
    * [{
        "area":"校本部",
        "teacher":"姚小玲",
        "weeks":"1-18周",——改成数组
        "name":"英语Ⅲ(大学英语)",
        "position":"新文科楼218",
        "day":"星期一",——改成数字
        "sections":"3-4节"},
       {
        "area":"校本部",
        "teacher":"杨全丽　",
        "weeks":"1-15周",——改成数组
        "name":"现代电子技术",
        "position":"综合楼313",
        "day":"星期一",——改成数字
        "sections":"5-6节"},]
    */
    private void analysis(JSONArray time_table){
        JSONArray courses = new JSONArray();
        for (Iterator it = time_table.iterator();it.hasNext();){
            JSONObject course = (JSONObject)it.next();
            JSONObject course_details = new JSONObject();
            course_details.put("name",course.getString("kcmc"));
            course_details.put("position",course.getString("cdmc"));
            course_details.put("teacher",course.getString("xm"));
            course_details.put("weeks",str2ints(course.getString("zcd")));
            course_details.put("day",day2Int(course.getString("xqjmc")));
            course_details.put("sections",str2ints(course.getString("jc")));
            course_details.put("area",course.getString("xqmc"));
            courses.add(course_details);
        }
        this.courses = courses;
    }

    private static List<Integer> str2ints(String s){
        String s_or_d = null;
        if (s.matches("(.*)\\((.*)")){
            System.out.println(111);
            String[] split = s.split("\\(");
            s = split[0];
            s_or_d = split[1];
        }
        String[] strings = s.substring(0,s.length()-1).split("-");
        List<Integer> ret = new ArrayList<>();
        for (int i = Integer.parseInt(strings[0]);i <= Integer.parseInt(strings[1]);i++){
            ret.add(i);
        }
        if (s_or_d != null){
            if (s_or_d.equals("单)")){
                ret.removeIf(i -> i % 2 == 0);
            }else {
                ret.removeIf(i -> i % 2 != 0);
            }
        }
        return ret;
    }

    private Integer day2Int(String day){
        int ret = -1;
        switch (day){
            case "星期一":
                ret = 1;
            case "星期二":
                ret =  2;
            case "星期三":
                ret =  3;
            case "星期四":
                ret =  4;
            case "星期五":
                ret =  5;
        }
        return ret;
    }

    public JSONArray getCourses() {
        return courses;
    }
}
