package hu.dpc.openbank.tpp.acefintech.backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import hu.dpc.openbank.tpp.acefintech.backend.enity.HttpResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.Account;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.AccountResponse;
import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.AccountResponseData;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;
import hu.dpc.openbank.tpp.acefintech.backend.enity.oauth2.TokenResponse;
import hu.dpc.openbank.tpp.acefintech.backend.repository.BankRepository;
import hu.dpc.openbanking.oauth2.HttpsTrust;
import hu.dpc.openbanking.oauth2.OAuthConfig;
import hu.dpc.openbanking.oauth2.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    private BankRepository bankRepository;

    @GetMapping(path = "/accounts", produces = "application/json")
    public ResponseEntity getAccounts(@RequestHeader("x-tpp-bankid") String bankId) {
        System.out.println("BankID: " + bankId);

        try {
            BankInfo bankInfo = bankRepository.getOne(bankId);
            OAuthConfig oAuthConfig = new OAuthConfig(bankInfo);
            TokenManager tokenManager = new TokenManager(oAuthConfig);

            TokenResponse tokenResponse = tokenManager.getAccessTokenWithClientCredential(new String[]{"accounts"});
            System.out.println(tokenResponse.getAccessToken());

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + tokenResponse.getAccessToken());
            headers.put("x-fapi-interaction-id", UUID.randomUUID().toString());

            HttpResponse httpResponse = doPost(new URL(bankInfo.getAccountsUrl() + "/account-initiations"), headers, "{'Data':'valami'}");

            String content = httpResponse.getContent();
            if (null != content && content.startsWith("<")) {

            }

            return new ResponseEntity<>(content, HttpStatus.resolve(httpResponse.getResponseCode()));
        } catch (Throwable e) {
            return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping(path = "/accounts2", produces = "application/json")
    public String getAccounts2(@RequestHeader("x-tpp-bankid") String bankId) {
        return "{'haho': 'phie'}";
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
