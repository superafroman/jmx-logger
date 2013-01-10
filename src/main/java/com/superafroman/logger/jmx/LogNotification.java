package com.superafroman.logger.jmx;

import java.util.concurrent.atomic.AtomicInteger;

import javax.management.Notification;

public class LogNotification extends Notification {

	public static final String ERROR = "ERROR";

	public static final String WARN = "WARN";

	public static final String INFO = "INFO";

	public static final String DEBUG = "DEBUG";


	private static final long serialVersionUID = 858219400079608860L;

	private static final AtomicInteger SEQUENCE = new AtomicInteger(1);


	public LogNotification(String type, Object source, String message, Object userData) {
		super(type, source, SEQUENCE.getAndAdd(1), message);
		setUserData(userData);
	}
}
