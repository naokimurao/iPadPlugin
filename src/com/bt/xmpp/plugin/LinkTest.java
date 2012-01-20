package com.bt.xmpp.plugin;

import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.dom4j.Element;
import org.xmpp.packet.Message;

import com.bt.tmb.fastlinkstack.IDispatchFunctor;
import com.bt.tmb.fastlinkstack.LinkConfigException;
import com.bt.tmb.fastlinkstack.SiteManager;
import com.bt.tmb.fastlinkstack.StructuredMessage;
import com.bt.xmpp.plugin.itslink.DecodeUtilities;
import com.bt.xmpp.plugin.itslink.MessageDescriptors;

/**
 * 
 * this is a class to test Fastlinkstack library and XMPP message dispatcher
 *
 */

public class LinkTest implements IDispatchFunctor, IXMPPLinkMessageDispatcher  {
	
	private String siteID;
	private SiteManager siteMgr;
	private DecodeUtilities decodeUtilities = DecodeUtilities.getInstance();
	private Map lineMap = Collections.synchronizedMap(new HashMap());
    private Set elcLines = Collections.synchronizedSet(new HashSet());
    
 
	public void runTest() {
		
		
		Properties props = new Properties();
        props.put("itslink1", "10.10.40.102");
        props.put("itslink2", "10.10.40.102");
        props.put("itsport1", "3001");
        props.put("itsport2", "3001");
        props.put("sourcetag", "LinkTest");
        props.put("version", "5");
        props.put("cos", "32");
        try {
			props.put("homehost", InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        props.put("homeapp","iPad Anywhere plugin");
        
        siteID = props.getProperty("sourcetag");
		siteMgr = new SiteManager(props, this);
		try {
			if(!siteMgr.connectSite())
				System.out.println(" failed to start.");
		} catch (LinkConfigException e) {
			e.printStackTrace();
		}
     
	}
	


	@Override
	public void processDispatch(String linkMsg, StructuredMessage smsg)  {
		
		try {
			interpretLinkMessage(smsg.m_id, smsg.m_body);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void interpretLinkMessage(int m_id, String m_body) throws Exception {
		StringReader msgReader = new StringReader(m_body);
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
	    
		switch(m_id) {
			case MessageDescriptors.Type.IncommingCall:
				callID     = decodeUtilities.readCallID(msgReader);
				lineNumber = decodeUtilities.readInt(MessageDescriptors.Length.LineNumber,msgReader);
				lineName   = decodeUtilities.readString(MessageDescriptors.Length.LineName,msgReader);
				ddiDigits  = decodeUtilities.readDDIDigits(msgReader);
				phantomDDIDigits = decodeUtilities.readPhantomDDIDigits(msgReader);
				
				lineMap.put(new Integer(lineNumber),lineName);
				this.IncomingCall(callID, lineNumber, lineName, ddiDigits, phantomDDIDigits);
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
		    			PrivateCall(callID, lineNumber, consoleNumber, userID);
		    		    break;
		    
		    		case 'N' : 
		    			NonPrivateCall(callID, lineNumber, consoleNumber, userID);
		    		    break;
		    		
		    		default:
		    		    break;
		    	}
				break;
				
			case MessageDescriptors.Type.DialledDigits:
				callID = decodeUtilities.readCallID(msgReader);
			    lineNumber = decodeUtilities.readLineNumber(msgReader);
				digits = decodeUtilities.readString(MessageDescriptors.Length.DigitString,msgReader);
				this.DialedDigits(callID, lineNumber, digits);
				break;
				
			case MessageDescriptors.Type.Intercom:
				//this.IntercomCall(callID, lineNumber, lineName, consoleNumber, userID, userID2)
				callID = decodeUtilities.readCallID(msgReader);
			    lineNumber = decodeUtilities.readLineNumber(msgReader);
			    consoleNumber = decodeUtilities.readConsoleNumber(msgReader);
			    userID = Integer.toString(decodeUtilities.readUserID(msgReader));
			    userID2 = Integer.toString(decodeUtilities.readUserID(msgReader));

			    lineName = (String)lineMap.get(new Integer(lineNumber));
			    
			    this.IntercomCall(callID, lineNumber, lineName, consoleNumber, userID, userID2);
				break;
				
			case MessageDescriptors.Type.Transfer:
				callID = decodeUtilities.readCallID(msgReader);
			    lineNumber = decodeUtilities.readLineNumber(msgReader);
		        consoleNumber = decodeUtilities.readConsoleNumber(msgReader);
		        userID = Integer.toString(decodeUtilities.readUserID(msgReader));
		        userID2 = Integer.toString(decodeUtilities.readUserID(msgReader));
		        this.TransferedCall(callID, lineNumber, "", consoleNumber, userID, userID2);
				break;
		
			case MessageDescriptors.Type.CLIDetails:
				callID = decodeUtilities.readCallID(msgReader);
		        lineNumber = decodeUtilities.readLineNumber(msgReader);
		        cliDigits = decodeUtilities.readString(MessageDescriptors.Length.CLIDigits,msgReader);
				this.CLIDetails(callID, lineNumber, cliDigits);
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
				this.LineInfo(callID, lineNumber, lineName, state, speakerCount, hsCount, direction, isPrivate=='Y', ddiDigits, lineType, elcNumber);
				
				break;
			
			case MessageDescriptors.Type.Abandoned:
				callID = decodeUtilities.readCallID(msgReader);
			    lineNumber = decodeUtilities.readLineNumber(msgReader);
			    this.AbandonedCall(callID, lineNumber);
				
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
		            this.ELCConnectedCall(callID, lineNumber, lineName, consoleNumber, userID, handset, elcNumber);
		        }
		        else if (connDisConn == 'D') {
		        	elcLines.remove(new Integer(lineNumber));
		        	this.ELCDisconnectedCall(callID, lineNumber, lineName, consoleNumber, userID, handset, elcNumber);
		        }
				
				break;
				
			case MessageDescriptors.Type.ECKeyPress:
				consoleNumber = decodeUtilities.readConsoleNumber(msgReader);
		        userID = Integer.toString( decodeUtilities.readUserID(msgReader));
		        ecKeyGroup =  decodeUtilities.readInt(MessageDescriptors.Length.ECKeyGroup,msgReader);
		        ecKeyFunction =  decodeUtilities.readChar(MessageDescriptors.Length.ECKeyFunction,msgReader);
		 	    handset =  decodeUtilities.readInt(MessageDescriptors.Length.HandsetNumber,msgReader);
		 	    ecKeyState =   decodeUtilities.readInt(MessageDescriptors.Length.ECKeyState,msgReader);
		 	    this.ECKeyPressed(consoleNumber, userID, ecKeyGroup, ecKeyFunction, handset, ecKeyState);
				
				break;
				
			case MessageDescriptors.Type.CallProgress:
				callID = decodeUtilities.readCallID(msgReader);
				lineNumber = decodeUtilities.readLineNumber(msgReader);
				channelNumber = decodeUtilities.readInt(MessageDescriptors.Length.ChannelNumber,msgReader);
				type = decodeUtilities.readInt(MessageDescriptors.Length.ProgressFlag,msgReader);
				this.CallProgress(callID, lineNumber, channelNumber, type);
				
				break;
				
			case MessageDescriptors.Type.CallProceding:
				callID = decodeUtilities.readCallID(msgReader);
		        lineNumber = decodeUtilities.readLineNumber(msgReader);
		        dialledDigits = decodeUtilities.readString(MessageDescriptors.Length.ProceedingDigits,msgReader);
		        endFlag = decodeUtilities.readChar(MessageDescriptors.Length.YesNo ,msgReader);
				this.CallProceeding(callID, lineNumber, dialledDigits, endFlag=='Y');
				
				break;
				
			case MessageDescriptors.Type.CallMoved:
				callID = decodeUtilities.readCallID(msgReader);
		        lineNumber1 = decodeUtilities.readLineNumber(msgReader);
		        lineNumber2 = decodeUtilities.readLineNumber(msgReader);
		        this.CallMoved(callID, lineNumber1, lineNumber2);
				
				break;
				
			case MessageDescriptors.Type.DDIBusy:
				callID = decodeUtilities.readCallID(msgReader);
				lineNumber = decodeUtilities.readLineNumber(msgReader);
				realDDIDigits = decodeUtilities.readRealDDIDigits(msgReader);
				phantomDDIDigits = decodeUtilities.readPhantomDDIDigits(msgReader);
				ddiLabel = decodeUtilities.readString(MessageDescriptors.Length.DDILabel,msgReader);	
				this.DDIBusy(callID, lineNumber, realDDIDigits, phantomDDIDigits, ddiLabel);
				
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
				this.RecallTransferCall(callID, lineNumber1, lineNumber2, statusFlag);
				
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
				this.IntercomCall(callID, lineNumber, "", consoleNumber, userID, userID2);
				
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
			    this.ConsoleNumber(String.valueOf(intUserID), newConsoleNumber, oldConsoleNumber);
				
				break;
				
			case MessageDescriptors.Type.DDIDivert:
				callID = decodeUtilities.readCallID(msgReader);
				ddiDigits = decodeUtilities.readDDIDigits(msgReader);
				phantomDDIDigits = decodeUtilities.readPhantomDDIDigits(msgReader);
		 	   	lineNumber = decodeUtilities.readLineNumber(msgReader);
		 	   	flags = decodeUtilities.readInt(MessageDescriptors.Length.DivertFlag, msgReader);
		 	   	
		 	   	this.DDIDivert(callID, lineNumber, ddiDigits, phantomDDIDigits, flags);
				
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
            
            	elcLines.remove(new Integer(LineNumber)); // Remove from line that are in ELC
                switch (OldState) {
                    case 'C':   //NewState = Idle - OldState = connected
                    case 'B':   //NewState = Idle - OldState = Busy
                    case 'H':   //NewState = Idle - OldState = Held
                    case 'F':   //NewState = Idle - OldState = Conference
                    case 'U':   //NewState = Idle - OldState = Unknown
                    case 'A':   //NewState = Idle - OldState = Ambiguous
                    {
                        // End of call
                        IdleCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                        break;
                    }
                    case 'R':   //NewState = Idle - OldState = Ringing :- should not happen 
                    {
                        System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
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
            	System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
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
                                    		ConsoleConnectedCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                    	else
                                    		ConsoleJoinConfCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                        break;
                                    }
                                    case 'D':   // H/S Leave conference
                                    {
                                    	System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
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
                                       SpeakerConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                                       break;
                                    }
                                    case 'D':   // Spk leave call
                                    {
                                    	System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
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
                        NotHeldCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
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
                                    	System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                                       break;
                                    }
                                    case 'D':   // H/S Leave conference
                                    {
                                    	if (elcLines.contains(new Integer(LineNumber)))
                                    		ConsoleDisconnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                    	else
                                    		NotConferencedCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
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
                                       SpeakerConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                                       break;
                                    }
                                    case 'D':   // Spk leave call
                                    {
                                        SpeakerDisConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
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
                        AnsweredCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                        break;
                    }
                    case 'I':   //NewState = connected - OldState = Idle :- new outgoing call
                    {
                       // socketIO.TempLineInterest(LineNumber);
                        if (HorS == 'H')
                        {
                        	NewConnectedCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                        }
                        else
                        if (HorS == 'S')
                        {
                            SpeakerConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                        }
                        break;
                    }
                    case 'U':   //NewState = connected - OldState = Unknown :- should not happen 
                    case 'A':   //NewState = connected - OldState = Ambiguous :- should not happen
                    {
                    	System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
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
                                    		ConsoleConnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                    	else
                                    		NewConfCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                        break;
                                    }
                                    case 'D':   // H/S Leave conference
                                    {
                                    	System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
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
                                        SpeakerConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                                        break;
                                    }
                                    case 'D':   // Spk Leave conference
                                    {
                                    	System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
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
                                    		ConsoleConnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                    	else
                                    		ConsoleJoinConfCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                       break;
                                    }
                                    case 'D':   // H/S Leave conference
                                    {
                                    	if (elcLines.contains(new Integer(LineNumber)))
                                    		ConsoleDisconnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                    	else
                                    		ConsoleLeaveConfCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
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
                                        SpeakerConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
                                        break;
                                    }
                                    case 'D':   // Spk leave call
                                    {
                                        SpeakerDisConnect(CallID,LineNumber,LineName,ConsoleNumber,UserID,Speaker);
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
                    	System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
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
            		ELCDisconnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset, 0);
            	}
                HeldCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
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
                                    	System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                    case 'D':   // H/S Leave call
                                    {
                                    	if (elcLines.contains(new Integer(LineNumber)))
                                    		ConsoleDisconnectedCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                    	else
                                    		ConsoleLeaveConfCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
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
                                    	System.out.println("Unexpected Connect Change Message = '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                    case 'D':   // Spk Leave call
                                    {
                                        // End of call
                                       // socketIO.TempLineNoInterest(LineNumber);
                                        IdleCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
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
                                       if (isMakeCallPending(ConsoleNumber, UserID, Handset))
                                       {
                                    		   NewConnectedCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                       }
                                       else
                                       {
                                    	   if (elcLines.contains(new Integer(LineNumber)))
                                    		   ConsoleConnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                    	   else
                                    		   ConsoleJoinConfCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
                                       }

                                       break;
                                    }
                                    case 'D':   // H/S Leave call
                                    {
                                	   if (elcLines.contains(new Integer(LineNumber)))
                                		   ConsoleDisconnectedCall(CallID, LineNumber, LineName, ConsoleNumber, UserID, Handset);
                                	   else
                                		   ConsoleLeaveConfCall(CallID,LineNumber,LineName,ConsoleNumber,UserID,Handset);
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
                                    	System.out.println("Speaker Connect Change Message Ignored '" + OldState + "' -> " +  NewState + "'");
                                        break;
                                    }
                                    case 'D':   // Spk leave call
                                    {
                                    	System.out.println("Speaker Connect Change Message Ignored '" + OldState + "' -> " +  NewState + "'");
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

	
	private void dispatchXMPPMessage(Message message) {
		System.out.println(message.toXML());
	}
	
	private Message generateITSLinkXMPPMessage() {
		Message message = new Message();
		message.setFrom("TEST.XMPP");
		
		return message;
	}
	
	@Override
	public void IncomingCall(int callID, int lineNumber, String lineName, String ddiDigits, String phantomDDIDigits) {
		Message message = generateITSLinkXMPPMessage();

		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
		messageNode.addAttribute("type", MessageDescriptors.EventType.notIncomingCall);
		messageNode.addAttribute("lineNumber", String.valueOf(lineNumber));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ECKeyPressed(int consoleNumber, String userID, int ecKeyGroup, int ecKeyFunction, int handset, int ecKeyState) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
		messageNode.addAttribute("type", MessageDescriptors.EventType.TextMessage);
		messageNode.addAttribute("txt", txt);

		dispatchXMPPMessage(message);
	}

	@Override
	public void ConsoleNumber(String userID, int newConsoleNumber, int oldConsoleNumber) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
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
		Element messageNode = topElement.addElement("message");
		messageNode.addAttribute("type", MessageDescriptors.EventType.ITSLinkError);
		messageNode.addAttribute("code", String.valueOf(code));

		dispatchXMPPMessage(message);
	}

	@Override
	public void ITSLinkShutDown(int code) {
		Message message = generateITSLinkXMPPMessage();
		
		Element topElement = message.addChildElement("itslink-event", "http://bt.com/protocol/itslink");
		topElement.addElement("site").addAttribute("id", siteID);
		Element messageNode = topElement.addElement("message");
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
	
	public static void main(String []args) {
		LinkTest a = new LinkTest();
		a.runTest();
	}

}
