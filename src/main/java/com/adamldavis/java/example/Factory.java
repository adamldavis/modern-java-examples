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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is meant as an example of Strings in switch statements and the diamond
 * operator.
 * 
 * @author Adam L. Davis
 */
public class Factory {

	// for example Collection<Long> ids = makeNew("set", Long.class);

	public static <T> Collection<T> makeNew(String type, Class<T> tClass) {
		// You should probably never do something like this in real code.
		switch (type) {
		case "set":
			return new HashSet<>();
		case "lset":
			return new LinkedHashSet<>();
		case "treeset":
			return new TreeSet<>();
		case "vector":
			return new Vector<>();
		case "array":
			return new ArrayList<>();
		case "deque":
		case "queue":
		case "list":
		default:
			return new LinkedList<>();
		}
	}

	public static <K, V> Map<K, V> makeNewMap(String type, Class<K> kClass,
			Class<V> vClass) {
		switch (type) {
		case "con":
		case "concurrent":
			return new ConcurrentHashMap<>();
		case "hashtable":
			return new Hashtable<>();
		case "map":
		case "hashmap":
		default:
			return new HashMap<>();
		}
	}

}
