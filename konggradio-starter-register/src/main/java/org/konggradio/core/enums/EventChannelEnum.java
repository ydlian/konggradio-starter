/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: EventChannelEnum.java
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

package org.konggradio.core.enums;


public enum EventChannelEnum {
	EVENT_MSG(1L, "System event"),
    HTTP_REQUEST(2L, "Http request"),
    HTTP_HIT(3L, "Http hit"),
    TIME_COST(5L, "Time cost"),
	EVENT_LUA_FILE(10L, "Lua file"),
	EVENT_LUA_STRING(11L, "Lua command"),
	EVENT_LUA_REMOTE_FILE(12L, "Lua remote file"),

	EVENT_SHELL_FILE(20L, "Shell file"),
	EVENT_SHELL_STRING(21L, "Shell command"),
	EVENT_SHELL_REMOTE_FILE(22L, "Shell remote file"),

	EVENT_GROOVY_FILE(30L, "Groovy file"),
	EVENT_GROOVY_STRING(31L, "Groovy command"),
	EVENT_GROOVY_REMOTE_FILE(32L, "Groovy remote file"),
	EVENT_READ_FILE(40L, "Read file command"),
	EVENT_WRITE_FILE(41L, "Write file command"),
	EVENT_READ_PROPERTY(42L, "Read property command"),
	EVENT_WRITE_PROPERTY(43L, "Write property command"),
	EVENT_OPT_FILE(44L,""),
	EVENT_LIST_FILE(45,""),
	EVENT_MKDIR(46L,""),
	EVENT_TAIL_FILE(47L,""),
	EVENT_READ_LOADED_BEANS(48L, "Read loaded beans"),
	EVENT_REMOVE_BEANS(49L, "Remove beans when loading"),
	EVENT_RELOAD_APP(50L,""),

    ALARM_METRICS(99L, "Alarm metrics"),

    ;

    /**
     * 枚举编码
     */
    private long code;

    /**
     * 枚举名称
     */
    private String name;


    EventChannelEnum(long code, String name) {
        this.code = code;
        this.name = name;
    }




    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public static EventChannelEnum getEnumByCode(long code) {
        for (EventChannelEnum businessTypeEnum : values()) {
            if (businessTypeEnum.code == code) {
                return businessTypeEnum;
            }
        }
        return null;
    }

}
