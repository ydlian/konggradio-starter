/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: Singleton.java
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

package org.konggradio.core.tool.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton.
 */
public enum Singleton {

    /**
     * Inst singleton.
     */
    INST;

    /**
     * The Singles.
     */
    private static final Map<String, Object> SINGLES = new ConcurrentHashMap<>();

    /**
     * Single.
     *
     * @param clazz the clazz
     * @param o     the o
     */
    public void single(final Class<?> clazz, final Object o) {
        SINGLES.put(clazz.getName(), o);
    }

    /**
     * Get t.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> clazz) {
        return (T) SINGLES.get(clazz.getName());
    }
}
