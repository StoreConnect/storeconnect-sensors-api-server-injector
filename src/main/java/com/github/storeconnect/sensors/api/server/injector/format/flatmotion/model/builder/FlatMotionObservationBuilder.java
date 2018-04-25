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
package com.github.storeconnect.sensors.api.server.injector.format.flatmotion.model.builder;

import com.github.storeconnect.sensors.api.server.injector.format.flatmotion.model.AppUserId;
import com.github.storeconnect.sensors.api.server.injector.format.flatmotion.model.FlatMotionObservation;
import com.github.storeconnect.sensors.api.server.injector.format.flatmotion.model.Type;
import de.fraunhofer.iosb.ilt.sta.model.builder.api.AbstractBuilder;

import java.time.Instant;

/**
 * {@link FlatMotionObservation}'s {@link de.fraunhofer.iosb.ilt.sta.model.builder.api.Builder}
 *
 * @author Aurelien Bourdon
 */
public final class FlatMotionObservationBuilder extends AbstractBuilder<FlatMotionObservation> {

    private FlatMotionObservationBuilder() {
    }

    public static FlatMotionObservationBuilder builder() {
        return new FlatMotionObservationBuilder();
    }

    @Override
    protected FlatMotionObservation newBuildingInstance() {
        return new FlatMotionObservation();
    }

    public FlatMotionObservationBuilder appUserId(final AppUserId appUserId) {
        getBuildingInstance().setAppUserId(appUserId);
        return this;
    }

    public FlatMotionObservationBuilder venueId(final Integer venueId) {
        getBuildingInstance().setVenueId(venueId);
        return this;
    }

    public FlatMotionObservationBuilder building(final Integer building) {
        getBuildingInstance().setBuilding(building);
        return this;
    }

    public FlatMotionObservationBuilder floor(final Integer floor) {
        getBuildingInstance().setFloor(floor);
        return this;
    }

    public FlatMotionObservationBuilder lat(final Float lat) {
        getBuildingInstance().setLat(lat);
        return this;
    }

    public FlatMotionObservationBuilder lon(final Float lon) {
        getBuildingInstance().setLon(lon);
        return this;
    }

    public FlatMotionObservationBuilder deviceDate(final Instant deviceDate) {
        getBuildingInstance().setDeviceDate(deviceDate);
        return this;
    }

    public FlatMotionObservationBuilder type(final Type type) {
        getBuildingInstance().setType(type);
        return this;
    }

}
