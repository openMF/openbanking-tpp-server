package hu.dpc.openbank.tpp.acefintech.backend.controller;


import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.SupportedBanks;
import hu.dpc.openbank.tpp.acefintech.backend.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/bank/v1/")
public class BankController {
    @Autowired
    private BankRepository bankRepository;

    @GetMapping(path = "/supported", produces = "application/json")
    public SupportedBanks getSupportedBanks() {
        SupportedBanks supportedBanks = new SupportedBanks();
        supportedBanks.setBankInfoList(bankRepository.findAll());
        return supportedBanks;
    }
}
