/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: EncryptConstant.java
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

package org.konggradio.core.endecry.constants;

public class EncryptConstant {
	public static final String GW_TRUST_INNER_FLAG_HEADER = "x-gw-trust";
	public static final String INNER_API_SIGNATURE_HEADER = "x-api-sign";
	public static final String API_TIMESTAMP_HEADER = "x-timestamp";
	public static final String X_REQUEST_ID_HEADER = "x-request-id";
	public static final String X_REQUEST_SOURCE_TAG_HEADER = "x-request-source-tag";
	public static final String X_REQUEST_CLIENT_HOST_NAME_HEADER = "x-request-client-host-name";
	public static final String X_API_SIGN_RULE_HEADER = "x-api-sign-rule";
	public static final String X_ENABLE_GATEWAY_API_SIGN_HEADER = "x-enable-gw-api-sign";

	public static final String X_API_SIGN_RULE_SHA256 = "sha256";

	public static final String X_API_SIGN_RULE_RSA1024 = "rsa1024";


}
