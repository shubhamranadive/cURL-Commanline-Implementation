import java.io.*;
import java.net.*;
import java.util.*;


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
    static int clientports = 5000;
    static int sequencenumber = 100;
    int routerport = 3000;
    long serverseq, lastseq, maxseq = -1;
     static boolean done = false;


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

    public void  localrequest(String method,String url,String data,ArrayList<String> header) throws URISyntaxException, IOException {


        HashMap<Long, String> outputdata = new HashMap<>();
        String request = "";
        DatagramSocket s = threewayhandshake();
//        DatagramSocket s = new DatagramSocket(clientports);

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


            Packet sending =  new Packet(Packet.datatype.DATA.type,sequencenumber,InetAddress.getLocalHost(),port,request.getBytes());
            byte[] sending_byte = sending.toBytes();
            DatagramPacket request_packet = new DatagramPacket(sending_byte,sending_byte.length,InetAddress.getLocalHost(),routerport);
            s.send(request_packet);

            System.out.println("Packet_sent");

            recievepackets(s,outputdata);


        }else{

            request += "POST ";
            query = url.substring(url.indexOf("post")+4);
            request += query + " -d " + data +"\r\n";
            int index = url.indexOf("localhost");
            int port = Integer.parseInt(url.substring(index+10,url.indexOf("/",index+10)));
            System.out.println("Request:" +request);
            String output = "";

            Packet sending =  new Packet(Packet.datatype.DATA.type,sequencenumber,InetAddress.getLocalHost(),port,request.getBytes());
            byte[] sending_byte = sending.toBytes();
            DatagramPacket request_packet = new DatagramPacket(sending_byte,sending_byte.length,InetAddress.getLocalHost(),routerport);
            s.send(request_packet);
            System.out.println("Packet_sent");

            recievepackets(s,outputdata);
        }
    }

    DatagramSocket threewayhandshake() throws IOException {


        DatagramSocket s = new DatagramSocket();
        byte temp[] = new byte[Packet.MIN_LEN];
        Packet one = new Packet(Packet.datatype.SYN.type,sequencenumber,InetAddress.getLocalHost(),Httpserver.port,temp);
        DatagramPacket pd  = new DatagramPacket(one.toBytes(),one.toBytes().length,InetAddress.getLocalHost(),3000);
        s.send(pd);
        sequencenumber++;

        System.out.println("Setting up Connection");

        byte temp2[] = new byte[Packet.MAX_LEN];
        DatagramPacket pd2 = new DatagramPacket(temp2,temp2.length);

        s.receive(pd2);
        Packet two = Packet.fromBytes(pd2.getData());

        if(two.getType()==Packet.datatype.SYNACK.type){

            System.out.println("Connection Successful");
            Packet ack = new Packet(Packet.datatype.ACK.type,sequencenumber,InetAddress.getLocalHost(),two.getPeerPort(),new byte[Packet.MIN_LEN]);
            DatagramPacket ackpkt = new DatagramPacket(ack.toBytes(),ack.toBytes().length,InetAddress.getLocalHost(),routerport);
            serverseq = two.getSequenceNumber();
            s.send(ackpkt);
            return s;

        }
        else{

            System.out.println("Connection not successful");

        }
        return s;

    }

    void printdata(HashMap<Long,String> data){

        TreeMap<Long, String> map = new TreeMap<>();
        map.putAll(data);
        for(Map.Entry<Long, String> entry : map.entrySet()){

            if(!entry.getValue().contains("End")){

                System.out.print(entry.getValue());

            }


        }

    }

    void recievepackets(DatagramSocket s,HashMap<Long,String> outputdata) throws IOException, URISyntaxException {

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

//                System.out.println("5 seconds has passed :" + data.size());
                if(outputdata.size()>0){

                    for(int j=0;j<5;j++){

                        for(Long i:outputdata.keySet()) {
                            DatagramPacket dp = null;
                            try {
                                Packet p = new Packet(Packet.datatype.DATAACK.type,i,InetAddress.getLocalHost(), port,new byte[Packet.MIN_LEN]);
                                dp = new DatagramPacket(p.toBytes(), p.toBytes().length, InetAddress.getLocalHost(), routerport);

                                s.send(dp);
                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                        }

                    }

                    s.close();

                }
            }
        };


        while(true){
            byte[] received = new byte[Packet.MAX_LEN];
            DatagramPacket response = new DatagramPacket(received,received.length);
            s.receive(response);
            Packet p = Packet.fromBytes(response.getData());

            Packet p2 = new Packet(Packet.datatype.DATAACK.type,p.getSequenceNumber(),InetAddress.getLocalHost(),p.getPeerPort(),new byte[Packet.MIN_LEN]);
            DatagramPacket dp2 = new DatagramPacket(p2.toBytes(),p2.toBytes().length,InetAddress.getLocalHost(),routerport);

            s.send(dp2);


            if(!outputdata.containsKey(p.getSequenceNumber())){

                outputdata.put(p.getSequenceNumber(),new String(p.getPayload()).trim());
            }

            long seqrec = p.getSequenceNumber();
            if(seqrec>maxseq){

                maxseq = seqrec;
            }
            if(new String(p.getPayload()).trim().contains("End")){

                String words[] = new String(p.getPayload()).trim().split(":");
                port = Integer.parseInt(words[1]);
                lastseq = p.getSequenceNumber();


            }
            if(lastseq-serverseq == outputdata.size()){

                System.out.println("Packet received all");
                printdata(outputdata);
                timer.schedule(task,10);
                break;

            }


//                System.out.println("For packet:"+ seqrec + "\n"+lastseq + "Packet data:" + new String(p.getPayload()));

        }

//        s.close();


    }

}



//String input_url1 = "GET /get?course=networking&assignment=1 HTTP/1.0\r\n" + "Host:httpbin.org\r\n" +"\r\n";

