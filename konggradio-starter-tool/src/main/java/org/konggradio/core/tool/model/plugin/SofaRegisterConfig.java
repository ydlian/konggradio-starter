/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: SofaRegisterConfig.java
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
 * The type sofa register config.
 */
public class SofaRegisterConfig implements Serializable {

    private static final long serialVersionUID = -3770114790533455035L;

    private String register;

    private String group;

    private String protocol;

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
     * get protocol.
     *
     * @return protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * set protocol.
     *
     * @param protocol protocol
     */
    public void setProtocol(final String protocol) {
        this.protocol = protocol;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SofaRegisterConfig that = (SofaRegisterConfig) o;
        return Objects.equals(register, that.register) && Objects.equals(group, that.group) && Objects.equals(protocol, that.protocol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(register, group, protocol);
    }

    @Override
    public String toString() {
        return "SofaRegisterConfig{"
                + "register='"
                + register
                + '\''
                + ", group='"
                + group
                + '\''
                + ", protocol='"
                + protocol
                + '\''
                + '}';
    }
}
