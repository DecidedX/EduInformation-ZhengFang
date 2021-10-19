package decided.spider.eduinformation;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(19131);

        System.out.println("_______________________________");
        System.out.println("Listening Port:19131");
        while (true) {
            Socket socket = server.accept();

            System.out.println(new Date().getTime() + " |Client : " + socket.getRemoteSocketAddress() + " was connected");

            ConnectThread connectThread = new ConnectThread(socket);
            connectThread.start();
        }
    }

}
