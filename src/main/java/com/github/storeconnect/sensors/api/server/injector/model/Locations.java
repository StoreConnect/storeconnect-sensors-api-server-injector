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

import com.github.storeconnect.sensors.api.client.model.builder.LocationBuilder;
import com.github.storeconnect.sensors.api.client.model.feature.builder.PointFeatureBuilder;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import org.geojson.LngLatAlt;
import org.geojson.Point;

/**
 * Default {@link Location} instance
 *
 * @author Aurelien Bourdon
 */
public final class Locations {

    public static final Location UNKNOWN = LocationBuilder.builder()
            .name("Unknown")
            .description("Represents an unknown place, when a location cannot be given")
            .location(PointFeatureBuilder.builder()
                    .geometry(new Point(new LngLatAlt(0.0, 0.0)))
                    .build())
            .build();

}
