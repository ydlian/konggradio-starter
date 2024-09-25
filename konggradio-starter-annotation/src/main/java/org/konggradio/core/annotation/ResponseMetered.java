/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ResponseMetered.java
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

import java.lang.annotation.*;

/**
 * An annotation for marking a method of an annotated object as metered.
 * <p>
 * Given a method like this:
 * <pre><code>
 *     {@literal @}ResponseMetered(name = "fancyName", level = ResponseMeteredLevel.ALL)
 *     public String fancyName(String name) {
 *         return "Sir Captain " + name;
 *     }
 * </code></pre>
 * <p>
 * Meters for the defining class with the name {@code fancyName} will be created for response codes
 * based on the ResponseMeteredLevel selected. Each time the {@code #fancyName(String)} method is invoked,
 * the appropriate response meter will be marked.
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
public @interface ResponseMetered {
    /**
     * @return The name of the meter.
     */
    String name() default "";

    /**
     * @return If {@code true}, use the given name as an absolute name. If {@code false}, use the given name
     * relative to the annotated class. When annotating a class, this must be {@code false}.
     */
    boolean absolute() default false;

    /**
     * @return the ResponseMeteredLevel which decides which response code meters are marked.
     */
    ResponseMeteredLevel level() default ResponseMeteredLevel.COARSE;
}
