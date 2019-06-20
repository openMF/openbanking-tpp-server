package hu.dpc.openbank.tpp.acefintech.backend.controller;


import hu.dpc.openbank.tpp.acefintech.backend.enity.oauth2.TokenResponse;
import hu.dpc.openbank.tpp.acefintech.backend.repository.OAuthAuthorizationRequiredException;
import hu.dpc.openbanking.oauth2.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/token/v1/")
public class TokenController extends WSO2Controller {
    private static final Logger LOG = LoggerFactory.getLogger(TokenController.class);


    @GetMapping(path = "/code/{Code}", produces = "application/json")
    public ResponseEntity getTokenCode(@RequestHeader(value = "x-tpp-bankid") String bankId, @AuthenticationPrincipal User user, @PathVariable("Code") String code) {
        TokenManager tokenManager = getTokenManager(bankId);
        TokenResponse accessTokenResponse = tokenManager.getAccessTokenFromCode(code);
        int responseCode = accessTokenResponse.getHttpResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            createAndSaveUserAccessToken(accessTokenResponse, bankId, user.getUsername());

            return new ResponseEntity<>("", HttpStatus.OK);
        }
        LOG.warn("Code exchange not succeeded. HTTP[{}] RAWResponse [{}]", responseCode, accessTokenResponse.getRawContent());
        String consentId = getConsentId(bankId);
        LOG.info("No user AccessToken exists. OAuth authorization required! ConsentID: [" + consentId + "]");
        throw new OAuthAuthorizationRequiredException(consentId);
    }
}
