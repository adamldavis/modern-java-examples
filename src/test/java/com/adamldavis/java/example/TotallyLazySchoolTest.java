package com.adamldavis.java.example;

import static com.googlecode.totallylazy.Option.some;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import java.util.Iterator;

import org.bitbucket.dollar.lang.Maybe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TotallyLazySchoolTest {

	TotallyLazySchool school;

	@Before
	public void setupSchool() throws MissingDataException {
		school = new TotallyLazySchool();
		school.addStudent("John", "Doe", "PREMED");
		school.addStudent("Jane", "Doe", StudentType.PRELAW, 4.0);
		school.addStudent("Ruff", "Grade", StudentType.LIBERAL_ARTS, 2.2);
		school.addStudent("Bob", "Wittier", StudentType.LIBERAL_ARTS, 3.5);
		school.addStudent("Rob", "Smart", StudentType.LIBERAL_ARTS, 4.0);
		school.addStudent("Matt", "Avera", StudentType.LIBERAL_ARTS, 3.0);
		school.addStudent("Vera", "Blank", StudentType.SCIENCE, 3.2);
	}

	@After
	public void nullifyAllTheThings() {
		school = null;
	}

	@Test(expected = MissingDataException.class)
	public void shouldThrowMissingDataExceptionOnName() throws MissingDataException {
		school.addStudent(null, "doe", "PREMED");
	}

	@Test(expected = MissingDataException.class)
	public void shouldThrowMissingDataExceptionOnLastName() throws MissingDataException {
		school.addStudent("bob", null, "PREMED");
	}

	@Test(expected = MissingDataException.class)
	public void shouldThrowMissingDataExceptionOnType() throws MissingDataException {
		school.addStudent("bob", "foo", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionOnWrongType() throws Exception {
		school.addStudent("bar", "foo", "FOOBAR");
	}

	@Test
	public void testFindStudent() {
		assertThat(school.findStudent("John", "Doe").isDefined(), is(true));
	}

	@Test
	public void testRemoveStudent() {
		assumeThat(school.findStudent("John", "Doe").isDefined(), is(true));
		school.removeStudent("John", "Doe");
		assertThat(school.findStudent("John", "Doe").isDefined(), is(false));
	}

	@Test
	public void testUpdateStudentGpa() throws MissingDataException {
		school.updateStudentGpa("Jane", "Doe", 3.8);
		assertThat(
				school.findStudent("Jane", "Doe").get().gpa.otherwise(0d),
				equalTo(3.8));
	}

	@Test
	public void testGetHighestGPA() {
		assertThat(school.getHighestGPA(), equalTo(some(4.0)));
	}

	@Test
	public void testGetLowestGPA() {
		assertThat(school.getLowestGPA(), equalTo(some(2.2)));
	}

	@Test
	public void testGetAverageGPA() {
		assertThat(school.getAverageGPA(), equalTo(some(3.31667)));
	}

	@Test
	public void testGetStudentsWithNoGPA() {
		Iterable<Student> students = school.getStudentsWithNoGPA();
		Iterator<Student> iterator = students.iterator();
		assumeThat(iterator, notNullValue());
		assertThat(iterator.next(), equalTo(new Student("John", "Doe",
				StudentType.PREMED, Maybe.theAbsenceOfA(Double.class))));
	}
	
	@Test
	public void shouldGet2Students() {
		int count = 0;
		for (Student s :school.getStudentsWithHighestGPA()) {
			count++;
			System.out.println(s.firstName);
		}
		assertThat(count, is(2));
	}

}
