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
package fr.inria.lille.storeconnect.sensors.api.server.injector;

import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import fr.inria.lille.storeconnect.sensors.api.server.injector.format.flatmotion.FlatMotionObservationInjector;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Manage all existing {@link Injector}s
 *
 * @author Aurelien Bourdon
 */
public final class InjectorManager {

    private static final InjectorManager INSTANCE = new InjectorManager();
    private final Map<String, Class<? extends AbstractInjector<?>>> injectors = new HashMap<String, Class<? extends AbstractInjector<?>>>() {
        {
            put("flat-motion", FlatMotionObservationInjector.class);
        }
    };
    private final ExecutorService injectorsExecutionPool = Executors.newSingleThreadExecutor();

    private InjectorManager() {
    }

    public static InjectorManager getInstance() {
        return INSTANCE;
    }

    public void handle(final String injectorName, final File fileInput, final SensorThingsService sensorThingsService) {
        if (!canHandle(injectorName)) {
            throw new IllegalArgumentException("Unable to handle injector name " + injectorName);
        }
        final Class<? extends AbstractInjector<?>> injectorClass = injectors.get(injectorName);
        try {
            final Constructor<? extends AbstractInjector<?>> constructor = injectorClass.getDeclaredConstructor(File.class, SensorThingsService.class);
            final Injector<?> injector = constructor.newInstance(fileInput, sensorThingsService);
            startInjector(injector);
        } catch (final ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean canHandle(final String injectorName) {
        return injectors.containsKey(injectorName);
    }

    private void startInjector(final Injector<?> injector) {
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
