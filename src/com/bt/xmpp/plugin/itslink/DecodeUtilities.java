package com.bt.xmpp.plugin.itslink;

import java.io.IOException;
import java.io.StringReader;



/**
 * @author walkerb
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public  class DecodeUtilities {
	
	protected DecodeUtilities() {}

    static private DecodeUtilities _instance;


    static public DecodeUtilities getInstance()  {
    	if (_instance == null)  {
    		_instance = new DecodeUtilities();

    	}
    	return _instance;
    }  
	
	public int readInt(int len, StringReader MsgReader) throws IOException {
		char[] bfr = new char[len];

		MsgReader.read(bfr,0,len);
		return Integer.parseInt(new String(bfr));
    }

    public long readLong(int len, StringReader MsgReader) throws IOException {
    	char[] bfr = new char[len];

    	MsgReader.read(bfr,0,len);
    	return Long.parseLong(new String(bfr));
    }            
            
    public String readString(int len, StringReader MsgReader) throws IOException {
    	char[] bfr = new char[len];

    	MsgReader.read(bfr,0,len);
    	return new String(bfr).trim();
    }
      
    public char readChar(int len, StringReader MsgReader) throws Exception {
    	if (len != 1) {
    		throw new Exception("Length of single Character can't be " + len);
    	}
    	char[] bfr = new char[1];
      
    	MsgReader.read(bfr,0,1);
    	return bfr[0];
    }
      
    protected String GetTranslatedLineName( String ITSLineName ) {
    	return (new String(ITSLineName.replaceAll("  ", " ")));
    }
      
    public int readConsoleNumber(StringReader MsgReader) throws Exception {
    	return readInt(MessageDescriptors.Length.ConsoleNumber,MsgReader);
    }

    public int readUserID(StringReader MsgReader) throws Exception{
    	return readInt(MessageDescriptors.Length.UserID,MsgReader);
    }

    public int readLineNumber(StringReader MsgReader) throws Exception {
    	return readInt(MessageDescriptors.Length.LineNumber,MsgReader);
    }

    public String readLineName(StringReader MsgReader) throws Exception {
    	String ITSLineName = readString( MessageDescriptors.Length.LineName ,MsgReader);
    	String TranslatedLineName = this.GetTranslatedLineName(ITSLineName.trim());

    	return TranslatedLineName;
    }
      
    public int readCallID(StringReader MsgReader) throws Exception {
	  	int callId = readInt(MessageDescriptors.Length.CallID,MsgReader);
		if(callId==0) callId = -1;
		return callId;
	}
      
    public String readDDIDigits(StringReader MsgReader) throws Exception{
        long ddiNum;
        String ddiString = "";
		try {
			ddiString = readString(MessageDescriptors.Length.DDIDigits,MsgReader);
			ddiNum = Long.parseLong(ddiString);
		} catch (NumberFormatException e) {
			return ddiString;
		}
        return String.valueOf(ddiNum);
    }
      
    public String readRealDDIDigits(StringReader MsgReader) throws Exception {
        long ddiNum;
        String ddiString = "";
  		try {
  			ddiString = readString(MessageDescriptors.Length.RealDDIDigits,MsgReader);
  			ddiNum = Long.parseLong(ddiString);
  		} catch (NumberFormatException e) {
  			return ddiString;
  		}
          return String.valueOf(ddiNum);
    }
      
    public String readPhantomDDIDigits(StringReader MsgReader) throws Exception {
    	String ddiString = "";
        long ddiNum;
        try {
        	ddiString = readString(MessageDescriptors.Length.PhantomDDIDigits,MsgReader);
        	ddiNum = Long.parseLong(ddiString);
		} catch (NumberFormatException e) {
			return ddiString;
		}
        return String.valueOf(ddiNum);
    }        
}
