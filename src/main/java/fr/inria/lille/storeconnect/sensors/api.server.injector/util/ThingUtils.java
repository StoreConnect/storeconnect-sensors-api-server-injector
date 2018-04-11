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
package fr.inria.lille.storeconnect.sensors.api.server.injector.util;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import fr.inria.lille.storeconnect.sensors.api.server.injector.model.Locations;
import fr.inria.lille.storeconnect.sensors.api.server.injector.model.Things;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Set of utilities when handle {@link Thing}
 *
 * @author Aurelien Bourdon
 */
public final class ThingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThingUtils.class);

    public static void linkOrCreateUnknownThing(final SensorThingsService sensorThingsService) throws ServiceFailureException {
        final EntityList<Thing> candidates = sensorThingsService.things()
                .query()
                .filter(String.format("name eq '%s'", Things.UNKNOWN.getName()))
                .list();
        if (candidates.isEmpty()) {
            LOGGER.info("Creating missing UNKNOWN Thing: {}...", Things.UNKNOWN);
            sensorThingsService.create(Things.UNKNOWN);
            LOGGER.info("Creating missing UNKNOWN Thing: {}... Done.", Things.UNKNOWN);
        } else {
            Things.UNKNOWN.setId(candidates.iterator().next().getId());
            LOGGER.info("Linked to existing UNKNOWN Thing: {}", Things.UNKNOWN);
        }
    }

}
