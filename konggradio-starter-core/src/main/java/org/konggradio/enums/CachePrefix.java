/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: CachePrefix.java
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

/**
 * 缓存前缀
 *
 * @author
 * @create  15:03:07
 **/
public enum CachePrefix {

    /**
     * 带权限的机构初始化节点
     */
    USER_INSTITUTION_AUTH_INIT_NODE("DRIGHT:INSTITUTION:AUTH:INIT:NODE:USER:"),
    USER_INSTITUTION_AUTH_CHILD_PARENT("DRIGHT:INSTITUTION:AUTH:CHILD:PARENT:ID:USER:ID:"),
    USER_IS_SUPER_MANAGER("DRIGHT:USER:IS:SUPER:MANAGER:USER:ID:"),
    SYSTEM_USER_MENU_MANAGER("DRIGHT:SYSTEM:USER:MENU:ID:CODE:"),

    USER_BELONG_TO_INSTITUTION_LIST("DRIGHT:BELONG:TO:INSTITUTION:LIST:"),
    /**
     * 权限校验使用
     */
    SYSTEM_MENU_URL_EXISTS("DRIGHT:SYSTEM:MENU:URL:EXISTS:"),
    SYSTEM_USER_MENU_PERMISSION_URL_EXISTS("DRIGHT:SYSTEM:USER:MENU:PERMISSION:URL:EXISTS:"),
    /**
     * 登陆态验证
     */
    SYSTEM_USER_LOGIN_CHECK_RESULT("DRIGHT:SYSTEM:USER:LOGIN:CHECK:RESULT:"),

	METRIC_REG_PRE("METRIC:REGISTRY:NAME:"),
	METRIC_REG_NAME_SETS_PRE("METRIC:REGISTRY:NAMES:SETS:KEY"),
    ;

    private String prefix = null;

    CachePrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * 生成key
     *
     * @param postfix 后缀
     * @return Key
     */
    public String genKey(String postfix) {
        return this.prefix + postfix;
    }
}
