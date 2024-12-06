package com.epam.rd.autocode.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;


/**
 * @author D. Kolesnikov
 */
public class MatrixTest {
	
	///////////////////////////////////////////////////////////////////////////

	private static boolean isAllTestsMustFailed;

	private static Throwable complianceTestFailedCause;

	static {
		try {
			String testClassName = new Exception().getStackTrace()[0].getClassName();
			String className = testClassName.substring(0, testClassName.lastIndexOf("Test"));
			Class<?> c = Class.forName(className);

			java.lang.reflect.Method[] methods = { 
					c.getDeclaredMethod("oneThreadSearch", int[][].class, int.class),
					c.getDeclaredMethod("multipleThreadSearch", int[][].class, int.class)
				};

			org.apache.bcel.classfile.JavaClass jc = org.apache.bcel.Repository.lookupClass(c);
			for (java.lang.reflect.Method method : methods) {
				org.apache.bcel.classfile.Method m = jc.getMethod(method);
				org.apache.bcel.classfile.Code code = m.getCode();
				org.junit.jupiter.api.Assertions.assertTrue(code.getCode().length > 2,
						() -> m + " is not a stub");
			}
		} catch (Throwable t) {
			isAllTestsMustFailed = true;
			complianceTestFailedCause = t;
			t.printStackTrace();
		}
	}

	{
		if (isAllTestsMustFailed) {
			org.junit.jupiter.api.Assertions.fail(
					() -> "Compliance test failed: " + complianceTestFailedCause.getMessage());
		}
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	@ParameterizedTest
	@CsvSource({"4,10,1", "5,4,1", "3,7,1"})
	void shouldBeMaxValuesAreTheSame(int m, int n, int pause) throws Exception {
		int[][] ar = Matrix.matrixGenerator(m, n);

		String res1 = Matrix.oneThreadSearch(ar, pause);
		String res2 = Matrix.multipleThreadSearch(ar, pause);
		
		int max1 = Integer.parseInt(res1.split(" ")[0]);
		int max2 = Integer.parseInt(res2.split(" ")[0]);
		
		Assertions.assertEquals(max1, max2);
	}

	@ParameterizedTest
	@CsvSource({"9,15,3", "8,16,3", "7,14,3"})
	void shouldBeTimeValuesAreDifferMoreThenAtXTimes(int m, int n, int pause) throws Exception {
		int[][] ar = Matrix.matrixGenerator(m, n);

		String res1 = Matrix.oneThreadSearch(ar, pause);
		String res2 = Matrix.multipleThreadSearch(ar, pause);
		
		int time1 = Integer.parseInt(res1.split(" ")[1]);
		int time2 = Integer.parseInt(res2.split(" ")[1]);
		
		double x = m * 0.5;
		Assertions.assertTrue(time1 > x * time2);
	}

	@ParameterizedTest
	@CsvSource({"4,10,5", "5,4,10", "3,7,7"})
	void shouldBeAtLeastMThreadsWhenMultipleThreadSearchIsExecuted(int m, int n, int pause) throws Exception {
		List<String> threadNames = new ArrayList<>();
		int[][] ar = Matrix.matrixGenerator(m, n);
		Thread t = startThreadsDetector(threadNames, pause, "pool-");

		Matrix.multipleThreadSearch(ar, pause);

		t.join();
		long threadsCount = threadNames.size();
		Assertions.assertTrue(threadsCount >= m,
				() -> "'multipleThreadSearch' must use at least " + m + " threads, detected threads are: " + threadNames);
	}

	@ParameterizedTest
	@CsvSource({"4,10,5", "5,4,10", "3,7,7"})
	void shouldBeNoMoreThanOneThreadWhenSingleThreadSearchIsExecuted(int m, int n, int pause) throws Exception {
		List<String> threadNames = new ArrayList<>();
		int[][] ar = Matrix.matrixGenerator(m, n);
		Thread t = startThreadsDetector(threadNames, pause, "Thread-");
		
		Matrix.oneThreadSearch(ar, pause);

		t.join();
		long threadsCount = threadNames.size();
		Assertions.assertTrue(threadsCount <= 1,
				() -> "'oneThreadSearch' must use no more than one thread with name 'Thead-X', detected threads are: " + threadNames);
	}

	private Thread startThreadsDetector(List<String> threadNames, int delay, String namePrefix)  {
		Thread t = new Thread() {
			public void run() {
				setName("threads-detector");
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Thread.getAllStackTraces().keySet().stream()
					.map(t -> t.getName())
					.filter(name -> name.startsWith(namePrefix))
					.forEach(threadNames::add);
			}
		};
		t.start();
		return t;
	}
	
	@Test
	void appShouldUseRequiredTypes() {
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/main/java/");
		spoon.buildModel();

		List<String> usedNames = spoon.getModel()
			.getElements(new TypeFilter<>(CtTypeReference.class))
			.stream()
			.filter(r -> r.toString().startsWith("java.util.concurrent"))
			.map(CtTypeReference::getQualifiedName)
			.distinct()
			.collect(Collectors.toList());
		
		Arrays.asList("ExecutorService", "Callable", "Future")
			.stream()
			.map("java.util.concurrent"::concat)
			.allMatch(usedNames::contains);
	}
	
}
