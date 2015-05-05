package cz.muni.fi.jmx;



import cz.muni.fi.EsperJMXException;
import cz.muni.fi.MBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eduard Tomek
 * @since 30.10.14
 */
public class SimpleJMXAgent {
  protected Logger log = LoggerFactory.getLogger(getClass());

  private MBeanServer mbs = null;
  JMXConnectorServer cs = null;
  private Map<String, MBean> registeredMBeans = new HashMap<>();
  private static final String DEFAULT_MBEAN_SERVER_PORT = "9999";
  private static final String[] KEYS = {
      "com.sun.management.jmxremote.",
      "com.sun.management.jmxremote.ssl",
      "com.sun.management.jmxremote.authenticate",
      "com.sun.management.jmxremote.port",
      "com.sun.management.jmxremote.local.only",
  };
  private static final String[] KEYS_VALUE_DEFAULTS = {"", "false", "false", DEFAULT_MBEAN_SERVER_PORT, "false"};

  private static SimpleJMXAgent instance;

  public static SimpleJMXAgent getInstance() throws EsperJMXException {
    if (instance == null) {
      try {
        instance = new SimpleJMXAgent();
      } catch (IOException e) {
        throw new EsperJMXException("Cannot create JMX agent", e);
      }
    }
    return instance;
  }

  private SimpleJMXAgent() throws IOException {
    this.mbs = ManagementFactory.getPlatformMBeanServer();
    loadJMXAgent(Integer.valueOf(DEFAULT_MBEAN_SERVER_PORT), mbs);
  }

  /**
   * Sets jmx remote related attributes and creates connection. If attribute is already set by VM arguments
   *    doesn't changes it's value.
   *
   * @param port mBean server required port
   * @param mbs mBean server
   * @throws IOException
   */
  public void loadJMXAgent(int port, MBeanServer mbs) throws IOException {
    LocateRegistry.createRegistry(port);
    Map<String, Object> env = new HashMap<>();
    log.info("Initializing the environment map");
    for (int i = 0; i < KEYS.length; i++) {
      if (System.getProperty(KEYS[i]) == null) {
        env.put(KEYS[i], KEYS_VALUE_DEFAULTS[i]);
      }
    }

    log.info("Creating an RMI connector server");
    JMXServiceURL url =
        new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi");
    cs = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbs);
    log.info("Start the RMI connector server");
    cs.start();
  }

  public synchronized void register(MBean mBean) {
    try {
      ObjectName name = getObjectName(mBean.getClass());
      if (mbs.isRegistered(name)) {
        MBean registeredMBean = registeredMBeans.get(mBean.getClass().getName());
        registeredMBean.update(mBean);
      }
      else {
        mbs.registerMBean(mBean, name);
        registeredMBeans.put(mBean.getClass().getName(), mBean);
      }
    } catch (MalformedObjectNameException | NotCompliantMBeanException
            | InstanceAlreadyExistsException | MBeanRegistrationException e) {
      log.error("Cannot register mBean.", e);
    }
  }

  private ObjectName getObjectName(Class clazz) throws MalformedObjectNameException {
    return new ObjectName(clazz.getPackage().getName() + ":type=" + clazz.getSimpleName());
  }

  public void stop() throws EsperJMXException {
    try {
      cs.stop();
    } catch (IOException e) {
      throw new EsperJMXException("Cannot stop connection server", e);
    }
  }
}