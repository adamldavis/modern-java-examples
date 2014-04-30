/*
 * Copyright 2014 Adam L. Davis
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
package com.adamldavis.java.lazy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * @author Adam L. Davis
 * 
 */
public abstract class Downloader {

	public String[] urls = { "http://xkcd.com/",
			"http://www.reddit.com/", "http://www.adamldavis.com/",
			"http://www.coderbiz.com/", "http://www.yahoo.com/" };
	
	public abstract void download();

	public String fetch(String urlString) {
		System.out.println("Downloading..." + urlString);
		Object content = null;
		try {
			final URL url = new URL(urlString);
			content = url.openConnection().getContent();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (content instanceof String) {
			return (String) content;
		} else if (content instanceof InputStream) {
			try (InputStream input = (InputStream) content;
					OutputStream out = new ByteArrayOutputStream();) {
				int n = 0;
				byte[] arr = new byte[1024];

				while (-1 != (n = input.read(arr)))
					out.write(arr, 0, n);

				return out.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
