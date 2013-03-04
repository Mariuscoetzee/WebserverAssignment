/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Standard
 */
public class WebServer {

public static final int SERVER_PORT = 80;
public static final String WORKING_DIRECTORY =  System.getProperty("user.dir");
private static final String CRLF = "\r\n";
private static Map<String, File> file_Resources;

private static final String BAD_REQUEST = "HTTP/1.0 400 Illegal request" + CRLF + "connection: close" + CRLF + CRLF;
private static final String REQUEST_OK = "HTTP/1.0 200 OK" + CRLF + "connection: close" + CRLF + CRLF;
private static final String NOT_IMPLEMENTED = "HTTP/1.0 501 Not implemented: ";
private static final String FILE_NOT_FOUND = "HTTP/1.0 404 Not found: ";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(InetAddress.getLocalHost().getHostAddress());
       /**
        * get the files in the working directory of the server.
        */
       getFiles();
       
       ServerSocket serverSocket = null;
        try { 
            serverSocket = new ServerSocket(SERVER_PORT);
                while (true){
                     
                Socket clientSocket = serverSocket.accept();
                Scanner clientScanner = new Scanner(clientSocket.getInputStream());
                String [] requestLine = clientScanner.nextLine().split(" ");
                
                /**
                 * Full request :
                 * the difference between a Simple-Request and the Request-
                 * Line of a Full-Request is the presence of the HTTP-Version field and
                 * the availability of methods other than GET.
                 */
                if (requestLine.length == 3){
                         /**
                         * Servers should
                         * return the status code 501 (not implemented) if the method is
                         * unrecognized or not implemented.
                         */
                        if (requestLine[0].equals("GET")){
                            String resourceName = requestLine[1].substring(1, requestLine[1].length()); 
                           if (file_Resources.containsKey(resourceName)){
                                clientSocket.getOutputStream().write(REQUEST_OK.getBytes());
                                FileInputStream resourceInputStream = new FileInputStream(file_Resources.get(resourceName));
                                copy(resourceInputStream, clientSocket.getOutputStream());
                                clientSocket.getOutputStream().flush();
                           }else {
                               clientSocket.getOutputStream().write((FILE_NOT_FOUND 
                            +  requestLine[1] + CRLF + "connection: close" + CRLF + CRLF).getBytes());
                           }
                               
                        }else {
                            clientSocket.getOutputStream().write((NOT_IMPLEMENTED 
                            +  requestLine[0] + CRLF + "connection: close" + CRLF + CRLF).getBytes());
                        }   
                /**
                 * Simple request :
                 */        
                }else if (requestLine.length == 2){
                     clientSocket.getOutputStream().write(BAD_REQUEST.getBytes());
                     clientSocket.getOutputStream().flush();
                }
                /**
                 * Bad Request :
                 */
                else {
                     clientSocket.getOutputStream().write(BAD_REQUEST.getBytes());
                     clientSocket.getOutputStream().flush();
                }
               
//                FileInputStream test = new FileInputStream(WORKING_DIRECTORY + "\\tester.html");
//                copy(test, output);
//                
                              
               clientSocket.close();

                }
           
         }
         catch (IOException ex){
         System.out.println(ex.getMessage());
         }       
         
        finally {
            if (serverSocket != null){
                serverSocket.close();
            } 
            
        }
    }
  /**
   * In the web server you need to be able to copy the contents of a file to a socket, from Anders.
   * @param input- Input Stream associated with the resource
   * @param output - Output Stream associated with the client
   * @throws IOException 
   */
 private static void copy(final InputStream input, final OutputStream output) throws IOException {
        final byte[] buffer = new byte[1024];
        while (true) {
            int byteRead = input.read(buffer);
            if (byteRead == -1) { break; }
            output.write(buffer, 0, byteRead);
        }
  
    }
 
 private static void getFiles(){
  file_Resources = new HashMap<String, File>();
  File folder = new File(WORKING_DIRECTORY);
  File [] listOfFiles = folder.listFiles(); 

 
      for (File file : listOfFiles) {
          if (file.getName().endsWith(".html")|
              file.getName().endsWith(".doc")|
              file.getName().endsWith(".htm")|
              file.getName().endsWith(".gif")|
              file.getName().endsWith(".jpg")|
              file.getName().endsWith(".pdf")|
              file.getName().endsWith(".css")|
              file.getName().endsWith(".xml")|
              file.getName().endsWith(".jar")){
           file_Resources.put(file.getName(), file);   
          }
      }
 }
 
private static void handleRequest(){
    
}

}
//                Pattern requestPattern =  fromClient.delimiter();
//                Matcher matcher = requestPattern.matcher(fromClient.nextLine());
//                System.out.println(requestPattern.pattern());