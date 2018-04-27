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

import com.github.storeconnect.sensors.api.server.injector.format.flatmotion.FlatMotionObservationInjector;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manage all existing {@link FormatInjector}s
 *
 * @author Aurelien Bourdon
 */
public final class FormatInjectorManager {

    private static final FormatInjectorManager INSTANCE = new FormatInjectorManager();
    private final Map<String, Class<? extends AbstractFormatInjector<?>>> injectors = new HashMap<String, Class<? extends AbstractFormatInjector<?>>>() {
        {
            put("flat-motion", FlatMotionObservationInjector.class);
        }
    };
    // Only one injection at a time
    private final ExecutorService injectorsExecutionPool = Executors.newSingleThreadExecutor();

    private FormatInjectorManager() {
    }

    public static FormatInjectorManager getInstance() {
        return INSTANCE;
    }

    public void handle(final String dataFormat, final File fileInput, final SensorThingsService sensorThingsService) {
        if (!canHandle(dataFormat)) {
            throw new IllegalArgumentException("Unable to handle format " + dataFormat);
        }
        final Class<? extends AbstractFormatInjector<?>> injectorClass = injectors.get(dataFormat);
        try {
            final Constructor<? extends AbstractFormatInjector<?>> constructor = injectorClass.getDeclaredConstructor(File.class, SensorThingsService.class);
            final FormatInjector<?> injector = constructor.newInstance(fileInput, sensorThingsService);
            startInjector(injector);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean canHandle(final String injectorName) {
        return injectors.containsKey(injectorName);
    }

    private void startInjector(final FormatInjector<?> injector) {
        injectorsExecutionPool.execute(injector);
    }

    public Set<String> getHandledInjectorNames() {
        return injectors.keySet();
    }

    public void awaitTermination(final long timeout, final TimeUnit timeUnit) throws InterruptedException {
        injectorsExecutionPool.shutdown();
        injectorsExecutionPool.awaitTermination(timeout, timeUnit);
    }

}
