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
import com.github.storeconnect.sensors.api.server.injector.format.FormatInjectorManager;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Application definition and execution
 *
 * @author Aurelien Bourdon
 */
public class Application {

    private ApplicationIdentification applicationIdentification;
    private ApplicationArguments applicationArguments;

    /**
     * Create a new {@link Application} with the given raw program arguments
     *
     * @param rawArgs the raw program arguments (as it was typed by user)
     */
    public Application(final String[] rawArgs) {
        initConfiguration();
        initArguments(rawArgs);
    }

    private void initConfiguration() {
        try {
            applicationIdentification = new ApplicationIdentification();
        } catch (final RuntimeException e) {
            throw new IllegalStateException("Fatal error: Application cannot be initialized due to missing internal configuration file", e);
        }
    }

    private void initArguments(final String[] rawArgs) {
        applicationArguments = new ApplicationArguments();
        final JCommander jCommander = JCommander.newBuilder()
                .addObject(applicationArguments)
                .build();
        jCommander.setProgramName(String.format(
                "%s (v%s)",
                applicationIdentification.getApplicationName(),
                applicationIdentification.getApplicationVersion())
        );
        try {
            jCommander.parse(rawArgs);
        } catch (final ParameterException e) {
            JCommander.getConsole().println("Command line error: " + e.getMessage());
            jCommander.usage();
            throw e;
        }
    }

    /**
     * {@link Application}'s execution entry point
     */
    public void execute() {
        try {
            // Inject data based on the user arguments
            FormatInjectorManager.getInstance().handle(
                    applicationArguments.getDataFormat(),
                    new File(URI.create(applicationArguments.getFileInputPath().toString())),
                    new SensorThingsService(applicationArguments.getEndpoint())
            );
            // Wait long enough for injection to complete
            FormatInjectorManager.getInstance().awaitTermination(1, TimeUnit.DAYS);
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Endpoint URI is not valid", e);
        } catch (final InterruptedException e) {
            throw new IllegalStateException("Cannot wait until injector finished", e);
        }
    }

    /**
     * The {@link Application}'s internal configuration
     *
     * @author Aurelien Bourdon
     */
    public static final class ApplicationIdentification {

        private static final String APPLICATION_IDENTIFICATION_PATH = "/application-identification.properties";
        private final Properties identification;

        public ApplicationIdentification() {
            try {
                identification = new Properties();
                identification.load(getClass().getResourceAsStream(APPLICATION_IDENTIFICATION_PATH));
            } catch (final IOException e) {
                throw new IllegalStateException("Unable to load Application identification file", e);
            }
        }

        /**
         * The {@link Application}'s name
         *
         * @return the {@link Application}'s name
         */
        public String getApplicationName() {
            return identification.getProperty("application.name");
        }

        /**
         * The {@link Application}'s version
         *
         * @return the {@link Application}'s version
         */
        public String getApplicationVersion() {
            return identification.getProperty("application.version");
        }

    }

    /**
     * The {@link Application}'s arguments
     *
     * @author Aurelien Bourdon
     */
    public static final class ApplicationArguments {

        @Parameter(names = {"-i", "--input"}, description = "URL to the file containing data to inject to the StoreConnect Sensor API's server", required = true)
        private URL fileInputPath;

        @Parameter(names = {"-e", "--endpoint"}, description = "Endpoint URL to the StoreConnect Sensors API's server", required = true)
        private URL endpoint;

        @Parameter(names = {"-f", "--data-format"}, description = "The data format to inject", validateValueWith = DataFormatValueValidator.class, required = true)
        private String dataFormat;

        public URL getFileInputPath() {
            return fileInputPath;
        }

        public URL getEndpoint() {
            return endpoint;
        }

        public String getDataFormat() {
            return dataFormat;
        }

        /**
         * {@link IValueValidator} for the {@link #dataFormat} argument.
         * <p>
         * Check if the {@link #dataFormat} value can be handled by the {@link FormatInjectorManager}
         *
         * @author Aurelien Bourdon
         */
        public static class DataFormatValueValidator implements IValueValidator<String> {
            @Override
            public void validate(final String name, final String value) {
                if (!FormatInjectorManager.getInstance().canHandle(value)) {
                    throw new ParameterException(String.format(
                            "Unable to handle data format %s. Available data formats are: %s.",
                            value,
                            FormatInjectorManager.getInstance().getHandledInjectorNames()
                    ));
                }
            }
        }

    }

}
