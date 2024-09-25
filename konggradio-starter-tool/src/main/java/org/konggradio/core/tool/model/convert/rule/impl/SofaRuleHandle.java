/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SofaRuleHandle.java
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

package org.konggradio.core.tool.model.convert.rule.impl;


import org.konggradio.core.tool.constant.PluginConstants;
import org.konggradio.core.tool.enums.LoadBalanceEnum;
import org.konggradio.core.tool.model.convert.rule.RuleHandle;


import java.util.Objects;

/**
 * The type Sofa rule handle.
 */

public class SofaRuleHandle implements RuleHandle {

    /**
     * retries.
     */
    private Integer retries = 0;

    /**
     * the loadBalance.
     * {@linkplain LoadBalanceEnum}
     */
    private String loadBalance = LoadBalanceEnum.RANDOM.getName();

    /**
     * timeout is required.
     */
    private long timeout = PluginConstants.TIME_OUT;

    /**
     * get retries.
     *
     * @return retries
     */
    public Integer getRetries() {
        return retries;
    }

    /**
     * set retries.
     *
     * @param retries retries
     */
    public void setRetries(final Integer retries) {
        this.retries = retries;
    }

    /**
     * get loadBalance.
     *
     * @return loadBalance
     */
    public String getLoadBalance() {
        return loadBalance;
    }

    /**
     * set loadBalance.
     *
     * @param loadBalance loadBalance
     */
    public void setLoadBalance(final String loadBalance) {
        this.loadBalance = loadBalance;
    }

    /**
     * get timeout.
     *
     * @return timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * set timeout.
     *
     * @param timeout timeout
     */
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SofaRuleHandle that = (SofaRuleHandle) o;
        return timeout == that.timeout && Objects.equals(retries, that.retries) && Objects.equals(loadBalance, that.loadBalance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(retries, loadBalance, timeout);
    }

    @Override
    public String toString() {
        return "SofaRuleHandle{"
                + "retries="
                + retries
                + ", loadBalance='"
                + loadBalance
                + '\''
                + ", timeout="
                + timeout
                + '}';
    }
}
