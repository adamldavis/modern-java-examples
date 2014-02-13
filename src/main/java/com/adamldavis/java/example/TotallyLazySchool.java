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
package com.adamldavis.java.example;

import static com.googlecode.totallylazy.Option.none;
import static com.googlecode.totallylazy.Option.option;
import static com.googlecode.totallylazy.Option.some;
import static com.googlecode.totallylazy.Sequences.sequence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.Set;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.numbers.Numbers;

/**
 * Modern Java: Example of School with students in memory using the totallylazy
 * library. This demonstrates use of Functions, Predicates, and Option
 * pre-Java8.
 * 
 * @author Adam L. Davis
 */
public class TotallyLazySchool {

	private final Set<Student> students = new LinkedHashSet<>();

	// add a new Student without a GPA
	public void addStudent(final String firstName, final String lastName,
			final String studentType) throws MissingDataException {

		addStudent(firstName, lastName, studentType, null);
	}

	// add a Student with a GPA (or null GPA)
	public void addStudent(final String firstName, final String lastName,
			final String studentType, final Double gpa)
			throws MissingDataException {

		final StudentType type = StudentType.valueOf(option(studentType)
				.getOrThrow(new MissingDataException("Missing student type")));

		addStudent(firstName, lastName, type, gpa);
	}

	// add a Student with enum StudentType and a GPA (or null GPA)
	public void addStudent(final String firstName, final String lastName,
			final StudentType type, final Double gpa)
			throws MissingDataException {

		final String fn = option(firstName).getOrThrow(
				new MissingDataException("Missing first-name"));
		final String ln = option(lastName).getOrThrow(
				new MissingDataException("Missing last-name"));

		students.add(new Student(fn, ln, type, gpa));
	}

	// assumes student's are unique on first/last-name (never do this)
	public Option<Student> findStudent(final String firstName,
			final String lastName) {
		return sequence(students).find(new Predicate<Student>() {
			public boolean matches(Student student) {
				return student.firstName.equals(firstName)
						&& student.lastName.equals(lastName);
			}
		});
	}

	// returns the student if found; otherwise return none()
	public Option<Student> removeStudent(final String firstName,
			final String lastName) {
		final Option<Student> maybeStudent = findStudent(firstName, lastName);

		if (maybeStudent.isDefined()) {
			students.remove(maybeStudent.get());
		}
		return maybeStudent;
	}

	// returns the old student if found; otherwise return none()
	public Option<Student> updateStudentGpa(final String firstName,
			final String lastName, final double gpa)
			throws MissingDataException {
		final Option<Student> maybeStudent = removeStudent(firstName, lastName);

		if (maybeStudent.isEmpty()) {
			return maybeStudent; // not found
		}
		final Student student = maybeStudent.get(); // bug catcher

		addStudent(firstName, lastName, student.studentType, gpa);

		return maybeStudent;
	}

	public Option<Double> getHighestGPA() {
		if (students.isEmpty()) {
			return none();
		}
		// map/reduce
		// flatMap gets rid of empty gpa's since Option is Iterable

		return option(sequence(students).flatMap(studentToGpa())
				.reduce(Numbers.maximum).doubleValue());
	}

	public Option<Double> getLowestGPA() {
		if (students.isEmpty()) {
			return none();
		}
		// map/reduce
		// flatMap gets rid of empty gpa's

		return option(sequence(students).flatMap(studentToGpa())
				.reduce(Numbers.minimum).doubleValue());
	}

	// uses bigDecimal to be accurate to 5 digits.
	public Option<Double> getAverageGPA() {
		if (students.isEmpty()) {
			return none();
		}
		// filter/map/reduce
		final Sequence<Student> studentsWithGpa = sequence(students).filter(
				studentHasGpa());
		final Number sum = studentsWithGpa.flatMap(studentToGpa()).reduce(
				Numbers.sum);
		final int count = studentsWithGpa.size();

		return some(new BigDecimal(sum.doubleValue()).divide(
				new BigDecimal(count), 5, RoundingMode.HALF_UP).doubleValue());
	}

	public Iterable<Student> getStudentsWithLowestGPA() {
		if (getLowestGPA().isEmpty()) {
			return Sequences.empty();
		}
		return getStudentsWithGpa(getLowestGPA().get());
	}

	public Iterable<Student> getStudentsWithHighestGPA() {
		if (getHighestGPA().isEmpty()) {
			return Sequences.empty();
		}
		return getStudentsWithGpa(getHighestGPA().get());
	}

	public Iterable<Student> getStudentsWithNoGPA() {
		return Sequences.filter(students, new Predicate<Student>() {
			public boolean matches(Student student) {
				return student.gpa.isEmpty();
			}
		});
	}

	// returns a filtered view on the students Set.
	public Iterable<Student> getStudentsWithGpa(final Double gpa) {
		return Sequences.filter(students, new Predicate<Student>() {
			public boolean matches(Student student) {
				return gpa.equals(student.gpa.otherwise((Double) null));
			}
		});
	}

	// returns a filtered view of Students with known GPAs.
	public Iterable<Student> getStudentsWithGpa() {
		return Sequences.filter(students, studentHasGpa());
	}

	private Predicate<Student> studentHasGpa() {
		return new Predicate<Student>() {
			public boolean matches(Student student) {
				return student.gpa.isKnown();
			}
		};
	}

	private Callable1<Student, Option<Double>> studentToGpa() {
		return new Callable1<Student, Option<Double>>() {
			public Option<Double> call(Student s) {
				return option(s.gpa.otherwise((Double) null));
			}
		};
	}
}