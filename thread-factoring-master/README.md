# Thread Factoring 

The purpose of this task is to give you some practice using a variable atomic data type when working with multiple threads.


Duration: _45 minutes_.

## Description 

In this exercise, you will create a named thread factory – `ThreadUnion` – that will create threads, monitor the result of executing them, and allow the threads to terminate.
Before executing the task, please study the following class and interface from the Java library:  
* The [`ThreadGroup`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/ThreadGroup.html) class, which a set of threads that allows you to monitor the state of threads in a set (a group)
* The [`ThreadFactory`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/ThreadFactory.html) interface, which is used to create threads on demand  

You are given a description of the `FinishedThreadResult` class, which encapsulates the result of terminating a thread and contains three fields. The _threadName_ field is the name of the thread, the finished field is the time of the thread's termination, and the throwable field is a reference to the exception if the thread terminated due to an interrupt.  

Please proceed to the `ThreadUnion` interface and implement its methods:

* `int totalSize()`  
	Returns the total number of threads created in `ThreadUnion`

*	`int activeSize()`  
	Returns the total number of currently active threads created in `ThreadUnion`

*	`void shutdown()`  
	Interrupts all created threads and prevents creating of more threads

* `boolean isShutdown()`  
	Checks and returns _true_, if a threads shutdown was initiated

* `boolean isFinished()`  
	Checks and return _true_, if a threads shutdown was initiated in `ThreadUnion` and all of created threads were finished

* `void awaitTermination()`  
	Waits until all created threads are finished

* `List<FinishedThreadResult> results()`  
	Returns a list of results of finished threads

Add the following to the `ThreadUnionImpl` class that implements the `ThreadUnion` interface:  
* Fields for the name of the `ThreadUnion`, a list of the created threads, a list of the results of finished threads, the group of threads of the `ThreadGroup` type, a flag to initiate threads shutdown, and the number of threads created
* A constructor that gets a name for `ThreadUnion` and the thread group
* `Thread newThread(Runnable runnable)`, a method that creates and registers a new thread, including it in the group and the list of created threads

### Details

* The `ThreadUnion` interface has a static `newInstance()` method for creating an instance of a class that implements this interface. The method gets the name of the thread factory it creates.
* The `results()` method must not return results for threads that are not finished yet.
* When a new thread is created using the `newThread()` method, its name is created in the format "ThreadUnion_name-worker-Thread_number" – for example, "task-worker-0" where task is the name of the thread group and 0 is the number of the thread.
* The `java.util.concurrent.atomic.AtomicInteger` object is used to obtain the number of the next thread.

If necessary, please refer to test classes for clarification on this task.


## Example

Next code:

~~~java
ThreadUnion threadUnion = ThreadUnion.newInstance("thread");

System.out.println(threadUnion.totalSize()); // 0
System.out.println(threadUnion.activeSize()); // 0

List<Thread> threads = new ArrayList<>();
for (int j = 0; j < 4; j++) {
	threads.add(threadUnion.newThread(new Runnable() {
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName());
		}
	}));
}

System.out.println(threadUnion.totalSize()); // 4
System.out.println(threadUnion.activeSize()); // 0

threads.forEach(Thread::start);
for (Thread t : threads) {
	t.join();
}

System.out.println(threadUnion.totalSize()); // 4
System.out.println(threadUnion.activeSize()); // 0
~~~

produces the following output:

~~~
0
0
4
0
thread-worker-1
thread-worker-2
thread-worker-0
thread-worker-3
4
0
~~~
