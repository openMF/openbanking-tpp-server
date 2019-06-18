package hu.dpc.openbank.tpp.acefintech.backend.enity;

public class HttpResponse {
    private String content;
    private int responseCode;

    public HttpResponse() {
    }

    public HttpResponse(String content, int responseCode) {
        this.content = content;
        this.responseCode = responseCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
