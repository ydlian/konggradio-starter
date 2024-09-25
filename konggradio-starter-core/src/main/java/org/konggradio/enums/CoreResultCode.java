/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: CoreResultCode.java
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

package org.konggradio.enums;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.konggradio.core.tool.api.IResultCode;

public enum CoreResultCode implements BaseStatusEnum, IResultCode {
    SUCCESS(0, "success"),
    OPERATION_SUCCESS(200, "操作成功"),
    FLOW_LIMIT(400, "Flow limited"),
    GW_AUTH_FAIL(401, "网关鉴权失败"),
    //业务层控制返回，非法访问
    ACCESS_DENY(402, "No access"),
    NO_PERMISSION(403, "No access"),
    CONTENT_NOT_EXIST(404, "数据不存在"),
    //请求方法不支持
    OPERATION_FAIL(405, "操作失败"),
    PARAM_ERROR(406, "参数错误"),
	BIND_MID_ERROR(410, "绑定设备错误"),
	ACCOUNT_ERROR(411, "用户名或密码错误"),
	ACCOUNT_EXPIRED(412, "账户无效错误"),
	TR_ACCOUNT_ERROR(413, "用户名不合法"),
    //兜底错误码
    SERVER_BUSY(500, "系统繁忙"),
    SERVER_TIME_OUT(504, "服务器超时"),
    DATA_CONVERSION_FAIL(507, "数据转换异常"),
    SERVER_OUT_OF_SERVICE(509, "暂停服务"),
    ;
    private int code;
    private String msg;

    CoreResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String getStatus() {
        return String.valueOf(this.code);
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public String toJSONString() {
        return toString();
    }

    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
