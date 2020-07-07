/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.tpp.acefintech.backend.entity.bank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ACCESS_TOKEN")
public class AccessToken {

  public static final long BEFORE_EXPIRE_MS = 3_000L;
  @Id
  @Column(name = "ID")
  @GeneratedValue
  private int id;
  @Column(name = "BANK_ID")
  private String bankId;
  @Column(name = "USERNAME")
  private String userName;
  @Column(name = "ACCESS_TOKEN")
  private String accessToken;
  @Column(name = "ACCESS_TOKEN_TYPE")
  private String accessTokenType;
  @Column(name = "EXPIRES")
  private long expires;
  @Column(name = "REFRESH_TOKEN")
  private String refreshToken;


  /**
   * Token is expired?
   */
  public boolean isExpired() {
    return (System.currentTimeMillis() - BEFORE_EXPIRE_MS) > expires;
  }


  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("class AccessToken {\n");
    sb.append("    id: ").append(id).append("\n");
    sb.append("    bankId: ").append(bankId).append("\n");
    sb.append("    userName: ").append(userName).append("\n");
    sb.append("    accessToken: ").append(accessToken).append("\n");
    sb.append("    expires: ").append(expires).append("  expired: ").append(isExpired()).append("\n");
    sb.append("    refreshToken: ").append(refreshToken).append("\n");
    sb.append("}");
    return sb.toString();
  }
}
