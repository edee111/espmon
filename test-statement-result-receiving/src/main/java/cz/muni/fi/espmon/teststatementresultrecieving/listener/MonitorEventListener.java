package cz.muni.fi.espmon.teststatementresultrecieving.listener;

import cz.muni.fi.espmon.teststatementresultrecieving.subscriber.MonitorEventSubscriber;

/**
 * Listener for monitoring temperature events
 *
 * @author Eduard Tomek
 * @since 9.5.15
 */
public class MonitorEventListener extends BaseListener {

  public MonitorEventListener() {
    this.baseSubscriber = new MonitorEventSubscriber();
  }
}
