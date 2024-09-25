/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: DubboRuleHandle.java
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
 * The type Dubbo rule handle.
 */
public class DubboRuleHandle implements RuleHandle {

    /**
     * version.
     */
    private String version;

    /**
     * group.
     */
    private String group;

    /**
     * retries.
     */
    private Integer retries = 0;

    /**
     * the loadbalance.
     * {@linkplain LoadBalanceEnum}
     */
    private String loadbalance = LoadBalanceEnum.RANDOM.getName();

    /**
     * timeout is required.
     */
    private long timeout = PluginConstants.TIME_OUT;

    /**
     * get version.
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * set version.
     *
     * @param version version
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * get group.
     *
     * @return group
     */
    public String getGroup() {
        return group;
    }

    /**
     * set group.
     *
     * @param group group
     */
    public void setGroup(final String group) {
        this.group = group;
    }

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
     * Gets the value of loadbalance.
     *
     * @return the value of loadbalance
     */
    public String getLoadbalance() {
        return loadbalance;
    }

    /**
     * Sets the loadbalance.
     *
     * @param loadbalance loadbalance
     */
    public void setLoadbalance(final String loadbalance) {
        this.loadbalance = loadbalance;
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
        if (!(o instanceof DubboRuleHandle)) {
            return false;
        }
        DubboRuleHandle that = (DubboRuleHandle) o;
        return timeout == that.timeout
                && Objects.equals(version, that.version)
                && Objects.equals(group, that.group)
                && Objects.equals(retries, that.retries)
                && Objects.equals(loadbalance, that.loadbalance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, group, retries, loadbalance, timeout);
    }

    @Override
    public String toString() {
        return "DubboRuleHandle{"
                + "version='" + version + '\''
                + ", group='" + group + '\''
                + ", retries=" + retries
                + ", loadbalance='" + loadbalance + '\''
                + ", timeout=" + timeout
                + '}';
    }
}
