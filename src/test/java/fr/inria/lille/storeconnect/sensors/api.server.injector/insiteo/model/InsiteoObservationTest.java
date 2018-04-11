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
package fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model;

import de.fraunhofer.iosb.ilt.sta.jackson.ObjectMapperFactory;
import fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.builder.AppUserIdBuilder;
import fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.builder.InsiteoObservationBuilder;
import fr.inria.lille.storeconnect.sensors.api.server.injector.util.ResourcesUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("InsiteoObservation")
public class InsiteoObservationTest {

    @Test
    @DisplayName("Insiteo deserialization with valid format")
    public void testDeserializationWithValidFormat() throws IOException {
        final InsiteoObservation insiteoObservation = ObjectMapperFactory.get().readValue(
                ResourcesUtils.fromResources("insiteoObservation.json", getClass()),
                InsiteoObservation.class
        );
        assertEquals(
                InsiteoObservationBuilder.builder()
                        .appUserId(AppUserIdBuilder.builder()
                                .sensor("Cam1")
                                .sequence("T1")
                                .id(2)
                                .build())
                        .building(1)
                        .deviceDate(Instant.ofEpochMilli(1512041732159L))
                        .floor(0)
                        .lat(50.63347394198766f)
                        .lon(3.0242195454128904f)
                        .type(Type.LOCATION)
                        .venueId(95)
                        .build(),
                insiteoObservation,
                "An Insiteo deserialization must be deserialized with a valid format"
        );
    }

}
