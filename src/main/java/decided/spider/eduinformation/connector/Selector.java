package decided.spider.eduinformation.connector;

public class Selector {

    public static final int Timetable_O = 0;

    private final int features;
    private final Login login;

    public Selector(int features, Login login){
        this.features = features;
        this.login = login;
    }

    public String getRespond() throws Exception {
        String respond = null;
        switch (features){
            case Timetable_O:
                respond = respondOfTimetable();
                break;
        }
        return respond;
    }

    private String respondOfTimetable() throws Exception {
        TimeTable timeTable = new TimeTable(login).getTimeTable(Integer.parseInt(login.getUser()[3]),Integer.parseInt(login.getUser()[4]));
        return timeTable.getCourses().toJSONString();
    }

}
