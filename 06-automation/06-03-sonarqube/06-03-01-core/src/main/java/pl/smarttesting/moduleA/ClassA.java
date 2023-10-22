package pl.smarttesting.moduleA;

import pl.smarttesting.moduleB.ClassB;
import pl.smarttesting.moduleC.ClassC;

/**
 * Kod wykorzystany na slajdzie do wizualizacji wzajemnej zależności modułów.
 * Klasa ClassA z modułu A korzysta z modułów B oraz C.
 */
public class ClassA {
	public final ClassB classB;
	public final ClassC classC;

	public ClassA(ClassB classB, ClassC classC) {
		this.classB = classB;
		this.classC = classC;
	}
}
