import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        InputStream in = null;
        OutputStream out = null;
        Socket socket = null;

        int port;
        if (args.length < 1) {
            System.out.println("java –jar DictionaryClient.jar <port>");
        }

        port = Integer.parseInt(args[0]);

        System.out.println(port);

        try {
            socket = new Socket("localhost", port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            System.out.println("Could not bind to the port" + port);
            System.exit(-1);
        }

        try {
            PrintWriter pr = new PrintWriter(out, true);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));

            while(true) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter an action: ");
                String type = scanner.nextLine();

                String message;
                if(type.equals("query")) {
                    System.out.print("Enter a word you want to query: ");
                    String word = scanner.nextLine();
                    message = MessageProtocal.queryMessage(word);
                    pr.println(message);
                }else if(type.equals("add")){
                    System.out.print("Enter a word you want to add to the dictionary: ");
                    String word = scanner.nextLine();
                    System.out.print("Enter meanings of the word (type 'END' to finish): ");
                    List<String> meanings = new ArrayList<>();

                    while (true) {
                        String line = scanner.nextLine();
                        if (line.equalsIgnoreCase("END")) break;
                        meanings.add(line);
                    }

                    message = MessageProtocal.addMessage(word, meanings);
                    pr.println(message);

                }else if(type.equals("remove")) {
                    System.out.print("Enter a word you want to remove: ");
                    String word = scanner.nextLine();
                    message = MessageProtocal.removeMessage(word);
                    pr.println(message);

                }else{
                    System.out.println("unsupported functionality");
                    continue;
                }


                String str = bf.readLine();
                if(str == null){
                    System.out.println("Lost connection to port " + port);
                    break;
                }
                System.out.println(str);
            }
        } catch (IOException e) {
            System.out.println("Lost connection to port" + port);
        }
    }
}