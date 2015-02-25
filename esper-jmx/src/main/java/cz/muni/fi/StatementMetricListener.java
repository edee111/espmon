package cz.muni.fi;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.PropertyAccessException;
import com.espertech.esper.client.UpdateListener;
import cz.muni.fi.jmx.SimpleAgent;
import org.slf4j.LoggerFactory;

/**
 * @author Eduard Tomek
 * @since 11.2.15
 */
public class StatementMetricListener implements UpdateListener {
  protected org.slf4j.Logger log = LoggerFactory.getLogger(getClass());

  private SimpleAgent simpleAgent;
  private StatementMetricAggregator sma;

  private static StatementMetricListener instance;

  private StatementMetricListener() throws EsperJMXException {
    this.simpleAgent = SimpleAgent.getInstance();
    this.sma = new StatementMetricAggregator();
  }

  public static StatementMetricListener getInstance() throws EsperJMXException {
    if (instance == null) {
      instance = new StatementMetricListener();
    }
    return instance;
  }

  public void update(EventBean[] newEvents, EventBean[] oldEvents) {
    if (newEvents.length != 1) {
      throw new IllegalArgumentException("Unexpected number of new events: " + newEvents.length);
    }
    EventBean b = newEvents[0];

    StatementMetric sm = new StatementMetric();
    sm.setTimestamp((long) b.get("timestamp"));
    sm.setStatementName((String) b.get("statementName"));
    sm.setCpuTime((long) b.get("cpuTime"));
    sm.setWallTime((long) b.get("wallTime"));
    sm.setNumInput((long) b.get("numInput"));
    sm.setNumOutputIStream((long) b.get("numOutputIStream"));
    sm.setNumOutputRStream((long) b.get("numOutputRStream"));

    simpleAgent.register(sm, StatementMetric.class);
    sma.addNewStatementMetric(this.simpleAgent, sm);
  }
}

