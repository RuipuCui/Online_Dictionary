import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public Client(String host, int port) throws IOException {
        socket = new Socket(host, port);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String query(String word) throws IOException {
        writer.println(MessageProtocal.queryMessage(word));
        return reader.readLine();
    }

    public String add(String word, List<String> meanings) throws IOException {
        writer.println(MessageProtocal.addMessage(word, meanings));
        return reader.readLine();
    }

    public String remove(String word) throws IOException {
        writer.println(MessageProtocal.removeMessage(word));
        return reader.readLine();
    }

    public String addMeaning(String word, String newMeaning) throws IOException {
        writer.println(MessageProtocal.addMeaningsMessage(word, newMeaning));
        return reader.readLine();
    }

    public String updateMeaning(String word, String oldMeaning, String newMeaning) throws IOException {
        writer.println(MessageProtocal.updateMeaningMessage(word, oldMeaning, newMeaning));
        return reader.readLine();
    }

    public void close() throws IOException {
        socket.close();
    }
}
