/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: AdminDataSourceEnum.java
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

/**
 * The admin data source enum.
 */
public enum AdminDataSourceEnum {

    /**
     * h2.
     */
    H2("h2"),

    /**
     * mysql.
     */
    MYSQL("mysql"),

    /**
     * postgresql.
     */
    POSTGRESQL("postgresql"),

    /**
     * oracle.
     */
    ORACLE("oracle");


    private final String value;

    /**
     * all args constructor.
     *
     * @param value value
     */
    AdminDataSourceEnum(final String value) {
        this.value = value;
    }

    /**
     * get the value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }
}
