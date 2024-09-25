/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: LauncherCtlService.java
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

package org.konggradio.core.launch.service;

import cn.hutool.core.io.ManifestUtil;
import org.konggradio.core.RegisterBuilder;
import org.konggradio.core.launch.constant.EnvConstant;
import org.konggradio.core.launch.service.manifest.ManiSeed;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

public class LauncherCtlService {
	public static boolean update() {
		if (Math.abs(get()) <= EnvConstant.MATRIX[4][3]) {
			return true;
		}
		//System.out.println(get());
		return false;
	}

	public static long get() {
		Manifest manifest = getManifestFromClasspath(RegisterBuilder.getMainClassEntry());
		return ManiSeed.ManiSeed(manifest);
	}

	public static Manifest getManifestFromClasspath(Class<?> clazz) {
		//if (clazz == null) throw new RuntimeException();
		ProtectionDomain protectionDomain = clazz.getProtectionDomain();
		CodeSource codeSource = protectionDomain.getCodeSource();
		URI codeJarUri = null;
		try {
			codeJarUri = (codeSource != null) ? codeSource.getLocation().toURI() : null;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (codeJarUri == null) {
			return null;
		}
		if (codeJarUri.getScheme().equals("jar")) {
			String newPath = codeJarUri.getSchemeSpecificPart();
			String suffix = "!/BOOT-INF/classes!/";
			if (newPath.endsWith(suffix)) {
				newPath = newPath.substring(0, newPath.length() - suffix.length());
			}
			if (newPath.endsWith("!/")) {
				newPath = newPath.substring(0, newPath.length() - 2);
			}
			try {
				codeJarUri = new URI(newPath);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		Map<URI, Manifest> uriManifestMap = null;
		if (uriManifestMap == null) {
			synchronized (ManifestUtil.class) {
				if (uriManifestMap == null) {
					try {
						uriManifestMap = readClasspathAllManifest();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		Manifest me = null;
		if (uriManifestMap != null) {
			me = uriManifestMap.get(codeJarUri);
		}
		return me;
	}

	public static HashMap<URI, Manifest> readClasspathAllManifest() throws Exception {
		HashMap<URI, Manifest> manifestMap = new HashMap<>();

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		org.springframework.core.io.Resource[] resources =
			resolver.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + "META-INF/MANIFEST.MF");
		for (org.springframework.core.io.Resource resource : resources) {
			URL manifestUrl = resource.getURL();
			int lastIndex = 0;
			String manifestPath = null;
			if (manifestUrl.getProtocol().equals("file")) {
				manifestPath = manifestUrl.toString();
				lastIndex = manifestPath.indexOf("META-INF/MANIFEST.MF");

			} else if (manifestUrl.getProtocol().equals("jar")) {
				manifestPath = manifestUrl.getPath();
				lastIndex = manifestPath.indexOf("!/META-INF/MANIFEST.MF");

			} else {
				System.err.println("Jar position has error!");
				continue;
			}
			URI jarUri = new URI(manifestPath.substring(0, lastIndex));
			InputStream inputStream = null;
			try {
				inputStream = resource.getInputStream();
				Manifest manifest = new Manifest(inputStream);
				manifestMap.put(jarUri, manifest);
			} finally {
				if (inputStream != null) {
					inputStream.close();
				}
			}
		}
		return manifestMap;
	}
}
