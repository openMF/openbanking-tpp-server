/*
 * This Source Code Form is subject to the terms of the Mozilla
 * Public License, v. 2.0. If a copy of the MPL was not distributed
 * with this file, You can obtain one at
 *
 * https://mozilla.org/MPL/2.0/.
 */
package hu.dpc.openbank.tpp.acefintech.backend.rest.parser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import hu.dpc.openbank.tpp.acefintech.backend.util.DateUtils;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalFormatDateTimeDeserializer extends StdDeserializer<LocalDateTime> {

    public LocalFormatDateTimeDeserializer() {
        super(LocalDateTime.class);
    }

    @Override
    public LocalDateTime deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        return DateUtils.parseLocalFormatDateTime(p.getText());
    }
}
