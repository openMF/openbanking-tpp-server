package hu.dpc.openbanking.oauth2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Some http helper function.
 */
public class HttpHelper {

    /**
     * Stream respond from inputStream and close connection. If inputStream is not available then stream from errorStream.
     *
     * @param conn
     * @return response
     * @throws IOException when stream problem occur
     */
    public static String getResponseContent(HttpURLConnection conn) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = conn.getInputStream();
        } catch (IOException ioe) {
            // DO NOTHING
        }

        if (null == inputStream) {
            inputStream = conn.getErrorStream();
        }

        StringBuilder response = new StringBuilder(4096);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        conn.disconnect();

        return response.toString();
    }

}
