/*
 * Copyright (c) 2024.
 *
 *  @author ydlian  whulyd@foxmail.com
 *  @since 2024-9-25
 *  @file: Base64Image.java
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

package org.konggradio.core.image;

public class Base64Image {
	public String getBase64Header() {
		return base64Header;
	}

	public void setBase64Header(String base64Header) {
		this.base64Header = base64Header;
	}

	public String getImagePostfix() {
		return imagePostfix;
	}

	public void setImagePostfix(String imagePostfix) {
		this.imagePostfix = imagePostfix;
	}

	public String getBase64Content() {
		return base64Content;
	}

	public void setBase64Content(String base64Content) {
		this.base64Content = base64Content;
	}

	public Base64Image(String base64Header, String imagePostfix, String base64Content) {
		super();
		this.base64Header = base64Header;
		this.imagePostfix = imagePostfix;
		this.base64Content = base64Content;
	}

	public Base64Image(String base64Header) {
		this.base64Header = base64Header;
	}

	public Base64Image(String base64Header, String imagePostfix) {
		this.base64Header = base64Header;
		this.imagePostfix = imagePostfix;
	}

	public Base64Image() {
		super();
	}


	private String base64Header;
	private String imagePostfix;
	private String base64Content;
}
