package org.example;

import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Backend {
    private static final Map<String, String> store = new ConcurrentHashMap<>();
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        while (true) {
            Socket clientSocket = serverSocket.accept();
            if (clientSocket != null) {
                handle(clientSocket);
            }
        }
    }

    public static void handle(Socket clientSocket) throws Exception {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String readline = in.readLine();

        String request = readline.split(" ")[1];
        URI uri = new URI(request);
        System.out.println("REQUEST " + request);

        if(uri.getPath().startsWith("/getrespmsg")){
            handleRequest(out, uri);
        }
        in.close();
        out.close();
        clientSocket.close();
    }

    private static void handleRequest(PrintWriter out, URI resource)throws Exception{
        String query = resource.getQuery();
        Map<String, String> params = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String [] pairs = query.split("&");
            for (String pair : pairs) {
                String [] kv = pair.split("=", 2);
                String name = URLDecoder.decode(kv[0], StandardCharsets.UTF_8.name());
                String val = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8.name()) : "";
                params.put(name, val);
            }
        }

        String keyValue = params.getOrDefault("key", "");
        String value = params.getOrDefault("value", "");
        System.out.println("KEY  = " + keyValue);
        System.out.println("VALUE " + value);

        int statusCode = 200;
        String result;

        if (keyValue == null || keyValue.isEmpty()) {
            statusCode = 400;
            result = "{\"error\":\"bad_request\",\"message\":\"missing key\"}";
        } else {
            if (value != null && !value.isEmpty()) {
                result = invoke(new String[]{keyValue, value});
                statusCode = 200;
            } else {
                if (store.containsKey(keyValue)) {
                    String val = store.get(keyValue);
                    result = "{\"key\":\"" + escapeJson(keyValue) + "\",\"value\":\"" + escapeJson(val) + "\"}";
                    statusCode = 200;
                } else {
                    result = "{\"error\":\"key_not_found\",\"key\":\"" + escapeJson(keyValue) + "\"}";
                    statusCode = 404;
                }
            }
        }

        byte[] bodyBytes = result.getBytes(StandardCharsets.UTF_8);
        String reason = (statusCode == 200) ? "OK" : (statusCode == 400) ? "Bad Request" : (statusCode == 404) ? "Not Found" : "";
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 " + statusCode + " " + reason + "\r\n");
        response.append("Content-Type: application/json; charset=UTF-8\r\n");
        response.append("\r\n");
        response.append(result);
        out.println(response.toString());
        out.flush();
    }

    public static String invoke(String[] parameters) throws Exception{
        String key = parameters[0];
        String value = parameters[1];

        boolean existed = store.containsKey(key);
        store.put(key, value);
        String status = existed ? "replaced" : "created";

        String result = "{\"key\":\"" + escapeJson(key) + "\",\"value\":\"" + escapeJson(value) + "\",\"status\":\"" + status + "\"}";
        System.out.println("INVOKE -> " + result);
        return result;
    }

    public static String escapeJson(String s) {
        if (s == null) return "";
        return s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
