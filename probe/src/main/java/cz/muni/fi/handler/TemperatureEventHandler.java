package cz.muni.fi.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import cz.muni.fi.event.TemperatureEvent;
import cz.muni.fi.subscriber.StatementSubscriber;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

@Component
@Scope(value = "singleton")
@Deprecated
public class TemperatureEventHandler implements InitializingBean {

  private static Logger log = LoggerFactory.getLogger(TemperatureEventHandler.class);

  /**
   * Esper service
   */
  private EPServiceProvider epService;
  private EPStatement criticalEventStatement;
  private EPStatement warningEventStatement;
  private EPStatement monitorEventStatement;

  @Autowired
  @Qualifier("criticalEventSubscriber")
  private StatementSubscriber criticalEventSubscriber;

  @Autowired
  @Qualifier("warningEventSubscriber")
  private StatementSubscriber warningEventSubscriber;

  @Autowired
  @Qualifier("monitorEventSubscriber")
  private StatementSubscriber monitorEventSubscriber;

  /**
   * Configure Esper Statement(s).
   */
  public void initService() {

    log.debug("Initializing Servcie ..");
    Configuration config = new Configuration();
    config.addEventTypeAutoName("cz.muni.fi.event");
    epService = EPServiceProviderManager.getDefaultProvider(config);

    createCriticalTemperatureCheckExpression();
    createWarningTemperatureCheckExpression();
    createTemperatureMonitorExpression();

  }

  /**
   * EPL to check for a sudden critical rise across 4 events, where the last event is 1.5x greater
   * than the first event. This is checking for a sudden, sustained escalating rise in the
   * temperature
   */
  private void createCriticalTemperatureCheckExpression() {

    log.debug("create Critical Temperature Check Expression");
    criticalEventStatement = epService.getEPAdministrator().createEPL(criticalEventSubscriber.getStatement(), "Temperature critical statement");
    criticalEventStatement.setSubscriber(criticalEventSubscriber);
  }

  /**
   * EPL to check for 2 consecutive Temperature events over the threshold - if matched, will alert
   * listener.
   */
  private void createWarningTemperatureCheckExpression() {

    log.debug("create Warning Temperature Check Expression");
    warningEventStatement = epService.getEPAdministrator().createEPL(warningEventSubscriber.getStatement(), "Temperature warning statement");
    warningEventStatement.setSubscriber(warningEventSubscriber);
  }

  /**
   * EPL to monitor the average temperature every 10 seconds. Will call listener on every event.
   */
  private void createTemperatureMonitorExpression() {

    log.debug("create Timed Average Monitor");
    monitorEventStatement = epService.getEPAdministrator().createEPL(monitorEventSubscriber.getStatement(), "Temperature monitor statement");
    monitorEventStatement.setSubscriber(monitorEventSubscriber);
  }

  /**
   * Handle the incoming TemperatureEvent.
   */
  public void handle(TemperatureEvent event) {

    log.debug(event.toString());
    epService.getEPRuntime().sendEvent(event);

  }

  @Override
  public void afterPropertiesSet() {

    log.debug("Configuring..");
    initService();
  }
}
