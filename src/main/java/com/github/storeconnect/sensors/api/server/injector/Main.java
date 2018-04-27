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

import com.beust.jcommander.ParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Program's main entry point
 *
 * @author Aurelien Bourdon
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final int ERROR_EXIT_STATUS = 1;

    public static void main(final String[] args) {
        try {
            new Application(args).execute();
        } catch (final ParameterException e) {
            // Nothing to display as it has already been done
            System.exit(ERROR_EXIT_STATUS);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(ERROR_EXIT_STATUS);
        }
    }

}
