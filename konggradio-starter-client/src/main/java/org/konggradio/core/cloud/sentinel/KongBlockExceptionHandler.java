/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: KongBlockExceptionHandler.java
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

package org.konggradio.core.cloud.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

//@Component
public class KongBlockExceptionHandler implements BlockExceptionHandler {

	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
		System.out.println("BlockExceptionHandler ++++++++++++++++++++++++++"+e.getRule());

		Map<Integer,String> hashMap=new HashMap<>();

		if(e instanceof FlowException){
			hashMap.put(100,"接口限流了");
		}else if(e instanceof DegradeException){
			hashMap.put(101,"服务降级了");
		}else if(e instanceof ParamFlowException){
			hashMap.put(102,"热点参数限流了");
		}else if(e instanceof SystemBlockException){
			hashMap.put(103,"触发系统保护规则了");
		}else if(e instanceof AuthorityException){
			hashMap.put(104,"授权规则不通过");
		}

		//返回json数据
		httpServletResponse.setStatus(500);
		httpServletResponse.setCharacterEncoding("utf-8");
		httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(httpServletResponse.getWriter(),hashMap);
	}
}

