/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ContextMappingRuleHandle.java
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

import java.util.Objects;

/**
 * Context mapping thread handle.
 */
public class ContextMappingRuleHandle implements RuleHandle {

    private String contextPath;

    private String addPrefix;

    /**
     * get contextPath.
     *
     * @return contextPath
     */
    public String getContextPath() {
        return contextPath;
    }

    /**
     * set contextPath.
     *
     * @param contextPath contextPath
     */
    public void setContextPath(final String contextPath) {
        this.contextPath = contextPath;
    }

    /**
     * get addPrefix.
     *
     * @return addPrefix
     */
    public String getAddPrefix() {
        return addPrefix;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContextMappingRuleHandle that = (ContextMappingRuleHandle) o;
        return Objects.equals(contextPath, that.contextPath) && Objects.equals(addPrefix, that.addPrefix);
    }

    @Override
    public String toString() {
        return "ContextMappingRuleHandle{"
                + "contextPath='"
                + contextPath
                + '\''
                + ", addPrefix='"
                + addPrefix
                + '\''
                + '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(contextPath, addPrefix);
    }

    /**
     * set addPrefix.
     *
     * @param addPrefix addPrefix
     */
    public void setAddPrefix(final String addPrefix) {
        this.addPrefix = addPrefix;
    }
}
