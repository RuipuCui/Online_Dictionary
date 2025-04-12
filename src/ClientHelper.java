import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHelper {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHelper(String host, int port) throws IOException {
        socket = new Socket(host, port);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String query(String word) throws IOException {
        writer.println(MessageProtocol.queryMessage(word));
        String reply = reader.readLine();

        if (reply == null) {
            throw new IOException("Server disconnected or returned no data.");
        }

        return MessageProtocol.getMeaningFromReply(reply);
    }


    public String add(String word, List<String> meanings) throws IOException {
        writer.println(MessageProtocol.addMessage(word, meanings));
        String reply = reader.readLine();
        if (reply == null) {
            throw new IOException("Server disconnected or returned no data.");
        }

        return reply;
    }

    public String remove(String word) throws IOException {
        writer.println(MessageProtocol.removeMessage(word));
        String reply = reader.readLine();
        if (reply == null) {
            throw new IOException("Server disconnected or returned no data.");
        }

        return reply;
    }

    public String addMeaning(String word, String newMeaning) throws IOException {
        writer.println(MessageProtocol.addMeaningsMessage(word, newMeaning));
        String reply = reader.readLine();
        if (reply == null) {
            throw new IOException("Server disconnected or returned no data.");
        }

        return reply;
    }

    public String updateMeaning(String word, String oldMeaning, String newMeaning) throws IOException {
        writer.println(MessageProtocol.updateMeaningMessage(word, oldMeaning, newMeaning));
        String reply = reader.readLine();
        if (reply == null) {
            throw new IOException("Server disconnected or returned no data.");
        }
        return reply;
    }

    public void close() throws IOException {
        socket.close();
    }
}
