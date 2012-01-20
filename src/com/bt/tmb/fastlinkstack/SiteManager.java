/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bt.tmb.fastlinkstack;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 *
 * @author 606335827
 */
public class SiteManager 
{
    private Properties m_props;
    private IDispatchFunctor m_callback;
    private LinkDispatcher m_dispatcher;
    private LinkConnector m_conn1;
    private LinkConnector m_conn2;
    
    private String m_itslink1;
    private String m_itslink2;
    private String  m_itsport1;
    private String m_itsport2;
    
    
    public SiteManager(Properties props, IDispatchFunctor functor)
    {
        m_props = props;
        m_callback = functor;
    }
    
    private void verifyProperties() throws LinkConfigException
    {
        //verify and remember servers and ports for links servers
        m_itslink1 = m_props.getProperty("itslink1");
        if(m_itslink1 == null)
            throw new LinkConfigException("missing itslink1 property");
        
        m_itslink2 = m_props.getProperty("itslink2");
        if(m_itslink2 == null)
            throw new LinkConfigException("missing itslink2 property");
        
        m_itsport1 = m_props.getProperty("itsport1");
        if(m_itsport1 == null)
            throw new LinkConfigException("missing itsport1 property");
        
        m_itsport2 = m_props.getProperty("itsport2");
        if(m_itsport2 == null)
            throw new LinkConfigException("missing itsport2 property");
        
        //just verify the rest
        String other = m_props.getProperty("sourcetag");
        if(other == null)
            throw new LinkConfigException("missing sourcetag property");
        
        other = m_props.getProperty("version");
        if(other == null)
        {
            throw new LinkConfigException("missing version property");
        }
        else
        {
            if(!other.equals("4") && !other.equals("5"))
                m_props.setProperty("version", "5");
        }
        
        other = m_props.getProperty("homehost");
        if(other == null)
            throw new LinkConfigException("missing homehost property");
        other = m_props.getProperty("homeapp");
        if(other == null)
            throw new LinkConfigException("missing homeapp property");
      
    }
    
    public boolean connectSite() throws LinkConfigException
    {
        verifyProperties(); //throws LinkConfigException if problem evident
        Properties p1 = (Properties) m_props.clone();
        p1.setProperty("itslinkhost", this.m_itslink1);
        p1.setProperty("itslinkport", this.m_itsport1);
        
        Properties p2 = (Properties) m_props.clone();
        p2.setProperty("itslinkhost", this.m_itslink2);
        p2.setProperty("itslinkport", this.m_itsport2);
        if(m_callback == null)
            throw new LinkConfigException("null callback function object");
        this.m_dispatcher = new LinkDispatcher(m_callback);
        ExecutorService dispatchExec = Executors.newSingleThreadExecutor();        
        dispatchExec.execute(m_dispatcher);  //start the dispatcher pump
        
        m_conn1 = new LinkConnector(p1, m_dispatcher);
        ExecutorService connExec = Executors.newSingleThreadExecutor();        
        connExec.execute(m_conn1); //start the connector read loop
        
        m_conn2 = new LinkConnector(p2, m_dispatcher);
        ExecutorService conn2Exec = Executors.newSingleThreadExecutor();        
        conn2Exec.execute(m_conn2); //start the connector read loop
        
        System.out.println("Dispatcher and connectors running...");
        
        return true;
    }
    
    
    
}
