/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: GrpcRegisterConfig.java
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
 * The type grpc register config.
 */
public class GrpcRegisterConfig implements Serializable {

    private static final long serialVersionUID = -4496044027526152878L;

    private String threadpool;

    /**
     * get threadpool.
     *
     * @return threadpool
     */
    public String getThreadpool() {
        return threadpool;
    }

    /**
     * set threadpool.
     *
     * @param threadpool threadpool
     */
    public void setThreadpool(final String threadpool) {
        this.threadpool = threadpool;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GrpcRegisterConfig that = (GrpcRegisterConfig) o;
        return Objects.equals(threadpool, that.threadpool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadpool);
    }

    @Override
    public String toString() {
        return "GrpcRegisterConfig{"
                + "threadpool='"
                + threadpool
                + '\''
                + '}';
    }
}
