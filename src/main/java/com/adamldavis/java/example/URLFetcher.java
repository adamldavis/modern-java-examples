/*
 * Copyright 2013 Adam L. Davis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adamldavis.java.example;

import java.io.IOException;
import java.net.URL;

/**
 * This provides an easy way to fetch a URL. This is only meant as an example of
 * Java 7 multi-catch statements.
 * 
 * @author Adam L. Davis
 * 
 */
public class URLFetcher {

	/**
	 * @param urlString
	 *            URL to read from and then convert result to an Integer.
	 * @return null if input is null, malformed, or content retrieved is not a
	 *         number.
	 */
	public static Integer fetchURLAsInteger(String urlString) {
		try {

			URL url = new URL(urlString);
			String str = url.openConnection().getContent().toString();
			return Integer.parseInt(str);

		} catch (NullPointerException | NumberFormatException | IOException e) {
			return null;
		}
	}
}
