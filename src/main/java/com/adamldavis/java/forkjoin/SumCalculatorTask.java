package com.adamldavis.java.forkjoin;

import java.util.concurrent.RecursiveTask;

class SumCalculatorTask extends RecursiveTask<Integer>{
	int [] numbers;
	SumCalculatorTask(int[] numbers){
		this.numbers = numbers;
	}
	
	@Override
	protected Integer compute() {
		int sum = 0;
		for (int i : numbers){
			sum += i;
		}
		return sum;
	}
}
