/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.exceptions;

public class BankConfigException extends RuntimeException {

  private static final long serialVersionUID = 4742876299057861863L;


  public BankConfigException(final String message) {
    super(message);
  }
}

