/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: DerivativeGauge.java
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

package org.konggradio.mertrics;

/**
 * A gauge whose value is derived from the value of another gauge.
 *
 * @param <F> the base gauge's value type
 * @param <T> the derivative type
 */
public abstract class DerivativeGauge<F, T> implements Gauge<T> {
    private final Gauge<F> base;

    /**
     * Creates a new derivative with the given base gauge.
     *
     * @param base the gauge from which to derive this gauge's value
     */
    protected DerivativeGauge(Gauge<F> base) {
        this.base = base;
    }

    @Override
    public T getValue() {
        return transform(base.getValue());
    }

    /**
     * Transforms the value of the base gauge to the value of this gauge.
     *
     * @param value the value of the base gauge
     * @return this gauge's value
     */
    protected abstract T transform(F value);
}
