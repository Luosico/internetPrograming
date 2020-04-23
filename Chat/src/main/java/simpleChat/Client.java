package simpleChat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        System.out.println("正在连接");
        socket.connect(new InetSocketAddress("127.0.0.1",15000),20000);
        System.out.println("连接成功");
        System.out.println("******聊天开始*****");

        RecvMessage recvMessage = new RecvMessage(socket);
        SendMessage sendMessage = new SendMessage(socket);
        //开启线程
        new Thread(recvMessage).start();
        new Thread(sendMessage).start();

        //socket.close();
    }
}
