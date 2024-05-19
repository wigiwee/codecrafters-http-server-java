import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    // Uncomment this block to pass the first stage

     ServerSocket serverSocket = null;
     Socket clientSocket = null;

     try {
       serverSocket = new ServerSocket(4221);
       serverSocket.setReuseAddress(true);
       clientSocket = serverSocket.accept(); // Wait for connection from client.
//       clientSocket.getOutputStream().write(
//               "HTTP/1.1 200 OK\r\n\r\n".getBytes()
//       );
         OutputStream outputStream = clientSocket.getOutputStream();
         InputStream input = clientSocket.getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         String line =reader.readLine();
         String[] httpRequest = line.split(" ",  0);
         System.out.println(line);
         System.out.println(httpRequest[1]);
         httpRequest[1] = httpRequest[1].strip();
         if(httpRequest[1].equals("/")){
             outputStream.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
             System.out.println("Accepted new connection and responded.");
         }else {
             outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
             System.out.println("Rejected the bad connection.");
         }

     } catch (IOException e) {
       System.out.println("IOException: " + e.getMessage());
     }
  }
}
