/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.enity.bank;

import com.fasterxml.jackson.annotation.JsonProperty;

import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.AccountAccount;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SupportedBanks {
    @JsonProperty("BankInfo")
    private List<BankInfo> bankInfoList = new ArrayList<>();

    public void add(final BankInfo bankInfo) {
        bankInfoList.add(bankInfo);
    }

   
}
