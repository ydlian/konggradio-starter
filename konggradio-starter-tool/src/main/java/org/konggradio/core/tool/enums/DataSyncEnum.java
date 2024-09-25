/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: DataSyncEnum.java
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

/**
 * The enum Data sync enum.
 */
public enum DataSyncEnum {

    /**
     * Http data sync enum.
     */
    HTTP("http"),

    /**
     * Zookeeper data sync enum.
     */
    ZOOKEEPER("zookeeper"),

    /**
     * Websocket data sync enum.
     */
    WEBSOCKET("websocket");

    private final String name;

    /**
     * all args constructor.
     *
     * @param name name
     */
    DataSyncEnum(final String name) {
        this.name = name;
    }

    /**
     * get name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Acquire by name data sync enum.
     *
     * @param name the name
     * @return the data sync enum
     */
    public static DataSyncEnum acquireByName(final String name) {
        return Arrays.stream(DataSyncEnum.values())
                .filter(e -> e.getName().equals(name)).findFirst()
                .orElse(DataSyncEnum.HTTP);
    }
}
