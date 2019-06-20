package hu.dpc.openbank.tpp.acefintech.backend.repository;

public class BankIDNotFoundException extends RuntimeException {
    public BankIDNotFoundException(String bankId) {
        super("BankID not found [" + bankId + "]!");
    }

}
