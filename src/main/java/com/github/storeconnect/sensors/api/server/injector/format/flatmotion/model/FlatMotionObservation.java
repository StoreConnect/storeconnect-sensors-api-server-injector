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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.storeconnect.sensors.api.server.injector.jackson.EpochTimestampDeserializer;

import java.time.Instant;
import java.util.Objects;

/**
 * The StoreConnect's flat-motion observation format
 *
 * @author Aurelien Bourdon
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlatMotionObservation {

    @JsonProperty("appuserid")
    private AppUserId appUserId;
    @JsonProperty("venueid")
    private Integer venueId;
    private Integer building;
    private Integer floor;
    private Float lat;
    private Float lon;
    @JsonProperty("devicedate")
    @JsonDeserialize(using = EpochTimestampDeserializer.class)
    private Instant deviceDate;
    private Type type;

    @Override
    public int hashCode() {
        return Objects.hash(appUserId, venueId, building, floor, lat, lon, deviceDate, type);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof FlatMotionObservation)) return false;
        final FlatMotionObservation that = (FlatMotionObservation) o;
        return Objects.equals(appUserId, that.appUserId) &&
                Objects.equals(venueId, that.venueId) &&
                Objects.equals(building, that.building) &&
                Objects.equals(floor, that.floor) &&
                Objects.equals(lat, that.lat) &&
                Objects.equals(lon, that.lon) &&
                Objects.equals(deviceDate, that.deviceDate) &&
                type == that.type;
    }

    @Override
    public String toString() {
        return "FlatMotionObservation{" +
                "appUserId=" + appUserId +
                ", venueId=" + venueId +
                ", building=" + building +
                ", floor=" + floor +
                ", lat=" + lat +
                ", lon=" + lon +
                ", deviceDate=" + deviceDate +
                ", type=" + type +
                '}';
    }

    public AppUserId getAppUserId() {
        return appUserId;
    }

    public void setAppUserId(final AppUserId appUserId) {
        this.appUserId = appUserId;
    }

    public Integer getVenueId() {
        return venueId;
    }

    public void setVenueId(final Integer venueId) {
        this.venueId = venueId;
    }

    public Integer getBuilding() {
        return building;
    }

    public void setBuilding(final Integer building) {
        this.building = building;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(final Integer floor) {
        this.floor = floor;
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(final Float lat) {
        this.lat = lat;
    }

    public Float getLon() {
        return lon;
    }

    public void setLon(final Float lon) {
        this.lon = lon;
    }

    public Instant getDeviceDate() {
        return deviceDate;
    }

    public void setDeviceDate(final Instant deviceDate) {
        this.deviceDate = deviceDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

}
