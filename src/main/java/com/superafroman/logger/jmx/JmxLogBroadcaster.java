package com.superafroman.logger.jmx;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistrationException;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;


public class JmxLogBroadcaster extends NotificationBroadcasterSupport implements JmxLogBroadcasterMBean {

	private ObjectName objectName;


	public JmxLogBroadcaster() {
		super(Executors.newFixedThreadPool(10));
	}

	public void setObjectName(ObjectName objectName) {
		this.objectName = objectName;
		if (objectName != null) {
			try {
				ManagementFactory.getPlatformMBeanServer().registerMBean(this, objectName);
			} catch (InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void shutdown() {
		try {
			ManagementFactory.getPlatformMBeanServer().unregisterMBean(objectName);
		} catch (MBeanRegistrationException | InstanceNotFoundException e) {
		}
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {

		String[] types = new String[] {
			LogNotification.ERROR,
			LogNotification.WARN,
			LogNotification.INFO,
			LogNotification.DEBUG
		};

		String name = LogNotification.class.getName();

		String description = "Log notification";

		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);

		return new MBeanNotificationInfo[] { info };
	}
}
