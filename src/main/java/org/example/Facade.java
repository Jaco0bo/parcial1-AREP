package org.example;

import java.net.*;
import java.io.*;

import static org.example.HttpConection.getResponse;

public class Facade {
    private static int PORT = 35001;
    private static boolean RUNNING = true;

    public static void main(String[] args) throws Exception {
        Facade.start();
    }

    public static void start() throws Exception {
        ServerSocket server = new ServerSocket(PORT);
        while (RUNNING) {
            System.out.println("FACADE RUNNING");
            Socket client = server.accept();

            if (client != null) {
                handleRequest(client);
            }
        }
    }

    public static void handleRequest(Socket client) throws Exception {
        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        String readline = in.readLine();

        String request = readline.split(" ")[1];
        URI uir = new URI(request);
        System.out.println("REQUEST " + request);

        if (uir.getPath().startsWith("/cliente")) {
            getPage(out);
        } else if (uir.getPath().startsWith("/SET")) {
            handleSetRequest(out, uir);
        } else if (uir.getPath().startsWith("/GET")) {
            handleGetRequest(out, uir);
        }
        in.close();
        out.close();
        client.close();
    }

    private static void handleSetRequest(PrintWriter out, URI resource) throws Exception {
        String chatResponse = getResponse("getrespmsg?" + resource.getQuery());
        System.out.println();
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 200 OK\r\n");
        response.append("Content-Type: application/json\r\n");
        response.append("\r\n");
        response.append(chatResponse);
        out.println(response.toString());
        out.flush();
    }

    private static void handleGetRequest(PrintWriter out, URI resource) throws Exception {
        String chatResponse = getResponse("getrespmsg?" + resource.getQuery());
        System.out.println();
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 200 OK\r\n");
        response.append("Content-Type: application/json\r\n");
        response.append("\r\n");
        response.append(chatResponse);
        out.println(response.toString());
        out.flush();
    }

    public static void getPage(PrintWriter out) {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 200 OK\r\n");
        response.append("Content-Type: text/html\r\n");
        response.append("\r\n");
        response.append("\"<!DOCTYPE html>\\n\"");
        response.append("<html>\n");
        response.append("<head>\n");
        response.append("<title>Parcial1</title>\n");
        response.append("<meta charset=\"UTF-8\">\n");
        response.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        response.append("</head>\n");
        response.append("<body>\n");
        response.append("<h1>Form with SET</h1>\n");
        response.append("<form action=\"/set\">\n");
        response.append("<label for=\"key\">key:</label><br>\n");
        response.append("<input type=\"text\" id=\"key\" name=\"key\" value=\"Ponga llave\"><br><br>\n");
        response.append("<input type=\"button\" value=\"Submit\" onclick=\"loadSetValue()\">\n");
        response.append("<div></div>\n");
        response.append("<label for=\"valor\">valor:</label><br>\n");
        response.append("<input type=\"text\" id=\"valor\" name=\"valor\" value=\"Ponga valor\"><br><br>\n");
        response.append("<input type=\"button\" value=\"Submit\" onclick=\"loadSetKey()\">\n");
        response.append("</form>\n");
        response.append("<div id=\"getrespmsg\"></div>\n");
        response.append("<script>\n");
        response.append("function loadSetValue() {\n");
        response.append("let nameVar = document.getElementById(\"valor\").value;\n");
        response.append("function loadSetKey(nameVar) \n");
        response.append("}\n");
        response.append("function loadSetKey(var setValue) {\n");
        response.append("let nameKey = document.getElementById(\"key\").value;\n");
        response.append("const xhttp = new XMLHttpRequest();\n");
        response.append("xhttp.onload = function () {\n");
        response.append("document.getElementById(\"getrespmsg\").innerHTML =\n");
        response.append("this.responseText;\n");
        response.append("}\n");
        response.append("xhttp.open(\"SET\", \"/backend?key=\" + nameKey + \"&value=\" + setValue);\n");
        response.append("xhttp.send();\n");
        response.append("}\n");
        response.append("</script>\n");
        response.append("</body>\n");
        response.append("</html>\n");
        out.println(response.toString());
        out.flush();
    }
}

