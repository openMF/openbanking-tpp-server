/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.common.http;

import hu.dpc.openbank.exceptions.APICallException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Some http helper function.
 */
public class HttpHelper {
    /**
     * TryCount when connection refused occurs.
     */
    public static final  int    CONNECTION_REFUSED_TRYCOUNT   = 3;
    public static final  long   CONNECTION_REFUSED_WAIT_IN_MS = 500;
    private static final Logger LOG                           = LoggerFactory.getLogger(HttpHelper.class);
    /**
     * WSO2 error message, when BANK API not available.
     */
    private static final String BANK_NOT_WORKING_ERROR        = "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>101503</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>Error connecting to the back end</am:description></am:fault>";
    //    "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>303001</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>Currently , Address endpoint : [ Name : AccountInformationAPI--vv1_APIproductionEndpoint ] [ State : SUSPENDED ]</am:description></am:fault>";
    private static final String BANK_APIS_SUSPENDED           = "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>303001</am:code>";
    private static final String WSO2_METHOD_NOT_FOUND         = "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>404</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>No matching resource found for given API Request</am:description></am:fault>";

    /**
     * Stream respond from inputStream and close connection. If inputStream is not available then stream from errorStream.
     *
     * @param conn
     * @return response
     * @throws IOException when stream problem occur
     */
    @Nonnull
    public static String getResponseContent(final HttpURLConnection conn) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = conn.getInputStream();
        } catch (final IOException ioe) {
            // DO NOTHING
        }

        if (null == inputStream) {
            inputStream = conn.getErrorStream();
        }

        final StringBuilder  response = new StringBuilder(4096);
        final BufferedReader br       = new BufferedReader(new InputStreamReader(inputStream)); String line;
        while (null != (line = br.readLine())) {
            response.append(line);
        }
        conn.disconnect();

        return response.toString();
    }

    /**
     * Execute API request.
     *
     * @param httpMethod
     * @param headerParams
     * @param jsonContentData
     * @return
     */
    public static @NotNull HttpResponse doAPICall(final HttpMethod httpMethod, final URL url,
                                                  final Map<String, String> headerParams,
                                                  @Nullable final String jsonContentData) {
        final HttpResponse httpResponse = new HttpResponse();
        for (int trycount = CONNECTION_REFUSED_TRYCOUNT; 0 < trycount--; ) {
            try {
                LOG.info("doAPICall: {} {}", httpMethod.name(), url);
                final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                HttpsTrust.INSTANCE.trust(conn); conn.setReadTimeout(10000); conn.setConnectTimeout(15000);
                conn.setRequestMethod(httpMethod.name()); conn.setDoInput(true);
                final boolean hasContent = !(null == jsonContentData || jsonContentData.isEmpty());

                conn.setDoOutput(hasContent);

                conn.setRequestProperty(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
                for (final Map.Entry<String, String> entry : headerParams.entrySet()) {
                    LOG.info("doAPICall-Header: {}: {}", entry.getKey(), entry.getValue());
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }

                if (hasContent) {
                    LOG.info("doAPICall-body: [{}]", jsonContentData); try (final OutputStream os = conn
                            .getOutputStream(); final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
                        writer.write(jsonContentData); writer.flush();
                    }
                }

                final int responseCode = conn.getResponseCode(); LOG.info("Response code: {}", responseCode);
                final String response = getResponseContent(conn); LOG.info("Response: [{}]\n[{}]", response, conn
                        .getResponseMessage());

                httpResponse.setHttpResponseCode(responseCode); httpResponse.setHttpRawContent(response);
                return httpResponse;
            } catch (final ConnectException ce) {
                LOG.error("Connection refused: trying... {} {}", trycount, ce.getLocalizedMessage());
                if (0 < trycount) {
                    try {
                        Thread.sleep(CONNECTION_REFUSED_WAIT_IN_MS);
                    } catch (final InterruptedException e) {
                        // DO NOTHING
                    }
                } else {
                    throw new APICallException("Connection refused");
                }
            } catch (final IOException e) {
                httpResponse.setHttpResponseCode(-1); httpResponse.setHttpRawContent(e.getLocalizedMessage());
                LOG.error("doAPICall", e); return httpResponse;
            }
        }

        return httpResponse;
    }

    /**
     * Handle WSO2 errors
     *
     * @param content
     */
    public static void checkWSO2Errors(final String content) {
        if (!content.isEmpty() && '<' == content.charAt(0)) {
            LOG.error("Respond in XML, it's mean something error occured! {}", content);
            if (BANK_NOT_WORKING_ERROR.equals(content)) {
                throw new APICallException("API gateway cannot connect to BANK backend!");
            } if (content.startsWith(BANK_APIS_SUSPENDED)) {
                throw new APICallException("API gateway suspended!");
            } if (WSO2_METHOD_NOT_FOUND.equals(content)) {
                throw new APICallException("API method not found!");
            } throw new APICallException("API gateway problem!");
        }
    }
}
