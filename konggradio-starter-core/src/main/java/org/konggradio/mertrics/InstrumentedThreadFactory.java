/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: InstrumentedThreadFactory.java
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

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link ThreadFactory} that monitors the number of threads created, running and terminated.
 * <p>
 * It will register the metrics using the given (or auto-generated) name as classifier, e.g:
 * "your-thread-delegate.created", "your-thread-delegate.running", etc.
 */
public class InstrumentedThreadFactory implements ThreadFactory {
    private static final AtomicLong NAME_COUNTER = new AtomicLong();

    private final ThreadFactory delegate;
    private final Meter created;
    private final Counter running;
    private final Meter terminated;

    /**
     * Wraps a {@link ThreadFactory}, uses a default auto-generated name.
     *
     * @param delegate {@link ThreadFactory} to wrap.
     * @param registry {@link MetricRegistry} that will contain the metrics.
     */
    public InstrumentedThreadFactory(ThreadFactory delegate, MetricRegistry registry) {
        this(delegate, registry, "instrumented-thread-delegate-" + NAME_COUNTER.incrementAndGet());
    }

    /**
     * Wraps a {@link ThreadFactory} with an explicit name.
     *
     * @param delegate {@link ThreadFactory} to wrap.
     * @param registry {@link MetricRegistry} that will contain the metrics.
     * @param name     name for this delegate.
     */
    public InstrumentedThreadFactory(ThreadFactory delegate, MetricRegistry registry, String name) {
        this.delegate = delegate;
        this.created = registry.meter(MetricRegistry.name(name, "created"));
        this.running = registry.counter(MetricRegistry.name(name, "running"));
        this.terminated = registry.meter(MetricRegistry.name(name, "terminated"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Thread newThread(Runnable runnable) {
        Runnable wrappedRunnable = new InstrumentedRunnable(runnable);
        Thread thread = delegate.newThread(wrappedRunnable);
        created.mark();
        return thread;
    }

    private class InstrumentedRunnable implements Runnable {
        private final Runnable task;

        InstrumentedRunnable(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            running.inc();
            try {
                task.run();
            } finally {
                running.dec();
                terminated.mark();
            }
        }
    }
}
