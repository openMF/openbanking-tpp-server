/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.common.http;

import hu.dpc.openbank.exceptions.APICallException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Some http helper function.
 */
@Slf4j
public final class HttpHelper {


  /**
   * TryCount when connection refused occurs.
   */
  public static final int CONNECTION_REFUSED_TRYCOUNT = 3;
  public static final long CONNECTION_REFUSED_WAIT_IN_MS = 500;
  // Global timeout in ms.
  public static final int TIMEOUT_MS = 15_000;
  /**
   * WSO2 error message, when BANK API not available.
   */
  private static final String BANK_NOT_WORKING_ERROR = "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>101503</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>Error connecting to the back end</am:description></am:fault>";
  //    "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>303001</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>Currently , Address endpoint : [ Name : AccountInformationAPI--vv1_APIproductionEndpoint ] [ State : SUSPENDED ]</am:description></am:fault>";
  private static final String BANK_APIS_SUSPENDED = "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>303001</am:code>";
  private static final String WSO2_METHOD_NOT_FOUND = "<am:fault xmlns:am=\"http://wso2.org/apimanager\"><am:code>404</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>No matching resource found for given API Request</am:description></am:fault>";


  private HttpHelper() {
    // Utility class
  }


  /**
   * Stream respond from inputStream and close connection. If inputStream is not available then stream from errorStream.
   *
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

    final StringBuilder response = new StringBuilder(4096);
    final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
    String line;
    while (null != (line = br.readLine())) {
      response.append(line);
    }
    conn.disconnect();

    return response.toString();
  }


  /**
   * Execute API request.
   */
  @Nonnull
  public static HttpResponse doAPICall(final HttpMethod httpMethod, final URL url,
      final Map<String, String> headerParams,
      @Nullable final String jsonContentData) {
    final HttpResponse httpResponse = new HttpResponse();
    for (int trycount = CONNECTION_REFUSED_TRYCOUNT; trycount != 0; trycount--) {
      try {
        log.info("doAPICall: {} {}", httpMethod.name(), url);
        final HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        HttpsTrust.INSTANCE.trust(conn);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setRequestMethod(httpMethod.name());
        conn.setDoInput(true);
        final boolean hasContent = !(null == jsonContentData || jsonContentData.isEmpty());

        conn.setDoOutput(hasContent);

        conn.setRequestProperty(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        conn.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8");
        for (final Map.Entry<String, String> entry : headerParams.entrySet()) {
          log.info("doAPICall-Header: {}: {}", entry.getKey(), entry.getValue());
          conn.setRequestProperty(entry.getKey(), entry.getValue());
        }

        if (hasContent) {
          log.info("doAPICall-body: [{}]", jsonContentData);
          try (final OutputStream os = conn
              .getOutputStream(); final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            writer.write(jsonContentData);
            writer.flush();
          }
        }

        final int responseCode = conn.getResponseCode();
        log.info("Response code: {}", responseCode);
        final String response = getResponseContent(conn);
        log.info("Response: [{}]\n[{}]", response, conn.getResponseMessage());

        httpResponse.setHttpResponseCode(responseCode);
        httpResponse.setHttpRawContent(response);
        return httpResponse;
      } catch (final ConnectException ce) {
        log.error("Connection refused: trying... {} {}", trycount, ce.getLocalizedMessage());
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
        httpResponse.setHttpResponseCode(-1);
        httpResponse.setHttpRawContent(e.getLocalizedMessage());
        log.error("doAPICall", e);
        return httpResponse;
      }
    }

    return httpResponse;
  }


  /**
   * Handle WSO2 errors
   */
  public static void checkWSO2Errors(final String content) {
    if (!content.isEmpty() && '<' == content.charAt(0)) {
      log.error("Respond in XML, it's mean something error occured! {}", content);
      if (BANK_NOT_WORKING_ERROR.equals(content)) {
        throw new APICallException("API gateway cannot connect to BANK backend!");
      }
      if (content.startsWith(BANK_APIS_SUSPENDED)) {
        throw new APICallException("API gateway suspended!");
      }
      if (WSO2_METHOD_NOT_FOUND.equals(content)) {
        throw new APICallException("API method not found!");
      }
      throw new APICallException("API gateway problem!");
    }
  }
}
