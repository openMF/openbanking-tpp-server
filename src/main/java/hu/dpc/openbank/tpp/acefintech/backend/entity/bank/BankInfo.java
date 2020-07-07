/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.entity.bank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "BANKS")
public class BankInfo {

  @Id
  @JsonProperty("BankId")
  @Column(name = "ID")
  private String bankId;

  @Column(name = "NAME")
  @JsonProperty("BankName")
  private String bankName;

  @Column(name = "SHORTNAME")
  @JsonProperty("ShortName")
  private String shortName;

  @Column(name = "LONGNAME")
  @JsonProperty("LongName")
  private String longName;

  @Column(name = "LOGOURL")
  @JsonProperty("LogoUrl")
  private String logoUrl;

  @JsonIgnore
  @Column(name = "TOKEN_URL")
  private String tokenUrl;

  @JsonIgnore
  @Column(name = "ACCOUNTS_URL")
  private String accountsUrl;

  @Column(name = "CLIENT_ID")
  @JsonProperty("ClientId")
  private String clientId;

  @JsonIgnore
  @Column(name = "CLIENT_SECRET")
  private String clientSecret;

  @Column(name = "CALLBACK_URL")
  @JsonProperty("CallbackUrl")
  private String callBackUrl;

  @JsonIgnore
  @Column(name = "USERNAME")
  private String username;

  @Column(name = "AUTHORIZE_URL")
  @JsonProperty("AuthorizeUrl")
  private String authorizeUrl;
  
  @JsonIgnore
  @Column(name = "PAYMENTS_URL")
  private String paymentsUrl;
}
