package com.adamldavis.java.lazy;

import com.googlecode.totallylazy.Arrays;
import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Sequences;

public class ConcurrentDownloader extends Downloader {

	@Override
	public void download() {
		Sequences.forEachConcurrently(Arrays.list(urls),
				new Callable1<String, Void>() {
					public Void call(String urlString) throws Exception {
						fetch(urlString);
						return null;
					}
				});
	}

}
