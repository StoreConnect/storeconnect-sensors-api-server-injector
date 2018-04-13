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
package fr.inria.lille.storeconnect.sensors.api.server.injector.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.util.regex.Pattern;

/**
 * Raised when an expected {@link Pattern} is unsatisfied for a given JSON's attribute
 *
 * @author Aurelien Bourdon
 */
public class InvalidPatternException extends InvalidFormatException {

    public InvalidPatternException(final JsonParser p, final Object value, final Pattern pattern) {
        super(p, "Invalid format. Expected pattern: " + pattern.pattern(), value, String.class);
    }

}
