package com.adamldavis.java.forkjoin;

import java.util.concurrent.ForkJoinPool;

public class ForkJoinTest {
	static ForkJoinPool forkJoinPool = new ForkJoinPool();
	public static final int LENGTH = 2000;
	
	public static void main(String[] args) {
		int [] numbers = new int[LENGTH];
		// Create  an array with some values. 
		for(int i=0; i<LENGTH; i++){
			numbers[i] = i * 2;
		}
		int sum = forkJoinPool.invoke(new NumberDividerTask(numbers));
	  
		System.out.println("Sum: "+sum);
	}
}
