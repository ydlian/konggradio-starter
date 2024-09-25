/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: CachedGauge.java
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * An annotation for marking a method as a gauge, which caches the result for a specified time.
 *
 * <p>
 * Given a method like this:
 * <pre><code>
 *     {@literal @}CachedGauge(name = "queueSize", timeout = 30, timeoutUnit = TimeUnit.SECONDS)
 *     public int getQueueSize() {
 *         return queue.getSize();
 *     }
 *
 * </code></pre>
 * <p>
 *
 * A gauge for the defining class with the name queueSize will be created which uses the annotated method's
 * return value as its value, and which caches the result for 30 seconds.
 *
 * @since 3.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.ANNOTATION_TYPE })
public @interface CachedGauge {

    /**
     * @return The name of the counter.
     */
    String name() default "";

    /**
     * @return If {@code true}, use the given name as an absolute name. If {@code false}, use the given name
     * relative to the annotated class.
     */
    boolean absolute() default false;

    /**
     * @return The amount of time to cache the result
     */
    long timeout();

    /**
     * @return The unit of timeout
     */
    TimeUnit timeoutUnit() default TimeUnit.MILLISECONDS;

}
