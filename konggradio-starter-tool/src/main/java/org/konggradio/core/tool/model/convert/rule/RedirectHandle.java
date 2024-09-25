/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RedirectHandle.java
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
 * This is redirect plugin handle.
 */
public class RedirectHandle {
    /**
     * redirect url.
     */
    private String redirectURI;

    /**
     * get redirectURI.
     *
     * @return redirectURI
     */
    public String getRedirectURI() {
        return redirectURI;
    }

    /**
     * set redirectURI.
     *
     * @param redirectURI redirectURI
     */
    public void setRedirectURI(final String redirectURI) {
        this.redirectURI = redirectURI;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RedirectHandle that = (RedirectHandle) o;
        return Objects.equals(redirectURI, that.redirectURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(redirectURI);
    }

    @Override
    public String toString() {
        return "RedirectHandle{"
                + "redirectURI='"
                + redirectURI
                + '\''
                + '}';
    }
}
