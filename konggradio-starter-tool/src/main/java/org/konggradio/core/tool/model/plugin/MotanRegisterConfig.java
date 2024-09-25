/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: MotanRegisterConfig.java
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

package org.konggradio.core.tool.model.plugin;

import java.io.Serializable;
import java.util.Objects;

/**
 * Motan register config.
 */
public class MotanRegisterConfig implements Serializable {

    private String register;

    /**
     * get register.
     *
     * @return register
     */
    public String getRegister() {
        return register;
    }

    /**
     * set register.
     *
     * @param register register
     */
    public void setRegister(final String register) {
        this.register = register;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MotanRegisterConfig that = (MotanRegisterConfig) o;
        return Objects.equals(register, that.register);
    }

    @Override
    public int hashCode() {
        return Objects.hash(register);
    }

    @Override
    public String toString() {
        return "MotanRegisterConfig{"
                + "register='"
                + register
                + '\''
                + '}';
    }
}
