/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbanking.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.BankInfo;
import hu.dpc.openbank.tpp.acefintech.backend.enity.bank.SupportedBanks;

import java.util.UUID;

public class Serialize {
    public static void main(String[] args) {

        Object obj = supportedBanks();

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            System.out.println(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private static SupportedBanks supportedBanks() {
        SupportedBanks supportedBanks = new SupportedBanks();
        {
            BankInfo bankInfo = new BankInfo();
            bankInfo.setBankId(UUID.randomUUID().toString());
            bankInfo.setBankName("Lion");
            bankInfo.setShortName("Lion");
            bankInfo.setLongName("Lion Bank Ltd.");
            bankInfo.setLogoUrl("/images/bank/lion.png");

            supportedBanks.add(bankInfo);
        }
        {
            BankInfo bankInfo = new BankInfo();
            bankInfo.setBankId(UUID.randomUUID().toString());
            bankInfo.setBankName("Elephant");
            bankInfo.setShortName("Elephant");
            bankInfo.setLongName("Elephant Bank Ltd.");
            bankInfo.setLogoUrl("/images/bank/elephant.png");

            supportedBanks.add(bankInfo);
        }

        return supportedBanks;
    }
}
