/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: IOUtil.java
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

package org.konggradio.core.deploy.framework.util;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class IOUtil {
	private static final Logger logger = LoggerFactory.getLogger(IOUtil.class);


    /**
     * <p>
     * Method ：ensureExists
     * <p>
     * Description : 确保路径存在
     *
     * @param path
     * @author 顾力行-ops@KongGradio.com
     */
    public static String ensureExists(String path) {
        if (!new File(path).exists()) {
            new File(path).mkdirs();
        }
        return path;
    }

    public static boolean existsFile(String path) {
    	return new  File(path).exists();
    }

    public static boolean existsClassPathFile(String path) {
    	return  new ClassPathResource(path).exists();
    }

    public static byte[] toByteArray(File f) {
    	try {
			return Files.toByteArray(f);
		} catch (IOException e) {
			logger.error("",e);
		}
    	return null;
    }

    public String fileToString(String fileUri) {
    	try {
    		return Files.toString(new File(fileUri),Charsets.UTF_8);
    	} catch (IOException e) {
			logger.error("",e);
    	}
    	return null;
    }

    public static long sizeOf(String fileUri) throws IOException {
          return sizeOf(new File(fileUri));
    }

    public static long sizeOf(File file) throws IOException {
		  return Files.asByteSource(file).size();
    }

    public static long bytesToMB(long bytes){
    	return bytes/1024/1024;
    }


    /**
     * <p>Method ：writeFile
     * <p>Description : 字节数组写入磁盘文件
     * @param b
     * @param outputFile
     * @author  ops@KongGradio.com
     * @version 1.0.0
     */
    public static void writeFile(byte[] b, String outputFile) {
    	try {
			Files.write(b, new File(outputFile));
		} catch (IOException e) {
			logger.error("",e);
		}
    }

    public static void main(String[] args) throws IOException {
    	System.out.println(new ClassPathResource("").getURI());
    	System.out.println(IOUtil.class.getClassLoader().getResource(""));

    	int a = 2,b=32;
    	System.out.println(Math.pow(a,b));
    	System.out.println(Math.pow(2.0,32));
    	System.out.println(Files.toString(new File("E:/git/cicd/readme.md"), Charsets.UTF_8));
    }
}
