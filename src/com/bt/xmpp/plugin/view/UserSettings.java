package com.bt.xmpp.plugin.view;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.util.Log;

import com.bt.xmpp.plugin.ITSUser;
import com.bt.xmpp.plugin.ITSUserDao;
import com.bt.xmpp.plugin.Site;
import com.bt.xmpp.plugin.SiteDao;

public class UserSettings  extends HttpServlet {
	
	private static final long serialVersionUID = 5636234770730080943L;
	private String action                   = "";
	private String username  				= "";
	private long   siteID;
	private UserManager userManager;
	private ITSUserDao userDao;
	private SiteDao siteDao;

	private String errorMessage = null;

	public void init(ServletConfig config) throws ServletException {
	    super.init(config);

	    userManager = XMPPServer.getInstance().getUserManager();
	}

	private boolean validateXMPPUsernmae(String username) {
		boolean validateResult = false;
		Collection<User> users = userManager.getUsers();
		for(User user : users) {
			if(user.getUsername().equals(username)) {
				validateResult = true;
				break;
			}
		}

		return validateResult;
	}


	private boolean isUserMapExist(String username) {
		boolean result = false;
		Collection<ITSUser>users = userDao.getUsers();
		for(ITSUser user : users) {
			if(user.getUsername().equals(username)) {
				result = true;
				break;
			}
		}

		return result;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Content-Type", "text/html");
		response.setHeader("Connection", "close");

		ServletOutputStream out = response.getOutputStream();
		Map<String, String> errors = new HashMap<String, String>();

		userDao = new ITSUserDao();
		siteDao = new SiteDao();

		action = request.getParameter("action");

		if(action == null) {
			action = " ";
		}
		if(action.equals("top")) {
			username = "";
			displayPage(out, errors.size());
		}
		else if(action.equals("add")) {

			username = request.getParameter("username");
			siteID   = Long.parseLong(request.getParameter("siteID"));
			boolean validationResult = this.validateXMPPUsernmae(username);

			if(validationResult) {
				boolean mapResult = this.isUserMapExist(username);

				if(mapResult) {
					errors.put("username", "");
					errorMessage = "This XMPP User has already mapped to ITS Site";

					displayPage(out, errors.size());
				}
				else {
					if(errors.isEmpty()) {
						ITSUser user = new ITSUser();
						user.setUsername(username);
						user.setSiteID(siteID);

						try {
							userDao.insert(user);
						}
						catch (SQLException e) {
							Log.error(e.getMessage(), e);
						}

						RequestDispatcher rd = request.getRequestDispatcher("user-summary");
						rd.forward(request, response);
					}
					else {
						errors.put("username", "");
						errorMessage = "Invalid Parameters";

						displayPage(out, errors.size());
					}
				}
			}
			else {
				errors.put("username", "");
				errorMessage = "Invalid XMPP Username";

				displayPage(out, errors.size());
			}
		}
		else {
			displayPage(out, errors.size());
		}
	}



	private void displayPage(ServletOutputStream out, int errorSize) {
		try {
			out.println("");
			out.println("<html>");
			out.println("    <head>");
			out.println("        <title>User Mapping</title>");
			out.println("        <meta name=\"pageID\" content=\"USER-SUMMARY\"/>");
			out.println("    </head>");
			out.println("    <body>");

			if (errorSize > 0) {
				out.println("<div class=\"error\">");
				out.println(errorMessage);
				out.println("</div>");
			}
			out.println("");
			out.println("Use the form below to map user to ITS site<br>");
			out.println("</p>");
			out.println("<form action=\"user-settings\" method=\"get\">");
			out.println("<input type='hidden' name='action' value='add'>");
			out.println("");

			out.println("<div class=\"jive-contentBoxHeader\">User Mapping</div>");
			out.println("<div class=\"jive-contentBox\">");
			out.println("<table>");
			out.println("<tr>");
			out.println("<td>XMPP Username</td>");
			out.println("<td>");
			out.println("<select size='1' name='username'>");

			Collection<User> users = userManager.getUsers();
			for(User user : users) {
				out.println("<option value='" + user.getUsername() + "'>" + user.getUsername() + " (" + user.getName() + ")</option>");
			}
			
			out.println("</select>");
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("<td>ITS Site</td>");
			out.println("<td>");
			out.println("<select size='1' name='siteID'>");

			Collection<Site> sites = siteDao.getSites();
			for(Site site : sites) {
				out.println("<option value='" + site.getSiteID() + "'>" + site.getName() + "</option>");
			}
			
			out.println("</select>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("</div>");
			out.println("");

			out.println("&nbsp;<p/>&nbsp;<p/><input type=\"submit\" value=\"Save Properties\">");
			out.println("</form>");
			out.println("");
			out.println("</body>");
			out.println("</html>");
        }
        catch (Exception e) {
        	Log.error(e);
        }
	}

}
