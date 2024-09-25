/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: HttpMethodEnum.java
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


import org.konggradio.core.tool.exceptions.KongGradioRuntimeException;

import java.util.Arrays;

/**
 * this is http method support.
 */
public enum HttpMethodEnum {

    /**
     * Get http method enum.
     */
    GET("get", true),

    /**
     * Post http method enum.
     */
    POST("post", true),

    /**
     * Put http method enum.
     */
    PUT("put", true),

    /**
     * Delete http method enum.
     */
    DELETE("delete", true);

    private final String name;

    private final Boolean support;

    /**
     * all args constructor.
     *
     * @param name    name
     * @param support support
     */
    HttpMethodEnum(final String name, final Boolean support) {
        this.name = name;
        this.support = support;
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
     * get support.
     *
     * @return support
     */
    public Boolean getSupport() {
        return support;
    }

    /**
     * convert by name.
     *
     * @param name name
     * @return {@link HttpMethodEnum }
     */
    public static HttpMethodEnum acquireByName(final String name) {
        return Arrays.stream(HttpMethodEnum.values())
                .filter(e -> e.support && e.name.equals(name)).findFirst()
                .orElseThrow(() -> new KongGradioRuntimeException(String.format(" this http method can not support %s", name)));
    }

}
