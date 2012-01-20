/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bt.tmb.fastlinkstack;

/**
 *
 * @author 606335827
 */
public interface IDispatchFunctor 
{
    /**
     * 
     * @param linkMsg of the form SRC_TAG|RAW_ITSLINK_MESSAGE_TEXT 
     * @return 
     */
    
    //boolean processDispatch(String linkMsg); 
    void processDispatch(String linkMsg, StructuredMessage smsg); 
    
}
