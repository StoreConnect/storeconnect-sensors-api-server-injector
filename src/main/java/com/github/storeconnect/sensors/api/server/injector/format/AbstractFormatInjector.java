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
package com.github.storeconnect.sensors.api.server.injector.format;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Base class for any {@link FormatInjector}
 *
 * @param <T> the type of data unit this {@link FormatInjector} can handle
 */
public abstract class AbstractFormatInjector<T extends Format> implements FormatInjector<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractFormatInjector.class);

    private final SensorThingsService sensorThingsService;
    private final File input;

    public AbstractFormatInjector(final File input, final SensorThingsService sensorThingsService) {
        this.sensorThingsService = sensorThingsService;
        this.input = input;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Starting {}", this);
            LOGGER.info("Preparing environment...");
            initEnvironment();
            LOGGER.info("Preparing environment... Done.");
            LOGGER.info("Parsing input file...");
            final List<T> data = parse(input);
            LOGGER.info("Parsing input file... Done.");
            LOGGER.info("Injecting data...");
            inject(data);
            LOGGER.info("Injecting data... Done.");
            LOGGER.info("Injection process done for {}", this);
        } catch (final ServiceFailureException | IOException e) {
            LOGGER.error("Unexpected failure for {}", this, e);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Injector '%s' set with '%s' as StoreConnect Sensor API endpoint and '%s' as File input",
                getClass().getSimpleName(),
                getSensorThingsService().getEndpoint(), getInput().getAbsolutePath()
        );
    }

    public SensorThingsService getSensorThingsService() {
        return sensorThingsService;
    }

    public File getInput() {
        return input;
    }
}
