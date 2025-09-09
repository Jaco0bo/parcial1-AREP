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
        int code = HttpConection.getLastResponseCode();
        String body = chatResponse == null ? "" : chatResponse;

        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 " + codeReason(code) + "\r\n");
        response.append("Content-Type: application/json; charset=UTF-8\r\n");
        response.append("Content-Length: " + body.getBytes("UTF-8").length + "\r\n");
        response.append("\r\n");
        response.append(body);
        out.println(response.toString());
        out.flush();
    }

    private static void handleGetRequest(PrintWriter out, URI resource) throws Exception {
        String chatResponse = getResponse("getrespmsg?" + resource.getQuery());
        int code = HttpConection.getLastResponseCode();
        String body = chatResponse == null ? "" : chatResponse;

        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 " + codeReason(code) + "\r\n");
        response.append("Content-Type: application/json; charset=UTF-8\r\n");
        response.append("Content-Length: " + body.getBytes("UTF-8").length + "\r\n");
        response.append("\r\n");
        response.append(body);
        out.println(response.toString());
        out.flush();
    }

    public static void getPage(PrintWriter out) {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 200 OK\r\n");
        response.append("Content-Type: text/html\r\n");
        response.append("\r\n");
        response.append("<!DOCTYPE html>\n");
        response.append("<html>\n");
        response.append("<head>\n");
        response.append("<title>Parcial1</title>\n");
        response.append("<meta charset=\"UTF-8\">\n");
        response.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        response.append("</head>\n");
        response.append("<body>\n");
        response.append("<h1>Form with SET</h1>\n");
        response.append("<form onsubmit=\"return false;\">\n");
        response.append("<label for=\"key\">key:</label><br>\n");
        response.append("<input type=\"text\" id=\"key\" name=\"key\" value=\"Ponga llave\"><br><br>\n");
        response.append("<label for=\"valor\">valor:</label><br>\n");
        response.append("<input type=\"text\" id=\"valor\" name=\"valor\" value=\"Ponga valor\"><br><br>\n");
        response.append("<input type=\"button\" value=\"Submit\" onclick=\"loadSetValue()\">\n");
        response.append("</form>\n");
        response.append("<div id=\"getrespmsg\"></div>\n");
        response.append("<div></div>\n");
        response.append("<h1>Form with GET</h1>\n");
        response.append("<form onsubmit=\"return false;\">\n");
        response.append("<label for=\"keyGet\">key:</label><br>\n");
        response.append("<input type=\"text\" id=\"keyGet\" name=\"keyGet\" value=\"Ponga llave\"><br><br>\n");
        response.append("<input type=\"button\" value=\"Search\" onclick=\"loadGetValue()\">\n");
        response.append("</form>\n");
        response.append("<div id=\"getrespget\"></div>\n");
        response.append("<script>\n");
        response.append("function loadSetValue() {\n");
        response.append("const setValue = document.getElementById('valor').value;\n");
        response.append("loadSetKey(setValue) \n");
        response.append("}\n");
        response.append("function loadSetKey(setValue) {\n");
        response.append("const nameKey = document.getElementById(\"key\").value;\n");
        response.append("const xhttp = new XMLHttpRequest();\n");
        response.append("xhttp.onload = function () {\n");
        response.append("document.getElementById(\"getrespmsg\").innerHTML = this.responseText;\n");
        response.append("}\n");
        response.append("xhttp.open('GET', '/SET?key=' + encodeURIComponent(nameKey) + '&value=' + encodeURIComponent(setValue), true);\n");
        response.append("xhttp.send();\n");
        response.append("}\n");
        response.append("function loadGetValue() {\n");
        response.append("const nameKey = document.getElementById(\"keyGet\").value;\n");
        response.append("const xhttp = new XMLHttpRequest();\n");
        response.append("xhttp.onload = function () {\n");
        response.append("document.getElementById(\"getrespget\").innerHTML = this.responseText;\n");
        response.append("}\n");
        response.append("xhttp.open('GET', '/GET?key=' + encodeURIComponent(nameKey), true);\n");
        response.append("xhttp.send();\n");
        response.append("}\n");
        response.append("</script>\n");
        response.append("</body>\n");
        response.append("</html>\n");
        out.println(response.toString());
        out.flush();
    }

    private static String codeReason(int code) {
        if (code == 200) return "200 OK";
        if (code == 400) return "400 BAD REQUEST";
        if (code == 404) return "404 NOT FOUND";
        return code + " OK";
    }
}

