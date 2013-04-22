package edu.carleton.comp4104.assignment3.jms;
/**
 * Group Identities:
 * Andrew Thompson, SN: 100745521
 * Roger Cheung, SN: 100741823
 * Chopel Tsering SN:100649290
 * 
 */
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.network.NetworkConnector;

public class Broker {
	/**
	 * Starts up a broker given the following properties:
	 * @param name - Name of the broker
	 * @param address - Address string of the broker
	 * @param networkConnector - any network connectors it should use
	 * @return - Handle to the new broker service
	 * @throws Exception
	 * @author Tony White
	 */
	public static BrokerService startBroker(String name, String address, String networkConnector) throws Exception {
		BrokerService broker = new BrokerService();
		if (name != null)
			broker.setBrokerName(name);
		broker.setUseJmx(true);
		broker.addConnector(address);
		if (networkConnector != null) {
			NetworkConnector n = broker.addNetworkConnector(networkConnector);
			n.setDuplex(true);
		}
		broker.start();
		return broker;
	}
}
