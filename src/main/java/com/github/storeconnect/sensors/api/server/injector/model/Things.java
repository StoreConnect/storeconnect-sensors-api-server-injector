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
package com.github.storeconnect.sensors.api.server.injector.model;

import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.builder.ThingBuilder;

/**
 * Default {@link Thing} instances
 *
 * @author Aurelien Bourdon
 */
public final class Things {

    public static final Thing UNKNOWN = ThingBuilder.builder()
            .name("Unknown")
            .description("Fake Thing used when no Thing can be given for a Datastream creation")
            .location(Locations.UNKNOWN)
            .build();

}
