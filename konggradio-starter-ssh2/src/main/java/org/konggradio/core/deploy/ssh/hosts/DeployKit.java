/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: DeployKit.java
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

package org.konggradio.core.deploy.ssh.hosts;


import org.konggradio.core.deploy.ssh.core.SshUtil;
import org.konggradio.core.deploy.ssh.model.CICDInfo;
import org.konggradio.core.deploy.ssh.model.DeployEnv;

public interface DeployKit {
	CICDInfo stop(Integer applicationNumber, SshUtil ssh, CICDInfo cicdInfo);

	CICDInfo restart(Integer applicationNumber, SshUtil ssh, CICDInfo cicdInfo);

	CICDInfo tailLog(Integer applicationNumber, SshUtil ssh, CICDInfo cicdInfo);

	CICDInfo installConsul(Integer applicationNumber, SshUtil ssh, CICDInfo cicdInfo);

	CICDInfo installJdk(Integer applicationNumber, SshUtil ssh, CICDInfo cicdInfo);
	CICDInfo installApplication(Integer appNumber, SshUtil ssh, CICDInfo cicdInfo);
	CICDInfo pkg(Integer appNumber, SshUtil ssh, CICDInfo cicdInfo);
	CICDInfo clear(Integer appNumber, SshUtil ssh, CICDInfo cicdInfo);

	void setDeployEnv(DeployEnv deployEnv);

	DeployEnv getDeployEnv();
}
