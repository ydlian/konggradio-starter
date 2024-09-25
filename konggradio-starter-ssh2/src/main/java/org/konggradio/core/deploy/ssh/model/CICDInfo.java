/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: CICDInfo.java
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

package org.konggradio.core.deploy.ssh.model;

import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Accessors(chain = true)
public class CICDInfo {
	private List<String> helpList;
	private List<String> appList;
	private List<String> installPrompts;
	private List<String> artifactoryServers;
	private String initShellPath;
	private String workspace;
	private String deployBasePath;
	private Set<String> hostIpPrefix;

	//appNumber,deployPath
	private HashMap<String, String> deployPathMap;
	//appNumber,appName
	private HashMap<String, String> appNameMap;
	//appNumber,fileName
	private HashMap<String, String> fileNameMap;
	//appNumber,version
	private HashMap<String, String> deployVersionMap;
	//appNumber,profile;
	private HashMap<String, String> envProfileMap;

	public void init(){
		deployPathMap = Maps.newHashMap();
		appNameMap = Maps.newHashMap();
		fileNameMap = Maps.newHashMap();
		envProfileMap = Maps.newHashMap();
	}
	public HashMap<String, String> updateDeployPathMap(String appNumber, String deployPath) {
		deployPathMap.put(appNumber,deployPath);
		return deployPathMap;
	}
	public HashMap<String, String> updateAppNameMap(String appNumber, String appName) {
		appNameMap.put(appNumber,appName);
		return appNameMap;
	}
	public HashMap<String, String> updateFileNameMap(String appNumber, String fileName) {
		fileNameMap.put(appNumber,fileName);
		return fileNameMap;
	}
	public HashMap<String, String> updateDeployVersionMap(String appNumber, String version) {
		deployVersionMap.put(appNumber,version);
		return deployVersionMap;
	}
	public HashMap<String, String> updateEnvProfileMap(String appNumber, String profile) {
		envProfileMap.put(appNumber,profile);
		return envProfileMap;
	}
}
