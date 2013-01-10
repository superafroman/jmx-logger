package com.superafroman.logger.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;


public class JmxLogAppender extends AppenderSkeleton {

	private static final String DEFAULT_RMI_REGISTRY_PORT = "3099";

	private static final String RMI_ENABLED_SYSTEM_PROPERTY = "com.sun.management.jmxremote";

	private static final String RMI_HOST_NAME_SYSTEM_PROPERTY = "java.rmi.server.hostname";

	private static final String RMI_REGISTRY_PORT_SYSTEM_PROPERTY = "com.superafroman.jmxremote.port";

	private static JMXConnectorServer CONNECTOR_SERVER = null;


	private JmxLogBroadcaster broadcaster;


	public JmxLogAppender() {
		broadcaster = new JmxLogBroadcaster();
	}

	@Override
	public void close() {
		// TODO: will shutdown even if only one app is closing.  Add some sort of count so only
		// closed when last app is shutdown.
//		synchronized (CONNECTOR_SERVER) {
//			if (CONNECTOR_SERVER.isActive()) {
//				try {
//					CONNECTOR_SERVER.stop();
//				} catch (IOException e) {
//					System.err.println("Exception stopping ConnectorServer.");
//					e.printStackTrace();
//				}
//			}
//		}
		broadcaster.shutdown();
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	public void setObjectName(String newObjectName) {
		if (newObjectName != null) {
			try {
				broadcaster.setObjectName(new ObjectName(newObjectName));
			} catch (MalformedObjectNameException e) {
				throw new RuntimeException("Exception setting objectName to '" + newObjectName + "'.", e);
			}
		}
	}

	@Override
	protected void append(LoggingEvent event) {
		String type;
		switch (event.getLevel().toInt()) {
			case Level.ERROR_INT:
				type = LogNotification.ERROR;
				break;
			case Level.WARN_INT:
				type = LogNotification.WARN;
				break;
			case Level.INFO_INT:
				type = LogNotification.INFO;
				break;
			default:
				type = LogNotification.DEBUG;
		}
		Notification notification = new LogNotification(type, getClass().getName(), layout.format(event), event);
		broadcaster.sendNotification(notification);
	}

	static {
		if (System.getProperty(RMI_ENABLED_SYSTEM_PROPERTY) != null) {

			int rmiRegistryPort = Integer.parseInt(System.getProperty(RMI_REGISTRY_PORT_SYSTEM_PROPERTY,
					DEFAULT_RMI_REGISTRY_PORT));

			int rmiServerPort = rmiRegistryPort;

			System.out.println("Configuring JMX with remote port " + rmiRegistryPort + " and server port "
					+ rmiServerPort);

			try {
				String hostname = InetAddress.getLocalHost().getHostName();
				String remoteHostName = System.getProperty(RMI_HOST_NAME_SYSTEM_PROPERTY, hostname);

				LocateRegistry.createRegistry(rmiRegistryPort);

				MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

				Map<String, Object> env = new HashMap<>();

				JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://" + hostname + ":" + rmiServerPort
						+ "/jndi/rmi://" + hostname + ":" + rmiRegistryPort + "/jmxrmi");

				JMXServiceURL remoteUrl = new JMXServiceURL("service:jmx:rmi://" + remoteHostName + ":" + rmiServerPort
						+ "/jndi/rmi://" + remoteHostName + ":" + rmiRegistryPort + "/jmxrmi");

				System.out.println("Local connection URL: " + url);
				System.out.println("Remote connection URL: " + remoteUrl);
				System.out.println("Creating RMI connector server");

				CONNECTOR_SERVER = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mBeanServer);
				CONNECTOR_SERVER.start();
			} catch (IOException e) {
				throw new RuntimeException("Exception configuring RMI registry.", e);
			}
		}
	}
}
