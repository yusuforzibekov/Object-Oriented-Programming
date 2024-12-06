# Spam

The purpose of this exercise is to practice managing threads using methods of the `Thread` class.

Duration: _1 hour_

## Description 

In this exercise, you will model spamming in the form of output to the console of different messages at different intervals. To do this, you need to describe:
* The `Spam` class, which to creates sets of messages and intervals for sending them and sends each message in a separate thread 
* The `EnterKeyInputStream` class, which inherits from the `InputStream` class and implements a programmatic reading of pressing the `Enter` key without pressing it physically
* The `Demo` class, which starts spamming and simulates pressing the `Enter` key a few seconds after execution begins, signaling completion of the application
 

First, please proceed to the  `Spam ` class and implement its content:
* `public Spam(String[] messages, long[] intervals) `

  Accepts arrays of messages and the  intervals for sending them. Both arrays must have the same length
* `public void start()` 

  Starts as many threads as there are messages in the array received
* `public void stop()` 

  Sends interruption requests to all threads that send messages and waits for them to be completed
* `public static void send()` 

  Describes arrays of messages and the intervals for sending them, creates the `Spam` object, starts spamming, and then waits/reads for the `Enter` key to be pressed and stops spamming
* `static class Worker` 

  A class-thread of the `Runnable` type that, when created, receives one message and the interval for sending it. Its task is to print a message in a loop at a given interval until it receives a request to interrupt its work


Then, please proceed to the `EnterKeyInputStream` class and implement its content: 
* `public EnterKeyInputStream(long interval)` 
  
  Accepts a time interval, after which it reads  a newline character as if the `Enter` key were pressed
* `public int read()` 
  
  Checks the number of calls to it: If the method is called for the first time, it pauses its work for the specified interval, and then it reads  and returns the first byte of the line obtained by calling the `System.lineSeparator` method. Subsequent calls read and return the rest of the bytes without delay. At the end, __-1__ is returned

Finally, please proceed to the `Demo` class and implement its `main()` method:
* Set the `EnterKeyInputStream` stream to read from the keyboard with a delay of 2 seconds
* Run the `send()` method of the `Spam` class in a separate thread
* Wait for the spam to finish



### Details

* The `Spam` class is not a thread and should not be inherited from the `Thread` class or implement the `Runnable` / `Callable<V>` interfaces.
* The `Spam` class should have fields for storing an array of messages and intervals for sending them, as well as an array of threads, each of which sends one message in a loop.
* The constructor of the `Spam` class must throw `IllegalArgumentException` if messages or time intervals are nulls, or their length is not less than or equal to __2__. This guarantees that the time intervals are non-negative and that each message is not empty.
* When creating arrays of messages and their sending intervals in the `send()` method of the `Spam` class, set the number of elements to at least __2__. Do not set large time intervals. At least four messages should be printed to the console for each thread.
* To catch pressing the `Enter` key in the `send()` method of the `Spam` class, all you have to do is read the console input. To do this, use the `nextLine()` method of the `Scanner` class or the `readLine()` method of the `BufferedReader` class since their execution is completed once they read the `Enter` keystroke.
* The `Spam.Worker` class should have fields for storing a message and an interval for sending it.
* The `EnterKeyInputStream` class should have a field for storing a time interval and additional fields for reading the `Enter` keystroke.



### Restrictions

* You must not use __daemon__ threads for threads that send messages.  
* The message-sending threads `Spam.Worker` must be stopped before the `send()` method of the `Spam` class is completed.

## Example
Assume that in the `main() ` method of the `Demo` class:
   1.  You are setting your input stream implementation instead of the standard keyboard input stream:

```java
System.setIn(YOUR_OWN_INPUT_STREAM);
```
  2. You launched the `send()` method of the `Spam` class in a separate thread:
```java
Thread t = new Thread(() -> Spam.send());
t.start();
```
  3. You suspended the main thread until the spamming was completed, i.e., the `send()` method of the `Spam` class:
```java
t.join();
```
  4. Before completing the application, you must return to the system the standard keyboard input stream:
```java
System.setIn(CAСHED_VALUE_OF_SYSTEM_IN)
```
Console output upon reading the `Enter` key pressed after 1 second:
```java
bbbbbbb
@@@
bbbbbbb
@@@
bbbbbbb
bbbbbbb
@@@
```