/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: HttpClientUtil.java
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

package org.konggradio.core.utils;

import okhttp3.*;
import org.konggradio.core.constants.RegisterConstants;
import org.konggradio.core.model.vo.SendMessageVO;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

public class HttpClientUtil {

	public static String getRequestAsString(String queryUrl, String token) {
		if (!StringUtils.hasText(token)) {
			token = "";
		}
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
			.connectTimeout(5, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS).build();
		Request request = new Request.Builder().url(queryUrl).addHeader(RegisterConstants.GW_X_AUTH_TOKEN, token).method("GET", null).build();
		try (Response response = okHttpClient.newCall(request).execute()) {
			return response.body().string();
		} catch (Exception e) {

		}
		return "";
	}

	public static String getHidRequestAsString(String queryUrl, String token,String hid) {
		if (!StringUtils.hasText(token)) {
			token = "";
		}
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
			.connectTimeout(5, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS).build();
		Request request = new Request.Builder().url(queryUrl)
			.addHeader(RegisterConstants.GW_X_AUTH_TOKEN, token)
			.addHeader(RegisterConstants.GW_X_HID, hid)
			.method("GET", null).build();
		try (Response response = okHttpClient.newCall(request).execute()) {
			return response.body().string();
		} catch (Exception e) {

		}
		return "";
	}
	public static String putRequestAsString(String queryUrl, String token) {
		if (!StringUtils.hasText(token)) {
			token = "";
		}
		MediaType mediaType = MediaType.parse("text/plain");
		RequestBody body = RequestBody.create(mediaType, "jsonString");
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
			.connectTimeout(5, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS).build();
		Request request = new Request.Builder().url(queryUrl).put(body)
			.addHeader("Content-Type", "application/json")
			.addHeader("Connection", "keep-alive")
			.addHeader(RegisterConstants.GW_X_AUTH_TOKEN, token).build();
		try (Response response = okHttpClient.newCall(request).execute()) {
			return response.body().string();
		} catch (Exception e) {

		}
		return "";
	}

	public static String getRequestAsString(String queryUrl, String token, SendMessageVO sendMessageVO) {
		if (!StringUtils.hasText(token)) {
			token = "";
		}
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
			.connectTimeout(5, TimeUnit.SECONDS)
			.readTimeout(10, TimeUnit.SECONDS)
			.writeTimeout(10, TimeUnit.SECONDS).build();
		Request request = new Request.Builder().url(queryUrl).addHeader(RegisterConstants.GW_X_AUTH_TOKEN, token).method("GET", null).build();
		try (Response response = okHttpClient.newCall(request).execute()) {
			return response.body().string();
		} catch (Exception e) {

		}
		return "";
	}
}
