/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: FixedNameCsvFileProvider.java
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

package org.konggradio.mertrics;

import java.io.File;

/**
 * This implementation of the {@link CsvFileProvider} will always return the same name
 * for the same metric. This means the CSV file will grow indefinitely.
 */
public class FixedNameCsvFileProvider implements CsvFileProvider {

    @Override
    public File getFile(File directory, String metricName) {
        return new File(directory, sanitize(metricName) + ".csv");
    }

    protected String sanitize(String metricName) {
        //Forward slash character is definitely illegal in both Windows and Linux
        //https://msdn.microsoft.com/en-us/library/windows/desktop/aa365247(v=vs.85).aspx
        return metricName.replaceFirst("^/", "").replaceAll("/", ".");
    }
}
