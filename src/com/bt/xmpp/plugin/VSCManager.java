package com.bt.xmpp.plugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimeZone;

/**
 * This class manage pseudo console/user allocation/deallocation 
 * @author naokimurao
 *
 */
public class VSCManager {
	
	private Collection<VSC> vscs = new ArrayList<VSC>();
	SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	SimpleDateFormat jsDF = new SimpleDateFormat( "MMMM dd, yyyy HH:mm:ss" );

	public VSCManager() {}
	
	public void init(boolean vscUseHandsets34, int[] ranges, int [] counts) {
		df.setTimeZone(TimeZone.getDefault()); 
		
		for(int i=0; i < ranges.length; i++) {
			if(ranges[i] > 0 && counts[i] >0 ) {
				for(int k=0; k < counts[i]; k++) {
					addVSC(String.valueOf(ranges[i] + k), "1");
					if(vscUseHandsets34)
						addVSC(String.valueOf(ranges[i] + k), "3");
				}
			}
		}
	}
	
	
	private void addVSC(String consoleID, String handset) {
		VSC vsc = new VSC();
		vsc.setFree(true);
		vsc.setConsoleID(consoleID);
		vsc.setHandset(handset);
		
		vscs.add(vsc);
	}
	
	public boolean isAvailable() {
		boolean available = false;
		for(VSC vsc : vscs) {
			if(vsc.isFree) {
				available = true;
				break;
			}
		}
		
		return available;
	}
	
	public VSC allocate(String traderID, String traderName) {
		for(VSC vsc : vscs) {
			if(vsc.isFree) {
				vsc.setAllocatedTraderID(traderID);
				vsc.setTraderName(traderName);
				vsc.setFree(false);
				vsc.setStartTime(df.format(new java.util.Date()));
				
				return vsc;
			}
		}
		
		return null;
	}
	
	public void deallocate() {
		
	}
	
	public void validate() {
		
	}
	

}
