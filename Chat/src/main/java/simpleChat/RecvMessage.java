package simpleChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class RecvMessage implements Runnable {

    Socket socket;

    public RecvMessage(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String s = "";
            while (!s.equals("quit")) {
                if (in.ready()) {
                    s = in.readLine();
                    System.out.println(s);
                }
            }
            System.out.println("******聊天结束*****");
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}