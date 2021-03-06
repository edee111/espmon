package cz.muni.fi.espmon.testeventrepresentation.subscriber;


import java.util.Map;

/**
 * Subscriber for monitoring temperatures on servers
 *
 * @author Eduard Tomek
 * @since 27.2.15
 */
public class MonitorEventSubscriber extends AbstractSubscriber implements StatementSubscriber {
  /**
   * {@inheritDoc}
   */
  @Override
  public String getStatement() {
    return "select avg(temperature) as avg_val, serverName from TemperatureEvent.win:time_batch(5 sec) group by serverName";
  }

  /**
   * Subscriber method called when Esper has detected a pattern match.
   */
  public void update(Map<String, Object> eventMap) {

    // average temp over 10 secs
    Double avg = (Double) eventMap.get("avg_val");
    String serverName = (String) eventMap.get("serverName");

    StringBuilder sb = new StringBuilder();
    sb.append("\n---------------------------------");
    sb.append("\n- [MONITOR] Server " + serverName + " average Temp = " + avg);
    sb.append("\n---------------------------------");

    log.debug(sb.toString());
  }
}
