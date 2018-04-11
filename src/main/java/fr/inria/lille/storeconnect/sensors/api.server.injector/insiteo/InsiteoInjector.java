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
package fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo;

import com.fasterxml.jackson.core.type.TypeReference;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.jackson.ObjectMapperFactory;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.ObservedProperty;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.TimeObject;
import de.fraunhofer.iosb.ilt.sta.model.builder.SensorBuilder;
import de.fraunhofer.iosb.ilt.sta.model.builder.api.AbstractSensorBuilder;
import de.fraunhofer.iosb.ilt.sta.model.ext.DataArrayDocument;
import de.fraunhofer.iosb.ilt.sta.model.ext.DataArrayValue;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import fr.inria.lille.storeconnect.sensors.api.client.model.ObservedProperties;
import fr.inria.lille.storeconnect.sensors.api.client.model.builder.DatastreamBuilder;
import fr.inria.lille.storeconnect.sensors.api.client.model.feature.builder.PointFeatureBuilder;
import fr.inria.lille.storeconnect.sensors.api.client.model.motion.FeatureProperty;
import fr.inria.lille.storeconnect.sensors.api.client.model.motion.builder.MotionEventBuilder;
import fr.inria.lille.storeconnect.sensors.api.client.model.motion.builder.MotionObservationBuilder;
import fr.inria.lille.storeconnect.sensors.api.client.model.motion.builder.MotionSubjectBuilder;
import fr.inria.lille.storeconnect.sensors.api.server.injector.AbstractInjector;
import fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.InsiteoObservation;
import fr.inria.lille.storeconnect.sensors.api.server.injector.model.Locations;
import fr.inria.lille.storeconnect.sensors.api.server.injector.model.Things;
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
 * An {@link fr.inria.lille.storeconnect.sensors.api.server.injector.Injector} of Insiteo sensor's observation
 *
 * @author Aurelien Bourdon
 */
public class InsiteoInjector extends AbstractInjector<InsiteoObservation> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InsiteoInjector.class);

    public InsiteoInjector(final File input, final SensorThingsService sensorThingsService) {
        super(input, sensorThingsService);
    }

    protected static DataArrayValue toDataArrayValue(final Map.Entry<Datastream, List<InsiteoObservation>> entry) {
        return entry.getValue()
                .stream()
                .peek(insiteoObservation -> LOGGER.info("Processing of {}", insiteoObservation))
                .reduce(
                        newDataArrayValue(entry.getKey()),
                        (acc, insiteoObservation) -> {
                            acc.addObservation(newObservation(insiteoObservation));
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
                        DataArrayValue.Property.PhenomenonTime
                ))
        );
    }

    protected static Observation newObservation(final InsiteoObservation insiteoObservation) {
        return MotionObservationBuilder.builder()
                .result(MotionEventBuilder.builder()
                        .subject(MotionSubjectBuilder.builder()
                                .id(String.format("%s-%d", insiteoObservation.getAppUserId().getSequence(), insiteoObservation.getAppUserId().getId()))
                                .build()
                        )
                        .location(PointFeatureBuilder.builder()
                                .geometry(new Point(new LngLatAlt(insiteoObservation.getLat(), insiteoObservation.getLon())))
                                .property(FeatureProperty.BUILDING, insiteoObservation.getBuilding())
                                .property(FeatureProperty.FLOOR, insiteoObservation.getFloor())
                                .build()
                        )
                        .build()
                )
                .phenomenonTime(new TimeObject(insiteoObservation.getDeviceDate().atZone(ZoneId.systemDefault()))) // TODO make it configurable
                .build();
    }

    @Override
    public void initEnvironment() throws ServiceFailureException {
        initMotionObservation();
        initUnknownLocation();
        initUnknownThing();
    }

    protected void initMotionObservation() throws ServiceFailureException {
        final EntityList<ObservedProperty> candidates = getSensorThingsService().observedProperties()
                .query()
                .filter(String.format("name eq '%s'", ObservedProperties.MOTION.getName()))
                .list();
        if (candidates.isEmpty()) {
            LOGGER.info("Creating missing MOTION ObservedProperty: {}...", ObservedProperties.MOTION);
            getSensorThingsService().create(ObservedProperties.MOTION);
            LOGGER.info("Creating missing MOTION ObservedProperty: {}... Done.", ObservedProperties.MOTION);
        } else {
            ObservedProperties.MOTION.setId(candidates.iterator().next().getId());
            LOGGER.info("Linked to existing MOTION ObservedProperty: {}", ObservedProperties.MOTION);
        }
    }

    protected void initUnknownLocation() throws ServiceFailureException {
        final EntityList<Location> candidates = getSensorThingsService().locations()
                .query()
                .filter(String.format("name eq '%s'", Locations.UNKNOWN.getName()))
                .list();
        if (candidates.isEmpty()) {
            LOGGER.info("Creating missing UNKNOWN Location: {}...", Locations.UNKNOWN);
            getSensorThingsService().create(Locations.UNKNOWN);
            LOGGER.info("Creating missing UNKNOWN Location: {}... Done.", Locations.UNKNOWN);
        } else {
            Locations.UNKNOWN.setId(candidates.iterator().next().getId());
            LOGGER.info("Linked to existing UNKNOWN Location: {}", Locations.UNKNOWN);
        }
    }

    protected void initUnknownThing() throws ServiceFailureException {
        final EntityList<Thing> candidates = getSensorThingsService().things()
                .query()
                .filter(String.format("name eq '%s'", Things.UNKNOWN.getName()))
                .list();
        if (candidates.isEmpty()) {
            LOGGER.info("Creating missing UNKNOWN Thing: {}...", Things.UNKNOWN);
            getSensorThingsService().create(Things.UNKNOWN);
            LOGGER.info("Creating missing UNKNOWN Thing: {}... Done.", Things.UNKNOWN);
        } else {
            Things.UNKNOWN.setId(candidates.iterator().next().getId());
            LOGGER.info("Linked to existing UNKNOWN Thing: {}", Things.UNKNOWN);
        }
    }

    @Override
    public List<InsiteoObservation> parse(final File input) throws IOException {
        return ObjectMapperFactory.get().readValue(input, new TypeReference<List<InsiteoObservation>>() {
        });
    }

    @Override
    public void inject(final List<InsiteoObservation> data) throws ServiceFailureException {
        LOGGER.info("Preparing Insiteo's observations for sending...");
        final DataArrayDocument insiteoObservations = toDatastreams(data)
                .entrySet()
                .stream()
                .map(InsiteoInjector::toDataArrayValue)
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
        LOGGER.info("Preparing Insiteo's observations for sending... Done.");

        LOGGER.info("Sending InsiteoObservations to server...");
        getSensorThingsService().create(insiteoObservations);
        LOGGER.info("Sending InsiteoObservations to server... Done.");
    }

    protected Map<Datastream, List<InsiteoObservation>> toDatastreams(final List<InsiteoObservation> data) {
        return data.stream()
                .collect(Collectors.toMap(
                        insiteoObservation -> {
                            try {
                                return getOrCreateAssociatedDatastream(getOrCreateAssociatedSensor(insiteoObservation));
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
                .filter(String.format("Sensor/id eq '%s'", sensor.getId()))
                .filter(String.format("ObservedProperty/id eq '%s'", ObservedProperties.MOTION.getId()))
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
                .sensor(sensor)
                .thing(Things.UNKNOWN)
                .build();
        LOGGER.info("Creating new Datastream {}...", newAssociatedDatastream);
        getSensorThingsService().create(newAssociatedDatastream);
        LOGGER.info("Creating new Datastream {}... Done.", newAssociatedDatastream);
        return newAssociatedDatastream;
    }

    protected Sensor getOrCreateAssociatedSensor(final InsiteoObservation insiteoObservation) throws ServiceFailureException {
        // Search the Sensor associated to the given InsiteoObservation
        final EntityList<Sensor> sensors = getSensorThingsService().sensors()
                .query()
                .filter(String.format("name eq '%s'", insiteoObservation.getAppUserId().getSensor()))
                .list();

        // If found, then returns it
        if (!sensors.isEmpty()) {
            return sensors.iterator().next();
        }
        // Else, create it
        final Sensor newAssociatedSensor = SensorBuilder.builder()
                .name(insiteoObservation.getAppUserId().getSensor())
                .description(insiteoObservation.getAppUserId().getSensor())
                .encodingType(AbstractSensorBuilder.ValueCode.PDF) // TODO make it configurable
                .metadata(String.format("http://example.org/sensors/%s/jsonschema", insiteoObservation.getAppUserId().getSensor())) // TODO make it configurable
                .build();
        LOGGER.info("Creating new Sensor {}...", newAssociatedSensor);
        getSensorThingsService().create(newAssociatedSensor);
        LOGGER.info("Creating new Sensor {}... Done.", newAssociatedSensor);
        return newAssociatedSensor;
    }

}
