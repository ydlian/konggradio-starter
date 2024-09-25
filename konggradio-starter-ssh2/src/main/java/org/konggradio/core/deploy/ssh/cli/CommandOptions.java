/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: CommandOptions.java
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

package org.konggradio.core.deploy.ssh.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class CommandOptions {

	private Options options;

	private Option paramOption;

	public Options getOptions() {
		return options;
	}

	public CommandOptions() {
		// 创建 Options 对象
		options = new Options();

		// 添加 -h 参数
		options.addOption("h", false, "Lists short help");
		//options.addOption("l", true, "Lists short help");
		//options.addOption("i", true, "Lists short help");

		Option optList = new Option("l", "list", false, "Lists all applications");
		optList.setRequired(false);
		options.addOption(optList);

		Option optInit = new Option("init", "init", false, "Init env");
		optInit.setRequired(false);
		options.addOption(optInit);


		// 添加 -exec 参数
		//paramOption = Option.builder("ls").hasArgs().build();
		//options.addOption(paramOption);

		//-i|install
		paramOption = Option.builder("i").hasArgs().build();
		options.addOption(paramOption);

		paramOption = Option.builder("c").hasArgs().build();
		options.addOption(paramOption);

		paramOption = Option.builder("k").hasArgs().build();
		options.addOption(paramOption);

		//-g|get
		paramOption = Option.builder("conf").hasArgs().build();
		options.addOption(paramOption);

		//-g|get
		paramOption = Option.builder("g").hasArgs().build();
		options.addOption(paramOption);

		//-p|put
		paramOption = Option.builder("p").hasArgs().build();
		options.addOption(paramOption);
	}

}
