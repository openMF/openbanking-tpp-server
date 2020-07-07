/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */

package hu.dpc.openbank.exceptions;

public class APICallException extends RuntimeException {

    private static final long serialVersionUID = -8771896802408933169L;


    public APICallException(final String message) {
    super(message);
  }
}

