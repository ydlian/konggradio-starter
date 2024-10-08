/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: InstrumentedExecutorService.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An {@link ExecutorService} that monitors the number of tasks submitted, running,
 * completed and also keeps a {@link Timer} for the task duration.
 * <p>
 * It will register the metrics using the given (or auto-generated) name as classifier, e.g:
 * "your-executor-service.submitted", "your-executor-service.running", etc.
 */
public class InstrumentedExecutorService implements ExecutorService {
    private static final AtomicLong NAME_COUNTER = new AtomicLong();

    private final ExecutorService delegate;
    private final Meter submitted;
    private final Counter running;
    private final Meter completed;
    private final Timer idle;
    private final Timer duration;

    /**
     * Wraps an {@link ExecutorService} uses an auto-generated default name.
     *
     * @param delegate {@link ExecutorService} to wrap.
     * @param registry {@link MetricRegistry} that will contain the metrics.
     */
    public InstrumentedExecutorService(ExecutorService delegate, MetricRegistry registry) {
        this(delegate, registry, "instrumented-delegate-" + NAME_COUNTER.incrementAndGet());
    }

    /**
     * Wraps an {@link ExecutorService} with an explicit name.
     *
     * @param delegate {@link ExecutorService} to wrap.
     * @param registry {@link MetricRegistry} that will contain the metrics.
     * @param name     name for this executor service.
     */
    public InstrumentedExecutorService(ExecutorService delegate, MetricRegistry registry, String name) {
        this.delegate = delegate;
        this.submitted = registry.meter(MetricRegistry.name(name, "submitted"));
        this.running = registry.counter(MetricRegistry.name(name, "running"));
        this.completed = registry.meter(MetricRegistry.name(name, "completed"));
        this.idle = registry.timer(MetricRegistry.name(name, "idle"));
        this.duration = registry.timer(MetricRegistry.name(name, "duration"));

        if (delegate instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) delegate;
            registry.registerGauge(MetricRegistry.name(name, "pool.size"),
                    executor::getPoolSize);
            registry.registerGauge(MetricRegistry.name(name, "pool.core"),
                    executor::getCorePoolSize);
            registry.registerGauge(MetricRegistry.name(name, "pool.max"),
                    executor::getMaximumPoolSize);
            final BlockingQueue<Runnable> queue = executor.getQueue();
            registry.registerGauge(MetricRegistry.name(name, "tasks.active"),
                    executor::getActiveCount);
            registry.registerGauge(MetricRegistry.name(name, "tasks.completed"),
                    executor::getCompletedTaskCount);
            registry.registerGauge(MetricRegistry.name(name, "tasks.queued"),
                    queue::size);
            registry.registerGauge(MetricRegistry.name(name, "tasks.capacity"),
                    queue::remainingCapacity);
        } else if (delegate instanceof ForkJoinPool) {
            ForkJoinPool forkJoinPool = (ForkJoinPool) delegate;
            registry.registerGauge(MetricRegistry.name(name, "tasks.stolen"),
                    forkJoinPool::getStealCount);
            registry.registerGauge(MetricRegistry.name(name, "tasks.queued"),
                    forkJoinPool::getQueuedTaskCount);
            registry.registerGauge(MetricRegistry.name(name, "threads.active"),
                    forkJoinPool::getActiveThreadCount);
            registry.registerGauge(MetricRegistry.name(name, "threads.running"),
                    forkJoinPool::getRunningThreadCount);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable runnable) {
        submitted.mark();
        delegate.execute(new InstrumentedRunnable(runnable));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Future<?> submit(Runnable runnable) {
        submitted.mark();
        return delegate.submit(new InstrumentedRunnable(runnable));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Runnable runnable, T result) {
        submitted.mark();
        return delegate.submit(new InstrumentedRunnable(runnable), result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        submitted.mark();
        return delegate.submit(new InstrumentedCallable<>(task));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        submitted.mark(tasks.size());
        Collection<? extends Callable<T>> instrumented = instrument(tasks);
        return delegate.invokeAll(instrumented);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        submitted.mark(tasks.size());
        Collection<? extends Callable<T>> instrumented = instrument(tasks);
        return delegate.invokeAll(instrumented, timeout, unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
        submitted.mark(tasks.size());
        Collection<? extends Callable<T>> instrumented = instrument(tasks);
        return delegate.invokeAny(instrumented);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        submitted.mark(tasks.size());
        Collection<? extends Callable<T>> instrumented = instrument(tasks);
        return delegate.invokeAny(instrumented, timeout, unit);
    }

    private <T> Collection<? extends Callable<T>> instrument(Collection<? extends Callable<T>> tasks) {
        final List<InstrumentedCallable<T>> instrumented = new ArrayList<>(tasks.size());
        for (Callable<T> task : tasks) {
            instrumented.add(new InstrumentedCallable<>(task));
        }
        return instrumented;
    }

    @Override
    public void shutdown() {
        delegate.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return delegate.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return delegate.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return delegate.isTerminated();
    }

    @Override
    public boolean awaitTermination(long l, TimeUnit timeUnit) throws InterruptedException {
        return delegate.awaitTermination(l, timeUnit);
    }

    private class InstrumentedRunnable implements Runnable {
        private final Runnable task;
        private final Timer.Context idleContext;

        InstrumentedRunnable(Runnable task) {
            this.task = task;
            this.idleContext = idle.time();
        }

        @Override
        public void run() {
            idleContext.stop();
            running.inc();
            try (Timer.Context durationContext = duration.time()) {
                task.run();
            } finally {
                running.dec();
                completed.mark();
            }
        }
    }

    private class InstrumentedCallable<T> implements Callable<T> {
        private final Callable<T> callable;
        private final Timer.Context idleContext;

        InstrumentedCallable(Callable<T> callable) {
            this.callable = callable;
            this.idleContext = idle.time();
        }

        @Override
        public T call() throws Exception {
            idleContext.stop();
            running.inc();
            try (Timer.Context context = duration.time()) {
                return callable.call();
            } finally {
                running.dec();
                completed.mark();
            }
        }
    }
}
