package com.bt.xmpp.plugin;

import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.bt.tmb.fastlinkstack.IDispatchFunctor;
import com.bt.tmb.fastlinkstack.LinkConfigException;
import com.bt.tmb.fastlinkstack.SiteManager;
import com.bt.tmb.fastlinkstack.StructuredMessage;
import com.bt.xmpp.plugin.itslink.DecodeUtilities;
import com.bt.xmpp.plugin.itslink.MessageDescriptors;

import org.jivesoftware.util.Log;

public class FastLinkStackHandler implements IDispatchFunctor {
	
	private String siteName;
	private String siteID;
	private SiteManager siteMgr;
	private Properties props;
	private IPadAnywhereComponent component;
	private XMPPLinkMessageDispatcher xmppLinkMessageDispatcher;
	
	private DecodeUtilities decodeUtilities = DecodeUtilities.getInstance();
	private Map lineMap  = Collections.synchronizedMap(new HashMap());
    private Set elcLines = Collections.synchronizedSet(new HashSet());
	
	public FastLinkStackHandler(IPadAnywhereComponent component, String siteName, Properties props) {
		Log.info("Initializing FastLinkStackHandler for " + siteName);
		this.component = component;
		this.props = props;
		
		siteID = props.getProperty("sourcetag");
		siteMgr = new SiteManager(props, this);
		try {
			if(!siteMgr.connectSite())
				Log.warn(siteName + " failed to start.");
		} catch (LinkConfigException e) {
			e.printStackTrace();
		}
		
		xmppLinkMessageDispatcher = new XMPPLinkMessageDispatcher(component, siteID);
	}
	

	@Override
	public void processDispatch(String linkMsg, StructuredMessage smsg) {
		
		try {
			interpretLinkMessage(smsg.m_id, smsg.m_body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void interpretLinkMessage(int m_id, String m_body) throws Exception {
		
		int callID;
	    int lineNumber;
	    String lineName;
	    int consoleNumber;
	    String userID;
	    int handset;
	    String ddiDigits;
	    String phantomDDIDigits;
	    char YesNo;
	    String digits;
	    String userID2;
	    String cliDigits;
	    char state;
	    int speakerCount;
	    int hsCount;
	    char direction;
	    char isPrivate;
	    int lineType;
	    int elcNumber;
	    char connDisConn;
	    int ecKeyGroup;
	    int ecKeyFunction;
	    int ecKeyState;
	    int channelNumber;
	    int type;
	    String dialledDigits;
	    char endFlag;
	    int lineNumber1;
	    int lineNumber2;
	    String realDDIDigits;
	    String ddiLabel;
	    int dummy;
	    int statusFlag;
	    int newConsoleNumber;
	    int oldConsoleNumber;
	    int flags;
	    
	    StringReader msgReader = new StringReader(m_body);
	    
		switch(m_id) {
			case MessageDescriptors.Type.IncommingCall:
				callID     = decodeUtilities.readCallID(msgReader);
				lineNumber = decodeUtilities.readInt(MessageDescriptors.Length.LineNumber,msgReader);
				lineName   = decodeUtilities.readString(MessageDescriptors.Length.LineName,msgReader);
				ddiDigits  = decodeUtilities.readDDIDigits(msgReader);
				phantomDDIDigits = decodeUtilities.readPhantomDDIDigits(msgReader);
				
				lineMap.put(new Integer(lineNumber),lineName);
				xmppLinkMessageDispatcher.IncomingCall(callID, lineNumber, lineName, ddiDigits, phantomDDIDigits);
				break;
				
			case MessageDescriptors.Type.ConnectChange:
				processConnectChange(m_body);
				break;
				
			case MessageDescriptors.Type.Private:
				callID     = decodeUtilities.readCallID(msgReader);
				lineNumber = decodeUtilities.readInt(MessageDescriptors.Length.LineNumber,msgReader);
				consoleNumber = decodeUtilities.readConsoleNumber(msgReader);
				userID = Integer.toString(decodeUtilities.readUserID(msgReader));
				handset = decodeUtilities.readInt(MessageDescriptors.Length.HandsetNumber,msgReader);
				YesNo = decodeUtilities.readString(MessageDescriptors.Length.YesNo,msgReader).charAt(0);
				
				switch (YesNo) {
		    		case 'Y' : 
		    			xmppLinkMessageDispatcher.PrivateCall(callID, lineNumber, consoleNumber, userID);
		    		    break;
		    
		    		case 'N' : 
		    			xmppLinkMessageDispatcher.NonPrivateCall(callID, lineNumber, consoleNumber, userID);
		    		    break;
		    		
		    		default:
		    		    break;
		    	}
				break;
				
			case MessageDescriptors.Type.DialledDigits:
				callID = decodeUtilities.readCallID(msgReader);
			    lineNumber = decodeUtilities.readLineNumber(msgReader);
				digits = decodeUtilities.readString(MessageDescriptors.Length.DigitString,msgReader);
				xmppLinkMessageDispatcher.DialedDigits(callID, lineNumber, digits);
				break;
				
			case MessageDescriptors.Type.Intercom:
				callID = decodeUtilities.readCallID(msgReader);
			    lineNumber = decodeUtilities.readLineNumber(msgReader);
			    consoleNumber = decodeUtilities.readConsoleNumber(msgReader);
			    userID = Integer.toString(decodeUtilities.readUserID(msgReader));
			    userID2 = Integer.toString(decodeUtilities.readUserID(msgReader));
			    lineName = (String)lineMap.get(new Integer(lineNumber));			    
			    xmppLinkMessageDispatcher.IntercomCall(callID, lineNumber, lineName, consoleNumber, userID, userID2);
				break;
				
			case MessageDescriptors.Type.Transfer:
				callID = decodeUtilities.readCallID(msgReader);
			    lineNumber = decodeUtilities.readLineNumber(msgReader);
		        consoleNumber = decodeUtilities.readConsoleNumber(msgReader);
		        userID = Integer.toString(decodeUtilities.readUserID(msgReader));
		        userID2 = Integer.toString(decodeUtilities.readUserID(msgReader));
		        xmppLinkMessageDispatcher.TransferedCall(callID, lineNumber, "", consoleNumber, userID, userID2);
				break;
		
			case MessageDescriptors.Type.CLIDetails:
				callID = decodeUtilities.readCallID(msgReader);
		        lineNumber = decodeUtilities.readLineNumber(msgReader);
		        cliDigits = decodeUtilities.readString(MessageDescriptors.Length.CLIDigits,msgReader);
		        xmppLinkMessageDispatcher.CLIDetails(callID, lineNumber, cliDigits);
				break;
				
			case MessageDescriptors.Type.LineInfo:
				callID = decodeUtilities.readCallID(msgReader);
				lineNumber = decodeUtilities.readLineNumber(msgReader);
				lineName = decodeUtilities.readLineName(msgReader);
				state = decodeUtilities.readChar(MessageDescriptors.Length.LineState,msgReader);
				speakerCount = decodeUtilities.readInt(MessageDescriptors.Length.ConnectionCount,msgReader);
				hsCount = decodeUtilities.readInt(MessageDescriptors.Length.ConnectionCount,msgReader);
				direction = decodeUtilities.readChar(MessageDescriptors.Length.Direction,msgReader);
				isPrivate =decodeUtilities.readChar(MessageDescriptors.Length.YesNo,msgReader);
				ddiDigits = decodeUtilities.readDDIDigits(msgReader);
				lineType = decodeUtilities.readInt(MessageDescriptors.Length.LineType,msgReader);
				elcNumber = decodeUtilities.readInt(MessageDescriptors.Length.ELCNumber,msgReader);
				xmppLinkMessageDispatcher.LineInfo(callID, lineNumber, lineName, state, speakerCount, hsCount, direction, isPrivate=='Y', ddiDigits, lineType, elcNumber);
				
				break;
			
			case MessageDescriptors.Type.Abandoned:
				callID = decodeUtilities.readCallID(msgReader);
			    lineNumber = decodeUtilities.readLineNumber(msgReader);
			    xmppLinkMessageDispatcher.AbandonedCall(callID, lineNumber);
				
				break;
				
			case MessageDescriptors.Type.ELC:
				callID = decodeUtilities.readCallID(msgReader);
		        lineNumber = decodeUtilities.readLineNumber(msgReader);
		        consoleNumber = decodeUtilities.readConsoleNumber(msgReader);
		        userID = Integer.toString(decodeUtilities.readUserID(msgReader));
		        handset = decodeUtilities.readInt(MessageDescriptors.Length.HandsetNumber,msgReader);
		        elcNumber = decodeUtilities.readInt(MessageDescriptors.Length.ELCNumber,msgReader);
		        connDisConn = decodeUtilities.readChar(MessageDescriptors.Length.ConnectDisconnect,msgReader);

		        lineName = (String)lineMap.get(new Integer(lineNumber));
		        
		        if (connDisConn == 'C') {
		        	elcLines.add(new Integer(lineNumber));
		        	xmppLinkMessageDispatcher.ELCConnectedCall(callID, lineNumber, lineName, consoleNumber, userID, handset, elcNumber);
		        }
		        else if (connDisConn == 'D') {
		        	elcLines.remove(new Integer(lineNumber));
		        	xmppLinkMessageDispatcher.ELCDisconnectedCall(callID, lineNumber, lineName, consoleNumber, userID, handset, elcNumber);
		        }
				
				break;
				
			case MessageDescriptors.Type.ECKeyPress:
				consoleNumber = decodeUtilities.readConsoleNumber(msgReader);
		        userID = Integer.toString( decodeUtilities.readUserID(msgReader));
		        ecKeyGroup =  decodeUtilities.readInt(MessageDescriptors.Length.ECKeyGroup,msgReader);
		        ecKeyFunction =  decodeUtilities.readChar(MessageDescriptors.Length.ECKeyFunction,msgReader);
		 	    handset =  decodeUtilities.readInt(MessageDescriptors.Length.HandsetNumber,msgReader);
		 	    ecKeyState =   decodeUtilities.readInt(MessageDescriptors.Length.ECKeyState,msgReader);
		 	    xmppLinkMessageDispatcher.ECKeyPressed(consoleNumber, userID, ecKeyGroup, ecKeyFunction, handset, ecKeyState);
				
				break;
				
			case MessageDescriptors.Type.CallProgress:
				callID = decodeUtilities.readCallID(msgReader);
				lineNumber = decodeUtilities.readLineNumber(msgReader);
				channelNumber = decodeUtilities.readInt(MessageDescriptors.Length.ChannelNumber,msgReader);
				type = decodeUtilities.readInt(MessageDescriptors.Length.ProgressFlag,msgReader);
				xmppLinkMessageDispatcher.CallProgress(callID, lineNumber, channelNumber, type);
				
				break;
				
			case MessageDescriptors.Type.CallProceding:
				callID = decodeUtilities.readCallID(msgReader);
		        lineNumber = decodeUtilities.readLineNumber(msgReader);
		        dialledDigits = decodeUtilities.readString(MessageDescriptors.Length.ProceedingDigits,msgReader);
		        endFlag = decodeUtilities.readChar(MessageDescriptors.Length.YesNo ,msgReader);
		        xmppLinkMessageDispatcher.CallProceeding(callID, lineNumber, dialledDigits, endFlag=='Y');
				
				break;
				
			case MessageDescriptors.Type.CallMoved:
				callID = decodeUtilities.readCallID(msgReader);
		        lineNumber1 = decodeUtilities.readLineNumber(msgReader);
		        lineNumber2 = decodeUtilities.readLineNumber(msgReader);
		        xmppLinkMessageDispatcher.CallMoved(callID, lineNumber1, lineNumber2);
				
				break;
				
			case MessageDescriptors.Type.DDIBusy:
				callID = decodeUtilities.readCallID(msgReader);
				lineNumber = decodeUtilities.readLineNumber(msgReader);
				realDDIDigits = decodeUtilities.readRealDDIDigits(msgReader);
				phantomDDIDigits = decodeUtilities.readPhantomDDIDigits(msgReader);
				ddiLabel = decodeUtilities.readString(MessageDescriptors.Length.DDILabel,msgReader);	
				xmppLinkMessageDispatcher.DDIBusy(callID, lineNumber, realDDIDigits, phantomDDIDigits, ddiLabel);
				
				break;
				
			case MessageDescriptors.Type.RecallTransfer:
				callID = decodeUtilities.readCallID(msgReader);
				lineNumber1 = decodeUtilities.readLineNumber(msgReader); // ITS Reference line number
				dummy = decodeUtilities.readLineNumber(msgReader); // Held party line number
				dummy = decodeUtilities.readInt(4,msgReader); // Held party block number
				dummy = decodeUtilities.readInt(2,msgReader); // Held party channel
				lineNumber2 = decodeUtilities.readLineNumber(msgReader); // Consult line number
				dummy = decodeUtilities.readInt(4,msgReader); // Consult block number
				dummy = decodeUtilities.readInt(2,msgReader); // Consult channel
				statusFlag = decodeUtilities.readInt(1,msgReader);
				xmppLinkMessageDispatcher.RecallTransferCall(callID, lineNumber1, lineNumber2, statusFlag);
				
				break;
				
			case MessageDescriptors.Type.ReAdvisal:
				//do we need this msg type?
				break;
				
			case MessageDescriptors.Type.PlatformIntercom:
				callID = decodeUtilities.readCallID(msgReader);
				lineNumber = decodeUtilities.readLineNumber(msgReader);
				consoleNumber = decodeUtilities.readConsoleNumber(msgReader);
				userID = Integer.toString(decodeUtilities.readUserID(msgReader));
				userID2 = Integer.toString(decodeUtilities.readUserID(msgReader));
				xmppLinkMessageDispatcher.IntercomCall(callID, lineNumber, "", consoleNumber, userID, userID2);
				
				break;
				
			case MessageDescriptors.Type.LineNumber:
				lineNumber = decodeUtilities.readLineNumber(msgReader);
				lineName = decodeUtilities.readLineName(msgReader);
				lineMap.put(new Integer(lineNumber), lineName);
				
				break;
				
			case MessageDescriptors.Type.ConsoleNumber:
				int intUserID = decodeUtilities.readUserID(msgReader);
			    newConsoleNumber = decodeUtilities.readConsoleNumber(msgReader);
			    oldConsoleNumber = decodeUtilities.readConsoleNumber(msgReader);
			    xmppLinkMessageDispatcher.ConsoleNumber(String.valueOf(intUserID), newConsoleNumber, oldConsoleNumber);
				
				break;
				
			case MessageDescriptors.Type.DDIDivert:
				callID = decodeUtilities.readCallID(msgReader);
				ddiDigits = decodeUtilities.readDDIDigits(msgReader);
				phantomDDIDigits = decodeUtilities.readPhantomDDIDigits(msgReader);
		 	   	lineNumber = decodeUtilities.readLineNumber(msgReader);
		 	   	flags = decodeUtilities.readInt(MessageDescriptors.Length.DivertFlag, msgReader);
		 	    xmppLinkMessageDispatcher.DDIDivert(callID, lineNumber, ddiDigits, phantomDDIDigits, flags);
				
				break;
		}
	}
	
		
	private void processConnectChange(String m_body) throws Exception {
		StringReader msgReader = new StringReader(m_body);
		int CallID 		  = decodeUtilities.readCallID(msgReader);

	    int LineNumber 	  = decodeUtilities.readLineNumber(msgReader);
	    String LineName   = decodeUtilities.readLineName(msgReader);
	    int ConsoleNumber = decodeUtilities.readConsoleNumber(msgReader);
	    String UserID 	  = Integer.toString(decodeUtilities.readUserID(msgReader));
	    char OldState	  = decodeUtilities.readChar(MessageDescriptors.Length.LineState,msgReader);
	    char NewState	  = decodeUtilities.readChar(MessageDescriptors.Length.LineState,msgReader);
	    char HorS 	      = decodeUtilities.readChar(MessageDescriptors.Length.HorS,msgReader);
	    int Speaker	      = decodeUtilities.readInt(MessageDescriptors.Length.SpeakerNumber,msgReader);
	    int Handset 	  = decodeUtilities.readInt(MessageDescriptors.Length.HandsetNumber,msgReader);
	    char ConDisCon 	  = decodeUtilities.readChar(MessageDescriptors.Length.ConnectDisconnect,msgReader);
	    
	    switch (NewState) {
            case 'I': {  //NewState = Idle
            
            	//elcLines.remove(new Integer(LineNumber)); // Remove from line that are in ELC
                switch (OldState) {
                    case 'C':   //NewState = Idle - OldState = connected
                    case 'B':   //NewState = Idle - OldState = Busy
                    case 'H':   //NewState = Idle - OldState = Held
                    case 'F':   //NewState = Idle - OldState = Conference
                    case 'U':   //NewState = Idle - OldState = Unknown
                    case 'A':   //NewState = Idle - OldState = Ambiguous
                    {
                        // End of call
                    	xmppLinkMessageDispatcher.IdleCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                        break;
                    }
                    case 'R':   //NewState = Idle - OldState = Ringing :- should not happen 
                    {
                        Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
            case 'R':   //NewState = Ringing :- should not happen 
            {
                //Oldstate don't care
            	Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                break;
            }
            case 'C':   //NewState = connected
            {
                switch (OldState)
                {
                    case 'B':   //NewState = connected -  OldState = Busy :- H/S or Spk joins a Hoot?
                    {
                        switch(HorS)
                        {
                            case 'H':   //  It's a Handset
                            {
                                switch (ConDisCon)
                                {
                                    case 'C':   // H/S Join call
                                    {
                                    	if (elcLines.contains(new Integer(LineNumber)))
                                    		xmppLinkMessageDispatcher.ConsoleConnectedCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                    	else
                                    		xmppLinkMessageDispatcher.ConsoleJoinConfCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                        break;
                                    }
                                    case 'D':   // H/S Leave conference
                                    {
                                    	Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                    default:
                                        break;
                                }
                                break;
                            }
                            case 'S':   //  It's a Speaker
                            {
                                switch (ConDisCon)
                                {
                                    case 'C':   // Spk Join call
                                    {
                                    	xmppLinkMessageDispatcher.SpeakerConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                                       break;
                                    }
                                    case 'D':   // Spk leave call
                                    {
                                    	Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                    default:
                                        break;
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        break;
                    }
                     case 'H':   //NewState = connected - OldState = Held :- out of hold
                    {
                    	xmppLinkMessageDispatcher.NotHeldCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                        break;
                    }
                    case 'F':   //NewState = connected -  OldState = Conferenced :- end of conference
                    {
                        // H/S or Speaker???
                        switch(HorS)
                        {
                            case 'H':   //  It's a Handset
                            {
                                switch (ConDisCon)
                                {
                                    case 'C':   // H/S Join conference
                                    {
                                    	Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                                       break;
                                    }
                                    case 'D':   // H/S Leave conference
                                    {
                                    	if (elcLines.contains(new Integer(LineNumber)))
                                    		xmppLinkMessageDispatcher.ConsoleDisconnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                    	else
                                    		xmppLinkMessageDispatcher.NotConferencedCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                        break;
                                    }
                                    default:
                                        break;
                                }
                                break;
                            }
                            case 'S':   //  It's a Speaker
                            {
                                switch (ConDisCon)
                                {
                                    case 'C':   // Spk Join Conference
                                    {
                                    	xmppLinkMessageDispatcher.SpeakerConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                                       break;
                                    }
                                    case 'D':   // Spk leave call
                                    {
                                    	xmppLinkMessageDispatcher.SpeakerDisConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                                        break;
                                    }
                                    default:
                                        break;
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        break;
                    }
                    case 'R':   //NewState = connected - OldState = Ringing :- new answered call
                    {
                        //socketIO.TempLineInterest(LineNumber);
                    	xmppLinkMessageDispatcher.AnsweredCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                        break;
                    }
                    case 'I':   //NewState = connected - OldState = Idle :- new outgoing call
                    {
                       // socketIO.TempLineInterest(LineNumber);
                        if (HorS == 'H')
                        {
                        	xmppLinkMessageDispatcher.NewConnectedCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                        }
                        else
                        if (HorS == 'S')
                        {
                        	xmppLinkMessageDispatcher.SpeakerConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                        }
                        break;
                    }
                    case 'U':   //NewState = connected - OldState = Unknown :- should not happen 
                    case 'A':   //NewState = connected - OldState = Ambiguous :- should not happen
                    {
                    	Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
            case 'F':   //NewState = Conferenced
            {
                switch (OldState)
                {
                    case 'C':   //NewState = Conferenced - OldState = connected :- new conference
                    {
                         switch(HorS)
                        {
                             case 'H':  //new HS conference
                             {
                                 switch (ConDisCon)
                                {
                                    case 'C':   // H/S Join conference
                                    {
                                    	if (elcLines.contains(new Integer(LineNumber)))
                                    		xmppLinkMessageDispatcher.ConsoleConnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                    	else
                                    		xmppLinkMessageDispatcher.NewConfCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                        break;
                                    }
                                    case 'D':   // H/S Leave conference
                                    {
                                    	Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                }
                                break;
                             }
                             case 'S':  //new Spk conference 
                             {
                                 switch (ConDisCon)
                                {
                                    case 'C':   // Spk Join conference
                                    {
                                    	xmppLinkMessageDispatcher.SpeakerConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                                        break;
                                    }
                                    case 'D':   // Spk Leave conference
                                    {
                                    	Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                    default:
                                        break;
                                }
                                break;
                             }
                            default:
                                break;
                        }
                        break;
                    }
                    case 'F':   //NewState = Conferenced - OldState = conferenced :- HS or SPK join or leave conference
                    {
                        switch(HorS)
                        {
                            case 'H':   //  It's a Handset
                            {
                                switch (ConDisCon)
                                {
                                    case 'C':   // H/S Join conference
                                    {
                                    	if (elcLines.contains(new Integer(LineNumber)))
                                    		xmppLinkMessageDispatcher.ConsoleConnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                    	else
                                    		xmppLinkMessageDispatcher.ConsoleJoinConfCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                       break;
                                    }
                                    case 'D':   // H/S Leave conference
                                    {
                                    	if (elcLines.contains(new Integer(LineNumber)))
                                    		xmppLinkMessageDispatcher.ConsoleDisconnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                    	else
                                    		xmppLinkMessageDispatcher.ConsoleLeaveConfCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                        break;
                                    }
                                }
                                break;
                            }
                            case 'S':   //  It's a Speaker
                            {
                                switch (ConDisCon)
                                {
                                    case 'C':   // Spk Join Conference
                                    {
                                    	xmppLinkMessageDispatcher.SpeakerConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                                        break;
                                    }
                                    case 'D':   // Spk leave call
                                    {
                                    	xmppLinkMessageDispatcher.SpeakerDisConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                                        break;
                                    }
                                    default:
                                        break;
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        break;
                    }
                    case 'H':   //NewState = Conferenced - OldState = Held :- Should not happen
                    case 'R':   //NewState = Conferenced - OldState = Ringing :- Should not happen
                    case 'I':   //NewState = Conferenced - OldState = Idle :- Should not happen
                    case 'U':   //NewState = Conferenced - OldState = Unknown :- Should not happen
                    case 'A':   //NewState = Conferenced - OldState = Ambiguous :- Should not happen
                    {
                    	Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                        break;
                    }
                    default:
                        break;

                }
                break;
            }
            case 'H':       //NewState = Held
            {
            	if (OldState == 'B') { // NewState = Held - OldState = Busy
            		// GCC optimised a call from ELC - now a held call
            		// ELCDisconnect is only sent by the switch if a handset is connected
            		elcLines.remove(new Integer(LineNumber));
            		xmppLinkMessageDispatcher.ELCDisconnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset, 0);
            	}
            	xmppLinkMessageDispatcher.HeldCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                break;
            }
            case 'B':       //NewState = Busy
            {
                switch (OldState)
                {
                    case 'C':   //NewState = Busy - OldState = connected :- H/S or Skp leaves a Hoot?
                    {
                        switch(HorS)
                        {
                            case 'H':   //  It's a Handset
                            {
                                switch (ConDisCon)
                                {
                                    case 'C':   // H/S Join call
                                    {
                                    	Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                    case 'D':   // H/S Leave call
                                    {
                                    	if (elcLines.contains(new Integer(LineNumber)))
                                    		xmppLinkMessageDispatcher.ConsoleDisconnectedCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                    	else
                                    		xmppLinkMessageDispatcher.ConsoleLeaveConfCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                        break;
                                    }
                                    default:
                                        break;
                                }
                                break;
                            }
                            case 'S':   //  It's a Speaker
                            {
                                switch (ConDisCon)
                                {
                                    case 'C':   // Spk Join call
                                    {
                                    	Log.warn("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                    case 'D':   // Spk Leave call
                                    {
                                        // End of call
                                       // socketIO.TempLineNoInterest(LineNumber);
                                    	xmppLinkMessageDispatcher.IdleCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                        break;
                                    }
                                    default:
                                        break;
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        break;
                    }
                    default:
                        break;
                }
                break;
            }
            case 'U':       //NewState = Unknown
            {
                 break;
            }
            case 'A':       //NewState = Ambiguous
            {
                switch (OldState)
                {
                    case 'A':   //NewState = Ambiguous - OldState = Ambiguous :- could be hs or spk on or off
                    case 'U':  //NewState = Ambiguous - OldState = Unknown :- could be hs or spk on or off 
                    {
                        switch(HorS)
                        {
                            case 'H':   //  It's a Handset
                            {
                                switch (ConDisCon)
                                {
                                    case 'C':   // H/S Join call
                                    {
                                       // This could be as a result of a makecall request so lets ask if a makecall is pending
                                       if (xmppLinkMessageDispatcher.isMakeCallPending(ConsoleNumber, UserID, Handset))
                                       {
                                    	   xmppLinkMessageDispatcher.NewConnectedCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                       }
                                       else
                                       {
                                    	   if (elcLines.contains(new Integer(LineNumber)))
                                    		   xmppLinkMessageDispatcher.ConsoleConnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                    	   else
                                    		   xmppLinkMessageDispatcher.ConsoleJoinConfCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                       }

                                       break;
                                    }
                                    case 'D':   // H/S Leave call
                                    {
                                	   if (elcLines.contains(new Integer(LineNumber)))
                                		   xmppLinkMessageDispatcher.ConsoleDisconnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                	   else
                                		   xmppLinkMessageDispatcher.ConsoleLeaveConfCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                       break;
                                    }
                                    default:
                                        break;
                                }
                                break;
                            }
                            case 'S':   //  It's a Speaker
                            {
                                switch (ConDisCon)
                                {
                                    case 'C':   // Spk Join Call
                                    {
                                    	Log.debug("Speaker Connect Change Message Ignored '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                    case 'D':   // Spk leave call
                                    {
                                    	Log.debug("Speaker Connect Change Message Ignored '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                    default:
                                        break;
                                }
                                break;
                            }
                            default:
                                break;
                        }
                        break;
                    }
                    default:
                        break;

                }
                break;
            }
        }
		
	}

	
	
}
