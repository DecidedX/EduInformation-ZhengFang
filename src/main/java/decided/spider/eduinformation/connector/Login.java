package decided.spider.eduinformation.connector;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import decided.spider.eduinformation.SslUtils;
import decided.spider.eduinformation.encoder.*;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Date;

public class Login {

    protected DataContainer data;
    private final String[] user;
    private boolean login = false;
    private String log_msg = null;

    public Login(String[] user) throws Exception {
        this.user = user;
        data = new DataContainer(user[1],user[2]);
        data.time = new Date().getTime();
        getCsrftoken();
        getRSAPublicKey();
    }

    public boolean isLogin(){
        return login;
    }

    public String getLog_msg(){
        return log_msg;
    }

    public String[] getUser() {
        return user;
    }

    // 获取csrftoken和Cookies
    private void getCsrftoken(){
        try{
            SslUtils.ignoreSsl();
            Connection connection = Jsoup.connect(data.url+ "/jwglxt/xtgl/login_slogin.html?language=zh_CN&_t=" + data.time);
            connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
            Connection.Response response = connection.execute();
            data.cookies = response.cookies();
            //保存csrftoken
            Document document = Jsoup.parse(response.body());
            data.csrftoken = document.getElementById("csrftoken").val();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    // 获取公钥并加密密码
    private void getRSAPublicKey() throws Exception{
        SslUtils.ignoreSsl();
        Connection connection = Jsoup.connect(data.url+ "/jwglxt/xtgl/login_getPublicKey.html?" +
                "time="+ data.time);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        Connection.Response response = connection.cookies(data.cookies).ignoreContentType(true).execute();
        JSONObject jsonObject = JSON.parseObject(response.body());
        String modulus = jsonObject.getString("modulus");
        String exponent = jsonObject.getString("exponent");
        data.password = RSAEncoder.RSAEncrypt(data.password, B64.b64tohex(modulus), B64.b64tohex(exponent));
        data.password = B64.hex2b64(data.password);
    }

    public Login login() throws Exception {

        SslUtils.ignoreSsl();
        data.connection = Jsoup.connect(data.url+ "/jwglxt/xsxxxggl/xsgrxxwh_cxXsgrxx.html?gnmkdm=N100801&layout=default&su=" + data.stuNum);
        data.connection.followRedirects(true).execute();
        data.connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        data.connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");

        data.connection.data("csrftoken",data.csrftoken);
        data.connection.data("yhm",data.stuNum);
        data.connection.data("mm",data.password);
        data.connection.data("mm",data.password);
        data.connection.cookies(data.cookies).ignoreContentType(true)
                .method(Connection.Method.POST).execute();

        Connection.Response response = data.connection.execute();
        Document document = Jsoup.parse(response.body());
        if(document.getElementById("tips") == null){
            login = true;
        }else{
            log_msg = document.getElementById("tips").text();
            System.out.println(log_msg);
            login =  false;
        }

        System.out.println("login result:" + login);

        return this;
    }

}
