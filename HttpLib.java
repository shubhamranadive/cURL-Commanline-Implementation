import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class HttpLib {
    public String host;
    public int port;
    public String path;
    public String httpreq = "";
    String output;
    String query;
    int content_length= 0;
    String response="";
    int redirectCount =0;
    String writeFile ="";

    public void get(Boolean verbose,String save_to_file,ArrayList<String> header,String url1) throws IOException {

        URL url = new URL(url1);
        path = url.getPath();
        host = url.getHost();
        port = 80;
        query = url.getQuery();

        Socket s = new Socket(host, port);
        PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        //Request Forming
        httpreq = "GET " + path;

        httpreq += "?" + query;

        //Add protocol
        httpreq = httpreq + " HTTP/1.0\r\n" + "Host:" + host +"\r\n";

        //Adding headers
        for(String headers : header){
            if(!headers.equals("Host:"+host)){

                httpreq += headers +"\r\n";
            }
        }
        httpreq += "\r\n";


        System.out.println("Request:" + httpreq );
        pw.write(httpreq);
        pw.flush();

        //Response
        output = br.readLine();

        //If verbose not enabled
        if(!verbose){
            while(output!=null){
                if(output.length()==0){

                    break;
                }
                response += output +"\r\n";
                output=br.readLine();
            }
        }

        while(output!=null){
            if(save_to_file==null){

                System.out.println(output);

            }

            response += output + "\r\n";
            writeFile += output + "\r\n";
            output = br.readLine();
        }

        //redirect option
        if(response.subSequence(response.indexOf(" ") + 1, response.indexOf(" ") + 2).equals("3")) {
            if(redirectCount<5){
                redirectCount++;
                String newURL = "";
                int locationIndex = response.indexOf("Location:");
                if (locationIndex != -1) {
                    int index = response.indexOf("Location:") + 10;
                    while (response.charAt(index) != '\n') {
                        newURL = newURL + String.valueOf(response.charAt(index));
                        index++;
                    }

                    if(!(url1.trim()).contentEquals(newURL.trim())){
                        System.out.println("\n---------------------------------------------------------------------");
                        System.out.print("Redirecting ");
                        System.out.print(url1);
                        System.out.println("    to ");
                        System.out.println(newURL);
                        System.out.println("------------------------------------------------------------------------");
                        writeFile += "\n-----------------------------------------------------------------------------";
                        writeFile += "\nRedirecting  " +url1 +"  to\n" +newURL;
                        writeFile += "\n---------------------------------------------------------------------------\n";
                        get(verbose,save_to_file,header,newURL);
                    }
                }
                else{
                    this.redirectCount=0;
                    System.out.println("\n-------------------------------------------------------------------");
                    System.out.print("Redirecting ");
                    System.out.print(url1);
                    System.out.println(" to []");
                    System.out.println("New url is not provided for redirecting.");
                    System.out.println("---------------------------------------------------------------------");
                    writeFile += "\n--------------------------------------------------------------------------";
                    writeFile += "\nRedirecting  " +url1 +"  to  []";
                    writeFile += "\nNew url is not provided for redirecting.";
                    writeFile += "\n-------------------------------------------------------------------------\n";
                }
            }
            else{
                this.redirectCount=0;
                System.out.println("\n------------------------------------------------------------------------");
                System.out.println("Redirecting limit reached!!!");
                System.out.println("--------------------------------------------------------------------------");
                writeFile += "\n------------------------------------------------------------------------------";
                writeFile += "\nRedirecting limit reached!!!";
                writeFile += "\n-----------------------------------------------------------------------------\n";
            }
        }

        //If -o used
        if(save_to_file!=null){
            writeToFile(save_to_file,writeFile);
        }

        s.close();
        pw.close();
        br.close();
    }


    public void post(boolean verbose,String save_to_file, ArrayList<String> header, ArrayList<String> data, ArrayList<String> file, String url1) throws IOException {

        URL url = new URL(url1);

        path = url.getPath();
        host = url.getHost();
        port = 80;
        query = url.getQuery();

        Socket s = new Socket(host, port);
        PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));

        httpreq = "POST " + path;

        //Add query
        //httpreq += "?" + query;
        if(query!=null){
            httpreq += "?" + query;
        }

        //Add protocol
        httpreq = httpreq + " HTTP/1.0\r\n" + "Host:" + host +"\r\n";


        //If datafile present
        if(file != null){
            for(String files:file){

                File data_file =  new File("src/main/"+files);

                if(data_file.exists()){
                    BufferedReader br1 = new BufferedReader(new FileReader(data_file));
                    String file_output;
                    while((file_output=br1.readLine())!=null){

                        data.add(file_output);
                    }
                }
                else{
                    System.out.println("File does not exist");
                    return;
                }
            }
        }


        //Add Content-Length
        for(String d :data){
            content_length += d.length();
        }
        httpreq += "Content-Length:" + content_length +"\r\n";

        //Adding headers
        for(String headers : header){
            if(!headers.equals("Host:"+host)){

                httpreq += headers +"\r\n";
            }
        }


        //Data Adding
        if(data.size()!=0){
            for(int i=0;i<data.size();i++){
                httpreq = httpreq + "\r\n" + data.get(i);
            }
        }
        else{
            httpreq += "\r\n";
        }

        System.out.println("Request:" + httpreq );
        System.out.println("---------------------------------");
        pw.write(httpreq);
        pw.flush();

        //Response
        output = br.readLine();

        //If verbose not enabled
        if(!verbose){
            while(output!=null){
                if(output.length()==0){
                    break;
                }
                response += output +"\r\n";
                output=br.readLine();
            }
        }

        while(output!=null){

            if(save_to_file==null){

                System.out.println(output);

            }
//            System.out.println(output);
            response += output + "\r\n";
            writeFile += output + "\r\n";
            output = br.readLine();
        }

        //redirect option
        if(response.subSequence(response.indexOf(" ") + 1, response.indexOf(" ") + 2).equals("3")){
            System.out.println("\n\nThe POST method does not allow redirection.");
            System.out.println("Please check the url!!!");
            writeFile += "\nThe POST method does not allow redirection.\nPlease check the url!!!";
        }

        //If -o used
        if(save_to_file!=null){
            writeToFile(save_to_file,writeFile);
        }

        s.close();
        pw.close();
        br.close();
    }

    void writeToFile(String file_name,String response) throws IOException {
        File file = new File(file_name);
        FileWriter fw = new FileWriter(file);
        fw.write(response);
        fw.close();
    }

    public void localrequest(String method,String url,String data,ArrayList<String> header) throws URISyntaxException, IOException {

        String request = "";
        Socket s=null;
        PrintWriter pw =null;
        if(method.equalsIgnoreCase("GET")){

            request += "GET ";
            query = url.substring(url.indexOf("get")+3);
            request += query;

            if(header!=null){

                request += " -h";
                for(int i=0;i<header.size();i++){

                    request += " " + header.get(i);
                }
                request += " \r\n";

            }else{

                request += " \r\n";
            }

            int index = url.indexOf("localhost");
            int port = Integer.parseInt(url.substring(index+10,url.indexOf("/",index+10)));


//            System.out.println("Request:" +request);

            s = new Socket(InetAddress.getLocalHost(),port);
            pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pw.write(request);
            pw.flush();
            String output = "";
            while((output = br.readLine())!=null){
                    System.out.println(output);
            }

            br.close();
            s.close();


        }else{

            request += "POST ";
            query = url.substring(url.indexOf("post")+4);
            request += query + " -d " + data +"\r\n";
            int index = url.indexOf("localhost");
            int port = Integer.parseInt(url.substring(index+10,url.indexOf("/",index+10)));
            System.out.println("Request:" +request);
            s = new Socket(InetAddress.getLocalHost(),port);
            pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pw.write(request);
            pw.flush();
            String output = "";
            while((output = br.readLine())!=null){

                System.out.println(output);

            }
            br.close();
            s.close();
        }
    }

}



//String input_url1 = "GET /get?course=networking&assignment=1 HTTP/1.0\r\n" + "Host:httpbin.org\r\n" +"\r\n";

