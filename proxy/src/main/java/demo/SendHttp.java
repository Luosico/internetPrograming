package demo;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @Author: luo kai fa
 * @Date: 2020/4/16
 */
public class SendHttp {
    public static void main(String[] args) throws IOException {
        String url = "www.baidu.com";
        String content = "GET http://www.baidu.com HTTP/1.1\r\nHost:www.baidu.com\r\n\r\n"; //注意请求头和请求体之间的空行
        Socket socket = new Socket(url,80);
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());

        out.write(content.getBytes());
        out.flush();

        byte[] temp = new byte[1024];
        int len;
        while((len =bufferedInputStream.read(temp))!=-1){
            System.out.println("len= "+len);
            if (bufferedInputStream.available()>0)
                System.out.println(new String(Arrays.copyOf(temp,len), StandardCharsets.UTF_8));
            else
                System.out.println("暂时没有数据");
        }

        StringBuilder stringBuilder = new StringBuilder();
        String line ;
        
    }
}
