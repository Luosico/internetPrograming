package httpProxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Arrays;

/**
 * @Author: luo kai fa
 * @Date: 2020/4/22
 */
public class ProxyThread implements Runnable {
    OutputStream outputStream;
    InputStream inputStream;

    public ProxyThread(OutputStream outputStream, InputStream inputStream) {
        this.outputStream = outputStream;
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        byte[] temp = new byte[1024];
        int len;
        try {
            while (inputStream != null && outputStream != null && (len = inputStream.read(temp)) != -1) {
                outputStream.write(Arrays.copyOf(temp, len));
            }
        } catch (SocketException socketException) {
            //Thread.interrupted();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("-------线程终止");
        }
    }
}
