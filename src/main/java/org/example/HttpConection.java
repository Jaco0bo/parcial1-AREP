package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConection {
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String GET_URL = "http://localhost:35000/";
    public static int lastResponseCode = 0;

    public static String getResponse(String request) throws Exception {

        URL obj = new URL(GET_URL + request);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        //The following invocation perform the connection implicitly before getting the code
        int responseCode = con.getResponseCode();
        lastResponseCode = responseCode;
        System.out.println("GET Response Code :: " + responseCode);
        String result = "";
        BufferedReader in = null;
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
             in = new BufferedReader(new InputStreamReader(
                    con.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            result = response.toString();
            // print result
            System.out.println("respuesta " + response.toString());
            System.out.println();
        }
        return result;
    }

    public static int getLastResponseCode(){
        return lastResponseCode;
    }
}
