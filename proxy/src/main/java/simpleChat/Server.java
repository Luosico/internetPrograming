package simpleChat;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(15000);
        System.out.println("server等待连接");
        Socket socket = serverSocket.accept();
        System.out.println("连接成功");
        System.out.println("******聊天开始*****");
        RecvMessage recvMessage = new RecvMessage(socket);
        SendMessage sendMessage = new SendMessage(socket);
        //开启线程
        new Thread(recvMessage).start();
        new Thread(sendMessage).start();

        //socket.close();
        //serverSocket.close();
    }
}

