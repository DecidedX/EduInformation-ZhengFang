package decided.spider.eduinformation.connector;

import org.jsoup.Connection;

import java.util.HashMap;
import java.util.Map;

public class DataContainer {

    protected final String url = "https://jwgl.taru.edu.cn";
    protected Map<String,String> cookies = new HashMap<>();
    protected String csrftoken;
    protected Connection connection;
    protected String stuNum;
    protected String password;
    protected long time;

    public DataContainer(String stuNum,String password){
        this.stuNum = stuNum;
        this.password = password;
    }

}
