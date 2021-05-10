import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Httpserverlib {

    static String init = Httpserver.dir;
    static ArrayList<String> files;
    static ArrayList<String> header = null;

    synchronized String getrequest(String request) throws IOException {

//        System.out.println(request);

        String response = "",file_output = "";
        String content = "";
        Boolean content_type = false;
        Boolean content_disposition = false;
        String disposition_type ="";
        if(request.contains("-h")){
            header =  new ArrayList<>();
            request=addheader(request);
        }

//        System.out.println(header.size());
        if(header!=null){
            for(String headers : header){
                if(headers.contains("Content-Type")){

                    content_type = true;
                    String temp[] = headers.split(":");
                    content = temp[1];
                }
                if(headers.contains("Content-Disposition")){

                    content_disposition=true;
                    String temp[] = headers.split(":");
                    disposition_type = temp[1];

                }
            }

        }


        String data[] = request.split(" ");

        //Get files name
        if(data[1].endsWith("/")){
            File f=null;
            if(data[1].length()!=1){
                String path = Httpserver.dir + data[1].substring(1,data[1].length()-1);
                f = new File(path);
            }else{
                f = new File(Httpserver.dir);
            }

            for(String files:f.list()){

                if(content_type){

                    if(files.contains(content)){

                        response += files+"\r\n";

                    }

                }else{

                    response += files+"\r\n";

                }

            }

        }else{
            String path = Httpserver.dir + data[1].substring(1);
//            System.out.println("Path:" + path);
            File f = new File(path);
            File fw;
            FileWriter fww = null;
            if(f.exists()){
                if(f.isFile()){
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                    response = "HTTP 200 Found\r\n";
                    while((file_output=br.readLine())!=null){

                        response += file_output+"\r\n";
                    }
//                    System.out.println("Content Dis:" + content_disposition + " Content-type:" + disposition_type);
                    if(content_disposition && disposition_type.equals("attachment")){

                        fw = new File("output.txt");
                        fww = new FileWriter(fw);
                        fww.write(response);
                        fww.close();
                        response = "HTTP 200.1 Found. Action Complete to File\r\n";
                    }

                    br.close();
                }else{
                    response = "Not a file, its a directory.";
                }

            }
            else{
                String filenames[] = request.split("/");
//                System.out.println("Filename:" + filenames[filenames.length-1]+ "\r\nPath:" + Httpserver.dir+data[1].substring(1));
                files =  new ArrayList<>();
                filesystem(new File("src/main/"));
                printdata();
                if(files.contains(filenames[filenames.length-1].trim())){

                    response = "HTTP 404.1 Found/Access Denied[Not in current directory]";

                }else{

                    response = "HTTP 404 Not Found";

                }

            }
        }
//        System.out.println("Response on server:\n" + response );
        return response;
    }

    synchronized String  postrequest(String request) throws IOException {

        String response = "";
        String data[] = request.split(" ");
        String path = Httpserver.dir + data[1].substring(1) ;
        File f = new File(path);

        response = "HTTP 200 Post Successful";
        FileWriter fw = new FileWriter(f);
        fw.write(request.substring(request.indexOf("-d")+3));
        fw.close();

        return response;
    }


    static void filesystem(File file){


        for(File name : file.listFiles()){

//            System.out.println(name.getName());
            if(name.isFile()){

                files.add(name.getName());

            }else{

                filesystem(name);
            }
        }
    }

    static void printdata(){

        for(String file:files){

            System.out.println(file);
        }

    }

    public String addheader(String request){

        String temp[]= request.split(" ");
        int index=0;
        for(int i=0;i<temp.length;i++){

            if(temp[i].equals("-h")){

                index = i + 1;

            }
        }

        for(int i=index;i<temp.length;i++){

            header.add(temp[i]);

        }

        return request.substring(0,request.indexOf("-h")-1).trim();
    }


}
