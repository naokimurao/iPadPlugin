/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bt.tmb.fastlinkstack;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
/**
 *
 * @author 606335827
 */
public class LinkFramer implements Framer
{
    private InputStream in;
    private final OutputStream out;
    
    private static final byte DELIMITER ='\n'; // decimal 10 (line feed)
    private static final byte[] OUTDELIM = {13, 10};
    
    public LinkFramer(InputStream in, OutputStream out)
    {
        this.in = in;
        this.out = out;
    }

    @Override
    public void frameMsg(byte[] message) throws IOException
    {
        for(byte b : message)
        {
            if(b == DELIMITER)
            {
                throw new IOException("message contains delimiter -- illegal");
            }
        }
        
        //synchronize, because output stream shared by housekeeping
        // and southbound message dispatcher thread
        synchronized(out) 
        {
            out.write(message);
            out.write(OUTDELIM);
            out.flush();   
        }
        
    }

    @Override
    public byte[] nextMsg() throws IOException
    {
        ByteArrayOutputStream messageBuffer = 
                new ByteArrayOutputStream();
        int nextByte;
        
        while( (nextByte = in.read()) != DELIMITER)
        {
            if(nextByte == -1)
            {
                if(messageBuffer.size() == 0)
                {
                    return null; //no data
                }
                else
                {
                    throw new IOException("non-empty message with no delimiter");
                }
            }
            messageBuffer.write(nextByte);
        }
        
        //note: return value contains the trailing CR (decimal 13)
        // and callers should trim that out
        return messageBuffer.toByteArray();    
    }   
    
}
