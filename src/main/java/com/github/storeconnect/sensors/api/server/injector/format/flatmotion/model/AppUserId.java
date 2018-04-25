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
package com.github.storeconnect.sensors.api.server.injector.format.flatmotion.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.storeconnect.sensors.api.server.injector.format.flatmotion.jackson.AppUserIdDeserializer;

import java.util.Objects;

/**
 * Specify the type of the {@link FlatMotionObservation#appUserId}
 *
 * @author Aurelien Bourdon
 */
@JsonDeserialize(using = AppUserIdDeserializer.class)
public class AppUserId {

    private String sensor;
    private String sequence;
    private Integer id;

    @Override
    public int hashCode() {
        return Objects.hash(sensor, sequence, id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof AppUserId)) return false;
        final AppUserId appUserId = (AppUserId) o;
        return Objects.equals(sensor, appUserId.sensor) &&
                Objects.equals(sequence, appUserId.sequence) &&
                Objects.equals(id, appUserId.id);
    }

    @Override
    public String toString() {
        return "AppUserId{" +
                "sensor='" + sensor + '\'' +
                ", sequence='" + sequence + '\'' +
                ", id=" + id +
                '}';
    }

    public String getSensor() {
        return sensor;
    }

    public void setSensor(final String sensor) {
        this.sensor = sensor;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(final String sequence) {
        this.sequence = sequence;
    }

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }
}
