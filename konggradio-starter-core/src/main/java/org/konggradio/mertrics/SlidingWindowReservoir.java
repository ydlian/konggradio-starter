/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SlidingWindowReservoir.java
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

import static java.lang.Math.min;

/**
 * A {@link Reservoir} implementation backed by a sliding window that stores the last {@code N}
 * measurements.
 */
public class SlidingWindowReservoir implements Reservoir {
    private final long[] measurements;
    private long count;

    /**
     * Creates a new {@link SlidingWindowReservoir} which stores the last {@code size} measurements.
     *
     * @param size the number of measurements to store
     */
    public SlidingWindowReservoir(int size) {
        this.measurements = new long[size];
        this.count = 0;
    }

    @Override
    public synchronized int size() {
        return (int) min(count, measurements.length);
    }

    @Override
    public synchronized void update(long value) {
        measurements[(int) (count++ % measurements.length)] = value;
    }

    @Override
    public Snapshot getSnapshot() {
        final long[] values = new long[size()];
        for (int i = 0; i < values.length; i++) {
            synchronized (this) {
                values[i] = measurements[i];
            }
        }
        return new UniformSnapshot(values);
    }
}
