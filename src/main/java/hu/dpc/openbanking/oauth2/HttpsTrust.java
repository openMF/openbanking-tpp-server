/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbanking.oauth2;

import javax.net.ssl.*;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class HttpsTrust {

    static final public HttpsTrust INSTANCE = new HttpsTrust(new Ssl());

    static class Ssl {
        SSLSocketFactory newFactory(TrustManager... managers) throws NoSuchAlgorithmException, KeyManagementException {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, managers, new SecureRandom());
            return context.getSocketFactory();
        }
    }

    private final SSLSocketFactory socketFactory;
    private final HostnameVerifier hostnameVerifier;

    private HttpsTrust(Ssl context) {
        this.socketFactory = createSocketFactory(context);
        this.hostnameVerifier = createHostnameVerifier();
    }

    public void trust(HttpURLConnection connection) {
        if (connection instanceof HttpsURLConnection) {
            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
            httpsConnection.setSSLSocketFactory(socketFactory);
            httpsConnection.setHostnameVerifier(hostnameVerifier);
        }
    }

    /**
     * Trust all certificates
     */
    private static SSLSocketFactory createSocketFactory(Ssl context) {
        try {
            return context.newFactory(new AlwaysTrustManager());
        } catch (Exception e) {
            throw new IllegalStateException("Fail to build SSL factory", e);
        }
    }

    /**
     * Trust all hosts
     */
    private static HostnameVerifier createHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
    }

    static class AlwaysTrustManager implements X509TrustManager {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            // Do not check
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
            // Do not check
        }
    }
}