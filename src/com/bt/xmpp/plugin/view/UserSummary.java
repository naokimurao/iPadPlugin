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

import com.bt.xmpp.plugin.ITSUser;
import com.bt.xmpp.plugin.ITSUserDao;

public class UserSummary extends HttpServlet {
	
	private static final long serialVersionUID = 9197777332088997375L;
	private String username;
	private ITSUserDao userDao;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Content-Type", "text/html");
		response.setHeader("Connection", "close");
		
		ServletOutputStream out = response.getOutputStream();
		userDao = new ITSUserDao();
		String action = request.getParameter("action");
		if(request.getParameter("username") != null) {
			username = request.getParameter("username");
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
		    	userDao.removeByUsername(username);
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
			out.println("        <title>User Mapping Summary</title>");
			out.println("        <meta name=\"pageID\" content=\"USER-SUMMARY\"/>");
			out.println("    </head>");
			out.println("    <body>");
			out.println("");
			out.println("<br>");
            out.println("<div class=\"jive-table\">");
            out.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\">");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th nowrap>Username</th>");
            out.println("<th nowrap>ITS Site</th>");
            out.println("<th nowrap>Delete</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
			
            Collection<ITSUser> users = userDao.getUsers();
            Iterator<ITSUser> iter4 = users.iterator();
            int i = 1;

            while(iter4.hasNext()) {
                ITSUser user = (ITSUser)iter4.next();

                if(user != null)
                {
                    if(i % 2 == 1)
                        out.println("<tr class=\"jive-odd\">");
                    else
                        out.println("<tr class=\"jive-even\">");

                    out.println("<td width=\"40%\">");
                    out.println(user.getUsername());
                    out.println("</td>");
                    out.println("<td width=\"30%\">");
                    out.println(user.getSiteName());
                    out.println("</td>");
                   out.println("<td width=\"15%\">");
                    out.println("<a href=\"user-summary?action=deleteConfirm&username=" + user.getUsername() + "\"><img src=\"images/delete-16x16.gif\" alt=\"Delete User\" border=\"0\"></a>");
                    out.println("</td>");
                    out.println("</tr>");
                    i++;
                }
            }
            
            out.println("<tr>");
            out.println("<td colspan='3'>");
            out.println("<a href=\"user-settings?action=top\"><img src=\"images/add-16x16.gif\" alt=\"Add new Site\" border=\"0\">Add new User Mapping</a>");
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
			out.println("        <title>Delete User Mapping?</title>");
			out.println("        <meta name=\"pageID\" content=\"USER-SUMMARY\"/>");
			out.println("    </head>");
			out.println("    <body>");
			out.println("");
			out.println("<p>");
			out.println("<b>");
			out.println("Are you sure you want to delete the User Mapping from the site?");
			out.println("</p>");
			out.println("</b>");
			out.println("<form action=\"user-summary\">");
			out.println("<input type='hidden' name='username' value='" + username + "'>");
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
