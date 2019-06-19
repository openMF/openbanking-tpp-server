package hu.dpc.openbank.tpp.acefintech.backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import hu.dpc.openbank.tpp.acefintech.backend.enity.HttpResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.Account;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.AccountResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.AccountResponseData;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.AccessToken;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;
import hu.dpc.openbank.tpp.acefintech.backend.enity.oauth2.TokenResponse;
import hu.dpc.openbank.tpp.acefintech.backend.repository.AccessTokenRepository;
import hu.dpc.openbank.tpp.acefintech.backend.repository.BankRepository;
import hu.dpc.openbanking.oauth2.HttpsTrust;
import hu.dpc.openbanking.oauth2.OAuthConfig;
import hu.dpc.openbanking.oauth2.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "/aisp/v1/")
public class AISPController {

    private static final Logger LOG = LoggerFactory.getLogger(AISPController.class);
    /**
     * Getting bank infomations.
     * TODO cache results
     */
    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private AccessTokenRepository accessTokenRepository;


    private AccessToken getLatestUserAccessToken(String userId, String bankId) {
        return accessTokenRepository.getLatest(bankId, userId, "accounts");
    }

    private AccessToken getLatestAccessToken(String bankId) {
        return accessTokenRepository.getLatest(bankId, "accounts");
    }


    /**
     * Get Accounts ConsentId
     *
     * @param bankId
     * @return consentId if request it was not success return empty.
     */
    public String getConsentId(String bankId) {
        LOG.info("BankID: {}", bankId);

        try {
            BankInfo bankInfo = bankRepository.getOne(bankId);
            if (bankId.equals(bankInfo.getBankId())) {
                // Testing result
            }

            // TODO Checking previous accessToken is still valid and use that
            String accessToken = null;
            AccessToken prevAccessToken = getLatestAccessToken(bankId);
            if (null != prevAccessToken) {
                LOG.info("Client level access token exists: {} is still valid {} {} {}", prevAccessToken.getAccessToken(), (System.currentTimeMillis() / 1000), prevAccessToken.getExpires(), (System.currentTimeMillis() / 1000) < prevAccessToken.getExpires());
                if ((System.currentTimeMillis() / 1000) < prevAccessToken.getExpires()) {
                    LOG.info("Latest access token IS STILL valid!");
                    accessToken = prevAccessToken.getAccessToken();
                } else {
                    LOG.info("Latest access token expired!");
                    prevAccessToken = null;
                }
            } else {
                LOG.info("No previous level access token exists!");
            }

            if (null == prevAccessToken) {
                // Create OAuthConfig
                OAuthConfig oAuthConfig = new OAuthConfig(bankInfo);
                // Create TokenManager
                TokenManager tokenManager = new TokenManager(oAuthConfig);

                // Get new AccessToken
                TokenResponse tokenResponse = tokenManager.getAccessTokenWithClientCredential(new String[]{"accounts"});
                accessToken = tokenResponse.getAccessToken();
                LOG.debug("AccessToken: {}", accessToken);

                // Save accessToken for later usage
                AccessToken newAccessToken = new AccessToken();
                newAccessToken.setAccessToken(accessToken);
                newAccessToken.setAccessTokenType("client");
                newAccessToken.setScope("accounts");
                newAccessToken.setExpires(tokenResponse.getJwtExpires());
                newAccessToken.setBankId(bankId);
                accessTokenRepository.save(newAccessToken);
            }

            // Setup HTTP headers
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);
            headers.put("x-fapi-interaction-id", UUID.randomUUID().toString());
            // get ConsentID
            HttpResponse httpResponse = doPost(new URL(bankInfo.getAccountsUrl() + "/account-initiations"), headers, "{'Data':'valami'}");

            // Sometimes WSO2 respond errors in xml
            String content = httpResponse.getContent().trim();
            if (null != content && content.startsWith("<")) {
                LOG.error("Respond in XML, it's mean something error occured! {}", content);
                // <am:fault xmlns:am="http://wso2.org/apimanager"><am:code>101503</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>Error connecting to the back end</am:description></am:fault>
                return "";
            }
            int respondCode = httpResponse.getResponseCode();
            if (!(respondCode >= 200 && respondCode < 300)) {
                LOG.error("Respond code {}; respond: [{}]", respondCode, content);
                return "ConsentId:12345";
            }

            // TODO Parse response & return consentId
            return "";
        } catch (Throwable e) {
            LOG.error("Something went wrong!", e);
            return "";
        }
    }


    /**
     * GetAccounts
     *
     * @param bankId
     * @return
     */
    @GetMapping(path = "/accounts", produces = "application/json")
    public ResponseEntity getAccounts(@RequestHeader("x-tpp-bankid") String bankId, @AuthenticationPrincipal User user) {
        LOG.info("User {}", user);
        LOG.info("BankID: {}", bankId);
        try {
            BankInfo bankInfo = bankRepository.getOne(bankId);
            if (bankId.equals(bankInfo.getBankId())) {
                // Testing result
            }
            // Create OAuthConfig
            OAuthConfig oAuthConfig = null;
            oAuthConfig = new OAuthConfig(bankInfo);
            // Create TokenManager
            TokenManager tokenManager = new TokenManager(oAuthConfig);

            AccessToken prevAccessToken = getLatestUserAccessToken(user.getUsername(), bankId);
            if (null != prevAccessToken) {
                LOG.info("User has last access token? Is Still vaild? {} {} {}", prevAccessToken.getExpires(), (System.currentTimeMillis() / 1000), (System.currentTimeMillis() / 1000) < prevAccessToken.getExpires());
                // TODO try refresh token
                if ((System.currentTimeMillis() / 1000) > prevAccessToken.getExpires()) {
                    LOG.info("Previous access token expired!");
                    TokenResponse refreshToken = tokenManager.refreshToken(prevAccessToken.getRefreshToken());
                    if (refreshToken.getHttpResponseCode()==200) {
                        // Save accessToken for later usage
                        AccessToken newAccessToken = new AccessToken();
                        newAccessToken.setAccessToken(refreshToken.getAccessToken());
                        newAccessToken.setAccessTokenType("user");
                        newAccessToken.setScope("accounts");
                        newAccessToken.setExpires(refreshToken.getJwtExpires());
                        newAccessToken.setRefreshToken(refreshToken.getRefreshToken());
                        newAccessToken.setBankId(bankId);
                        newAccessToken.setUserName(user.getUsername());
                        accessTokenRepository.save(newAccessToken);

                        prevAccessToken = newAccessToken;
                    } else {
                        LOG.info("Refresh token refreshing failed!");
                        String consentId = getConsentId(bankId);
                        return new ResponseEntity<>("Authorize require", HttpStatus.PRECONDITION_REQUIRED);
                    }
                }
            } else {
                LOG.info("User has'nt got access token yet!");
                // TODO Authorize required
                String consentId = getConsentId(bankId);
                return new ResponseEntity<>("Authorize require", HttpStatus.PRECONDITION_REQUIRED);
            }

            // TODO Checking previous accessToken is still valid and use that
            // TODO Save accessToken for later usage


            // Setup HTTP headers
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + prevAccessToken.getAccessToken());
            headers.put("x-fapi-interaction-id", UUID.randomUUID().toString());
            // get ConsentID
            HttpResponse httpResponse = doPost(new URL(bankInfo.getAccountsUrl() + "/accounts"), headers, "{'Data':'valami'}");

            // Sometimes WSO2 respond errors in xml
            String content = httpResponse.getContent().trim();
            if (null != content && content.startsWith("<")) {
                LOG.error("Respond in XML, it's mean something error occured! {}", content);
                // <am:fault xmlns:am="http://wso2.org/apimanager"><am:code>101503</am:code><am:type>Status report</am:type><am:message>Runtime Error</am:message><am:description>Error connecting to the back end</am:description></am:fault>
                return new ResponseEntity(content, HttpStatus.BAD_REQUEST);
            }
            int respondCode = httpResponse.getResponseCode();
            if (!(respondCode >= 200 && respondCode < 300)) {
                LOG.error("Respond code {}; respond: [{}]", respondCode, content);
            }
            return new ResponseEntity<>(content, HttpStatus.resolve(respondCode));
        } catch (Throwable e) {
            LOG.error("Something went wrong!", e);
            return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(path = "/accounts/{AccountId}", produces = "application/json")
    public AccountResponseData getAccount(@PathVariable("AccountId") String accountId) {
        AccountResponseData response = new AccountResponseData();

        AccountResponse accountResponse = new AccountResponse();
        Account account = new Account();
        account.setAccountId(accountId);
        accountResponse.addAccount(account);
        response.setResponse(accountResponse);

        return response;
    }

    @GetMapping(path = "/balances", produces = "application/json")
    public AccountResponseData getBalances() {
        AccountResponseData response = new AccountResponseData();

        AccountResponse accountResponse = new AccountResponse();
        Account account = new Account();
        account.setAccountId("accountId");
        accountResponse.addAccount(account);
        response.setResponse(accountResponse);

        return response;
    }

    @GetMapping(path = "/accounts/{AccountId}/balances", produces = "application/json")
    public AccountResponseData getBalance(@PathVariable("AccountId") String accountId) {
        AccountResponseData response = new AccountResponseData();

        AccountResponse accountResponse = new AccountResponse();
        Account account = new Account();
        account.setAccountId("accountId");
        accountResponse.addAccount(account);
        response.setResponse(accountResponse);

        return response;
    }


    /**
     * Execute token POST request.
     *
     * @return
     */
    public HttpResponse doPost(URL url, HashMap<String, String> headerParams, String jsonContentData) {
        HttpResponse httpResponse = new HttpResponse();
        int responseCode = -1;
        try {
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            HttpsTrust.INSTANCE.trust(conn);
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));

            writer.write(jsonContentData);

            writer.flush();
            writer.close();
            os.close();

            responseCode = conn.getResponseCode();

            ObjectMapper mapper = new ObjectMapper();
            String response = "";
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader((responseCode == HttpsURLConnection.HTTP_OK) ? conn.getInputStream() : conn.getErrorStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
            conn.disconnect();


            httpResponse.setResponseCode(responseCode);
            httpResponse.setContent(response);
            return httpResponse;
        } catch (IOException e) {
            httpResponse.setResponseCode(-1);
            httpResponse.setContent(e.getLocalizedMessage());
            e.printStackTrace();
        }

        return httpResponse;
    }


}
