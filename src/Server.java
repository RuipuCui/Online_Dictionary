import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        InputStream in = null;
        OutputStream out = null;
        Socket socket = null;

        int port;
        if (args.length < 2) {
            System.out.println("java –jar DictionaryServer.jar <port> <dictionary-file>");
        }

        port = Integer.parseInt(args[0]);
        String dictFile = args[1];

        try (ServerSocket serverSocket = new ServerSocket(port)){
            socket = serverSocket.accept();
            System.out.println("Client connected");
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Could not bind to the port " + port);
            System.exit(-1);
        }

        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            PrintWriter pr = new PrintWriter(out, true);

            while(true) {
                String str = bf.readLine();
                if(str == null){
                    System.out.println("Lost connection to port" + port);
                    break;
                }

                String reply = DictionaryHandler.Handler(str, dictFile);
                System.out.println("Received from client: " + str);
                pr.println(reply);
            }
        } catch (IOException e) {
            System.out.println("Lost connection to port " + port);
        }
    }
}