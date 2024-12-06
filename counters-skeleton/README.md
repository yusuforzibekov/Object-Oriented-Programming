# Counters

The purpose of this exercise is to practice applying threads synchronization based on locks.

Duration: *45 minutes*


## Description

In this exercise, you will increment the value of a counter with multiple threads - in one case, without synchronization, and in the other, with synchronization based on locks. To do this, you need to describe two thread: `Worker` and `SynchronizedWorker`.  

You are given a description of the `Counter` class, which contains two integer variables, *value1* and *value2*, and two methods. One increments the variable *value1*, and the second - *value2*.

First, please proceed to `Worker` class and implement its content:

* `public Worker(int numberOfIterations, int pause, Counter counter)`  
  Creates a thread of the `Runnable` type that receives the number of iterations, the time in milliseconds to pause between iterations, and the `Counter` object to process it  

* `public void run()`  
   Performs the following actions *numberOfIterations* times:
   -	Compares the values of the fields of the `Counter` *value1* and *value2* and prints the result, as well as the values of the fields themselves, separated by a space – to the console
   -	Increments the value of the `Counter` *value1*
   -	Pauses the execution of the thread for *pause* milliseconds
   -	Increments the value of the `Counter` *value2*


Next, please proceed to `SynchronizedWorker` class and implement its content. This thread has the same purpose and functionality as the `Worker` class. The difference is that all comparison, console output, and incrementing of the `Counter` field values are performed in a synchronized lock.  

Finally, please proceed to the `Task` class and implement its content:  
* `public Task(int numberOfThreads, Runnable worker)`  
   Initializes an object of the `Task` type that creates the number of threads specified in *numberOfThreads* based on the worker object received. The worker object can be an instance of the `Worker` or `SynchronizedWorker` type

* `public void startThreads()`  
   Starts all created threads

* `public void joinThreads()`  
   Suspend the current thread until all running threads have completed

* `public static void main(String[] args)`  
   Demonstrates the processing of objects of the `Counter` type by multiple threads with and without synchronization
 

### Details
* In the *run()* method of the `SynchronizedWorker` class, you must use an object of the `ReentrantLock` type to synchronize threads.  

* The `Task` class has the *threads* field for storing references to threads that increment the `Counter` object.
* Feel free to add the methods you need.

## Restrictions
* You may not use the keyword *synchronized* for any purpose.
* You may not change the contents of the `Counter` class.


## Example

The code in the `main()` method of the Task class is the following:  
```java
Task demo = new Task(3, new Worker(5, 100, new Counter()));
demo.start();
demo.join();
System.out.println("~~~");

demo = new Task(3, new SynchronizedWorker(5, 100, new Counter()));
demo.start();
demo.join();
```
The following is a sample output:

```
true 0 0 
true 0 0 
false 2 1 
true 2 2 
false 4 3 
false 4 3 
false 6 4 
false 6 5 
false 8 7 
false 8 7 
~~~
true 0 0 
true 1 1 
true 2 2 
true 3 3 
true 4 4 
true 5 5 
true 6 6 
true 7 7 
true 8 8 
true 9 9 
```
