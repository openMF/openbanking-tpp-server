/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.controller;


import hu.dpc.openbank.tpp.acefintech.backend.entity.bank.SupportedBanks;
import hu.dpc.openbank.tpp.acefintech.backend.repository.BankRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/banks/v1/")
public class BankController {

  private final BankRepository bankRepository;


  public BankController(final BankRepository bankRepository) {
    this.bankRepository = bankRepository;
  }


  @GetMapping(path = "/supported", produces = MediaType.APPLICATION_JSON_VALUE)
  public SupportedBanks getSupportedBanks() {
    return new SupportedBanks(bankRepository.findAll());
  }
}
