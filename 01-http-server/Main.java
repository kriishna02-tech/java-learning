import java.io.IOException;
import java.net.ServerSocket;

public class Main{
    public static void main(String[] args)throws IOException {
        ServerSocket  serverSocket  =new ServerSocket(8080);
        System.out.println("Server stated on port 8080");

        var clientSocket = serverSocket.accept();
        System.out.println("server connected");

        var output = clientSocket.getOutputStream();

        // output.write("Hello java server !".getBytes());
        output.write("HTTP/1.1 200 OK\r\n\r\nHello java server!".getBytes());
        output.flush(); 
        clientSocket.close();
    }
}