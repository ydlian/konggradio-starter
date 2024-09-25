/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RewriteHandle.java
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

import java.util.Objects;

/**
 * this is rewrite plugin handle.
 */
public class RewriteHandle {

    /**
     * java regular expression.
     */
    private String regex;

    /**
     * replace string.
     */
    private String replace;

    /**
     * get regex.
     *
     * @return regex
     */
    public String getRegex() {
        return regex;
    }

    /**
     * set regex.
     *
     * @param regex regex
     */
    public void setRegex(final String regex) {
        this.regex = regex;
    }

    /**
     * get replace.
     *
     * @return replace
     */
    public String getReplace() {
        return replace;
    }

    /**
     * set replace.
     *
     * @param replace replace
     */
    public void setReplace(final String replace) {
        this.replace = replace;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RewriteHandle that = (RewriteHandle) o;
        return Objects.equals(regex, that.regex) && Objects.equals(replace, that.replace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regex, replace);
    }

    @Override
    public String toString() {
        return "RewriteHandle{"
                + "regex='"
                + regex
                + '\''
                + ", replace='"
                + replace
                + '\''
                + '}';
    }
}
