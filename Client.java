import java.net.URISyntaxException;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

public class Client {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to cURL Application!!!");
        methodPassing();
        System.out.println("\nThank you!!!");
    }

    //Type checking & appropriate method calling
    public static void methodPassing() throws IOException, URISyntaxException {
        HttpLib lib = new HttpLib();
        Scanner command = new Scanner(System.in);
        System.out.print(">");
        String input = command.nextLine();
        String[] data = input.split(" ");

        //check for right client
        if (data[0].equalsIgnoreCase("httpc")) {

            //url is retrieved
            if(data.length>1){

                //Help for get & post commands
                if (data[1].equalsIgnoreCase("help")) {
                    if (data.length == 2) {
                        System.out.println("httpc is a curl-like application but supports HTTP protocol only. ");
                        System.out.println("Usage: httpc command [arguments]");
                        System.out.println("The commands are:");
                        System.out.println("\t get  executes a HTTP GET request and prints the response.");
                        System.out.println("\t post executes a HTTP POST request and prints the response.");
                        System.out.println("\t help prints this screen.");
                        System.out.println("Use \"httpc help [command]\" for more information about a command.");
                    }
                    else if (data.length == 3) {
                        if (data[2].equalsIgnoreCase("get")) {
                            System.out.println("usage: httpc get [-v] [-h key:value] URL\n");
                            System.out.println("Get executes a HTTP GET request for a given URL.\n");
                            System.out.println("\t-v              Prints the detail of the response such as protocol, status, and headers.");
                            System.out.println("\t-h key:value    Associates headers to HTTP Request with the format 'key:value'.");
                            System.out.println("\t-o file         Writes the output in specified file.");
                        }
                        else if (data[2].equalsIgnoreCase("post")) {
                            System.out.println("usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n");
                            System.out.println("Post executes a HTTP POST request for a given URL with inline data or from file.\n");
                            System.out.println("\t-v              Prints the detail of the response such as protocol, status, and headers.");
                            System.out.println("\t-h key:value    Associates headers to HTTP Request with the format 'key:value'.");
                            System.out.println("\t-d string       Associates an inline data to the body HTTP POST request.");
                            System.out.println("\t-f file         Associates the content of a file to the body HTTP POST request.\n");
                            System.out.println("\t-o file         Writes the output in specified file.");
                            System.out.println("Either [-d] or [-f] can be used but not both.");
                        }
                        else {
                            System.out.println("Please Enter The Right Command !!!");
                            System.out.println("Use \"httpc help\" for more information about commands.");
                        }
                    }
                    else {
                        System.out.println("Please Enter The Right Command !!!");
                        System.out.println("Use \"httpc help\" for more information about commands.");
                    }
                }

                //Method calling for get command
                else if (data[1].equalsIgnoreCase("get")) {
                    if(data.length>2){
                        boolean command1=true;
                        boolean v1 =false;
                        String file1 = null;
                        String url2="";
                        ArrayList<String> h1 = new ArrayList<>();
                        ArrayList<String> head = new ArrayList<>();
                        boolean local1 = false;
                        for(int i=2; i<(data.length);i++){
                            if (data[i].equals("-v")) {
                                v1=true;
                            }
                            else if (data[i].equalsIgnoreCase("-h") || data[i].equalsIgnoreCase("--h")) {
                                String temp1= data[i+1];
                                if(temp1.startsWith("'")){
                                    String temp2= temp1.substring(1,temp1.length()-1);
                                    h1.add(temp2);
                                    head.add(temp2);
                                }
                                else {
                                    h1.add(temp1);
                                    head.add(temp1);
                                }
                                i+=1;
                            }
                            else if (data[i].equalsIgnoreCase("-o")) {
                                String temp1= data[i+1];
                                if(temp1.startsWith("'")){
                                    file1= temp1.substring(1,temp1.length()-1);
                                }
                                else file1=temp1;
                                i+=1;
                            }
                            else if (data[i].contains("http:")||data[i].contains("https:")) {
                                String url1="";
                                if(data[i].contains("http://localhost")||data[i].contains("https://localhost")){
                                    url1=data[i];
                                    local1=true;
                                }
                                else
                                    url1=data[i];
                                if(url1.startsWith("'")) url2 = url1.substring(1, url1.length() - 1);
                                else url2 = url1;
                            }
                            else command1=false;
                        }
                        if(command1 && !local1) {
                            try {
                                List<String> header1 = h1.stream().distinct().collect(Collectors.toList());
                                lib.get(v1,file1, (ArrayList<String>) header1, url2);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else if(command1){
                                lib.localrequest("get",url2 ,null,head);
                        }
                        else {
                            System.out.println("Please Enter The Right Command !!!");
                            System.out.println("Use \"httpc help get\" for more information about commands.");
                        }
                    }
                    else{
                        System.out.println("Please Enter The Right Command !!!");
                        System.out.println("Use \"httpc help get\" for more information about commands.");
                    }
                }

                // Method calling for post command
                else if (data[1].equalsIgnoreCase("post")) {
                    if ((Arrays.asList(data).contains("-f") || Arrays.asList(data).contains("--f")) && (Arrays.asList(data).contains("-d") || Arrays.asList(data).contains("--d"))) {
                        System.out.println("Please Enter The Right Command !!!");
                        System.out.println("Either [-d] or [-f] can be used but not both.");
                        System.out.println("Use \"httpc help post\" for more information about commands.");
                    }
                    else{
                        if(data.length>2){
                            boolean command2 = true;
                            boolean v2 = false;
                            boolean local2=false;
                            String file2=null;
                            String url2="";
                            ArrayList<String> h2 = new ArrayList<>();
                            ArrayList<String> d2 = new ArrayList<>();
                            ArrayList<String> f2 = new ArrayList<>();
                            for(int i=2;i<(data.length-1);i++){
                                if (data[i].contains("http:")||data[i].contains("https:")) {
                                    String url1="";
                                    if(data[i].contains("http://localhost")||data[i].contains("https://localhost")){
                                        url1=data[i];
                                        local2=true;
                                    }
                                    else
                                        url1=data[i];
                                    if(url1.startsWith("'")) url2 = url1.substring(1, url1.length() - 1);
                                    else url2 = url1;
                                }
                            }
                            if(local2){
                                String data1="", data2="";
                                if(input.contains("-d")){
                                    int index=input.indexOf("-d")+2;
                                    data1=data1+input.substring(index);
                                    data2=data1.substring(2,data1.length()-1);
                                    lib.localrequest("post",url2,data2,null);
                                }
                                else{
                                    System.out.println("Please Enter The Right Command !!!");
                                    System.out.println("Use \"httpc help post\" for more information about commands.");
                                }
                            }
                            else{
                                for(int i=2;i<(data.length-1);i++) {
                                    if (data[i].equalsIgnoreCase("-v")) {
                                        v2 = true;
                                    }
                                    else if (data[i].equalsIgnoreCase("-h") || data[i].equalsIgnoreCase("--h")) {
                                        String temp1= data[i+1];
                                        if(temp1.startsWith("'")){
                                            String temp2= temp1.substring(1,temp1.length()-1);
                                            h2.add(temp2);
                                        }
                                        else h2.add(temp1);
                                        i+=1;
                                    }
                                    else if (data[i].equalsIgnoreCase("-d") || data[i].equalsIgnoreCase("--d")) {
                                        String temp1= data[i+1];
                                        if(temp1.startsWith("'")){
                                            String temp2= temp1.substring(1,temp1.length()-1);
                                            d2.add(temp2);
                                        }
                                        else d2.add(temp1);

                                        i+=1;
                                    }
                                    else if (data[i].equalsIgnoreCase("-f") || data[i].equalsIgnoreCase("--f")) {
                                        String temp1= data[i+1];
                                        if(temp1.startsWith("'")){
                                            String temp2= temp1.substring(1,temp1.length()-1);
                                            f2.add(temp2);
                                        }
                                        else f2.add(temp1);
                                        i+=1;
                                    }
                                    else if (data[i].equalsIgnoreCase("-o")) {
                                        String temp1= data[i+1];
                                        if(temp1.startsWith("'")){
                                            file2= temp1.substring(1,temp1.length()-1);
                                        }
                                        else file2=temp1;
                                        i+=1;
                                    }
                                    else if (data[i].contains("http:")||data[i].contains("https:")) { }
                                    else command2=false;
                                }
                                if(command2) {
                                    try {
                                        List<String> header2 = h2.stream().distinct().collect(Collectors.toList());
                                        lib.post(v2,file2, (ArrayList<String>) header2,d2,f2,url2);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    System.out.println("Please Enter The Right Command !!!");
                                    System.out.println("Use \"httpc help post\" for more information about commands.");
                                }
                            }

                        }
                        else{
                            System.out.println("Please Enter The Right Command !!!");
                            System.out.println("Use \"httpc help post\" for more information about commands.");
                        }
                    }
                }
                else {
                    System.out.println("Please Enter The Right Command !!!");
                    System.out.println("Use \"httpc help\" for more information about commands.");
                }
            }
            else{
                System.out.println("Please Enter The Right Command !!!");
                System.out.println("The command should have \"httpc command [arguments]\" format");
                System.out.println("Use \"httpc help\" for more information about commands.");
            }
        }
        /*
        else if (data[0].equalsIgnoreCase("httpfs")){
            ArrayList<String> d3 = new ArrayList<>();
            ArrayList<String> h3 = new ArrayList<>();
            for(int i=2; i<(data.length);i++) {
                if (data[i].equalsIgnoreCase("-d")){
                    String temp1= data[i+1];
                    if(temp1.startsWith("'")){
                        String temp2= temp1.substring(1,temp1.length()-1);
                        d3.add(temp2);
                    }
                    else d3.add(temp1);
                    i+=1;
                }
                else if (data[i].equalsIgnoreCase("-h")){
                    String temp1= data[i+1];
                    if(temp1.startsWith("'")){
                        String temp2= temp1.substring(1,temp1.length()-1);
                        h3.add(temp2);
                    }
                    else h3.add(temp1);
                    i+=1;
                }
                else if (data[i].contains("http:")||data[i].contains("https:")){

                }
            }
        }
        */
        else {
            System.out.println("Please Enter The Right Command !!!");
            System.out.println("The command should have \"httpc command [arguments]\" format");
            System.out.println("Use \"httpc help\" for more information about commands.");
        }

        //Method calling for continuation
        System.out.println("\nPress Y/y to CONTINUE or any key to EXIT...");
        String continue_command = command.nextLine();
        if(continue_command.equalsIgnoreCase("Y")){
            methodPassing();
        }
    }
}

/*
httpc help

httpc help post

httpc help get

httpc get 'http://httpbin.org/get?course=networking&assignment=1' -o output.txt

httpc get -v 'http://httpbin.org/get?course=networking&assignment=1' -o output.txt

httpc post -h Content-Type:application/json --d {"Assignment":1} 'http://httpbin.org/post' -o output.txt

httpc post -h Content-Type:application/json --d {"Assignment":1} -d {"Assignment":2} --d {"Assignment":3} 'http://httpbin.org/post' -o output.txt

httpc post -h Content-Type:application/json -f {} -d {} 'http://httpbin.org/post' -o output.txt

httpc post -h Content-Type:application/json -f data3.txt 'http://httpbin.org/post' -o output.txt

httpc post -h Content-Type:application/json -d {"Assignment":1} -o output.txt 'http://httpbin.org/post'

httpc get -v -h Content-Type:application/json 'https://httpstat.us/302'

httpc get -v -h Content-Type:application/json https://httpbin.org/status/302

httpc get -v -h Content-Type:application/json http://www.socengine.com/seo/

httpc get -v -h Content-Type:application/json https://www.hugedomains.com/domain_profile.cfm?d=socengine&e=com
blog.ahrefs.com
 */



