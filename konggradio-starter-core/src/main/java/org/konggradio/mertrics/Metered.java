/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: Metered.java
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
 * An object which maintains mean and moving average rates.
 */
public interface Metered extends Metric, Counting {
    /**
     * Returns the number of events which have been marked.
     *
     * @return the number of events which have been marked
     */
    @Override
    long getCount();

    /**
     * Returns the fifteen-minute moving average rate at which events have
     * occurred since the meter was created.
     *
     * @return the fifteen-minute moving average rate at which events have
     * occurred since the meter was created
     */
    double getFifteenMinuteRate();

    /**
     * Returns the five-minute moving average rate at which events have
     * occurred since the meter was created.
     *
     * @return the five-minute moving average rate at which events have
     * occurred since the meter was created
     */
    double getFiveMinuteRate();

    /**
     * Returns the mean rate at which events have occurred since the meter was created.
     *
     * @return the mean rate at which events have occurred since the meter was created
     */
    double getMeanRate();

    /**
     * Returns the one-minute moving average rate at which events have
     * occurred since the meter was created.
     *
     * @return the one-minute moving average rate at which events have
     * occurred since the meter was created
     */
    double getOneMinuteRate();
}
