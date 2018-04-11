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
package fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.AppUserId;
import fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.builder.AppUserIdBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link AppUserIdDeserializer}'s unit tests
 *
 * @author Aurelien Bourdon
 */
@DisplayName("AppUserIdDeserializer")
public class AppUserIdDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("An AppUserIdDeserializer must deserialize AppUserId when valid format is given")
    public void testAppUserIdDeserializationWithValidFormat() throws IOException {
        final AppUserId appUserId = objectMapper.readValue("\"Sensor_Sequence_1\"", AppUserId.class);
        assertEquals(
                AppUserIdBuilder.builder()
                        .id(1)
                        .sensor("Sensor")
                        .sequence("Sequence")
                        .build(),
                appUserId
        );
    }

    @Test
    @DisplayName("An AppUserIdDeserializer must not deserialize AppUserId when invalid format is given")
    public void testAppUserIdDeserializationWithInvalidFormat() {
        assertAll(
                () -> assertThrows(InvalidFormatException.class, () -> objectMapper.readValue("\"Sensor_Sequence_wrong\"", AppUserId.class), "An AppUserIdDeserializer must not deserialize AppUserId when id is not an integer"),
                () -> assertThrows(InvalidFormatException.class, () -> objectMapper.readValue("\"wrong\"", AppUserId.class), "An AppUserIdDeserializer must not deserialize AppUserId when line is not composed by AppUserId's separator")
        );
    }

}
