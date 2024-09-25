/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: LoadBalanceService.java
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

package org.konggradio.mertrics.core.load;


import org.konggradio.core.tool.model.load.ServiceEntity;

import java.util.Set;

public interface LoadBalanceService {
	String SERVICE_CONNECTOR = "-";
	String LOAD_PRE = "K-LOAD-PRE-";
	String LOAD_SERVER_REG_LIST_KEY = "K-SERVICE-REG-LIST-KEY";
	Set<Object> addInstance(ServiceEntity instance);

	Set<Object> registerInstance(ServiceEntity instance);
	Set<Object> removeInstance(ServiceEntity instance);

	boolean clearInstance(String instanceId);

	Set<Object> listInstance(String instanceId);

	Set<Object> listRegisterInstance();

	boolean filter();

	boolean filter(ServiceEntity instance);

	ServiceEntity makeService(String ip, Integer port, String appName);

	ServiceEntity makeService();
}
