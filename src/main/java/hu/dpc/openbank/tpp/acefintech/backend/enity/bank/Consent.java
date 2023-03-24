/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.enity.bank;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import hu.dpc.openbank.tpp.acefintech.backend.enity.aisp.AccountAccount;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="CONSENTS")
@Getter
@Setter
public class Consent {
    @Id
    @Column(name="ID")
    private int id;
    @Column(name="BANK_ID")
    private String bankId;
    @Column(name="CONSENTID")
    private String consentId;
    @Column(name="EXPIRES")
    private int expires;

  
}
