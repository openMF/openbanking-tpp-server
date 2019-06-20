package hu.dpc.openbank.tpp.acefintech.backend.repository;

public class OAuthAuthorizationRequiredException extends RuntimeException {
    private String consentId;

    public OAuthAuthorizationRequiredException(String consentId) {
        super("ConsentID = [" + consentId + "]");
        this.consentId = consentId;
    }

    public String getConsentId() {
        return consentId;
    }
}
