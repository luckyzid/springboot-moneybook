package kr.money.book.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtil {

    public static String sendGet(String targetUrl, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = createConnection(targetUrl, "GET", headers);
        return getResponse(connection);
    }

    public static String sendPost(String targetUrl, Map<String, String> headers, String body) throws IOException {
        return sendRequestWithBody(targetUrl, "POST", headers, body);
    }

    public static String sendPut(String targetUrl, Map<String, String> headers, String body) throws IOException {
        return sendRequestWithBody(targetUrl, "PUT", headers, body);
    }

    public static String sendDelete(String targetUrl, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = createConnection(targetUrl, "DELETE", headers);
        return getResponse(connection);
    }

    private static HttpURLConnection createConnection(String targetUrl, String method, Map<String, String> headers)
        throws IOException {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setDoInput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        return connection;
    }

    private static String sendRequestWithBody(String targetUrl, String method, Map<String, String> headers, String body)
        throws IOException {
        HttpURLConnection connection = createConnection(targetUrl, method, headers);
        connection.setDoOutput(true);

        if (body != null && !body.isEmpty()) {
            connection.setRequestProperty("Content-Type", "application/json");
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        return getResponse(connection);
    }

    private static String getResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        boolean success = responseCode >= 200 && responseCode < 300;

        try (InputStream inputStream = success ? connection.getInputStream()
            : connection.getErrorStream();
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } finally {
            connection.disconnect();
        }
    }
}
