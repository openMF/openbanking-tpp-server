/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

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
