import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Httpserver {

    static int port = 8080;
    static String dir = "src/main/";
    static int clientnumber = 0;
    static boolean verbose = false;

    public static void main(String args[]) throws IOException {

        Scanner sc = new Scanner(System.in);
        String command = sc.nextLine();
        String words[] = command.split(" ");

        if (words[0].equals("httpfs")) {

            for (int i = 0; i < words.length; i++) {

                switch (words[i]) {

                    case "-p":
                        port = Integer.parseInt(words[i + 1]);
                        break;
                    case "-v":
                        verbose = true;
                        break;
                    case "-d":
                        dir = words[i + 1];
                        break;
                    default:
                        break;
                }
            }

            if (verbose) {

                System.out.println("Server Started at Port:" + port);
            } else {

                System.out.println("Server Started");
            }
            serverrunning();

        } else {

            System.out.println("Type httpfs [-v] [-p PORT] [-d PATH-TO-DIR]");
        }
    }

    static void serverrunning() throws IOException {

        ServerSocket ss = new ServerSocket(port);


        while (true) {
            Socket s1 = ss.accept();
            String message="";
            clientnumber++;
            if(verbose){

                System.out.println("Client:" + clientnumber +" connected");
            }


            Httpserverlib request = new Httpserverlib();
            BufferedReader br = new BufferedReader(new InputStreamReader(s1.getInputStream()));
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(s1.getOutputStream()));
            String output = br.readLine();

            if(verbose){
                System.out.println(output);
            }
            if(output.startsWith("GET")){

                message=request.getrequest(output);

            }else if(output.startsWith("POST")){

                message=request.postrequest(output);

            }
            pw.write(message+"\r\n");
            pw.flush();
            pw.close();
            s1.close();

            if(verbose){

                System.out.println("Client:" + clientnumber +" disconnected");

            }
        }


    }
}