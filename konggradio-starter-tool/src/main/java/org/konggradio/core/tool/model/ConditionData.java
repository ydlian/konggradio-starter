/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: ConditionData.java
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

import org.konggradio.core.tool.enums.OperatorEnum;

import java.util.Objects;

/**
 * ConditionDTO.
 *
 * @since 2.0.0
 */
public class ConditionData {

    /**
     * param type (post  query  uri).
     */
    private String paramType;

    /**
     * {@linkplain OperatorEnum}.
     */
    private String operator;

    /**
     * param name.
     */
    private String paramName;

    /**
     * param value.
     */
    private String paramValue;

    /**
     * get paramType.
     *
     * @return paramType
     */
    public String getParamType() {
        return paramType;
    }

    /**
     * set paramType.
     *
     * @param paramType paramType
     */
    public void setParamType(final String paramType) {
        this.paramType = paramType;
    }

    /**
     * get operator.
     *
     * @return operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * set operator.
     *
     * @param operator operator
     */
    public void setOperator(final String operator) {
        this.operator = operator;
    }

    /**
     * get paramName.
     *
     * @return paramName
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * set paramName.
     *
     * @param paramName paramName
     */
    public void setParamName(final String paramName) {
        this.paramName = paramName;
    }

    /**
     * get paramValue.
     *
     * @return paramValue
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * set paramValue.
     *
     * @param paramValue paramValue
     */
    public void setParamValue(final String paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConditionData that = (ConditionData) o;
        return Objects.equals(paramType, that.paramType) && Objects.equals(operator, that.operator)
                && Objects.equals(paramName, that.paramName) && Objects.equals(paramValue, that.paramValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paramType, operator, paramName, paramValue);
    }

    @Override
    public String toString() {
        return "ConditionData{"
                + "paramType='"
                + paramType
                + '\''
                + ", operator='"
                + operator
                + '\''
                + ", paramName='"
                + paramName
                + '\''
                + ", paramValue='"
                + paramValue
                + '\''
                + '}';
    }
}
