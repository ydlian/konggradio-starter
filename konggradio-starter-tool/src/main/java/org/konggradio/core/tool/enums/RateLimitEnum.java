/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RateLimitEnum.java
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

package org.konggradio.core.tool.enums;

/**
 * rate limit.
 */
public enum RateLimitEnum {

    SLIDING_WINDOW("sliding_window_request_rate_limiter", "sliding_window_request_rate_limiter.lua"),

    LEAKY_BUCKET("request_leaky_rate_limiter", "request_leaky_rate_limiter.lua"),

    CONCURRENT("concurrent_request_rate_limiter", "concurrent_request_rate_limiter.lua"),

    TOKEN_BUCKET("request_rate_limiter", "request_rate_limiter.lua");

    private final String keyName;

    private final String scriptName;

    RateLimitEnum(final String keyName, final String scriptName) {
        this.keyName = keyName;
        this.scriptName = scriptName;
    }

    /**
     * getKeyName.
     *
     * @return keyName
     */
    public String getKeyName() {
        return this.keyName;
    }

    /**
     * getScriptName.
     *
     * @return scriptName
     */
    public String getScriptName() {
        return this.scriptName;
    }
}
