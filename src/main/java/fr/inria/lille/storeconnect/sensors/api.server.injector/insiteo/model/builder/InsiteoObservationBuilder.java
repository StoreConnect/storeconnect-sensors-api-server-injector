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
package fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.builder;

import de.fraunhofer.iosb.ilt.sta.model.builder.api.AbstractBuilder;
import fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.AppUserId;
import fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.InsiteoObservation;
import fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.Type;

import java.time.Instant;

/**
 * {@link InsiteoObservation}'s {@link de.fraunhofer.iosb.ilt.sta.model.builder.api.Builder}
 *
 * @author Aurelien Bourdon
 */
public final class InsiteoObservationBuilder extends AbstractBuilder<InsiteoObservation> {

    private InsiteoObservationBuilder() {
    }

    public static InsiteoObservationBuilder builder() {
        return new InsiteoObservationBuilder();
    }

    @Override
    protected InsiteoObservation newBuildingInstance() {
        return new InsiteoObservation();
    }

    public InsiteoObservationBuilder appUserId(final AppUserId appUserId) {
        getBuildingInstance().setAppUserId(appUserId);
        return this;
    }

    public InsiteoObservationBuilder venueId(final Integer venueId) {
        getBuildingInstance().setVenueId(venueId);
        return this;
    }

    public InsiteoObservationBuilder building(final Integer building) {
        getBuildingInstance().setBuilding(building);
        return this;
    }

    public InsiteoObservationBuilder floor(final Integer floor) {
        getBuildingInstance().setFloor(floor);
        return this;
    }

    public InsiteoObservationBuilder lat(final Float lat) {
        getBuildingInstance().setLat(lat);
        return this;
    }

    public InsiteoObservationBuilder lon(final Float lon) {
        getBuildingInstance().setLon(lon);
        return this;
    }

    public InsiteoObservationBuilder deviceDate(final Instant deviceDate) {
        getBuildingInstance().setDeviceDate(deviceDate);
        return this;
    }

    public InsiteoObservationBuilder type(final Type type) {
        getBuildingInstance().setType(type);
        return this;
    }

}
