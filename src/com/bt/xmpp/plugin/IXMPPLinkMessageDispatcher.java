package com.bt.xmpp.plugin;

public interface IXMPPLinkMessageDispatcher {
	
	public void IncomingCall(int callID,int lineNumber, String lineName, String ddiDigits, String phantomDDIDigits);
	public void DDIBusy(int callID, int lineNumber, String realDDIDigits , String phantomDDIDigits, String ddiLabel);
	/**
	 * The DDIDivertMessage from ITSlink is a little unsure of it's place in life!
	 * For Immediate this is a Set Forward message - it reports when the divert is activated or de-activated
	 * For No Reply this a divert message - it reports when a call is diverted due to the Divert no reply settings
	 */
	public void DDIDivert(int callID, int lineNumber, String realDDIDigits, String phantomDDIDigits, int flags);
	public void DialedDigits(int callID, int lineNumber, String digits);
	public void AnsweredCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset);
	public void AbandonedCall(int callID, int lineNumber);
	public void CallMoved(int callID, int lineNumber1, int lineNumber2);
	public void CallProceeding(int callID, int lineNumber, String dialledDigits, boolean endFlag);
	public void CallProgress(int callID, int lineNumber, int channelNumber, int type);
	public void CLIDetails(int callID, int lineNumber, String cliDigits);
	public void IdleCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset);
	public void ConsoleConnectedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset);
	public void SpeakerConnect(int callID, int lineNumber, String lineName, int consoleNumber,String userID, int speaker);
	public void SpeakerDisConnect(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int speaker);
	public void HeldCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset);
	public void NotHeldCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset);
	public void NewConfCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset);
	public void NotConferencedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset);
	public void ConsoleJoinConfCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset);
	public void ConsoleLeaveConfCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset);
	public void ConsoleDisconnectedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset);
	public void NewConnectedCall(int callID, int lineNumber, String lineName, int consoleNumber,String userID, int handset);
	public void ELCConnectedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset, int elcNumber);
	public void ELCDisconnectedCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, int handset, int elcNumber);
	public void PrivateCall(int callID, int lineNumber, int consoleNumber, String userID);
	public void NonPrivateCall(int callID, int lineNumber, int consoleNumber, String userID);
	public void TransferedCall(int callID, int lineNumber, String lineName, int consoleNumber,String userID, String userID2);
	public void RecallTransferCall(int callID, int lineNumber1, int lineNumber2, int statusFlag);
	public void IntercomCall(int callID, int lineNumber, String lineName, int consoleNumber, String userID, String userID2);

	/** Callback after a CLIDetails request if the requested line is not incoming - may be outgoing or idle */
	public void notIncomingCall(int lineNumber);
	
	public void ECKeyPressed(int consoleNumber , String userID , int ecKeyGroup , int ecKeyFunction , int handset, int ecKeyState);
	public void TextMessage(String txt);

	public void ConsoleNumber(String userID, int newConsoleNumber, int oldConsoleNumber);
	public void LineInfo(int callID, int lineNumber, String lineName, char state, int SpeakerCount, int HSCount, char direction, boolean isPrivate, String ddiDigits, int lineType, int elcNumber);
	
	public void ITSLinkVersion(int major,int minor,int build );
	public void ITSLinkError(int code);
	public void ITSLinkShutDown(int code);
	
	/**
	 * Listener must return if it pending a make call request on the device identified by the parameters
	 * @param consoleNumber
	 * @param userID TODO
	 * @param handset
	 * 
	 * @return
	 */
	public boolean isMakeCallPending(int consoleNumber, String userID, int handset);

}
