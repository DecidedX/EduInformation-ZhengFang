package decided.spider.eduinformation;

import decided.spider.eduinformation.connector.Login;
import decided.spider.eduinformation.connector.Selector;

import java.io.*;
import java.net.Socket;

public class ConnectThread extends Thread{

    private final Socket client;
    private final String[] user;

    public ConnectThread(Socket client){
        this.client = client;
        this.user = getUser();
    }

    public void run(){
        System.out.print("Running for ");
        if (user.length >= 3 && user.length < 5){
            try {
                respondIncomeError();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println(user[1]);
            try {
                Login login = new Login(user).login();
                if (login.isLogin()){
                    respond(login);
                }else {
                    respondLoginError(login.getLog_msg());
                }
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //客户端传入String"操作:学号:密码:学年:学期"
    private String[] getUser(){
        String[] user = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            user = reader.readLine().split("\\:");//"\\"解决php传入无法分割
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    private void respond(Login login) throws Exception {
        Selector selector = new Selector(Integer.parseInt(user[0]),login);
        PrintWriter printer = new PrintWriter(client.getOutputStream());
        printer.println(selector.getRespond());
        printer.flush();
        printer.close();
        System.out.println("Successful Response\nClient : " + client.getRemoteSocketAddress().toString() + " was disconnected");
        System.out.println("_______________________________");
    }

    private void respondLoginError(String msg) {
        try {
            PrintWriter printer = new PrintWriter(client.getOutputStream());
            printer.println("Error!!! " + msg);
            printer.flush();
            printer.close();
            System.out.println("_______________________________");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void respondIncomeError(){
        try {
            PrintWriter printer = new PrintWriter(client.getOutputStream());
            printer.println("IncomingError!!!\nPlease pass in right Parameter Format");
            printer.flush();
            printer.close();
            System.out.println("_______________________________");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
