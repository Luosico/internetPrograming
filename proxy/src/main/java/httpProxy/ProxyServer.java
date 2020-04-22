package httpProxy;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

/**
 * @Author: luo kai fa
 * @Date: 2020/4/16
 */
public class ProxyServer {

    static ServerSocket serverSocket;
    static int port = 10140;

    public ProxyServer() {
        try {
            while (serverSocket == null)
                serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("serverSocket创建失败");
            e.printStackTrace();
        }
    }

    //接收 socket 连接
    public void receiveConnect(ServerSocket serverSocket) {
        Socket socket;
        try {
            while ((socket = serverSocket.accept()) != null) {
                System.out.println("一个新的连接：");
                Socket finalSocket = socket;
                System.out.println(">>>clientSocket:" + socket + "<<<");
                Runnable r = () -> {
                    executeProxy(finalSocket);

                };
                //启动线程
                new Thread(r).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeProxy(Socket socket) {
        RequestInfo requestInfo = new RequestInfo();
        InputStream clientIn = null;
        OutputStream clientOut = null;
        try {
            clientIn = socket.getInputStream();
            clientOut = socket.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(clientIn);
        String line;

        int symbol = 0;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = scanner.nextLine()) != null) {
            //System.out.println("---------length = "+ line.length());
            //阻塞 退出
            if (line.length() == 0)
                break;

            stringBuilder.append(line).append("\r\n");

            //请求行
            if (symbol == 0) {
                //请求方法为CONNECT，为HTTPS
                if (line.startsWith("CONNECT")) {
                    requestInfo.setMethod("CONNECT");
                    requestInfo.setPort(443);
                } else {//HTTP请求
                    requestInfo.setMethod("other");
                    requestInfo.setPort(80);
                }
                symbol = 1;
            } else {
                //找出 Host,port
                if (line.startsWith("Host")) {
                    String[] s = line.split(":");
                    if (s.length == 3) {
                        requestInfo.setPort(Integer.parseInt(s[2]));
                    }
                    requestInfo.setHost(s[1].trim());
                }
            }

        }
        stringBuilder.append("\r\n");

        //System.out.println(">>>>Host: " + requestInfo.getHost() + "<<<");
        //System.out.println(">>>Port: " + requestInfo.getPort() + "<<<");
        //System.out.println(">>>Method: " + requestInfo.getMethod() + "<<<");
        System.out.println(stringBuilder);

        Socket remoteSocket = null;
        OutputStream remoteOut = null;
        InputStream remoteIn = null;
        try {
            //与remote建立socket连接
            remoteSocket = new Socket(requestInfo.getHost(), requestInfo.getPort());
            remoteOut = remoteSocket.getOutputStream();
            remoteIn = remoteSocket.getInputStream();

            System.out.println(">>>remoteSocket: " + remoteSocket + "<<<");
        } catch (ConnectException connectException) {
            System.out.println("-------与remote连接中断------------");
            try {
                socket.close();
                //clientIn.close();
                //clientOut.close();
                //remoteIn.close();
                //remoteOut.close();
            } catch (IOException e) {
                System.out.println("异常：remoteSocket关闭");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if ( clientOut != null){
                //HTTPS，不转发请求头
                if (requestInfo.getMethod().equals("CONNECT")) {
                    String success = "HTTP/1.1 200 Connection Established\r\n\r\n";
                    clientOut.write(success.getBytes());
                    clientOut.flush();


                } else { //HTTP请求将请求头转发
                    remoteOut.write(stringBuilder.toString().getBytes());
                    remoteOut.flush();
                }
            }

            //client -> remote
            ProxyThread thread1 = new ProxyThread(remoteOut, clientIn);
            //remote -> client
            ProxyThread thread2 = new ProxyThread(clientOut, remoteIn);

            Thread t1 = new Thread(thread1);
            Thread t2 = new Thread(thread2);
            t1.start();
            t2.start();


        } catch (SocketException socketException) {
            System.out.println("异常：Socket已关闭");
        } catch (IOException  e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) {
        ProxyServer proxyServer = new ProxyServer();
        proxyServer.receiveConnect(serverSocket);
    }
}
