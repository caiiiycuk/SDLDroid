package com.epicport;

public interface ProgressPublisher<T> {

	void publish(T... values);
	
}
