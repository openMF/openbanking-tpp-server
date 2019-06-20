package hu.dpc.openbank.tpp.acefintech.backend.enity.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SupportedBanks {
    @JsonProperty("BankInfo")
    private List<BankInfo> bankInfoList = new ArrayList<>();

    public void add(BankInfo bankInfo) {
        bankInfoList.add(bankInfo);
    }

    public List<BankInfo> getBankInfoList() {
        return bankInfoList;
    }

    public void setBankInfoList(List<BankInfo> bankInfoList) {
        this.bankInfoList = bankInfoList;
    }
}
