/*
 * Copyright (c) 2018-2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: AuthFun.java
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
package org.konggradio.core.secure.auth;

import lombok.extern.slf4j.Slf4j;
import org.konggradio.core.secure.KongGradioUser;
import org.konggradio.core.secure.utils.SecureUtil;
import org.konggradio.core.tool.constant.RoleConstant;
import org.konggradio.core.tool.utils.CollectionUtil;
import org.konggradio.core.tool.utils.Func;
import org.konggradio.core.tool.utils.StringUtil;

import java.util.Optional;

/**
 * 权限判断
 *
 * @author ydlian
 */
@Slf4j
public class AuthFun {

	/**
	 * 放行所有请求
	 *
	 * @return {boolean}
	 */
	public boolean permitAll() {
		return true;
	}

	/**
	 * 只有超管角色才可访问
	 *
	 * @return {boolean}
	 */
	public boolean denyAll() {
		return hasRole(RoleConstant.ADMIN);
	}

	/**
	 * 判断是否有该角色权限
	 *
	 * @param role 单角色
	 * @return {boolean}
	 */
	public boolean hasRole(String role) {
		return hasAnyRole(role);
	}

	/**
	 * 判断是否有该角色权限
	 *
	 * @param role 角色集合
	 * @return {boolean}
	 */
	public boolean hasAnyRole(String... role) {
		KongGradioUser user = SecureUtil.getUser();
		Optional empty = Optional.ofNullable(user).map(u->u.getRoleName());
		if (empty.isPresent()) {
			String userRole = (String) empty.get();
			log.info("userRole:{},user:{}",userRole,user.toString());

			if (StringUtil.isBlank(userRole)) {
				return false;
			}
			String[] roles = Func.toStrArray(userRole);
			for (String r : role) {
				if (CollectionUtil.contains(roles, r)) {
					return true;
				}
			}
		}

		return false;
	}
	public static void main(String[] args) {
		KongGradioUser user = new KongGradioUser();
		user.setRoleName("test");
		Optional empty=Optional.ofNullable(user).map(u->u.getRoleName());
		if (empty.isPresent()) {
			String userRole = (String) empty.get();
			System.out.println(userRole);
		}else{
			System.out.println("None");
		}
	}

}
