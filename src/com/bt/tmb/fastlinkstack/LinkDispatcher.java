/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bt.tmb.fastlinkstack;

//import java.util.HashSet;
import java.util.Queue;
//import java.util.Set;
import java.util.LinkedList;
/**
 * This dispatcher is intended to be shared by two LinkConnectors connected to same ITS Link,
 * and performs de-dupe work for the message flow so that only one occurrence of a message
 * is sent out via the dispatch functor.
 * @author 606335827
 */
public class LinkDispatcher implements Runnable
{
    IDispatchFunctor m_functor = null;
    private Queue<Message> m_queue = new LinkedList<Message>();
    private PublishLog publisher = new PublishLog();
    
    public LinkDispatcher(IDispatchFunctor functor)
    {
        m_functor = functor;
        publisher.start();        
    }
    
    synchronized void setDispatchTarget(IDispatchFunctor functor)
    {
        m_functor = functor;
    }
    
    /**
     * This method offers the first line of de-duping defense
     * as it won't enqueue a message it already has
     * @param m the message to save
     */
    synchronized public void setMessage(Message m)
    {
        if(!this.m_queue.contains(m))
        {
            m_queue.add(m);
        }
    }
    
    synchronized private boolean getMessage(MessageWrapper w)
    {
        Message top = m_queue.poll();
        if(top != null)
        {
            w.m_msg = top;
            return true;
        }
        else
        {
            w.m_msg = null;
            return false;
        }
        
    }

    /**
     * the dispatching loop, does the dispatch de-dupe via memory cache
     */
    @Override
    public void run()
    {
        if(this.m_functor == null)
            return;
        
        
        while(true)
        {           
            MessageWrapper wrapper = new MessageWrapper();
            if(this.getMessage(wrapper))
            {
                if(wrapper.m_msg != null && this.m_functor != null)
                {
                    //check publishing memory
                    if(this.publisher.contains(wrapper.m_msg))
                    {
                        //we already published out the callback to client
                        //clean up the book-keeping and then nothing
                        publisher.remove(wrapper.m_msg);
                    }
                    else
                    {          
                        //need to do book-keeping and then publish out callback
                        publisher.add(wrapper.m_msg);
                        m_functor.processDispatch(wrapper.m_msg.getMessage(),
                                wrapper.m_msg.getStructuredMessage());
                    }
                }
            }   
                       
            try
            {
                Thread.sleep(250);                
            }
            catch(Exception e){}
        }//end while-loop
    }
    
    
}
