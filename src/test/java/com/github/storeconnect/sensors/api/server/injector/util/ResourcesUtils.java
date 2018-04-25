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
package com.github.storeconnect.sensors.api.server.injector.util;

import java.io.File;

/**
 * Utilities when using the <code>resources</code> folder
 *
 * @author Aurelien Bourdon
 */
public final class ResourcesUtils {

    /**
     * Get the {@link File} associated to the path within the <code>resources</code> folder relative to the given baseClass
     *
     * @param resourcePath the path to the desired file within the <code>resources</code> folder
     * @param baseClass    the base class from which interpret the resourcePath
     * @return the {@link File} associated to the path within the <code>resources</code> folder relative to the given baseClass
     */
    public static File fromResources(final String resourcePath, final Class<?> baseClass) {
        return new File(baseClass.getResource(resourcePath).getFile());
    }

}
