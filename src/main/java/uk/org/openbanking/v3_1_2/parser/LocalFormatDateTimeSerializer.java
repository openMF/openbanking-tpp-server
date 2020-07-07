/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package uk.org.openbanking.v3_1_2.parser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.LocalDateTime;

public class LocalFormatDateTimeSerializer extends StdSerializer<LocalDateTime> {

  private static final long serialVersionUID = 5399622556949932606L;


  public LocalFormatDateTimeSerializer() {
    super(LocalDateTime.class);
  }


  @Override
  public void serialize(final LocalDateTime value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
    gen.writeString(DateUtils.formatLocalFormatDateTime(value));
  }
}
