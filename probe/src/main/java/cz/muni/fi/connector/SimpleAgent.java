package cz.muni.fi.connector;


import org.springframework.stereotype.Component;

import javax.management.*;
import java.lang.management.ManagementFactory;

/**
 * @author Eduard Tomek
 * @since 30.10.14
 */
@Component
public class SimpleAgent {
  private MBeanServer mbs = null;

  public SimpleAgent() {

    // Get the platform MBeanServer
    mbs = ManagementFactory.getPlatformMBeanServer();

    // Unique identification of MBeans
    Hello helloBean = new Hello();
    ObjectName helloName = null;

    try {
      // Uniquely identify the MBeans and register them with the platform MBeanServer
      helloName = new ObjectName("FOO:name=HelloBean");
      mbs.registerMBean(helloBean, helloName);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public void register(CpuLoadMBean mBean, Class eventClass) {
    try {
      ObjectName name = getObjectName(eventClass);
      if (mbs.isRegistered(name)) { //todo inform other side about attribute change
        AttributeList attList = new AttributeList(2);
        attList.add(new Attribute("Timestamp", mBean.getTimestamp()));
        attList.add(new Attribute("Load", mBean.getLoad()));
        mbs.setAttributes(name, attList);
      }
      else {
        mbs.registerMBean(mBean, name);
      }
    } catch (MalformedObjectNameException e) {
      e.printStackTrace();
    } catch (NotCompliantMBeanException e) {
      e.printStackTrace();
    } catch (InstanceAlreadyExistsException e) {
      e.printStackTrace();
    } catch (MBeanRegistrationException e) {
      e.printStackTrace();
    } catch (InstanceNotFoundException e) {
      e.printStackTrace();
    } catch (ReflectionException e) {
      e.printStackTrace();
    }
  }

  private ObjectName getObjectName(Class clazz) throws MalformedObjectNameException {
    return new ObjectName(clazz.getPackage().getName() + ":name=" + clazz.getSimpleName());
  }
}