/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: Resilience4JHandle.java
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

package org.konggradio.core.tool.model.convert.rule;



import org.konggradio.core.tool.constant.PluginConstants;

import java.util.Objects;

/**
 * this is Resilience4J plugin handle.
 */
public class Resilience4JHandle {

    /**
     * ratelimiter timeoutDurationRate.
     */
    private int timeoutDurationRate = PluginConstants.TIMEOUT_DURATION_RATE;

    /**
     * ratelimiter limitRefreshPeriod.
     */
    private int limitRefreshPeriod = PluginConstants.LIMIT_REFRESH_PERIOD;

    /**
     * ratelimiter limitForPeriod.
     */
    private int limitForPeriod = PluginConstants.LIMIT_FOR_PERIOD;

    /**
     * circuitBreaker circuitEnable.
     */
    private int circuitEnable = PluginConstants.CIRCUIT_DISABLE;

    /**
     * circuitBreaker timeoutDuration.
     */
    private long timeoutDuration = PluginConstants.TIMEOUT_DURATION;

    /**
     * circuitBreaker timeoutDuration.
     */
    private String fallbackUri;

    /**
     * circuitBreaker slidingWindowSize.
     */
    private int slidingWindowSize = PluginConstants.SLIDING_WINDOW_SIZE;

    /**
     * circuitBreaker slidingWindowType.
     */
    private int slidingWindowType = PluginConstants.SLIDING_WINDOW_TYPE;

    /**
     * circuitBreaker minimumNumberOfCalls.
     */
    private int minimumNumberOfCalls = PluginConstants.MINIMUM_NUMBER_OF_CALLS;

    /**
     * circuitBreaker waitIntervalFunctionInOpenState.
     */
    private int waitIntervalFunctionInOpenState = PluginConstants.WAIT_INTERVAL_FUNCTION_IN_OPEN_STATE;

    /**
     * circuitBreaker waitIntervalFunctionInOpenState.
     */
    private int permittedNumberOfCallsInHalfOpenState = PluginConstants.PERMITTED_NUMBER_OF_CALLS_IN_HALF_OPEN_STATE;

    /**
     * circuitBreaker failureRateThreshold.
     */
    private float failureRateThreshold = PluginConstants.FAILURE_RATE_THRESHOLD;

    /**
     * circuitBreaker automaticTransitionFromOpenToHalfOpenEnabled.
     */
    private Boolean automaticTransitionFromOpenToHalfOpenEnabled = PluginConstants.AUTOMATIC_TRANSITION_FROM_OPEN_TO_HALF_OPEN_ENABLED;

    /**
     * get timeoutDurationRate.
     *
     * @return timeoutDurationRate
     */
    public int getTimeoutDurationRate() {
        return timeoutDurationRate;
    }

    /**
     * set timeoutDurationRate.
     *
     * @param timeoutDurationRate timeoutDurationRate
     */
    public void setTimeoutDurationRate(final int timeoutDurationRate) {
        this.timeoutDurationRate = timeoutDurationRate;
    }

    /**
     * get limitRefreshPeriod.
     *
     * @return limitRefreshPeriod
     */
    public int getLimitRefreshPeriod() {
        return limitRefreshPeriod;
    }

    /**
     * set limitRefreshPeriod.
     *
     * @param limitRefreshPeriod limitRefreshPeriod
     */
    public void setLimitRefreshPeriod(final int limitRefreshPeriod) {
        this.limitRefreshPeriod = limitRefreshPeriod;
    }

    /**
     * get limitForPeriod.
     *
     * @return limitForPeriod
     */
    public int getLimitForPeriod() {
        return limitForPeriod;
    }

    /**
     * set limitForPeriod.
     *
     * @param limitForPeriod limitForPeriod
     */
    public void setLimitForPeriod(final int limitForPeriod) {
        this.limitForPeriod = limitForPeriod;
    }

    /**
     * get circuitEnable.
     *
     * @return circuitEnable
     */
    public int getCircuitEnable() {
        return circuitEnable;
    }

    /**
     * set circuitEnable.
     *
     * @param circuitEnable circuitEnable
     */
    public void setCircuitEnable(final int circuitEnable) {
        this.circuitEnable = circuitEnable;
    }

    /**
     * get timeoutDuration.
     *
     * @return timeoutDuration
     */
    public long getTimeoutDuration() {
        return timeoutDuration;
    }

    /**
     * set timeoutDuration.
     *
     * @param timeoutDuration timeoutDuration
     */
    public void setTimeoutDuration(final long timeoutDuration) {
        this.timeoutDuration = timeoutDuration;
    }

    /**
     * get fallbackUri.
     *
     * @return fallbackUri
     */
    public String getFallbackUri() {
        return fallbackUri;
    }

    /**
     * set fallbackUri.
     *
     * @param fallbackUri fallbackUri
     */
    public void setFallbackUri(final String fallbackUri) {
        this.fallbackUri = fallbackUri;
    }

    /**
     * get slidingWindowSize.
     *
     * @return slidingWindowSize
     */
    public int getSlidingWindowSize() {
        return slidingWindowSize;
    }

    /**
     * set slidingWindowSize.
     *
     * @param slidingWindowSize slidingWindowSize
     */
    public void setSlidingWindowSize(final int slidingWindowSize) {
        this.slidingWindowSize = slidingWindowSize;
    }

    /**
     * get slidingWindowType.
     *
     * @return slidingWindowType
     */
    public int getSlidingWindowType() {
        return slidingWindowType;
    }

    /**
     * set slidingWindowType.
     *
     * @param slidingWindowType slidingWindowType
     */
    public void setSlidingWindowType(final int slidingWindowType) {
        this.slidingWindowType = slidingWindowType;
    }

    /**
     * get minimumNumberOfCalls.
     *
     * @return minimumNumberOfCalls
     */
    public int getMinimumNumberOfCalls() {
        return minimumNumberOfCalls;
    }

    /**
     * set minimumNumberOfCalls.
     *
     * @param minimumNumberOfCalls minimumNumberOfCalls
     */
    public void setMinimumNumberOfCalls(final int minimumNumberOfCalls) {
        this.minimumNumberOfCalls = minimumNumberOfCalls;
    }

    /**
     * get waitIntervalFunctionInOpenState.
     *
     * @return waitIntervalFunctionInOpenState
     */
    public int getWaitIntervalFunctionInOpenState() {
        return waitIntervalFunctionInOpenState;
    }

    /**
     * set waitIntervalFunctionInOpenState.
     *
     * @param waitIntervalFunctionInOpenState waitIntervalFunctionInOpenState
     */
    public void setWaitIntervalFunctionInOpenState(final int waitIntervalFunctionInOpenState) {
        this.waitIntervalFunctionInOpenState = waitIntervalFunctionInOpenState;
    }

    /**
     * get permittedNumberOfCallsInHalfOpenState.
     *
     * @return permittedNumberOfCallsInHalfOpenState
     */
    public int getPermittedNumberOfCallsInHalfOpenState() {
        return permittedNumberOfCallsInHalfOpenState;
    }

    /**
     * set permittedNumberOfCallsInHalfOpenState.
     *
     * @param permittedNumberOfCallsInHalfOpenState permittedNumberOfCallsInHalfOpenState
     */
    public void setPermittedNumberOfCallsInHalfOpenState(final int permittedNumberOfCallsInHalfOpenState) {
        this.permittedNumberOfCallsInHalfOpenState = permittedNumberOfCallsInHalfOpenState;
    }

    /**
     * get failureRateThreshold.
     *
     * @return failureRateThreshold
     */
    public float getFailureRateThreshold() {
        return failureRateThreshold;
    }

    /**
     * set failureRateThreshold.
     *
     * @param failureRateThreshold failureRateThreshold
     */
    public void setFailureRateThreshold(final float failureRateThreshold) {
        this.failureRateThreshold = failureRateThreshold;
    }

    /**
     * get automaticTransitionFromOpenToHalfOpenEnabled.
     *
     * @return automaticTransitionFromOpenToHalfOpenEnabled
     */
    public Boolean getAutomaticTransitionFromOpenToHalfOpenEnabled() {
        return automaticTransitionFromOpenToHalfOpenEnabled;
    }

    /**
     * set automaticTransitionFromOpenToHalfOpenEnabled.
     *
     * @param automaticTransitionFromOpenToHalfOpenEnabled automaticTransitionFromOpenToHalfOpenEnabled
     */
    public void setAutomaticTransitionFromOpenToHalfOpenEnabled(final Boolean automaticTransitionFromOpenToHalfOpenEnabled) {
        this.automaticTransitionFromOpenToHalfOpenEnabled = automaticTransitionFromOpenToHalfOpenEnabled;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Resilience4JHandle that = (Resilience4JHandle) o;
        return timeoutDurationRate == that.timeoutDurationRate && limitRefreshPeriod == that.limitRefreshPeriod && limitForPeriod == that.limitForPeriod
                && circuitEnable == that.circuitEnable && timeoutDuration == that.timeoutDuration && slidingWindowSize == that.slidingWindowSize
                && slidingWindowType == that.slidingWindowType && minimumNumberOfCalls == that.minimumNumberOfCalls
                && waitIntervalFunctionInOpenState == that.waitIntervalFunctionInOpenState && permittedNumberOfCallsInHalfOpenState == that.permittedNumberOfCallsInHalfOpenState
                && Float.compare(that.failureRateThreshold, failureRateThreshold) == 0 && Objects.equals(fallbackUri, that.fallbackUri)
                && Objects.equals(automaticTransitionFromOpenToHalfOpenEnabled, that.automaticTransitionFromOpenToHalfOpenEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeoutDurationRate, limitRefreshPeriod, limitForPeriod, circuitEnable, timeoutDuration, fallbackUri,
                slidingWindowSize, slidingWindowType, minimumNumberOfCalls, waitIntervalFunctionInOpenState,
                permittedNumberOfCallsInHalfOpenState, failureRateThreshold, automaticTransitionFromOpenToHalfOpenEnabled);
    }

    @Override
    public String toString() {
        return "Resilience4JHandle{"
                + "timeoutDurationRate="
                + timeoutDurationRate
                + ", limitRefreshPeriod="
                + limitRefreshPeriod
                + ", limitForPeriod="
                + limitForPeriod
                + ", circuitEnable="
                + circuitEnable
                + ", timeoutDuration="
                + timeoutDuration
                + ", fallbackUri='"
                + fallbackUri
                + '\''
                + ", slidingWindowSize="
                + slidingWindowSize
                + ", slidingWindowType="
                + slidingWindowType
                + ", minimumNumberOfCalls="
                + minimumNumberOfCalls
                + ", waitIntervalFunctionInOpenState="
                + waitIntervalFunctionInOpenState
                + ", permittedNumberOfCallsInHalfOpenState="
                + permittedNumberOfCallsInHalfOpenState
                + ", failureRateThreshold="
                + failureRateThreshold
                + ", automaticTransitionFromOpenToHalfOpenEnabled="
                + automaticTransitionFromOpenToHalfOpenEnabled
                + '}';
    }

    /**
     * check filed default value.
     *
     * @param resilience4JHandle {@linkplain Resilience4JHandle}
     */
    public void checkData(final Resilience4JHandle resilience4JHandle) {
        resilience4JHandle.setTimeoutDurationRate(resilience4JHandle.getTimeoutDurationRate() < 0 ? PluginConstants.TIMEOUT_DURATION_RATE : resilience4JHandle.getTimeoutDurationRate());
        resilience4JHandle.setLimitRefreshPeriod(resilience4JHandle.getLimitRefreshPeriod() < 0 ? PluginConstants.LIMIT_REFRESH_PERIOD : resilience4JHandle.getLimitRefreshPeriod());
        resilience4JHandle.setLimitForPeriod(resilience4JHandle.getLimitForPeriod() < 0 ? PluginConstants.LIMIT_FOR_PERIOD : resilience4JHandle.getLimitForPeriod());
        resilience4JHandle.setCircuitEnable(resilience4JHandle.getCircuitEnable() != PluginConstants.CIRCUIT_ENABLE ? PluginConstants.CIRCUIT_DISABLE : PluginConstants.CIRCUIT_ENABLE);
        resilience4JHandle.setTimeoutDuration(resilience4JHandle.getTimeoutDuration() < 0 ? PluginConstants.TIMEOUT_DURATION : resilience4JHandle.getTimeoutDuration());
        resilience4JHandle.setFallbackUri(!"0".equals(resilience4JHandle.getFallbackUri()) ? resilience4JHandle.getFallbackUri() : "");
        resilience4JHandle.setSlidingWindowSize(resilience4JHandle.getSlidingWindowSize() < 0 ? PluginConstants.SLIDING_WINDOW_SIZE : resilience4JHandle.getSlidingWindowSize());
        resilience4JHandle.setSlidingWindowType(resilience4JHandle.getSlidingWindowType() < 0 ? PluginConstants.SLIDING_WINDOW_TYPE : resilience4JHandle.getSlidingWindowType());
        resilience4JHandle.setMinimumNumberOfCalls(resilience4JHandle.getMinimumNumberOfCalls() < 0 ? PluginConstants.MINIMUM_NUMBER_OF_CALLS : resilience4JHandle.getMinimumNumberOfCalls());
        resilience4JHandle.setWaitIntervalFunctionInOpenState(resilience4JHandle.getWaitIntervalFunctionInOpenState() < 0
                ? PluginConstants.WAIT_INTERVAL_FUNCTION_IN_OPEN_STATE : resilience4JHandle.getWaitIntervalFunctionInOpenState());
        resilience4JHandle.setPermittedNumberOfCallsInHalfOpenState(resilience4JHandle.getPermittedNumberOfCallsInHalfOpenState() < 0
                ? PluginConstants.PERMITTED_NUMBER_OF_CALLS_IN_HALF_OPEN_STATE : resilience4JHandle.getPermittedNumberOfCallsInHalfOpenState());
        resilience4JHandle.setFailureRateThreshold(
                resilience4JHandle.getFailureRateThreshold() < 0 || resilience4JHandle.getFailureRateThreshold() > 100
                        ? PluginConstants.FAILURE_RATE_THRESHOLD : resilience4JHandle.getFailureRateThreshold());
    }
}
