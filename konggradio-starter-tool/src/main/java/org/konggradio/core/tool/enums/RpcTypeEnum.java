/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: RpcTypeEnum.java
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

import org.konggradio.core.tool.exceptions.KongGradioRuntimeException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RpcTypeEnum.
 */
public enum RpcTypeEnum {

    /**
     * Http rpc type enum.
     */
    HTTP("http", true),

    /**
     * Dubbo rpc type enum.
     */
    DUBBO("dubbo", true),

    /**
     * Sofa rpc type enum.
     */
    SOFA("sofa", true),

    /**
     * Tars rpc type enum.
     */
    TARS("tars", true),

    /**
     * Web socket rpc type enum.
     */
    WEB_SOCKET("websocket", true),

    /**
     * springCloud rpc type enum.
     */
    SPRING_CLOUD("springCloud", true),

    /**
     * motan.
     */
    MOTAN("motan", true),

    /**
     * grpc.
     */
    GRPC("grpc", true);


    private final String name;

    private final Boolean support;

    /**
     * all args constructor.
     *
     * @param name    name
     * @param support support
     */
    RpcTypeEnum(final String name, final Boolean support) {
        this.name = name;
        this.support = support;
    }

    /**
     * get name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * get support.
     *
     * @return support
     */
    public Boolean getSupport() {
        return support;
    }

    /**
     * acquire operator supports.
     *
     * @return operator support.
     */
    public static List<RpcTypeEnum> acquireSupports() {
        return Arrays.stream(RpcTypeEnum.values())
                .filter(e -> e.support).collect(Collectors.toList());
    }

    /**
     * acquire operator support URI RPC type.
     *
     * @return operator support.
     */
    public static List<RpcTypeEnum> acquireSupportURIs() {
        return Arrays.asList(RpcTypeEnum.GRPC, RpcTypeEnum.HTTP, RpcTypeEnum.TARS, RpcTypeEnum.SPRING_CLOUD, RpcTypeEnum.DUBBO);
    }

    /**
     * acquire operator support Metadata RPC type.
     *
     * @return operator support.
     */
    public static List<RpcTypeEnum> acquireSupportMetadatas() {
        return Arrays.asList(RpcTypeEnum.HTTP, RpcTypeEnum.DUBBO, RpcTypeEnum.GRPC, RpcTypeEnum.SPRING_CLOUD, RpcTypeEnum.SOFA, RpcTypeEnum.TARS, RpcTypeEnum.MOTAN);
    }

    /**
     * acquireByName.
     *
     * @param name this is rpc type
     * @return RpcTypeEnum rpc type enum
     */
    public static RpcTypeEnum acquireByName(final String name) {
        return Arrays.stream(RpcTypeEnum.values())
                .filter(e -> e.support && e.name.equals(name)).findFirst()
                .orElseThrow(() -> new KongGradioRuntimeException(String.format(" this rpc type can not support %s", name)));
    }
}
