/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: CacheRuleHandle.java
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

import org.konggradio.core.tool.model.convert.rule.RuleHandle;

/**
 * The type Divide rule handle.
 */
public class CacheRuleHandle implements RuleHandle {

    /**
     * the cache timeout seconds.
     */
    private Long timeoutSeconds = 60L;

    /**
     * Get the timeout seconds.
     * @return the timeout seconds
     */
    public Long getTimeoutSeconds() {
        return timeoutSeconds;
    }

    /**
     * Set timeout seconds.
     * @param timeoutSeconds the timeout seconds
     */
    public void setTimeoutSeconds(final Long timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
}
