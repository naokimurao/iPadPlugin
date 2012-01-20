package com.bt.xmpp.plugin;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDException;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.TXTRecord;

import org.jivesoftware.util.Log;

public class BonjourServiceAnnouncer implements IBonjourServiceAnnouncer, RegisterListener {

	private DNSSDRegistration serviceRecord;
	private boolean registered;
	private String xmppDomain;
	
	
	public BonjourServiceAnnouncer(String xmppDomain) {
		this.xmppDomain = xmppDomain;
	}
	
	@Override
	public void operationFailed(DNSSDService arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void serviceRegistered(DNSSDRegistration registration, int flags,String serviceName, String regType, String domain){
		registered = true;
	}

	@Override
	public void registerService() {
		try {
			TXTRecord txtRecord = new TXTRecord(  );
		    txtRecord.set("xmpphost", xmppDomain);
		    //txtRecord.set("DAL", "192.168.1.6");
			serviceRecord = DNSSD.register(0,0,"BT_iPAD_Turret_Service","_ipadturret._tcp", null,null,1234,txtRecord,this);
		} catch (DNSSDException e) {
			Log.error("Failed to register Bonjour service " + e.toString());
		} 
	}

	@Override
	public void unregisterService() {
		serviceRecord.stop();
        registered = false;
	}

	@Override
	public boolean isRegistered() {
		return registered;
	}

}
