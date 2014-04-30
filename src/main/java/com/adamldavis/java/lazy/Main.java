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

import static java.lang.System.out;

import java.util.LinkedList;
import java.util.List;

import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.numbers.Numbers;

/**
 * Runs serial and concurrent downloads of multiple web-sites multiple times and
 * calculates the average time for each strategy. Should demonstrate that
 * concurrent is better.
 * 
 * @author Adam L. Davis
 * 
 */
public class Main {

	private static final int TIMES = 10;

	public static class DownloadTimer implements Runnable {
		Downloader downloader;
		final List<Long> times = new LinkedList<>();

		public DownloadTimer(Downloader d) {
			downloader = d;
		}

		public void run() {
			for (int i = 0; i < TIMES; i++) {
				long start = System.currentTimeMillis();
				downloader.download();
				times.add(System.currentTimeMillis() - start);
			}
		}

		public Number getAverageTime() {
			return Sequences.sequence(times).reduce(Numbers.average);
		}
	}

	public static void main(String[] args) {
		out.println("Concurrent:");
		DownloadTimer dt = new DownloadTimer(new ConcurrentDownloader());
		dt.run();
		out.println("Serial:");
		DownloadTimer sdt = new DownloadTimer(new SerialDownloader());
		sdt.run();
		out.println("Concurrent Time " + dt.getAverageTime().longValue());
		out.println("Serial Time " + sdt.getAverageTime().longValue());
	}
}
