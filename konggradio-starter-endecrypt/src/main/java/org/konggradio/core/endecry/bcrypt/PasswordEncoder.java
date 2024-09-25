/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: PasswordEncoder.java
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

package org.konggradio.core.endecry.bcrypt;

/**
 * <pre>
 * 编码密码和check密码的接口
 * The preferred implementation is {@code BCryptPasswordEncoder}.
 * &#64;author: xiongchengwei
 * </pre>
 */
public interface PasswordEncoder {

    /**
     * Encode the raw password. Generally, a good encoding algorithm applies a SHA-1 or greater hash
     * combined with an 8-byte or greater randomly generated salt.
     */
    String encode(CharSequence rawPassword);

    /**
     * Encode the raw password.you must have salt for encode password
     */
    String encode(CharSequence rawPassword, String salt);

    /**
     * Verify the encoded password obtained from storage matches the submitted raw password after it too
     * is encoded. Returns true if the passwords match, false if they do not. The stored password itself
     * is never decoded.
     *
     * @param rawPassword the raw password to encode and match
     * @param encodedPassword the encoded password from storage to compare with
     * @return true if the raw password, after encoding, matches the encoded password from storage
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);

}
