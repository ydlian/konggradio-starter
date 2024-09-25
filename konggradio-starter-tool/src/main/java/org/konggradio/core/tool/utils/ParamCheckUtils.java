/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ParamCheckUtils.java
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

/**
 * The type Param check utils.
 */
public class ParamCheckUtils {

    /**
     * Dubbo body is empty boolean.
     *
     * @param body the body
     * @return the boolean
     */
    public static boolean dubboBodyIsEmpty(final String body) {
        return null == body || "".equals(body) || "{}".equals(body) || "null".equals(body);
    }
}