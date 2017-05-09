package com.ajna.riskman.md;


import java.util.Vector;
 


import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MarketDataGW {

 
	private HashMap<String, Vector<MarketDataClientInterface>> m_subscriptions;
	private HashMap<String, MarketData> m_mdMap;
	private Timer m_scheduler;

	public MarketDataGW() {
		
		m_subscriptions = new HashMap<String, Vector<MarketDataClientInterface>>();

		m_mdMap = new HashMap<String, MarketData>();
		
		//mainApp startes the timer now
		//m_scheduler = new Timer();
    	//m_scheduler.scheduleAtFixedRate(new refresherTimerTask(), 60*1000,60*1000);

	}

	/**
	 * TODO: not thread safe at the moment
	 */
	public void subscribe(String sym, MarketDataClientInterface bk) {
		
		Vector<MarketDataClientInterface> bkvect;
		System.out.println("MarketDataGW: Subscribe for " + sym);
		if (m_subscriptions.containsKey(sym)) {
			bkvect = m_subscriptions.get(sym);
		} else {
			bkvect = new Vector<MarketDataClientInterface>();
			m_subscriptions.put(sym, bkvect);
		}
		bkvect.add(bk);
		
	}


	/**
	 * TODO: not thread safe at the moment
	 */
	public void unSubscribe(String sym, MarketDataClientInterface bk) {

		Vector<MarketDataClientInterface> mdci = m_subscriptions.get(sym);

		if (mdci != null) {
			mdci.remove(bk);
		} else {

		}
		if (mdci.isEmpty()) {
			m_subscriptions.remove(sym);
		}
	}

	 
	public void unSubscribeAll() {
		//just creating a new hashmap is more effiecient?
		m_subscriptions = new HashMap<String, Vector<MarketDataClientInterface>>();
	}
	/**
	 * 
	 * @param Refresh thread to call this method
	 */
	public void refreshMarketData() {
		m_subscriptions.keySet();
		
		MarketData md;
		for (String symb : m_subscriptions.keySet()) {
			 md = m_mdMap.get(symb);
			 if (md == null) {
				 md = new MarketData(symb);
				 m_mdMap.put(symb, md);
			 }
			 refreshMarketData(md);
			 //publish updates
			 for (MarketDataClientInterface client : m_subscriptions.get(symb) ) {
				 //client.onLastTradePriceUpdate(md.getOptionLtp());
				 //client.onSpotPriceUpdate(md.getUnderlyingSpotPrice());
				 client.onMDUpdate(symb,md.getOptionLtp(),md.getUnderlyingSpotPrice());
			 }
		}
	}

	/**
	 * 
	 * @param sym
	 */
	public void refreshMarketData(MarketData md) {

		 

	}
	/**
	 * 
	 * 
	 *
	 */
	private class refresherTimerTask extends TimerTask {
		private final DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	    public void run() {
	    	Date date = new Date(); 
	    	System.out.println(sdf.format(date) + " Refresher Timer : " + Thread.currentThread().getName() );
	    	refreshMarketData();
	    }
	}
	
 
}



 

