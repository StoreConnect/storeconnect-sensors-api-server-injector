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
package com.github.storeconnect.sensors.api.server.injector.format.flatmotion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.storeconnect.sensors.api.client.model.ObservedProperties;
import com.github.storeconnect.sensors.api.client.model.builder.DatastreamBuilder;
import com.github.storeconnect.sensors.api.client.model.builder.FeatureOfInterestBuilder;
import com.github.storeconnect.sensors.api.client.model.feature.builder.PointFeatureBuilder;
import com.github.storeconnect.sensors.api.client.model.motion.FeatureProperty;
import com.github.storeconnect.sensors.api.client.model.motion.builder.MotionEventBuilder;
import com.github.storeconnect.sensors.api.client.model.motion.builder.MotionObservationBuilder;
import com.github.storeconnect.sensors.api.client.model.motion.builder.MotionSubjectBuilder;
import com.github.storeconnect.sensors.api.server.injector.format.AbstractFormatInjector;
import com.github.storeconnect.sensors.api.server.injector.format.flatmotion.model.FlatMotionObservation;
import com.github.storeconnect.sensors.api.server.injector.model.Things;
import com.github.storeconnect.sensors.api.server.injector.model.UnitOfMeasurements;
import com.github.storeconnect.sensors.api.server.injector.util.LocationUtils;
import com.github.storeconnect.sensors.api.server.injector.util.ObservedPropertyUtils;
import com.github.storeconnect.sensors.api.server.injector.util.ThingUtils;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.jackson.ObjectMapperFactory;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.FeatureOfInterest;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.TimeObject;
import de.fraunhofer.iosb.ilt.sta.model.builder.SensorBuilder;
import de.fraunhofer.iosb.ilt.sta.model.builder.api.AbstractSensorBuilder;
import de.fraunhofer.iosb.ilt.sta.model.ext.DataArrayDocument;
import de.fraunhofer.iosb.ilt.sta.model.ext.DataArrayValue;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.geojson.LngLatAlt;
import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link FlatMotionObservation} {@link com.github.storeconnect.sensors.api.server.injector.format.FormatInjector}
 *
 * @author Aurelien Bourdon
 */
public class FlatMotionObservationInjector extends AbstractFormatInjector<FlatMotionObservation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlatMotionObservationInjector.class);

    public FlatMotionObservationInjector(final File input, final SensorThingsService sensorThingsService) {
        super(input, sensorThingsService);
    }

    @Override
    public void initEnvironment() throws ServiceFailureException {
        ObservedPropertyUtils.linkOrCreateMotionObservedProperty(getSensorThingsService());
        LocationUtils.linkOrCreateUnknownLocation(getSensorThingsService());
        ThingUtils.linkOrCreateUnknownThing(getSensorThingsService());
    }

    @Override
    public List<FlatMotionObservation> parse(final File input) throws IOException {
        return ObjectMapperFactory.get().readValue(input, new TypeReference<List<FlatMotionObservation>>() {
        });
    }

    @Override
    public void inject(final List<FlatMotionObservation> data) throws ServiceFailureException {
        LOGGER.info("Preparing flat-motion observations for sending...");
        final DataArrayDocument flatMotionObservations = toDatastreams(data)
                .entrySet()
                .stream()
                .map(this::toDataArrayValue)
                .reduce(
                        new DataArrayDocument(),
                        (acc, dataArrayValue) -> {
                            acc.addDataArrayValue(dataArrayValue);
                            return acc;
                        },
                        (dataArrayDocument1, dataArrayDocument2) -> {
                            final DataArrayDocument merge = new DataArrayDocument();
                            dataArrayDocument1.getValue().forEach(merge::addDataArrayValue);
                            dataArrayDocument2.getValue().forEach(merge::addDataArrayValue);
                            return merge;
                        }
                );
        LOGGER.info("Preparing flat-motion observations for sending... {} flat-motion observations processed for {} Datastream{}.",
                flatMotionObservations.getObservations().size(),
                flatMotionObservations.getValue().size(),
                flatMotionObservations.getValue().size() > 1 ? "s" : ""
        );

        LOGGER.info("Sending flat-motion observations to server...");
        getSensorThingsService().create(flatMotionObservations);
        LOGGER.info("Sending flat-motion observations to server... Done.");
    }

    protected Map<Datastream, List<FlatMotionObservation>> toDatastreams(final List<FlatMotionObservation> data) {
        return data.stream()
                .collect(Collectors.toMap(
                        flatMotionObservation -> {
                            try {
                                return getOrCreateAssociatedDatastream(getOrCreateAssociatedSensor(flatMotionObservation));
                            } catch (ServiceFailureException e) {
                                throw new IllegalStateException(e);
                            }
                        },
                        value -> Arrays.asList(value),
                        (observations1, observations2) -> Stream.concat(observations1.stream(), observations2.stream()).collect(Collectors.toList())
                ));
    }

    protected Datastream getOrCreateAssociatedDatastream(final Sensor sensor) throws ServiceFailureException {
        // Search the Datastream associated to the given Sensor
        final EntityList<Datastream> candidates = getSensorThingsService().datastreams()
                .query()
                .filter(String.format("Sensor/id eq '%s' and ObservedProperty/id eq '%s'", sensor.getId(), ObservedProperties.MOTION.getId()))
                .list();

        // If found, then returns it
        if (!candidates.isEmpty()) {
            return candidates.iterator().next();
        }
        // Else, create it
        final Datastream newAssociatedDatastream = DatastreamBuilder.builder()
                .name(sensor.getName())
                .description(String.format("Human motions caught by Sensor '%s' ('%s')", sensor.getName(), sensor.getId()))
                .observedProperty(ObservedProperties.MOTION)
                .unitOfMeasurement(UnitOfMeasurements.UNKNOWN)
                .sensor(sensor)
                .thing(Things.UNKNOWN)
                .build();
        LOGGER.debug("Creating new Datastream {}...", newAssociatedDatastream);
        getSensorThingsService().create(newAssociatedDatastream);
        LOGGER.debug("Creating new Datastream {}... Done.", newAssociatedDatastream);
        return newAssociatedDatastream;
    }

    protected Sensor getOrCreateAssociatedSensor(final FlatMotionObservation flatMotionObservation) throws ServiceFailureException {
        // Search the Sensor associated to the given FlatMotionObservation
        final EntityList<Sensor> sensors = getSensorThingsService().sensors()
                .query()
                .filter(String.format("name eq '%s'", flatMotionObservation.getAppUserId().getSensor()))
                .list();

        // If found, then returns it
        if (!sensors.isEmpty()) {
            return sensors.iterator().next();
        }
        // Else, create it
        final Sensor newAssociatedSensor = SensorBuilder.builder()
                .name(flatMotionObservation.getAppUserId().getSensor())
                .description(flatMotionObservation.getAppUserId().getSensor())
                .encodingType(AbstractSensorBuilder.ValueCode.PDF) // TODO make it configurable
                .metadata(String.format("http://example.org/sensors/%s/jsonschema", flatMotionObservation.getAppUserId().getSensor())) // TODO make it configurable
                .build();
        LOGGER.debug("Creating new Sensor {}...", newAssociatedSensor);
        getSensorThingsService().create(newAssociatedSensor);
        LOGGER.debug("Creating new Sensor {}... Done.", newAssociatedSensor);
        return newAssociatedSensor;
    }

    protected DataArrayValue toDataArrayValue(final Map.Entry<Datastream, List<FlatMotionObservation>> entry) {
        return entry.getValue()
                .stream()
                .reduce(
                        newDataArrayValue(entry.getKey()),
                        (acc, flatMotionObservation) -> {
                            try {
                                acc.addObservation(newObservation(flatMotionObservation));
                            } catch (final ServiceFailureException e) {
                                throw new IllegalStateException(e);
                            }
                            return acc;
                        },
                        (dataArrayValue1, dataArrayValue2) -> {
                            dataArrayValue2.getObservations().forEach(dataArrayValue1::addObservation);
                            return dataArrayValue1;
                        }
                );
    }

    protected static DataArrayValue newDataArrayValue(final Datastream datastream) {
        return new DataArrayValue(
                datastream,
                new HashSet<>(Arrays.asList(
                        DataArrayValue.Property.Result,
                        DataArrayValue.Property.PhenomenonTime,
                        DataArrayValue.Property.FeatureOfInterest
                ))
        );
    }

    protected Observation newObservation(final FlatMotionObservation flatMotionObservation) throws ServiceFailureException {
        return MotionObservationBuilder.builder()
                .result(MotionEventBuilder.builder()
                        .subject(MotionSubjectBuilder.builder()
                                .id(String.format("%s-%d", flatMotionObservation.getAppUserId().getSequence(), flatMotionObservation.getAppUserId().getId()))
                                .build()
                        )
                        .location(PointFeatureBuilder.builder()
                                .geometry(new Point(new LngLatAlt(flatMotionObservation.getLat(), flatMotionObservation.getLon())))
                                .property(FeatureProperty.BUILDING, flatMotionObservation.getBuilding())
                                .property(FeatureProperty.FLOOR, flatMotionObservation.getFloor())
                                .build()
                        )
                        .build()
                )
                .featureOfInterest(getOrCreateAssociatedFeatureOfInterest(flatMotionObservation))
                .phenomenonTime(new TimeObject(flatMotionObservation.getDeviceDate().atZone(ZoneId.systemDefault()))) // TODO make it configurable
                .build();
    }

    protected FeatureOfInterest getOrCreateAssociatedFeatureOfInterest(final FlatMotionObservation flatMotionObservation) throws ServiceFailureException {
        // Search the FeatureOfInterest associated to the given FlatMotionObservation
        final EntityList<FeatureOfInterest> featureOfInterests = getSensorThingsService().featuresOfInterest()
                .query()
                .filter(String.format("feature/properties/%s eq %d", FeatureProperty.VENUE_ID.getName(), flatMotionObservation.getVenueId()))
                .list();

        // If found, then returns it
        if (!featureOfInterests.isEmpty()) {
            return featureOfInterests.iterator().next();
        }
        // Else, create it
        final FeatureOfInterest newAssociatedFeatureOfInterest = FeatureOfInterestBuilder.builder()
                .name(String.valueOf(flatMotionObservation.getVenueId()))
                .description("Dummy place for venueId " + flatMotionObservation.getVenueId())
                .feature(
                        PointFeatureBuilder.builder()
                                .geometry(new Point(new LngLatAlt(0.0, 0.0)))
                                .property(FeatureProperty.VENUE_ID, flatMotionObservation.getVenueId())
                                .build()
                )
                .build();
        LOGGER.debug("Creating new FeatureOfInterest {}...", newAssociatedFeatureOfInterest);
        getSensorThingsService().create(newAssociatedFeatureOfInterest);
        LOGGER.debug("Creating new FeatureOfInterest {}... Done.", newAssociatedFeatureOfInterest);
        return newAssociatedFeatureOfInterest;
    }

}
