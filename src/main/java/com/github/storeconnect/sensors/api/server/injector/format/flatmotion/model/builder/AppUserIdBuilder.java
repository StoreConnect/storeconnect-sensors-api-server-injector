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
package com.github.storeconnect.sensors.api.server.injector.format.flatmotion.model.builder;


import com.github.storeconnect.sensors.api.server.injector.format.flatmotion.model.AppUserId;
import de.fraunhofer.iosb.ilt.sta.model.builder.api.AbstractBuilder;

/**
 * {@link AppUserId}'s {@link de.fraunhofer.iosb.ilt.sta.model.builder.api.Builder}
 *
 * @author Aurelien Bourdon
 */
public final class AppUserIdBuilder extends AbstractBuilder<AppUserId> {

    private AppUserIdBuilder() {
    }

    public static AppUserIdBuilder builder() {
        return new AppUserIdBuilder();
    }

    @Override
    protected AppUserId newBuildingInstance() {
        return new AppUserId();
    }

    public AppUserIdBuilder sensor(final String sensor) {
        getBuildingInstance().setSensor(sensor);
        return this;
    }

    public AppUserIdBuilder sequence(final String sequence) {
        getBuildingInstance().setSequence(sequence);
        return this;
    }

    public AppUserIdBuilder id(final Integer id) {
        getBuildingInstance().setId(id);
        return this;
    }

}
