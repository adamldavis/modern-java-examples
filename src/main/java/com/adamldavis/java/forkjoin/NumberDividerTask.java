package com.adamldavis.java.forkjoin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;

class NumberDividerTask extends RecursiveTask<Integer> {
	int[] numbers;

	NumberDividerTask(int[] numbers) {
		this.numbers = numbers;
	}

	@Override
	protected Integer compute() {
		int sum = 0;
		List<RecursiveTask<Integer>> forks = new ArrayList<>();
		if (numbers.length > 20) {
			NumberDividerTask task1 = new NumberDividerTask(Arrays.copyOfRange(
					numbers, 0, numbers.length / 2));
			NumberDividerTask task2 = new NumberDividerTask(Arrays.copyOfRange(
					numbers, numbers.length / 2, numbers.length));
			forks.add(task1);
			forks.add(task2);
			task1.fork();
			task2.fork();
		} else {
			SumCalculatorTask sumCalculatorTask = new SumCalculatorTask(numbers);
			forks.add(sumCalculatorTask);
			sumCalculatorTask.fork();
		}
		// Combine the result from all the tasks
		for (RecursiveTask<Integer> task : forks) {
			sum += task.join();
		}
		return sum;
	}
}

