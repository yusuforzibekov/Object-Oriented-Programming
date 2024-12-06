package com.epam.rd.autocode.thread;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.bcel.classfile.Method;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * @author D. Kolesnikov
 */
class TaskTest {
	
	private static final PrintStream STD_OUT = System.out;

	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

	private final PrintStream out = new PrintStream(baos);
	
	@ParameterizedTest
	@CsvSource({"3,3", "3,5", "5,3", "5,5"})
	void shouldCountersBeEqualedForEveryIterationWhenSync(int n, int k) throws InterruptedException {
		System.setOut(out);
		Task task = new Task(n, new SynchronizedWorker(k, 100, new Counter()));
		task.start();
		task.join();
		out.flush();
		String actual = baos.toString();
		System.setOut(STD_OUT);
		for (String line : actual.split(System.lineSeparator())) {
			if ("false".equals(line.substring(0, line.indexOf(' ')))) {
				Assertions.fail(() -> "Not synchronized code detected: " + line);
			}
		}
	}

	@ParameterizedTest
	@CsvSource({"7,7", "7,5", "5,7", "5,5"})
	void shouldCountersBeNotEqualedForSomeIterationWhenNotSync(int n, int k) throws InterruptedException {
		System.setOut(out);
		Task task = new Task(n, new Worker(k, 100, new Counter()));
		task.start();
		task.join();
		out.flush();
		String actual = baos.toString();
		System.setOut(STD_OUT);
		for (String line : actual.split(System.lineSeparator())) {
			if ("false".equals(line.substring(0, line.indexOf(' ')))) {
				return;
			}
		}
		Assertions.fail(() -> "Synchronized code detected:\n" + actual);
	}

	@ParameterizedTest
	@CsvSource({"2,5,10", "4,5,5", "3,7,7"})
	void shouldBeNWorkingThreadsWhenCompareNotSyncKTimes(int n, int k, int pause) throws InterruptedException {
		List<String> threadNames = new ArrayList<>();
		Task task = new Task(n, new Worker(k, pause, new Counter()));
		task.start();
		Thread detector = getThreadsDetector(threadNames);
		detector.start();
		task.join();
		detector.join();
		long threadsCount = threadNames.size();
		Assertions.assertTrue(threadsCount == n, 
				() -> "'Worker thread must use " + n + " child threads, detected threads: " + threadNames);
	}
	
	@ParameterizedTest
	@CsvSource({"2,5,15", "4,5,15", "3,7,17"})
	void houldBeNWorkingThreadsWhenCompareSyncKTimes(int n, int k, int pause) throws InterruptedException {
		List<String> threadNames = new ArrayList<>();
		Task task = new Task(n, new SynchronizedWorker(k, pause, new Counter()));
		task.start();
		Thread detector = getThreadsDetector(threadNames);
		detector.start();
		task.join();
		detector.join();
		long threadsCount = threadNames.size();
		Assertions.assertTrue(threadsCount == n, 
				() -> "'SynchronizedWorker thread must use " + n + " child threads, detected threads: " + threadNames);
	}
	
	@Test
	void appShouldUseOnlyLockAndReentranLockTypesFromJavaUtilConcurrentLocksPackage() {
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/main/java/");
		spoon.buildModel();

		java.util.List<String> types = spoon.getModel()
				.getElements(new TypeFilter<>(CtTypeReference.class))
				.stream()
				.filter(r -> r.toString().startsWith("java.util.concurrent.locks"))
				.map(CtTypeReference::getSimpleName).distinct()
				.toList();

		assertIterableEquals(
				Arrays.asList("Lock", "ReentrantLock"),
				types,
				() -> "You must use exactly two types from java.util.concurrent.locks package and subpackages: "
						+ "java.util.concurrent.locks.Lock and java.util.concurrent.locks.ReentrantLock., but found:" + types);
	}
	
	@ParameterizedTest
	@ValueSource(classes = {Task.class, Counter.class, Worker.class, SynchronizedWorker.class}) 
	void appShouldNotUseSynchronizedKeyWordForAnyPurpose(Class<?> clazz) throws ClassNotFoundException {
		org.apache.bcel.classfile.JavaClass jc = org.apache.bcel.Repository.lookupClass(clazz);
		boolean isMonitorPresent = false;
		for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
			Method m = jc.getMethod(method);
			isMonitorPresent = isMonitorPresent
					| m.getCode().toString(false).lines()
						.filter(line -> line.contains("monitorenter"))
						.findAny().isPresent()
					| m.isSynchronized();
		}
		Assertions.assertTrue(!isMonitorPresent, "You must not use the synchronized key word for synchronization");
	}

	private Thread getThreadsDetector(List<String> threadNames)  {
		return new Thread() {
			public void run() {
				setName("threads-detector");
				Thread.getAllStackTraces().keySet().stream()
					.map(t -> t.getName())
					.filter(name -> name.startsWith("Thread-"))
					.forEach(threadNames::add);
			}
		};
	}

}
