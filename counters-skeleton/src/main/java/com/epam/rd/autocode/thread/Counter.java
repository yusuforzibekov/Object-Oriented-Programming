package com.epam.rd.autocode.thread;

public class Counter {
	
	private int value1;

	private int value2;
	
	public void incrementValue1() {
		++value1;
	}

	public void incrementValue2() {
		++value2;
	}
	
	public int getValue1() {
		return value1;
	}

	public int getValue2() {
		return value2;
	}
	
}
