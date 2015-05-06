package cz.muni.fi;

import cz.muni.fi.jmx.JMXAgent;

/**
 * Class subscribing statement metric event
 *
 * @author Eduard Tomek
 * @since 11.2.15
 */
public class StatementMetricSubscriber {
  private JMXAgent jmxAgent = JMXAgent.getInstance();
  private StatementMetricAggregator sma = new StatementMetricAggregator();

  /**
   * Update method called by Esper engine.
   *
   * @param sm passed statement metric event
   */
  public void update(com.espertech.esper.client.metric.StatementMetric sm) {
    StatementMetric smMBean = new StatementMetric(sm);
    jmxAgent.register(smMBean);
    sma.addStatementMetric(smMBean);
  }
}

