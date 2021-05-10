import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import javafx.concurrent.Task;

import java.io.*;
import java.net.*;
import java.util.*;

public class Httpserver {

    static int port = 8080;
    static String dir = "src/main/";
    static int clientnumber = 0;
    static boolean verbose = false;
    static int routerport = 3000;
    static long sequencenumber = 0;
    int window = 4;
    int nop;
    static int MAX = 1013;
    static HashMap<Long,Packet> data =  new HashMap<>();

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

        DatagramSocket ss = new DatagramSocket(port);
        boolean part1 = false;
        boolean part2 = false;



        while (true) {

            byte buffer[] =  new byte[Packet.MAX_LEN];
            DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
            ss.receive(dp);
            Packet request_packet = Packet.fromBytes(dp.getData());

            if(request_packet.getType()==Packet.datatype.SYN.type){


                Packet synackpacket = new Packet(Packet.datatype.SYNACK.type,sequencenumber,request_packet.getPeerAddress(),request_packet.getPeerPort(),("ACK:"+ request_packet.getSequenceNumber()).getBytes());
                DatagramPacket syndp = new DatagramPacket(synackpacket.toBytes(),synackpacket.toBytes().length,dp.getAddress(),dp.getPort());
                ss.send(syndp);
                sequencenumber++;

                byte temp[] = new byte[Packet.MAX_LEN];
                DatagramPacket recdp = new DatagramPacket(temp,temp.length);
                ss.receive(recdp);

                Packet ack = Packet.fromBytes(recdp.getData());
                if(ack.getType()==Packet.datatype.ACK.type){

                }

            }
            if(request_packet.getType()==Packet.datatype.DATA.type){

                String message="";
                clientnumber++;
                if(verbose){
                    System.out.println("Client:" + clientnumber +" connected");
                }


                Httpserverlib request = new Httpserverlib();
                String output = new String(request_packet.getPayload()).trim();


                if(verbose){
                    System.out.println(output);
                }
                if(output.startsWith("GET")){

                    message=request.getrequest(output);

                }else if(output.startsWith("POST")){

                    message=request.postrequest(output);

                }

                //Make packets to send
                byte responsebytes[] = message.getBytes();
                if(message.length()>MAX){

                    byte temp[] = new byte[MAX];
                    int j=0;
                    for(int i=0;i<responsebytes.length;i++){

                        temp[j] = responsebytes[i];
                        j++;
                        if(j==MAX || i == responsebytes.length-1){

                            Packet p =  new Packet(Packet.datatype.DATA.type,sequencenumber,InetAddress.getLocalHost(),request_packet.getPeerPort(),temp);
                            data.put(sequencenumber,p);
                            sequencenumber++;
                            temp = new byte[MAX];
                            j=0;

                        }

                    }


                }else{
                    Packet p =  new Packet(Packet.datatype.DATA.type,sequencenumber,InetAddress.getLocalHost(),request_packet.getPeerPort(),responsebytes);
                    data.put(sequencenumber,p);
                    sequencenumber++;

                }


                System.out.println("Printing ds:");
                for(long i : data.keySet()){
//                    nop++;
                    System.out.println("Packet:" + i + ":" + data.get(i).getPayload().length + ":" + Packet.MAX_LEN);
                    System.out.println(new String(data.get(i).getPayload()));
                    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");
                }

                sendpackets(data,request_packet.getPeerPort());


                if(verbose){

                    System.out.println("Client:" + clientnumber +" disconnected");

                }
            }

        }




    }

    static void sendpackets(HashMap<Long,Packet> data,int port) throws IOException {

        DatagramSocket s = new DatagramSocket();
//        System.out.println();
        for(Long i:data.keySet()){

//            System.out.println("Packet:" + i + ":" + data.get(i).getPayload().length + ":" + Packet.MAX_LEN);
//            System.out.println(new String(data.get(i).getPayload()));
//            System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------");
            DatagramPacket dp = new DatagramPacket(data.get(i).toBytes(),data.get(i).toBytes().length,InetAddress.getLocalHost(),routerport);
            s.send(dp);
        }
        String msg = "End:"+s.getLocalPort();
        byte temp[] =  msg.getBytes();
        Packet p =  new Packet(Packet.datatype.DATA.type,sequencenumber,InetAddress.getLocalHost(),port,temp);
        data.put(sequencenumber,p);
        sequencenumber++;
        DatagramPacket dp = new DatagramPacket(p.toBytes(),p.toBytes().length,InetAddress.getLocalHost(),routerport);
        s.send(dp);
        receivepackets(s,data);
    }

    static void receivepackets(DatagramSocket s,HashMap<Long,Packet> data) throws IOException{


        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

//                System.out.println("5 seconds has passed :" + data.size());
                if(data.size()>0){

                    for(Long i:data.keySet()) {
                        DatagramPacket dp = null;
                        try {
                            dp = new DatagramPacket(data.get(i).toBytes(), data.get(i).toBytes().length, InetAddress.getLocalHost(), routerport);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        try {
                            s.send(dp);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        };
        timer.schedule(task,5000,5000);

        while(data.size()!=0){

            byte temp[] = new byte[Packet.MAX_LEN];
            DatagramPacket dp =new DatagramPacket(temp,temp.length);


            s.receive(dp);
            Packet p = Packet.fromBytes(dp.getData());
            long seqrecv = p.getSequenceNumber();

            if(p.getType()==Packet.datatype.DATAACK.type){
                System.out.println("Received ACK:" + seqrecv);
                System.out.println("Packets in data:" + data.keySet());
                if(data.keySet().contains(seqrecv)){

                    data.remove(seqrecv);

                }

            }
        }

        task.cancel();
        timer.cancel();
        System.out.println("All packets ACK'd");

    }

}