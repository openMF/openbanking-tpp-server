/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package uk.org.openbanking.v3_1_2.parser;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public final class DateUtils {

  private static final SimpleDateFormat LOCAL_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");


  private DateUtils() {
    // Utility class
  }


  public static LocalDateTime parseLocalFormatDateTime(final String date) {
    return null == date ? null : LocalDateTime.ofInstant(OffsetDateTime.parse(date).toInstant(), ZoneOffset.UTC);
  }


  public static String formatLocalFormatDateTime(final LocalDateTime date) {
    return null == date ? null : LOCAL_DATE_TIME_FORMAT.format(Date.from(date.toInstant(ZoneOffset.UTC)));
  }
}
