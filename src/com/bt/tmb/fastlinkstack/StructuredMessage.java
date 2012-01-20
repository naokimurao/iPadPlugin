/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bt.tmb.fastlinkstack;

/**
 * Plain old data object
 * @author 606335827
 */
public class StructuredMessage 
{
    /**
     * The source tag of the message
     */
    public String m_site;
    /**
     * The ITS Link message number
     */
    public int m_id;
    
    /**
     * The sequence letter of the ITS message
     */
    public char m_sequence;
    /**
     * The ITS Link extended header timestamp if available
     */
    public String m_timestamp;
    
    /**
     * The data portion of the ITS message
     */
    public String m_body;
    
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("id:").append(Integer.toString(this.m_id)).append(" ");
        buf.append("seq:").append(this.m_sequence).append(" ");
        buf.append("ts:").append(this.m_timestamp).append(" ");
        buf.append("body:").append(this.m_body).append(" ");
        
        
        return buf.toString();
        
    }
    
    
}
