/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: NoopMetricRegistry.java
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

import java.io.OutputStream;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * A registry of metric instances which never creates or registers any metrics and returns no-op implementations of any metric type.
 *
 * @since 4.1.17
 */
public final class NoopMetricRegistry extends MetricRegistry {
    private static final EmptyConcurrentMap<String, Metric> EMPTY_CONCURRENT_MAP = new EmptyConcurrentMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    protected ConcurrentMap<String, Metric> buildMap() {
        return EMPTY_CONCURRENT_MAP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
        if (metric == null) {
            throw new NullPointerException("metric == null");
        }
        return metric;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAll(MetricSet metrics) throws IllegalArgumentException {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Counter counter(String name) {
        return NoopCounter.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Counter counter(String name, MetricSupplier<Counter> supplier) {
        return NoopCounter.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Histogram histogram(String name) {
        return NoopHistogram.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Histogram histogram(String name, MetricSupplier<Histogram> supplier) {
        return NoopHistogram.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meter meter(String name) {
        return NoopMeter.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meter meter(String name, MetricSupplier<Meter> supplier) {
        return NoopMeter.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.konggradio.mertrics.Timer timer(String name) {
        return NoopTimer.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public org.konggradio.mertrics.Timer timer(String name, MetricSupplier<org.konggradio.mertrics.Timer> supplier) {
        return NoopTimer.INSTANCE;
    }

    /**
     * {@inheritDoc}
     *
     * @since 4.2
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T extends Gauge> T gauge(String name) {
        return (T) NoopGauge.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T extends Gauge> T gauge(String name, MetricSupplier<T> supplier) {
        return (T) NoopGauge.INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(String name) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeMatching(MetricFilter filter) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(MetricRegistryListener listener) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(MetricRegistryListener listener) {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedSet<String> getNames() {
        return Collections.emptySortedSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public SortedMap<String, Gauge> getGauges() {
        return Collections.emptySortedMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public SortedMap<String, Gauge> getGauges(MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<String, Counter> getCounters() {
        return Collections.emptySortedMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<String, Counter> getCounters(MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<String, Histogram> getHistograms() {
        return Collections.emptySortedMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<String, Histogram> getHistograms(MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<String, Meter> getMeters() {
        return Collections.emptySortedMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<String, Meter> getMeters(MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<String, org.konggradio.mertrics.Timer> getTimers() {
        return Collections.emptySortedMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SortedMap<String, org.konggradio.mertrics.Timer> getTimers(MetricFilter filter) {
        return Collections.emptySortedMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAll(String prefix, MetricSet metrics) throws IllegalArgumentException {
        // NOP
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Metric> getMetrics() {
        return Collections.emptyMap();
    }

    static final class NoopGauge<T> implements Gauge<T> {
        private static final NoopGauge<?> INSTANCE = new NoopGauge<>();

        /**
         * {@inheritDoc}
         */
        @Override
        public T getValue() {
            return null;
        }
    }

    private static final class EmptySnapshot extends Snapshot {
        private static final EmptySnapshot INSTANCE = new EmptySnapshot();
        private static final long[] EMPTY_LONG_ARRAY = new long[0];

        /**
         * {@inheritDoc}
         */
        @Override
        public double getValue(double quantile) {
            return 0D;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long[] getValues() {
            return EMPTY_LONG_ARRAY;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getMax() {
            return 0L;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getMean() {
            return 0D;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getMin() {
            return 0L;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getStdDev() {
            return 0D;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dump(OutputStream output) {
            // NOP
        }
    }

    static final class NoopTimer extends org.konggradio.mertrics.Timer {
        private static final NoopTimer INSTANCE = new NoopTimer();
        private static final org.konggradio.mertrics.Timer.Context CONTEXT = new Context();

        private static class Context extends org.konggradio.mertrics.Timer.Context {
            private static final Clock CLOCK = new Clock() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public long getTick() {
                    return 0L;
                }

                /**
                 * {@inheritDoc}
                 */
                @Override
                public long getTime() {
                    return 0L;
                }
            };

            private Context() {
                super(INSTANCE, CLOCK);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public long stop() {
                return 0L;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void close() {
                // NOP
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update(long duration, TimeUnit unit) {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update(Duration duration) {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> T time(Callable<T> event) throws Exception {
            return event.call();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> T timeSupplier(Supplier<T> event) {
            return event.get();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void time(Runnable event) {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Timer.Context time() {
            return CONTEXT;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getCount() {
            return 0L;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getFifteenMinuteRate() {
            return 0D;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getFiveMinuteRate() {
            return 0D;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getMeanRate() {
            return 0D;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getOneMinuteRate() {
            return 0D;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Snapshot getSnapshot() {
            return EmptySnapshot.INSTANCE;
        }
    }

    static final class NoopHistogram extends Histogram {
        private static final NoopHistogram INSTANCE = new NoopHistogram();
        private static final Reservoir EMPTY_RESERVOIR = new Reservoir() {
            /**
             * {@inheritDoc}
             */
            @Override
            public int size() {
                return 0;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void update(long value) {
                // NOP
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public Snapshot getSnapshot() {
                return EmptySnapshot.INSTANCE;
            }
        };

        private NoopHistogram() {
            super(EMPTY_RESERVOIR);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update(int value) {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update(long value) {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getCount() {
            return 0L;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Snapshot getSnapshot() {
            return EmptySnapshot.INSTANCE;
        }
    }

    static final class NoopCounter extends Counter {
        private static final NoopCounter INSTANCE = new NoopCounter();

        /**
         * {@inheritDoc}
         */
        @Override
        public void inc() {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void inc(long n) {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dec() {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dec(long n) {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getCount() {
            return 0L;
        }
    }

    static final class NoopMeter extends Meter {
        private static final NoopMeter INSTANCE = new NoopMeter();

        /**
         * {@inheritDoc}
         */
        @Override
        public void mark() {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void mark(long n) {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getCount() {
            return 0L;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getFifteenMinuteRate() {
            return 0D;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getFiveMinuteRate() {
            return 0D;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getMeanRate() {
            return 0D;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public double getOneMinuteRate() {
            return 0D;
        }
    }

    private static final class EmptyConcurrentMap<K, V> implements ConcurrentMap<K, V> {
        /**
         * {@inheritDoc}
         */
        @Override
        public V putIfAbsent(K key, V value) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean remove(Object key, Object value) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V replace(K key, V value) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            return 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEmpty() {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V get(Object key) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V put(K key, V value) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V remove(Object key) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() {
            // NOP
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<K> keySet() {
            return Collections.emptySet();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Collection<V> values() {
            return Collections.emptySet();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<Entry<K, V>> entrySet() {
            return Collections.emptySet();
        }
    }
}
