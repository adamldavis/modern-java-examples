package com.adamldavis.java.example;

import static org.bitbucket.dollar.lang.ObjectUtil.equal;

import org.bitbucket.dollar.lang.Maybe;
import org.bitbucket.dollar.lang.ObjectUtil;

public class Student {
	
	public Student(String firstName, String lastName,
			StudentType studentType, Double gpa) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.studentType = studentType;
		this.gpa = Maybe.maybe(gpa);
	}
	
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