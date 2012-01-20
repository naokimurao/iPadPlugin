package com.bt.xmpp.plugin.itslink;

/**
 * @author walkerb
 *
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class MessageDescriptors {
	
    public static class Type {
		public static final int Heartbeat = 1;
		public static final int Shutdown = 2;
		public static final int Version = 3;
		public static final int Error = 4;
		public static final int BufferParams = 5;
		public static final int LogonChallenge = 6;
		public static final int TextMessage = 7;
		public static final int IncommingCall = 16;
		public static final int ConnectChange = 21;
		public static final int Private = 22;
		public static final int DialledDigits = 23;
		public static final int Intercom = 24;
		public static final int Transfer = 25;
		public static final int GroupTransfer = 26;
		public static final int CLIDetails = 27;
		public static final int LineInfo = 28;
		public static final int Abandoned = 29;
		public static final int ELC = 30;
		public static final int ECKeyPress = 31;
		public static final int CallProgress = 37;
		public static final int CallProceding = 38;
		public static final int CallMoved = 39;
		public static final int DDIBusy = 50;
		public static final int RecallTransfer = 51;
		public static final int ReAdvisal = 52;
		public static final int PlatformIntercom = 53;
		public static final int DDIDivert = 55;
		public static final int LineNumber = 64;
		public static final int ConsoleNumber = 65;
		public static final int Acknowlege = 127;

    }
    
    public static class EventType {
    	public static final String IncomingCall = "incomingcall";
		public static final String DDIBusy = "ddibusy";
		public static final String DDIDivert = "didivert";
		public static final String DialedDigits = "dialeddigits";
		public static final String AnsweredCall = "answeredcall";
		public static final String AbandonedCall = "abondonedcall";
		public static final String CallMoved = "callmoved";
		public static final String CallProceeding = "callproceeding";
		public static final String CallProgress = "callprogress";
		public static final String CLIDetails = "clidetails";
		public static final String IdleCall = "idlecall";
		public static final String ConsoleConnectedCall = "consoleconnected";
		public static final String SpeakerConnect = "speakerconnect";
		public static final String SpeakerDisConnect = "spealerdisconnec";
		public static final String HeldCall = "heldcall";
		public static final String NotHeldCall = "notheldcall";
		public static final String NewConfCall = "newconfcall";
		public static final String NotConferencedCall = "notconferencedcall";
		public static final String ConsoleJoinConfCall = "consolejoinconfcall";
		public static final String ConsoleLeaveConfCall = "consoleleaveconfcall";
		public static final String ConsoleDisconnectedCall = "consoledisconectedcall";
		public static final String NewConnectedCall = "newconnectedcall";
		public static final String ELCConnectedCall = "elcconnectedcall";
		public static final String ELCDisconnectedCall = "elcdisconnectedcall";
		public static final String PrivateCall = "privatecall";
		public static final String NonPrivateCall = "nonprivatecall";
		public static final String TransferedCall = "transferedcall";
		public static final String RecallTransferCall = "recalltransfercall";
		public static final String IntercomCall = "intercomcall";
		public static final String notIncomingCall = "notincomingcall";
		public static final String ECKeyPressed = "eckeypressed";
		public static final String TextMessage = "textmessage";
		public static final String ConsoleNumber = "consolenumber";
		public static final String LineInfo = "lineinfo";
		public static final String ITSLinkVersion = "itslinkversion";
		public static final String ITSLinkError = "itslinkerror";
		public static final String ITSLinkShutDown = "itslinkshutdown";
		public static final String isMakeCallPending = "ismakecallpending";

    }
    
    public static class Length {
        public static final int SequenceLetter = 1;
        public static final int MessageType = 3;
        public static final int MessageLength = 2;
        public static final int CallID = 10;
        public static final int LineNumber = 10;
        public static final int LineName = 20;
        public static final int ConsoleNumber = 10;
        public static final int UserID = 12;
        public static final int UserName = 24;
        public static final int HandsetNumber = 1;
        public static final int HorS = 1;
        public static final int Digit = 1;
        public static final int DigitString = 30;
        public static final int FDCNumber = 4;
        public static final int OutGroup = 1;
        public static final int CallType = 1;
        public static final int UserGroup = 3;
        public static final int KeyPageNumber = 4;
        public static final int YesNo = 1;
        public static final int SpeakerNumber = 2;
        public static final int ConnectionCount = 2;
        public static final int CLIDigits = 30;
        public static final int RealDDIDigits = 12;
        public static final int DDIDigits = 12;
        public static final int PhantomDDIDigits = 5;
        public static final int DDILabel = 20;
        public static final int LineState = 1;
        public static final int ConnectDisconnect = 1;
        public static final int Direction = 1;
        public static final int LineType = 3;
        public static final int ELCNumber = 4;
        public static final int ConsoleName = 15;
        public static final int ConsoleType = 3;
        public static final int MajorVersion = 3;
        public static final int MinorVersion = 3;
        public static final int BuildVersion = 3;
        public static final int BuildStatus = 1;
        public static final int ErrorCode = 4;
        public static final int ShutdownCode = 4;
        public static final int Text = 80;
        public static final int ChannelNumber = 2;
        public static final int ProgressFlag = 1;
        public static final int ProceedingDigits = 8;
        public static final int AcknowledgeLetter = 1;
        public static final int AcknowledgeCode = 4;
        public static final int ECKeyGroup = 3;
        public static final int ECKeyFunction = 5;
        public static final int ECKeyState = 3;
        public static final int DivertFlag = 1;



    }
}
