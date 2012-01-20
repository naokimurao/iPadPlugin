package com.bt.xmpp.plugin.view;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.util.Log;

import com.bt.xmpp.plugin.ITSUserDao;
import com.bt.xmpp.plugin.Site;
import com.bt.xmpp.plugin.SiteDao;

public class ITSSummary extends HttpServlet {
	
	private static final long serialVersionUID = 7194015895992511526L;
	private long   siteID;
	private SiteDao siteDao;
	private ITSUserDao userDao;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Content-Type", "text/html");
		response.setHeader("Connection", "close");
		
		ServletOutputStream out = response.getOutputStream();
		siteDao = new SiteDao();
		String action = request.getParameter("action");
		if(request.getParameter("siteID") != null) {
			siteID = Long.parseLong(request.getParameter("siteID"));
		}
		
		if(action == null) { action = " "; }
				
		if(action.equals("deleteConfirm")) {
			
			displayDeleteConfirm(out);
		}
		else if(action.equals("delete")) {
			boolean cancel = request.getParameter("cancel") != null;
		    boolean delete = request.getParameter("delete") != null;
		    if (cancel) {
		    	displayPage(out);
		    }
		    
		    if(delete) {
		    	userDao = new ITSUserDao();
		    	userDao.removeBySite(siteID);
		    	siteDao.remove(siteID);
		    	displayPage(out);
		    }
		}
		else {
			displayPage(out);
		}
	}
	
	private void displayPage(ServletOutputStream out) {
		try {
			out.println("");
			out.println("<html>");
			out.println("    <head>");
			out.println("        <title>ITS Summary</title>");
			out.println("        <meta name=\"pageID\" content=\"ITS-SUMMARY\"/>");
			out.println("    </head>");
			out.println("    <body>");
			out.println("");
			out.println("<br>");
            out.println("<div class=\"jive-table\">");
            out.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th nowrap>Site Name</th>");
            out.println("<th nowrap>ITSLink Primary</th>");
            out.println("<th nowrap>Edit</th>");
            out.println("<th nowrap>Delete</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
			
            Collection<Site> sites = siteDao.getSites();
            Iterator<Site> iter4 = sites.iterator();
            int i = 1;

            while(iter4.hasNext()) {
                Site site = (Site)iter4.next();
                if(site != null) {
                    if(i % 2 == 1)
                        out.println("<tr class=\"jive-odd\">");
                    else
                        out.println("<tr class=\"jive-even\">");

                    out.println("<td width=\"40%\">");
                    out.println(site.getName());
                    out.println("</td>");
                    out.println("<td width=\"30%\">");
                    out.println(site.getItslink1());
                    out.println("</td>");
                    out.println("<td width=\"15%\">");
                    out.println("<a href=\"its-settings?action=edit&siteID=" + site.getSiteID() + "\"><img src=\"images/edit-16x16.gif\" alt=\"Edit Site\" border=\"0\"></a>");
                    out.println("</td>");
                    out.println("<td width=\"15%\">");
                    out.println("<a href=\"its-summary?action=deleteConfirm&siteID=" + site.getSiteID() + "\"><img src=\"images/delete-16x16.gif\" alt=\"Delete Site\" border=\"0\"></a>");
                    out.println("</td>");
                    out.println("</tr>");
                    i++;
                }
            }
            
            out.println("<tr>");
            out.println("<td colspan='3'>");
            out.println("<a href=\"its-settings?action=top\"><img src=\"images/add-16x16.gif\" alt=\"Add new Site\" border=\"0\">Add new Site</a>");
            out.println("</td>");
            out.println("</tr>");
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
        catch (Exception e) {
        	Log.error(e);
        }
	}
	
	private void displayDeleteConfirm(ServletOutputStream out) {
		try {
			out.println("");
			out.println("<html>");
			out.println("    <head>");
			out.println("        <title>Delete Site?</title>");
			out.println("        <meta name=\"pageID\" content=\"ITS-SUMMARY\"/>");
			out.println("    </head>");
			out.println("    <body>");
			out.println("");
			out.println("<p>");
			out.println("<b>");
			out.println("Are you sure you want to delete the site from the system?");
			out.println("</p>");
			out.println("</b>");
			out.println("<form action=\"its-summary\">");
			out.println("<input type='hidden' name='siteID' value='" + siteID + "'>");
			out.println("<input type='hidden' name='action' value='delete'>");
			out.println("<input type=\"submit\" name=\"delete\" value=\"Delete\">");
			out.println("<input type=\"submit\" name=\"cancel\" value=\"Cancel\">");
			out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        }
        catch (Exception e) {
        	Log.error(e);
        }
	}


}
