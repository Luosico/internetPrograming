package simpleChat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SendMessage implements Runnable {
    Socket socket;
    public SendMessage(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            boolean flag = false;
            while(scanner.hasNext()){
                String s = scanner.nextLine();
                if(s.equals("quit")){
                    flag = true;
                }
                out.print(s+"\r\n");
                out.flush();
                if(flag==true){
                    System.out.println("******聊天结束*****");
                    break;
                }

            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
