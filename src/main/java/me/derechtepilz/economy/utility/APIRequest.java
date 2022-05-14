package me.derechtepilz.economy.utility;

import me.derechtepilz.economy.utility.exceptions.UnsuccessfulAPIRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIRequest {
    private HttpURLConnection connection;
    private String line;
    private BufferedReader reader;
    private StringBuffer responseContent = new StringBuffer();

    public String request(String url) throws IOException {
        URL requestUrl = new URL(url);
        connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode > 299) {
            throw new UnsuccessfulAPIRequestException("Could not finish api request!");
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }
        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        reader.close();
        return responseContent.toString();
    }
}
