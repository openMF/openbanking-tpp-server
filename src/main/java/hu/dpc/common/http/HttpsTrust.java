/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.common.http;

import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.annotation.Nullable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsTrust {

  public static final HttpsTrust INSTANCE = new HttpsTrust(new HttpsTrust.Ssl());


  private HttpsTrust(final HttpsTrust.Ssl context) {
    socketFactory = createSocketFactory();
    hostnameVerifier = createHostnameVerifier();
  }


  private final SSLSocketFactory socketFactory;
  private final HostnameVerifier hostnameVerifier;


  /**
   * Trust all certificates
   */
  private static SSLSocketFactory createSocketFactory() {
    try {
      return HttpsTrust.Ssl.newFactory(new HttpsTrust.AlwaysTrustManager());
    } catch (final Exception e) {
      throw new IllegalStateException("Fail to build SSL factory", e);
    }
  }


  /**
   * Trust all hosts
   */
  private static HostnameVerifier createHostnameVerifier() {
    return new HostnameVerifier() {
      @Override
      public boolean verify(@Nullable final String hostname, @Nullable final SSLSession session) {
        return true;
      }
    };
  }


  public void trust(final HttpURLConnection connection) {
    if (connection instanceof HttpsURLConnection) {
      final HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
      httpsConnection.setSSLSocketFactory(socketFactory);
      httpsConnection.setHostnameVerifier(hostnameVerifier);
    }
  }


  static class Ssl {

    static SSLSocketFactory newFactory(final TrustManager... managers) throws NoSuchAlgorithmException, KeyManagementException {
      final SSLContext context = SSLContext.getInstance("TLS");
      context.init(null, managers, new SecureRandom());
      return context.getSocketFactory();
    }
  }

  static class AlwaysTrustManager implements X509TrustManager {

    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return new X509Certificate[0];
    }


    @Override
    public void checkClientTrusted(@Nullable final X509Certificate[] chain,
        @Nullable final String authType) {
      // Do not check
    }


    @Override
    public void checkServerTrusted(@Nullable final X509Certificate[] chain,
        @Nullable final String authType) {
      // Do not check
    }
  }
}
