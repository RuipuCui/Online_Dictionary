import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ClientHandler implements Runnable{
    private final Socket socket;
    private final int port;

    private final String dictFile;

    public ClientHandler(Socket socket, int port, String dictFile){
        this.socket = socket;
        this.port = port;
        this.dictFile = dictFile;
    }

    public void run(){
        try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true))
        {
            while(true) {
                String str = in.readLine();
                if(str == null){
                    System.out.println("Lost connection to port" + port);
                    break;
                }

                String reply = DictionaryHandler.Handler(str, dictFile);
                System.out.println("Received from client: " + str);
                out.println(reply);
            }

        }catch(IOException e){
            System.out.println("Lost connection to port " + port);
        }
    }
}
