package httpProxy;

import java.io.*;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                Socket finalSocket = socket;
                System.out.println(socket);
                Runnable r = () -> {
                    System.out.println("一个新的连接：");
                    parseSocket(finalSocket);

                    /*//关闭socket
                    try {
                        finalSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                };
                //启动线程
                new Thread(r).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //解析http报文，根据Host建立与URL的连接
    public void parseSocket(Socket socket) {
        //String host = null;

        //线程：解析出http报文的host
        Runnable r = () -> {
            RequestInfo requestInfo = new RequestInfo();
            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                //注意长度
                byte[] temp = new byte[200];
                int len;
                int symbol = 0;
                Socket proxySocket = null;

                //由于HTTP1.1协议，将会保持长连接
                while ((len = bufferedInputStream.read(temp)) != -1) {
                    byte[] temps = Arrays.copyOf(temp, len);
                   //System.out.println("server接收client数据");
                    //System.out.println(new String(temps));

                    //获取Host的值
                    if (requestInfo.getHost() == null) {
                        //System.out.println("获取Host");
                        requestInfo.setContent(temps);

                        String s = new String(temps, StandardCharsets.UTF_8);
                        String patternString = "Host: .*?\r\n";
                        Pattern pattern = Pattern.compile(patternString);
                        Matcher matcher = pattern.matcher(s);
                        if (matcher.find()) {
                            String result = matcher.group();
                            String host = result.split(":")[1].trim();

                            //HTTP缺省为80端口，HTTPS缺省为443端口
                            requestInfo.setPort(80);
                            if(result.split(":").length==3)
                                requestInfo.setPort(Integer.parseInt(result.split(":")[2].trim()));

                            System.out.println("-----------Host:"+host+"----------------");
                            //将Host的值添加到requestInfo
                            requestInfo.setHost(host);
                        }
                    //向remote发送数据
                    } else {
                        System.out.println("Host已获取");

                        //与remote建立socket连接
                        if (symbol == 0) {
                            proxySocket = new Socket();
                            proxySocket.connect(new InetSocketAddress(requestInfo.getHost(), requestInfo.getPort()));
                            //proxySocket.setSoTimeout(5000);
                            System.out.println("与remote建立连接");
                            System.out.println(proxySocket);

                            BufferedInputStream proxyInput = new BufferedInputStream(proxySocket.getInputStream());

                            //线程：用于接收remote的数据
                            Runnable rr = () -> {
                                byte[] remoteMessage = new byte[1024];
                                int length;
                                try {
                                    while ((length = proxyInput.read(remoteMessage)) != -1) {
                                        //System.out.println("server接收remote数据");
                                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                                        //传回数据给client
                                        bufferedOutputStream.write(Arrays.copyOf(remoteMessage,length));
                                        bufferedOutputStream.flush();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            };
                            new Thread(rr).start();

                            //表示socket连接已经建立
                            symbol = 1;
                        }
                        //System.out.println(proxySocket);
                        BufferedOutputStream proxyOutput = new BufferedOutputStream(proxySocket.getOutputStream());

                        //向remote发送数据
                        if (requestInfo.getContent() != null) {
                            proxyOutput.write(requestInfo.getContent());
                            requestInfo.setContent(null);
                        }
                        proxyOutput.write(temps);
                        proxyOutput.flush();


                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        //启动线程
        new Thread(r).start();
    }

    //代理服务器处理数据交换
    public void proxyMessage(HttpMessage httpMessage, Socket clientSocket) {
        try {
            //与目标URL建立连接
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(httpMessage.host, 80), 5000); //设置连接超时时间
            //设置读超时时间
            //socket.setSoTimeout(10000);

            //传输http报文
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(httpMessage.content.getBytes());
            bufferedOutputStream.flush();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
            //返回的http报文
            byte[] message = new byte[1024];
            int len;

            //client的输入流
            BufferedOutputStream clientOutputStream = new BufferedOutputStream(clientSocket.getOutputStream());

            //将返回的http报文传给client
            while ((len = bufferedInputStream.read(message)) != -1) {
                clientOutputStream.write(Arrays.copyOf(message, len));
            }
            clientOutputStream.flush();

        } catch (InterruptedIOException e) {
            System.out.println(httpMessage.host + "超时");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ProxyServer proxyServer = new ProxyServer();
        proxyServer.receiveConnect(serverSocket);
    }
}
