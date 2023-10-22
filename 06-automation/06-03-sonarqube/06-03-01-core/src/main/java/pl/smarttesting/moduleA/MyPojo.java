package pl.smarttesting.moduleA;

/**
 * Klasa, która jest zwykłą strukturą z akcesorami (POJO - Plain Old Java Object).
 */
public class MyPojo {
	private String name;
	private String surname;
	private int age;

	String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	String getSurname() {
		return surname;
	}

	void setSurname(String surname) {
		this.surname = surname;
	}

	int getAge() {
		return age;
	}

	void setAge(int age) {
		this.age = age;
	}
}
