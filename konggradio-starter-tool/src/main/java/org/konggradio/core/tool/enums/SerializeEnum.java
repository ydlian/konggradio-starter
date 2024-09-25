/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SerializeEnum.java
 *  <p>
 *  Licensed under the Apache License Version 2.0;
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  <p>
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  <p>
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package org.konggradio.core.tool.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * SerializeEnum.
 */
public enum SerializeEnum {

    /**
     * Jdk serialize protocol enum.
     */
    JDK("jdk"),

    /**
     * Kryo serialize protocol enum.
     */
    KRYO("kryo"),

    /**
     * Hessian serialize protocol enum.
     */
    HESSIAN("hessian"),

    /**
     * Fast json serialize enum.
     */
    FAST_JSON("fastJson"),

    /**
     * Protostuff serialize protocol enum.
     */
    PROTOSTUFF("protostuff");

    private final String serialize;

    SerializeEnum(final String serialize) {
        this.serialize = serialize;
    }

    /**
     * get serialize.
     *
     * @return serialize serialize
     */
    public String getSerialize() {
        return serialize;
    }

    /**
     * Acquire serialize protocol serialize protocol enum.
     *
     * @param serialize the serialize protocol
     * @return the serialize protocol enum
     */
    public static SerializeEnum acquire(final String serialize) {
        return Arrays.stream(SerializeEnum.values())
                .filter(v -> Objects.equals(v.getSerialize(), serialize))
                .findFirst().orElse(SerializeEnum.JDK);

    }
}
