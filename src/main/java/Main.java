import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) {


        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        String directory = null;
        if(args.length == 2){
            if(args[0].equals("--directory")){
                directory = args[1];
            }
        }

        try {
            serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            while(true){
                clientSocket = serverSocket.accept(); // Wait for connection from client.
                HttpRequestHandler requestHandler = new HttpRequestHandler(clientSocket, directory );
                Thread.startVirtualThread(requestHandler::run);

            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
class HttpRequestHandler{
    Socket clientSocket;
    String directory;

    public HttpRequestHandler(Socket clientSocket, String directory){
        this.clientSocket = clientSocket;
        this.directory = directory;
    }
    public void run(){
        try{
            if(directory != null){
                System.out.println("Directory: "+ directory);
            }
            OutputStream outputStream = clientSocket.getOutputStream();
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line =reader.readLine();
            String[] httpRequest = line.split(" ",  0);
            System.out.println("[REQUEST] "+line);

            //routing
            if(httpRequest[1].startsWith("/echo/")){
                String param = httpRequest[1].substring(6);
                String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "
                        +param.length()+"\r\n\r\n"+param;
                System.out.println("[RESPONSE] "+response);
                outputStream.write(response.getBytes());

            }else if(httpRequest[1].equals("/")){
                String responce = "HTTP/1.1 200 OK\r\n\r\n";
                outputStream.write(responce.getBytes());
                System.out.println("[RESPONSE] "+ responce);

            }else if(httpRequest[1].equals("/user-agent")){
                reader.readLine();
                String userAgentValue = reader.readLine().split(" ", 0)[1];
                String response = "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: "
                        +userAgentValue.length()+"\r\n\r\n"+userAgentValue;
                outputStream.write(response.getBytes());
                System.out.println("[RESPONSE] "+ response);

            } else if (httpRequest[1].equals("/files/")) {
                String filename = httpRequest[1].substring(7);
                File file = new File(directory, filename);

                if(file.exists()) {
                    byte[] fileContents = Files.readAllBytes(file.toPath());
                    String fileContentString = new String(fileContents);
                    String response = "HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: "
                            + fileContentString.length() + "\r\n\r\n" + fileContentString;
                    System.out.println("[RESPONSE] "+ response);
                    outputStream.write(response.getBytes());
                }else {
                    String response = "HTTP/1.1 404 Not Found\r\n\r\n";
                    System.out.println("[RESPONSE] "+ response);
                    outputStream.write(response.getBytes());
                }
                
            } else {
                outputStream.write("HTTP/1.1 404 Not Found\r\n\r\n".getBytes());
                System.out.println("[ERROR] unknown route.");
            }

        }catch (IOException e){
            System.out.println("[IOEXCEPTION] "+ e.getMessage());
        }
    }
}