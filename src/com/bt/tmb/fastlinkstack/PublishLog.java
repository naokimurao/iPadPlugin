/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bt.tmb.fastlinkstack;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author 606335827
 */
public class PublishLog implements Runnable
{
    Set<Message> published = new HashSet<Message>();
    
    public PublishLog()
    {
        
    }
    
    public void start()
    {
        ExecutorService threadExec = Executors.newSingleThreadExecutor();        
        threadExec.execute(this);                
    }
    
    
    synchronized public boolean contains(Message m)
    {        
        return published.contains(m);
    }
    
    synchronized public void add(Message m)
    {
        published.add(m);
    }
    
    synchronized public void remove(Message m)
    {
        published.remove(m);
    }
    
    /**
     * Housekeeping function called by thread function: run()
     */
    synchronized private void cleanOut()
    {
        System.out.println("Publisher clearing log");
        long now = new Date().getTime(); //millis        
        Iterator<Message> it = published.iterator();
        while(it.hasNext())
        {
            Message m = it.next();
            if( (now - m.getTS()) > 10000) //older than 10 seconds
            {
                it.remove();
            }            
        }        
    }

    @Override
    public void run() 
    {
        while(true)
        {
            cleanOut();
            try
            {
                Thread.sleep(30 * 1000);
            }
            catch(Exception e){}
        }
        
    }
    
    
    
}
