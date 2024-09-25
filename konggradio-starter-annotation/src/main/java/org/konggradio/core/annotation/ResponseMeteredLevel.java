/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ResponseMeteredLevel.java
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

package org.konggradio.core.annotation;

/**
 * {@link ResponseMeteredLevel} is a parameter for the {@link ResponseMetered} annotation.
 * The constants of this enumerated type decide what meters are included when a class
 * or method is annotated with the {@link ResponseMetered} annotation.
 */
public enum ResponseMeteredLevel {
    /**
     * Include meters for 1xx/2xx/3xx/4xx/5xx responses
     */
    COARSE,

    /**
     * Include meters for every response code (200, 201, 303, 304, 401, 404, 501, etc.)
     */
    DETAILED,

    /**
     * Include meters for every response code in addition to top level 1xx/2xx/3xx/4xx/5xx responses
     */
    ALL;
}
