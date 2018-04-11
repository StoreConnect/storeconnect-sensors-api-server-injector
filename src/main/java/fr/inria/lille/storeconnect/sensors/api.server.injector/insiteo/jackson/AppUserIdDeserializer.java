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
package fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.AppUserId;
import fr.inria.lille.storeconnect.sensors.api.server.injector.insiteo.model.builder.AppUserIdBuilder;
import fr.inria.lille.storeconnect.sensors.injector.jackson.InvalidPatternException;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link AppUserId} {@link JsonDeserializer}
 *
 * @author Aurelien Bourdon
 */
public class AppUserIdDeserializer extends JsonDeserializer<AppUserId> {

    private static final Pattern RAW_APPUSERID_PATTERN = Pattern.compile("^(?<sensor>[^_]+)_(?<sequence>[^_]+)_(?<id>\\d+)$");

    @Override
    public AppUserId deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext) throws IOException {
        final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        final String rawAppUserId = node.asText();

        final Matcher matcher = RAW_APPUSERID_PATTERN.matcher(rawAppUserId);
        if (!matcher.find()) {
            throw new InvalidPatternException(jsonParser, rawAppUserId, RAW_APPUSERID_PATTERN);
        }
        return AppUserIdBuilder.builder()
                .sensor(matcher.group("sensor"))
                .sequence(matcher.group("sequence"))
                .id(Integer.parseInt(matcher.group("id"))) // Pattern constraints to only be an integer
                .build();
    }

}
