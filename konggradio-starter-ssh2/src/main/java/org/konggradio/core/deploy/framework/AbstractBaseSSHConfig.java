/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: AbstractBaseSSHConfig.java
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

package org.konggradio.core.deploy.framework;


import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.konggradio.core.deploy.framework.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * <p>
 * Class : app.ConfigLoader
 * <p>
 * Descdription: 系统配置文件初始化
 *
 * @author 顾力行-ops@KongGradio.com
 * @version 1.0.0
 */
public abstract class AbstractBaseSSHConfig {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractBaseSSHConfig.class);

    /**
     * Prop prop: 单独使用PropKit生成的prop对象，避免全局PropKit被其他调用“污染”
     */
    public static Prop prop;

    /**
     * <p>Method ：useConfig
     * <p>Description : 指定配置文件
     *
     * @param configUrl
     * @author ops@KongGradio.com
     * @version 1.0.0
     */
    protected static void useConfig(String configUrl) {
        String configFileExt = /* PathUtil.getJarPath() + File.separator + */ configUrl;
        logger.info("搜索配置文件{}...", configFileExt);
        if (IOUtil.existsFile(configFileExt)) {
            System.out.println("使用配置文件[" + configFileExt + "]初始化应用...");
            prop = PropKit.use(new File(configFileExt));
        } else {
            if (IOUtil.existsClassPathFile(configUrl)) {
                prop = PropKit.use(configUrl);
                System.out.println("使用配置文件[classpath:" + configUrl + "]初始化应用...");
            }
            //使用当前目录下的配置文件，用于从jar包启动应用等场景
            else {
                logger.error("下列路径下未发现配置文件:"
                        + PathKit.getRootClassPath() + File.separator + configUrl + ","
                        + configFileExt);
                throw new RuntimeException();
            }
        }
    }
}
