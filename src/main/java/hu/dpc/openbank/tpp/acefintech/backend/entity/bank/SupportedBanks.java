/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.entity.bank;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class SupportedBanks {

  @JsonProperty("BankInfo")
  private final List<BankInfo> bankInfoList;


  public SupportedBanks(final List<BankInfo> bankInfoList) {
    this.bankInfoList = List.copyOf(bankInfoList);
  }
}
