package com.bt.xmpp.plugin;

public interface IBonjourServiceAnnouncer {
	
	public void registerService();
	public void unregisterService();
	public boolean isRegistered();

}
