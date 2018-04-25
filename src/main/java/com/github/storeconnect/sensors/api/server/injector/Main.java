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
package com.github.storeconnect.sensors.api.server.injector;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Main program entry point
 *
 * @author Aurelien Bourdon
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(final String[] argv) {
        final Args args = parseArgs(argv);
        try {
            InjectorManager.getInstance().handle(
                    args.getDataType(),
                    new File(URI.create(args.getFileInputPath().toString())),
                    new SensorThingsService(args.getEndpoint())
            );
            // Wait long enough to complete all submitted tasks
            InjectorManager.getInstance().awaitTermination(1, TimeUnit.DAYS);
        } catch (final URISyntaxException e) {
            LOGGER.error("Endpoint URI is not valid", e);
        } catch (final InterruptedException e) {
            LOGGER.error("Cannot wait until all injectors finished", e);
        }
    }

    private static Args parseArgs(final String[] argv) {
        final Args args = new Args();
        final JCommander jCommander = JCommander.newBuilder()
                .addObject(args)
                .build();
        try {
            jCommander.parse(argv);
        } catch (final ParameterException e) {
            JCommander.getConsole().println("Bad command line usage: " + e.getMessage());
            jCommander.usage();
            System.exit(1);
        }
        return args;
    }

    /**
     * The Main program entry point's arguments
     *
     * @author Aurelien Bourdon
     */
    private static class Args {

        @Parameter(names = {"-i", "--input"}, description = "URL to the file containing data to inject to the StoreConnect Sensor API's server", required = true)
        private URL fileInputPath;

        @Parameter(names = {"-e", "--endpoint"}, description = "Endpoint URL to the StoreConnect Sensors API's server", required = true)
        private URL endpoint;

        @Parameter(names = {"-t", "--type"}, description = "The type of data to inject", validateValueWith = DataTypeValueValidator.class, required = true)
        private String dataType;

        public URL getFileInputPath() {
            return fileInputPath;
        }

        public URL getEndpoint() {
            return endpoint;
        }

        public String getDataType() {
            return dataType;
        }

        public static class DataTypeValueValidator implements IValueValidator<String> {
            @Override
            public void validate(final String name, final String value) {
                if (!InjectorManager.getInstance().canHandle(value)) {
                    throw new ParameterException(String.format(
                            "Unable to handle data type %s. Available data types: %s.",
                            value,
                            InjectorManager.getInstance().getHandledInjectorNames()
                    ));
                }
            }
        }

    }

}
