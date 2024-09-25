/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: WebsocketData.java
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

package org.konggradio.core.tool.model;


import java.util.List;
import java.util.Objects;
import org.konggradio.core.tool.enums.ConfigGroupEnum;
import org.konggradio.core.tool.enums.DataEventTypeEnum;
/**
 * Data set, including {@link AppAuthData}、{@link ConditionData}、{@link PluginData}、{@link RuleData}、{@link SelectorData}.
 *
 * @param <T> the type parameter
 * @since 2.0.0
 */
public class WebsocketData<T> {

    /**
     * group type.
     * {@linkplain ConfigGroupEnum}
     */
    private String groupType;

    /**
     * event type.
     * {@linkplain DataEventTypeEnum}
     */
    private String eventType;

    /**
     * data list.
     * {@link AppAuthData}、{@link ConditionData}、{@link PluginData}、{@link RuleData}、{@link SelectorData}.
     */
    private List<T> data;

    /**
     * no args constructor.
     */
    public WebsocketData() {
    }

    /**
     * all args constructor.
     *
     * @param groupType groupType
     * @param eventType eventType
     * @param data      data
     */
    public WebsocketData(final String groupType, final String eventType, final List<T> data) {
        this.groupType = groupType;
        this.eventType = eventType;
        this.data = data;
    }

    /**
     * get groupType.
     *
     * @return groupType
     */
    public String getGroupType() {
        return groupType;
    }

    /**
     * set groupType.
     *
     * @param groupType groupType
     * @return this
     */
    public WebsocketData<T> setGroupType(final String groupType) {
        this.groupType = groupType;
        return this;
    }

    /**
     * get eventType.
     *
     * @return eventType
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * set eventType.
     *
     * @param eventType eventType
     * @return this
     */
    public WebsocketData<T> setEventType(final String eventType) {
        this.eventType = eventType;
        return this;
    }

    /**
     * get data.
     *
     * @return data
     */
    public List<T> getData() {
        return data;
    }

    /**
     * set data.
     *
     * @param data data
     * @return this
     */
    public WebsocketData<T> setData(final List<T> data) {
        this.data = data;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebsocketData<?> that = (WebsocketData<?>) o;
        return Objects.equals(groupType, that.groupType) && Objects.equals(eventType, that.eventType) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupType, eventType, data);
    }

    @Override
    public String toString() {
        return "WebsocketData{"
                + "groupType='"
                + groupType
                + '\''
                + ", eventType='"
                + eventType
                + '\''
                + ", data="
                + data
                + '}';
    }
}
