package pl.smarttesting.moduleC;

import pl.smarttesting.moduleA.ClassA;
import pl.smarttesting.moduleB.ClassB;

/**
 * Kod wykorzystany na slajdzie do wizualizacji wzajemnej zależności modułów.
 * Klasa ClassC z modułu C korzysta z modułów A oraz B.
 */
public class ClassC {
	public final ClassA classA;
	public final ClassB classB;

	public ClassC(ClassA classA, ClassB classB) {
		this.classA = classA;
		this.classB = classB;
	}
}
