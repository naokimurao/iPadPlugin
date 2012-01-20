package com.bt.xmpp.plugin;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.event.SessionEventListener;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.util.Log;
import org.xmpp.component.Component;
import org.xmpp.component.ComponentException;
import org.xmpp.component.ComponentManager;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketError;

import org.dom4j.Element;

public class IPadAnywhereComponent implements SessionEventListener, Component  {
	
	private static final String NAME 		= "ipadanywhere";
	private static final String DESCRIPTION = "iPad Anywhere Plugin";
	
	private ComponentManager componentManager;
	private JID componentJID = null;
	
	private static Map<String, FastLinkStackHandler> linksessions;
	private static Map<String, VSCManager> vscsessions;
	
	private BonjourServiceAnnouncer bonjourServiceAnnouncer;
	private SiteDao siteDao;
    private ITSUserDao itsUserDao;
    private Collection<Site> sites;
    private Collection<ITSUser> itsUsers;

	@Override
	public void initialize(JID jid, ComponentManager componentManager) throws ComponentException {
		this.componentJID = jid;
        this.componentManager = componentManager;
        
        Log.info("iPad Anywhere Component initialize " + componentJID.toString());
        
        System.out.println("iPad Anywhere Component initialize " + componentJID.toString());
        
		try {	
			registerBonjourService();
			loadSitesAndUsers();
			linksessions    = Collections.synchronizedMap(new HashMap<String, FastLinkStackHandler>());
			vscsessions     = Collections.synchronizedMap(new HashMap<String, VSCManager>());
	        
	        for(Site site : sites) {
	        	addFastLinkStackHandler(site);
	        	addVSCManager(site);
	        }
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
	}
	
	private void registerBonjourService() {
		bonjourServiceAnnouncer = new BonjourServiceAnnouncer(XMPPServer.getInstance().getServerInfo().getXMPPDomain());
		bonjourServiceAnnouncer.registerService();
	}
	
	private void loadSitesAndUsers() {
		try {
			siteDao = new SiteDao();
			sites = siteDao.getSites();
			Log.info("Loaded All ITS Sites");
			
			itsUserDao = new ITSUserDao();
			itsUsers   = itsUserDao.getUsers();
			Log.info("Loaded All ITS Users");
		} catch (Exception e) {
			Log.error(e.getMessage());
		}
	}
	
	public void siteAdded(Site site) {
		sites.add(site);
		addFastLinkStackHandler(site);
		addVSCManager(site);
	}

	public void siteUpdated(Site site) {
		long siteID = site.getSiteID();
		
		// Site parameters in DB has updated. 
		// Should we update values in memory as well? or ask to reload the Openfire. 
		// Site update rarely happens once it is up and running,,,,  
		
	}

	public void siteRemoved(Site site) {
		removeFastLinkStackHandler(site);
		
		for(Site tmpSite : sites) {
			if(tmpSite.getSiteID() == site.getSiteID()) {
				sites.remove(tmpSite);
				break;
			}
		}
	}

	public void ITSUserAdded(ITSUser user) {
		itsUsers.add(user);
	}

	public void ITSUserRemoved(ITSUser user) {
		for(ITSUser tmpITSUser : itsUsers) {
			if(tmpITSUser.getUsername().equals(user.getUsername())) {
				itsUsers.remove(tmpITSUser);
				break;
			}
		}
	}

	public void ITSUserRemovedBySite(Collection<ITSUser> removedUsers) {
		for(ITSUser tmpITSUser : removedUsers) {
			this.ITSUserRemoved(tmpITSUser);
		}
	}

	private void addFastLinkStackHandler(Site site) {
		Properties props = new Properties();
        props.put("itslink1", site.getItslink1());
        props.put("itslink2", site.getItslink2());
        props.put("itsport1", site.getItslinkPort());
        props.put("itsport2", site.getItslinkPort());
        props.put("sourcetag", String.valueOf(site.getSiteID()));
        props.put("version", "5");
        props.put("cos", site.getItslinkCos());
        try {
			props.put("homehost", InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        props.put("homeapp","iPad Anywhere plugin");
     
        FastLinkStackHandler fastLinkStackHandler = new FastLinkStackHandler(this, site.getName(), props);
        linksessions.put(String.valueOf(site.getSiteID()), fastLinkStackHandler);
	}
	
	//how to shut down socket I/O ?? or Do we need to close ITSLink I/O?
	private void removeFastLinkStackHandler(Site site) {
		String siteID = String.valueOf(site.getSiteID());
		FastLinkStackHandler fastLinkStackHandler = linksessions.get(siteID);
		//itslinkPacketHandler.logoutFromITSLink();

		fastLinkStackHandler = null;
		linksessions.remove(siteID);
	}
	
	private void addVSCManager(Site site) {
		
		int[] ranges = {Integer.valueOf(site.getVscConsoleStartRange1()), Integer.valueOf(site.getVscConsoleStartRange2()), Integer.valueOf(site.getVscConsoleStartRange3()), Integer.valueOf(site.getVscConsoleStartRange4())};
		int[] counts = {Integer.valueOf(site.getVscConsoleCount1()), Integer.valueOf(site.getVscConsoleCount2()), Integer.valueOf(site.getVscConsoleCount3()), Integer.valueOf(site.getVscConsoleCount4())};
		VSCManager vscManager = new VSCManager();
		String strFlag = site.getVscUseHandsets34();
		if(strFlag.equalsIgnoreCase("yes"))	
			vscManager.init(true, ranges, counts);
		else
			vscManager.init(false, ranges, counts);
		
		this.vscsessions.put(String.valueOf(site.getSiteID()), vscManager);		
	}
	
	private void removeVSCManager(Site site) {
		String siteID = String.valueOf(site.getSiteID());
		vscsessions.remove(siteID);
	}

	@Override
	public void processPacket(Packet packet) {
		try {
            if(packet instanceof IQ)
                processPacket((IQ)packet);
            else if(packet instanceof Message)
                processPacket((Message)packet);
        }
        catch(Exception e) {
        	Log.error(e.toString());
        }
	}
	
	// XMPP IQ request from XMPP client(iPad)
	private void processPacket(IQ iq) {
		System.out.println(iq.toXML());
		
		Element element = iq.getChildElement();
		String namespace = element.getNamespaceURI();
		
		if(namespace.equals("http://bt.com/protocol/itslink")) {
			String reqType      = element.elementText("reqType");
			System.out.println(reqType);
        	if(reqType.equals("getSiteID")) {
        		handleSiteRequest(iq);
        	}
        	else if(reqType.equals("getVSCConsole")) {
        		String siteID = element.elementText("siteID");
        		String traderID = element.elementText("traderID");
        		String traderName = element.elementText("traderName");
        		handleVSCAllocationRequest(iq, siteID, traderID, traderName);
        	}
        }
	}

	// Received XMPP message from a XMPP client(iPad)
	private void processPacket(Message message) {
		System.out.println(message.toXML());
		Element element = message.getChildElement("itslink-event", "http://bt.com/protocol/itslink");

		if(element != null) {
			String siteID       = element.elementText("siteID");
			String reqType      = element.elementText("reqType");
			
			Element paramElement = element.element("parameters");
			//sessions.get(siteID).sendMessageToITSLink(Integer.valueOf(reqType), paramElement);
		}
	}

	private void handleSiteRequest(IQ iq) {
		Site site = getSite(iq);

		if(site != null)	{
			IQ reply = IQ.createResultIQ(iq);
			reply.setType(org.xmpp.packet.IQ.Type.result);

			Element linkNode = reply.setChildElement("siteReq-reply", "http://bt.com/protocol/itslink");	
			Element siteNode = linkNode.addElement("site");
			siteNode.addAttribute("id", String.valueOf(site.getSiteID()));
			siteNode.addAttribute("DAL", site.getDalUrl());
			//linkNode.addElement("site").addAttribute("id", String.valueOf(site.getSiteID()));
			this.sendPacket(reply);
		} else {
			IQ reply = IQ.createResultIQ(iq);
			reply.setType(org.xmpp.packet.IQ.Type.error);
			PacketError packetError = new PacketError(org.xmpp.packet.PacketError.Condition.undefined_condition, org.xmpp.packet.PacketError.Type.cancel, "Could not find a site for " + iq.getFrom().toBareJID());
			reply.setError(packetError);
			sendPacket(reply);
		}
	}
	
	private Site getSite(IQ iq) {
		JID jid = iq.getFrom();
		Site tmpSite = null;

		String user = jid.toString();
		int pos = user.indexOf("@");

		if (pos > -1) {
			user = user.substring(0, pos);
			long tmpSiteID = -1;
			boolean isFound = false;
			for(ITSUser tmpITSUser : itsUsers) {
				if(tmpITSUser.getUsername().equals(user)) {
					tmpSiteID   = tmpITSUser.getSiteID();
					isFound = true;
					break;
				}
			}
			
			if(isFound)	{
				tmpSite = siteDao.getSiteByID(tmpSiteID);
			}
		}

		return tmpSite;
	}

	
	private void handleVSCAllocationRequest(IQ iq, String siteID, String traderID, String traderName ) {
		VSCManager vscManager = vscsessions.get(siteID);
		if(vscManager.isAvailable()) {
			VSC vsc = vscManager.allocate(traderID, traderName);
			
			IQ reply = IQ.createResultIQ(iq);
			reply.setType(org.xmpp.packet.IQ.Type.result);

			Element linkNode = reply.setChildElement("vsc-reply", "http://bt.com/protocol/itslink");	
			Element vscNode = linkNode.addElement("vsc");
			vscNode.addAttribute("console", vsc.getConsoleID());
			vscNode.addAttribute("handset", vsc.getHandset());
			this.sendPacket(reply);
		}
		// All pseudo consoles are taken by other users. 
		else {
			IQ reply = IQ.createResultIQ(iq);
			reply.setType(org.xmpp.packet.IQ.Type.result);

			Element linkNode = reply.setChildElement("vsc-reply", "http://bt.com/protocol/itslink");	
			Element vscNode = linkNode.addElement("vsc");
			vscNode.addAttribute("console", "0");
			vscNode.addAttribute("handset", "0");
			this.sendPacket(reply);
		}
	}
	
	public void sendPacket(Packet packet) {
        try {
            componentManager.sendPacket(this, packet);
        }
        catch (Exception e) {
            Log.error(e);
        }
    }

	@Override
	public void shutdown() {
		if(bonjourServiceAnnouncer.isRegistered())
			bonjourServiceAnnouncer.unregisterService();
	}

	@Override
	public void start() {}
	
	public JID getComponentJID() {
        return componentJID;
    }

	
	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	
	@Override
	public void anonymousSessionCreated(Session arg0) {}

	@Override
	public void anonymousSessionDestroyed(Session arg0) {}

	@Override
	public void resourceBound(Session arg0) {}

	@Override
	public void sessionCreated(Session arg0) {}

	@Override
	public void sessionDestroyed(Session arg0) {}

}
