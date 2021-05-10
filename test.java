import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class test {

    public static void main(String args[]) throws IOException {


        Thread t1 =  new Thread(()->{

            Socket s = null;
            PrintWriter pw = null;
            try {
                s = new Socket(InetAddress.getLocalHost(),8080);
                pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                pw.write("GET /test/data3.txt -d hello1\r\n");
                pw.flush();
                String output = "";
                while((output = br.readLine())!=null){

                    System.out.println(output);

                }

                br.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread t2 =  new Thread(()->{

            Socket s = null;
            PrintWriter pw = null;
            try {
                s = new Socket(InetAddress.getLocalHost(),8080);
                pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                pw.write("POST /test/data3.txt -d hello2\r\n");
                pw.flush();
                String output = br.readLine();
                System.out.println(output);
                br.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        Thread t3 =  new Thread(()->{

            try{
                HttpLib lib = new HttpLib();
                lib.localrequest("post","http://localhost:8080/post/test/data.txt","test2",null);

            }catch(Exception e){

                e.printStackTrace();
            }

        });

        Thread t4 =  new Thread(()->{

            try{
                HttpLib lib = new HttpLib();
                lib.localrequest("GET","http://localhost:8080/get/test/data.txt",null,null);

            }catch(Exception e){

                e.printStackTrace();
            }

        });

        Thread t5 =  new Thread(()->{

            try{
                HttpLib lib = new HttpLib();
                lib.localrequest("post","http://localhost:8080/post/test/data.txt","test",null);

            }catch(Exception e){

                e.printStackTrace();
            }

        });

        Thread t6 =  new Thread(()->{

            try{
                HttpLib lib = new HttpLib();
                lib.localrequest("GET","http://localhost:8080/get/data3.txt",null,null);

            }catch(Exception e){

                e.printStackTrace();
            }


        });

//        t1.start();
//        t2.start();
        t3.start();
        t4.start();
        t5.start();
//        t6.start();





    }

}
