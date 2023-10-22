package pl.smarttesting.moduleB;

import pl.smarttesting.moduleA.ClassA;
import pl.smarttesting.moduleC.ClassC;

/**
 * Kod wykorzystany na slajdzie do wizualizacji wzajemnej zależności modułów.
 * Klasa ClassB z modułu B korzysta z modułów A oraz C.
 */
public class ClassB {
	public final ClassA classA;
	public final ClassC classC;

	public ClassB(ClassA classA, ClassC classC) {
		this.classA = classA;
		this.classC = classC;
	}
}
