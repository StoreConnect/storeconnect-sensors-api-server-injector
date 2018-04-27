/**
 * Copyright 2018 Inria Lille
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.storeconnect.sensors.api.server.injector.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import de.fraunhofer.iosb.ilt.sta.jackson.ObjectMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * EpochTimeDeserializer's unit tests
 *
 * @author Aurelien Bourdon
 */
@DisplayName("EpochTimeDeserializer")
public class EpochTimeDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = ObjectMapperFactory.get();
    }

    @Test
    @DisplayName("An EpochTimeDeserializer must deserialize Instant when milliseconds from epoch is given")
    public void testEpochTimeDeserializationWithValidFormat() throws IOException {
        final InstantWrapper expected = new InstantWrapper(Instant.ofEpochMilli(1523361600000L));
        final InstantWrapper actual = objectMapper.readValue("{ \"instant\": \"1523361600000\" }", InstantWrapper.class);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("An EpochTimeDeserializer must not deserialize Instant when invalid format is given")
    public void testAppUserIdDeserializationWithInvalidFormat() {
        assertThrows(InvalidFormatException.class, () -> objectMapper.readValue("{ \"instant\": \"wrong\" }", InstantWrapper.class));
    }

    private static class InstantWrapper {

        @JsonDeserialize(using = EpochTimestampDeserializer.class)
        private Instant instant;

        public InstantWrapper() {
        }

        public InstantWrapper(final Instant instant) {
            this.instant = instant;
        }

        public Instant getInstant() {
            return instant;
        }

        @Override
        public int hashCode() {
            return Objects.hash(instant);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof InstantWrapper)) return false;
            final InstantWrapper that = (InstantWrapper) o;
            return Objects.equals(instant, that.instant);
        }

    }

}
