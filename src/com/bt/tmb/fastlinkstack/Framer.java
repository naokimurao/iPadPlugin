/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bt.tmb.fastlinkstack;
import java.io.IOException;
import java.io.OutputStream;
/**
 *
 * @author 606335827
 */
public interface Framer
{  
    void frameMsg(byte[] message) throws IOException;
    byte[] nextMsg() throws IOException;   
}
