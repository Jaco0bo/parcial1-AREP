package org.example;

import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class Backend {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        Socket clientSocket = null;
        try {
            System.out.println("Listo para recibir ...");
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
        if (clientSocket != null){
            handle(clientSocket);
        }
    }

    public static void handle(Socket clientSocket) throws Exception {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String readline = in.readLine();

        String request = readline.split(" ")[1];
        URI uri = new URI(request);
        System.out.println("REQUEST " + request);

        if(uri.getPath().startsWith("/compreflex")){
            handleRequest(out, uri);
        }
        in.close();
        out.close();
        clientSocket.close();
    }

    private static void handleRequest(PrintWriter out, URI resource)throws Exception{
        String key = resource.getQuery().split("=")[1];
        System.out.println("KEY " + key);
        int index = key.indexOf("}");
        String keyValue = key.substring(1, index).toLowerCase();
        String value = resource.getQuery().split("=")[2];
        System.out.println("VALUE " + key);
        int valueIndex = value.indexOf("}");
        String Value = key.substring(1, index).toLowerCase();

        System.out.println("KEY " + keyValue + " WITH VALUE " + Value);
        String result = "";
        if(function.equals("class")){
            result = classFunction(values);
        }
        else {

        }
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 200 OK\r\n");
        response.append("Content-Type: application/json\r\n");
        response.append("\r\n");
        response.append(result);
        out.println(response.toString());
        out.flush();
    }

    public static String invoke(String[] parameters) throws Exception{
        String className = parameters[0];
        String methodName = parameters[1];
        Class<?> c = Class.forName(className);
        StringBuilder response = new StringBuilder();

        Method m = c.getDeclaredMethod(methodName);
        Object result = m.invoke(null);
        response.append("{\"result\":"+ "\"" + result.toString() + "\"" + "}");
        System.out.println(response.toString());
        return response.toString();
    }
}
