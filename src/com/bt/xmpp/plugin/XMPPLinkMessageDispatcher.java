package com.bt.xmpp.plugin;

import java.util.Collection;

import org.dom4j.Element;
import org.jivesoftware.openfire.SessionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.session.ClientSession;
import org.xmpp.packet.Message;

import com.bt.xmpp.plugin.itslink.MessageDescriptors;

public class XMPPLinkMessageDispatcher implements IXMPPLinkMessageDispatcher {
	
	private IPadAnywhereComponent component;
	private String siteID;
	
	public XMPPLinkMessageDispatcher(IPadAnywhereComponent component, String siteID ) {
		this.component = component;
		this.siteID    = siteID;
		
	}
	
	private void dispatchXMPPMessage(Message message) {
		SessionManager sessionManager = XMPPServer.getInstance().getSessionManager();
		Collection<ClientSession> sessions = sessionManager.getSessions();

		// send out XMPP message to all active XMPP clients. XMPP client discard the message if siteID does not match. 
		for (ClientSession session : sessions) {
			message.setTo(session.getAddress());
			component.sendPacket(message);
		}
	}
	
	private Message generateITSLinkXMPPMessage() {
		Message message = new Message();
		message.setFrom(component.getComponentJID());
		
		return message;
	}
	
	@Override
	public void IncomingCall(int callID, int lineNumber, String lineName, String ddiDigits, String phantomDDIDigits) {
		Message message = generateITSLinkXMPPMessage();

		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.IncomingCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("ddiDigits", ddiDigits);
		messageNode.addAttribute("phantomDDIDigits", phantomDDIDigits);
		
		dispatchXMPPMessage(message);
	}

	@Override
	public void DDIBusy(int callID, int lineNumber, String realDDIDigits, String phantomDDIDigits, String ddiLabel) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.DDIBusy);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("realDDIDigits", realDDIDigits);
		messageNode.addAttribute("phantomDDIDigits", phantomDDIDigits);
		messageNode.addAttribute("ddiLabel", ddiLabel);
		
		dispatchXMPPMessage(message);
	}

	@Override
	public void DDIDivert(int callID, int lineNumber, String realDDIDigits, String phantomDDIDigits, int flags) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.DDIDivert);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("realDDIDigits", realDDIDigits);
		messageNode.addAttribute("phantomDDIDigits", phantomDDIDigits);
		messageNode.addAttribute("flags", String.valueOf(flags));

		dispatchXMPPMessage(message);
	}

	@Override
	public void DialedDigits(int callID, int lineNumber, String digits) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.DialedDigits);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("digits", digits);
		
		dispatchXMPPMessage(message);
	}

	@Override
	public void AnsweredCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.AnsweredCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", lineName);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void AbandonedCall(int callID, int lineNumber) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.AbandonedCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));

		dispatchXMPPMessage(message);
	}

	@Override
	public void CallMoved(int callID, int lineNumber1, int lineNumber2) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.CallMoved);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber1", String.valueOf(lineNumber1));
		messageNode.addAttribute("lineNumber2", String.valueOf(lineNumber2));

		dispatchXMPPMessage(message);
	}

	@Override
	public void CallProceeding(int callID, int lineNumber, String dialledDigits, boolean endFlag) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.CallProceeding);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("dialledDigits", dialledDigits);
		messageNode.addAttribute("endFlag", String.valueOf(endFlag));

		dispatchXMPPMessage(message);
	}

	@Override
	public void CallProgress(int callID, int lineNumber, int channelNumber, int type) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.CallProgress);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("channelNumber", String.valueOf(channelNumber));
		messageNode.addAttribute("type", String.valueOf(type));

		dispatchXMPPMessage(message);
	}

	@Override
	public void CLIDetails(int callID, int lineNumber, String cliDigits) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.CLIDetails);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("cliDigits", cliDigits);

		dispatchXMPPMessage(message);
	}

	@Override
	public void IdleCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.IdleCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ConsoleConnectedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ConsoleConnectedCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void SpeakerConnect(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int speaker) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.SpeakerConnect);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("speaker", String.valueOf(speaker));
		
		dispatchXMPPMessage(message);
	}

	@Override
	public void SpeakerDisConnect(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int speaker) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.SpeakerDisConnect);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("speaker", String.valueOf(speaker));
		
		dispatchXMPPMessage(message);
	}

	@Override
	public void HeldCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.HeldCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void NotHeldCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.NotHeldCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void NewConfCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.NewConfCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void NotConferencedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.NotConferencedCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ConsoleJoinConfCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ConsoleJoinConfCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ConsoleLeaveConfCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ConsoleLeaveConfCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ConsoleDisconnectedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ConsoleDisconnectedCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void NewConnectedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.NewConnectedCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ELCConnectedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset, int elcNumber) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ELCConnectedCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));
		messageNode.addAttribute("elcNumber", String.valueOf(elcNumber));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ELCDisconnectedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset, int elcNumber) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ELCDisconnectedCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("handset", String.valueOf(handset));
		messageNode.addAttribute("elcNumber", String.valueOf(elcNumber));

		dispatchXMPPMessage(message);
	}

	@Override
	public void PrivateCall(int callID, int lineNumber, int consoleNumber, String userID) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.PrivateCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);

		dispatchXMPPMessage(message);
	}

	@Override
	public void NonPrivateCall(int callID, int lineNumber, int consoleNumber, String userID) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.NonPrivateCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);

		dispatchXMPPMessage(message);
	}

	@Override
	public void TransferedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, String userID2) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.TransferedCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("userID2", userID2);

		dispatchXMPPMessage(message);
	}

	@Override
	public void RecallTransferCall(int callID, int lineNumber1, int lineNumber2, int statusFlag) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.RecallTransferCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber1", String.valueOf(lineNumber1));
		messageNode.addAttribute("lineNumber2", String.valueOf(lineNumber2));
		messageNode.addAttribute("statusFlag", String.valueOf(statusFlag));

		dispatchXMPPMessage(message);
	}

	@Override
	public void IntercomCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, String userID2) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.IntercomCall);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("userID2", userID2);

		dispatchXMPPMessage(message);
	}

	@Override
	public void notIncomingCall(int lineNumber) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.notIncomingCall);
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ECKeyPressed(int consoleNumber, String userID, int ecKeyGroup, int ecKeyFunction, int handset, int ecKeyState) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ECKeyPressed);
		messageNode.addAttribute("consoleNumber", String.valueOf(consoleNumber));
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("ecKeyGroup", String.valueOf(ecKeyGroup));
		messageNode.addAttribute("ecKeyFunction", String.valueOf(ecKeyFunction));
		messageNode.addAttribute("handset", String.valueOf(handset));
		messageNode.addAttribute("ecKeyState", String.valueOf(ecKeyState));

		dispatchXMPPMessage(message);
	}

	@Override
	public void TextMessage(String txt) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.TextMessage);
		messageNode.addAttribute("txt", txt);

		dispatchXMPPMessage(message);
	}

	@Override
	public void ConsoleNumber(String userID, int newConsoleNumber, int oldConsoleNumber) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ConsoleNumber);
		messageNode.addAttribute("userID", userID);
		messageNode.addAttribute("newConsoleNumber", String.valueOf(newConsoleNumber));
		messageNode.addAttribute("oldConsoleNumber", String.valueOf(oldConsoleNumber));

		dispatchXMPPMessage(message);
	}

	@Override
	public void LineInfo(int callID, int lineNumber, String lineName, char state, int SpeakerCount, int HSCount, char direction, boolean isPrivate, String ddiDigits, int lineType, int elcNumber) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.LineInfo);
		messageNode.addAttribute("callID", String.valueOf(callID));
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));
		messageNode.addAttribute("lineName", lineName);
		messageNode.addAttribute("state", String.valueOf(state));
		messageNode.addAttribute("SpeakerCount", String.valueOf(SpeakerCount));
		messageNode.addAttribute("HSCount", String.valueOf(HSCount));
		messageNode.addAttribute("direction", String.valueOf(direction));
		messageNode.addAttribute("isPrivate", String.valueOf(isPrivate));
		messageNode.addAttribute("ddiDigits", ddiDigits);
		messageNode.addAttribute("lineType", String.valueOf(lineType));
		messageNode.addAttribute("elcNumber", String.valueOf(elcNumber));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ITSLinkVersion(int major, int minor, int build) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ITSLinkVersion);
		messageNode.addAttribute("major", String.valueOf(major));
		messageNode.addAttribute("minor", String.valueOf(minor));
		messageNode.addAttribute("build", String.valueOf(build));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ITSLinkError(int code) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ITSLinkError);
		messageNode.addAttribute("code", String.valueOf(code));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ITSLinkShutDown(int code) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("linkmessage");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ITSLinkShutDown);
		messageNode.addAttribute("code", String.valueOf(code));
		
		dispatchXMPPMessage(message);
	}

	@Override
	public boolean isMakeCallPending(int consoleNumber, String userID,
			int handset) {
		// TODO Auto-generated method stub
		return false;
	}

}
