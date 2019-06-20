package hu.dpc.openbank.tpp.acefintech.backend.repository;

public class APICallException extends RuntimeException {
    public APICallException(String message) {
        super(message);
    }
}
