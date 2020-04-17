package demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @Author: luo kai fa
 * @Date: 2020/4/16
 */
public class Test {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(20000);
        System.out.println("server等待连接");
        Socket socket = serverSocket.accept();
        System.out.println("连接成功");

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        String line ;
        /*while((line = reader.readLine())!=null){
            System.out.println(line);
        }*/
        char[] temp = new char[1024];
        while(reader.read(temp)!=-1){
            System.out.println(Arrays.toString(temp));
            //temp = null;
        }

        socket.close();
        serverSocket.close();
    }
}
