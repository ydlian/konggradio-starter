/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: MovingAverages.java
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
 * A triple of moving averages (one-, five-, and fifteen-minute
 * moving average) as needed by {@link Meter}.
 * <p>
 * Included implementations are:
 * <ul>
 * <li>{@link ExponentialMovingAverages} exponential decaying average similar to the {@code top} Unix command.
 * <li>{@link SlidingTimeWindowMovingAverages} simple (unweighted) moving average
 * </ul>
 */
public interface MovingAverages {

    /**
     * Tick the internal clock of the MovingAverages implementation if needed
     * (according to the internal ticking interval)
     */
    void tickIfNecessary();

    /**
     * Update all three moving averages with n events having occurred since the last update.
     *
     * @param n
     */
    void update(long n);

    /**
     * Returns the one-minute moving average rate
     *
     * @return the one-minute moving average rate
     */
    double getM1Rate();

    /**
     * Returns the five-minute moving average rate
     *
     * @return the five-minute moving average rate
     */
    double getM5Rate();

    /**
     * Returns the fifteen-minute moving average rate
     *
     * @return the fifteen-minute moving average rate
     */
    double getM15Rate();
}
