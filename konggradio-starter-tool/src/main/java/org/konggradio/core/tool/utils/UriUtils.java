/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: UriUtils.java
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

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.Objects;

/**
 * uri util.
 */
public class UriUtils {

    private static final String PRE_FIX = "/";

    /**
     * create URI {@link URI}.
     *
     * @param uri uri string eg:/fallback
     * @return created {@link URI} from uri
     */
    public static URI createUri(final String uri) {
        if (StringUtils.isNotBlank(uri)) {
            return URI.create(uri);
        }
        return null;
    }

    /**
     * Repair data string.
     *
     * @param name the name
     * @return the string
     */
    public static String repairData(final String name) {
        return name.startsWith(PRE_FIX) ? name : PRE_FIX + name;
    }

    /**
     * Remove prefix string.
     *
     * @param name the name
     * @return the string
     */
    public static String removePrefix(final String name) {
        return name.startsWith(PRE_FIX) ? name.substring(1) : name;
    }

    /**
     * Get the path of uri with parameters.
     *
     * @param uri the uri.
     * @return absolute uri string with parameters.
     */
    public static String getPathWithParams(final URI uri) {
        if (Objects.isNull(uri)) {
            return StringUtils.EMPTY;
        }
        String params = StringUtils.isEmpty(uri.getQuery()) ? "" : "?" + uri.getQuery();
        return uri.getPath() + params;
    }
}
