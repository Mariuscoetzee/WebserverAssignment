/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.net.*;
import java.io.*;
import org.junit.Test;
import static org.junit.Assert.*;
import webserver.WebServer;
//
///**
// *
// * @author andersb
// */
public class WebServerTest {

  private static final String CRLF = "\r\n";

  @Test
  public void testResponseOK() throws IOException {
    final Socket client = new Socket("localhost", WebServer.SERVER_PORT);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /assignment.html HTTP/1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 200 OK", statusLine);
    client.close();
  }

  @Test
  public void testResponseNotOK() throws IOException {
    final Socket client = new Socket("localhost", WebServer.SERVER_PORT);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /doesNotExist.html HTTP/1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 404 Not found: /doesNotExist.html", statusLine);
    client.close();
  }

  @Test
  public void testIllegalProtocol() throws IOException{
    final Socket client = new Socket("localhost", WebServer.SERVER_PORT);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /doesNotExist.html" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 400 Illegal request", statusLine);
    client.close();
  }

  @Test
  public void testMissingProtocol() throws IOException {
    final Socket client = new Socket("localhost", WebServer.SERVER_PORT);

    final OutputStream output = client.getOutputStream();
    output.write(("GET /doesNotExist.html HTTP 1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 400 Illegal protocol: HTTP 1.0", statusLine);
    client.close();
  }

  @Test
  public void testNotImplemented() throws IOException {
    final Socket client = new Socket("localhost", WebServer.SERVER_PORT);

    final OutputStream output = client.getOutputStream();
    output.write(("PUT /doesNotExist.html HTTP/1.0" + CRLF + CRLF).getBytes());
    output.flush();

    final BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
    final String statusLine = input.readLine();
    assertEquals("HTTP/1.0 501 Not implemented: PUT", statusLine);
    client.close();
  }
}
