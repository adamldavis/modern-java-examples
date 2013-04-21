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

import static org.bitbucket.dollar.Dollar.$;
import static org.bitbucket.dollar.lang.Maybe.definitely;
import static org.bitbucket.dollar.lang.Maybe.maybe;
import static org.bitbucket.dollar.lang.Maybe.nothing;
import static org.bitbucket.dollar.lang.Maybe.theAbsenceOfA;
import static org.bitbucket.dollar.lang.ObjectUtil.equal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.bitbucket.dollar.functions.BiFunction;
import org.bitbucket.dollar.functions.Function;
import org.bitbucket.dollar.functions.Predicate;
import org.bitbucket.dollar.lang.Maybe;
import org.bitbucket.dollar.lang.ObjectUtil;

/**
 * Modern Java: Example of School with students in memory. This example is meant
 * to introduce some functional concepts. Although this example use the dollar
 * and dollar-lang libraries, very similar things can be done with either Guava,
 * Functional Java, or (when it comes out) Java 8.
 * 
 * @author Adam L. Davis
 */
public class School {

	public static class Student {
		public Student(String firstName, String lastName,
				StudentType studentType, Maybe<Double> gpa) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.studentType = studentType;
			this.gpa = gpa;
		}

		public final String firstName;
		public final String lastName;
		public final StudentType studentType;
		public final Maybe<Double> gpa;

		@Override
		public int hashCode() {
			return ObjectUtil.hashCode(firstName, lastName, studentType, gpa);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Student other = (Student) obj;
			return equal(firstName, other.firstName)
					&& equal(lastName, other.lastName)
					&& equal(studentType, other.studentType)
					&& equal(gpa, other.gpa);
		}

	}

	public enum StudentType {
		PREMED, PRELAW, SCIENCE, LIBERAL_ARTS;
	}

	@SuppressWarnings("serial")
	public static class MissingDataException extends Exception {

		public MissingDataException() {
			super();
		}

		public MissingDataException(String message) {
			super(message);
		}
	}

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

		final StudentType type = StudentType.valueOf(maybe(studentType)
				.otherwiseThrow(MissingDataException.class,
						"Missing student type"));

		addStudent(firstName, lastName, type, gpa);
	}

	// add a Student with enum StudentType and a GPA (or null GPA)
	public void addStudent(final String firstName, final String lastName,
			final StudentType type, final Double gpa)
			throws MissingDataException {

		final String fn = maybe(firstName).otherwiseThrow(
				MissingDataException.class, "Missing first-name");
		final String ln = maybe(lastName).otherwiseThrow(
				MissingDataException.class, "Missing last-name");

		students.add(new Student(fn, ln, type, maybe(gpa)));
	}

	// assumes student's are unique on first/last-name (never do this)
	public Maybe<Student> findStudent(final String firstName,
			final String lastName) {
		try {
			return definitely($(students).find(new Predicate<Student>() {
				public boolean test(Student student) {
					return student.firstName.equals(firstName)
							&& student.lastName.equals(lastName);
				}
			}));
		} catch (NoSuchElementException ex) {
			return nothing(); // no such student.
		}
	}

	// returns the student if found; otherwise return nothing()
	public Maybe<Student> removeStudent(final String firstName,
			final String lastName) {
		final Maybe<Student> maybeStudent = findStudent(firstName, lastName);

		if (maybeStudent.isKnown()) {
			students.remove(maybeStudent
					.otherwiseThrow(NullPointerException.class));
		}
		return maybeStudent;
	}

	// returns the old student if found; otherwise return nothing()
	public Maybe<Student> updateStudentGpa(final String firstName,
			final String lastName, final double gpa)
			throws MissingDataException {
		final Maybe<Student> maybeStudent = removeStudent(firstName, lastName);

		if (maybeStudent.isEmpty()) {
			return maybeStudent; // not found
		}
		final Student student = maybeStudent
				.otherwiseThrow(NullPointerException.class); // bug catcher

		addStudent(firstName, lastName, student.studentType, gpa);

		return maybeStudent;
	}

	public Maybe<Double> getHighestGPA() {
		if (students.isEmpty()) {
			return nothing();
		}
		// map/reduce
		return definitely($(students).map(studentToGpa()).reduce(0d,
				new BiFunction<Maybe<Double>, Double, Double>() {
					public Double apply(Maybe<Double> maybeGPA, Double max) {
						final double gpa = maybeGPA.otherwise(0d);
						return gpa > max ? gpa : max;
					}
				}));
	}

	public Maybe<Double> getLowestGPA() {
		if (students.isEmpty()) {
			return nothing();
		}
		// map/reduce
		return definitely($(students).map(studentToGpa()).reduce(5d,
				new BiFunction<Maybe<Double>, Double, Double>() {
					public Double apply(Maybe<Double> maybeGPA, Double min) {
						final double gpa = maybeGPA.otherwise(5d);
						return gpa < min ? gpa : min;
					}
				}));
	}

	// uses bigDecimal to be accurate with large numbers of students.
	public Maybe<Double> getAverageGPA() {
		if (students.isEmpty()) {
			return nothing();
		}
		// filter/map/reduce
		final BigDecimal sum = $(students)
				.filter(studentHasGpa())
				.map(studentToGpa())
				.reduce(BigDecimal.ZERO,
						new BiFunction<Maybe<Double>, BigDecimal, BigDecimal>() {
							public BigDecimal apply(Maybe<Double> maybeGPA,
									BigDecimal sum) {
								final double gpa = maybeGPA.otherwise(0d);
								return sum.add(new BigDecimal(gpa));
							}
						});
		return definitely(sum.divide(new BigDecimal(students.size()), 5,
				RoundingMode.HALF_UP).doubleValue());
	}

	public Iterable<Student> getStudentsWithLowestGPA() {
		return getStudentsWithGpa(getLowestGPA());
	}

	public Iterable<Student> getStudentsWithHighestGPA() {
		return getStudentsWithGpa(getHighestGPA());
	}

	public Iterable<Student> getStudentsWithNoGPA() {
		return getStudentsWithGpa(theAbsenceOfA(Double.class));
	}

	// returns a filtered view on the students Set.
	public Iterable<Student> getStudentsWithGpa(final Maybe<Double> gpa) {
		return $(students).filter(new Predicate<Student>() {
			public boolean test(Student student) {
				return student.gpa.equals(gpa);
			}
		});
	}

	// returns a filtered view of Students with known GPAs.
	public Iterable<Student> getStudentsWithGpa() {
		return $(students).filter(studentHasGpa());
	}

	private Predicate<Student> studentHasGpa() {
		return new Predicate<Student>() {
			public boolean test(Student student) {
				return student.gpa.isKnown();
			}
		};
	}

	private Function<Student, Maybe<Double>> studentToGpa() {
		return new Function<School.Student, Maybe<Double>>() {
			public Maybe<Double> apply(Student s) {
				return s.gpa;
			}
		};
	}
}