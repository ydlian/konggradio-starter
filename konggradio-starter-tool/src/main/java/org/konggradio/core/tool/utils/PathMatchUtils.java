/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: PathMatchUtils.java
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

package org.konggradio.core.tool.utils;

import com.google.common.base.Splitter;
import org.springframework.util.AntPathMatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Path match utils.
 */
public class PathMatchUtils {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    /**
     * replace url {id} to real param.
     *
     * @param path        the total path
     * @param regex       the regex content
     * @param replacement the replacement content
     * @return the string
     */
    public static String replaceAll(final String path, final String regex, final String replacement) {
        return path.replaceAll(Pattern.quote(regex), Matcher.quoteReplacement(replacement));
    }

    /**
     * Match boolean.
     *
     * @param matchUrls to ignore urls
     * @param path      the path
     * @return the boolean
     */
    public static boolean match(final String matchUrls, final String path) {
        return Splitter.on(",").omitEmptyStrings().trimResults().splitToList(matchUrls).stream().anyMatch(url -> reg(url, path));
    }

    private static boolean reg(final String pattern, final String path) {
        return MATCHER.match(pattern, path);
    }

}
