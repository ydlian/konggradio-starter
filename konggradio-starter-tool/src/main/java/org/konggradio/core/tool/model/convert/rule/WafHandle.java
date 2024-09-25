/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: WafHandle.java
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
 * this is waf plugin handle.
 */
public class WafHandle {

    /**
     * permission.
     */
    private String permission;

    /**
     * statusCode.
     */
    private String statusCode;

    /**
     * get permission.
     *
     * @return permission
     */
    public String getPermission() {
        return permission;
    }

    /**
     * set permission.
     *
     * @param permission permission
     */
    public void setPermission(final String permission) {
        this.permission = permission;
    }

    /**
     * get statusCode.
     *
     * @return statusCode
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * set statusCode.
     *
     * @param statusCode statusCode
     */
    public void setStatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WafHandle wafHandle = (WafHandle) o;
        return Objects.equals(permission, wafHandle.permission) && Objects.equals(statusCode, wafHandle.statusCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permission, statusCode);
    }

    @Override
    public String toString() {
        return "WafHandle{"
                + "permission='"
                + permission
                + '\''
                + ", statusCode='"
                + statusCode
                + '\''
                + '}';
    }
}