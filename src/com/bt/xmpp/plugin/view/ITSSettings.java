package com.bt.xmpp.plugin.view;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.jivesoftware.util.Log;
import org.jivesoftware.database.SequenceManager;

import com.bt.xmpp.plugin.Site;
import com.bt.xmpp.plugin.SiteDao;

public class ITSSettings extends HttpServlet {
	
	private static final long serialVersionUID = -5502146814688883310L;
	private String action                   = "";
	private long   siteID					= 0;
	private String name  					= "";
	private String dalUrl					= "";
	private String itslink1  				= "";
	private String itslink2  				= "";
	private String itslinkPort  			= "";
	private String itslinkCos  				= "";
	private String lineStatusOnLoad  		= "";
	private String voiceCallset  			= "";
	private String vscUseHandsets34  		= "";
	private String vscConsoleStartRange1  	= "";
	private String vscConsoleCount1  		= "";
	private String vscConsoleStartRange2  	= "";
	private String vscConsoleCount2  		= "";
	private String vscConsoleStartRange3 	= "";
	private String vscConsoleCount3  		= "";
	private String vscConsoleStartRange4  	= "";
	private String vscConsoleCount4  	  	= "";
	
	private SiteDao siteDao;

	private String errorMessage = null;


	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Content-Type", "text/html");
		response.setHeader("Connection", "close");

		ServletOutputStream out = response.getOutputStream();
		Map<String, String> errors = new HashMap<String, String>();


		siteDao = new SiteDao();

		action = request.getParameter("action");

		if(action == null) {
			action = " ";
		}

		if(action.equals("top")) {
			name  					= "";
			dalUrl					= "";
			itslink1  				= "";
			itslink2  				= "";
			itslinkPort  			= "3001";
			itslinkCos  			= "32";
			lineStatusOnLoad  		= "";
			voiceCallset  			= "";
			vscUseHandsets34  		= "";
			vscConsoleStartRange1  	= "0";
			vscConsoleCount1  		= "0";
			vscConsoleStartRange2  	= "0";
			vscConsoleCount2  		= "0";
			vscConsoleStartRange3 	= "0";
			vscConsoleCount3  		= "0";
			vscConsoleStartRange4  	= "0";
			vscConsoleCount4  	  	= "0";
			
			displayPage(out, errors.size());
		}

		else if(action.equals("add")) {

			name 					= request.getParameter("name");
			dalUrl					= request.getParameter("dalUrl");
			itslink1 				= request.getParameter("itslink1");
			itslink2 				= request.getParameter("itslink2");
			itslinkPort 			= request.getParameter("itslinkPort");
			itslinkCos 				= request.getParameter("itslinkCos");
			
			if(request.getParameter("lineStatusOnLoad") == null)
				lineStatusOnLoad = "no";
			else
				lineStatusOnLoad = "yes";

			voiceCallset 			= request.getParameter("voiceCallset");
			
			if(request.getParameter("vscUseHandsets34") == null)
				vscUseHandsets34 = "no";
			else
				vscUseHandsets34 = "yes";

			vscConsoleStartRange1 	= request.getParameter("vscConsoleStartRange1");
			vscConsoleCount1 		= request.getParameter("vscConsoleCount1");
			vscConsoleStartRange2 	= request.getParameter("vscConsoleStartRange2");
			vscConsoleCount2 		= request.getParameter("vscConsoleCount2");
			vscConsoleStartRange3 	= request.getParameter("vscConsoleStartRange3");
			vscConsoleCount3 		= request.getParameter("vscConsoleCount3");
			vscConsoleStartRange4 	= request.getParameter("vscConsoleStartRange4");
			vscConsoleCount4 	  	= request.getParameter("vscConsoleCount4");


			if(name.length() < 1 ) {
				errors.put("sitename", "");
				errorMessage = "Please specify Site Name";
			}

			if(itslink1.length() < 1 ) {
				errors.put("itslink1", "");
				errorMessage = "Please specify Primary ITSLink";
			}

			if(errors.isEmpty()) {
				Site site = new Site();
				site.setSiteID(SequenceManager.nextID(site));
				site.setName(name);
				site.setDalUrl(dalUrl);
				site.setItslink1(itslink1);
				site.setItslink2(itslink2);
				site.setItslinkPort(itslinkPort);
				site.setItslinkCos(itslinkCos);
				site.setLineStatusOnLoad(lineStatusOnLoad);
				site.setVoiceCallset(voiceCallset);
				site.setVscUseHandsets34(vscUseHandsets34);
				site.setVscConsoleStartRange1(vscConsoleStartRange1);
				site.setVscConsoleCount1(vscConsoleCount1);
				site.setVscConsoleStartRange2(vscConsoleStartRange2);
				site.setVscConsoleCount2(vscConsoleCount2);
				site.setVscConsoleStartRange3(vscConsoleStartRange3);
				site.setVscConsoleCount3(vscConsoleCount3);
				site.setVscConsoleStartRange4(vscConsoleStartRange4);
				site.setVscConsoleCount4(vscConsoleCount4);
				
				try {
					siteDao.insert(site);
					Log.info("ITS Setings: Database added: site " + site.getSiteID());
				}
				catch (SQLException e) {
					Log.error(e.getMessage(), e);
				}

				RequestDispatcher rd = request.getRequestDispatcher("its-summary");
				rd.forward(request, response);

			}
			else {
				displayPage(out, errors.size());
			}
		}
		else if(action.equals("edit")) {
			siteID = Long.parseLong(request.getParameter("siteID"));
			Site site = siteDao.getSiteByID(siteID);
			name  					= site.getName();
			dalUrl					= site.getDalUrl();
			itslink1  				= site.getItslink1();
			itslink2  				= site.getItslink2();
			itslinkPort  			= site.getItslinkPort();
			itslinkCos  			= site.getItslinkCos();
			lineStatusOnLoad  		= site.getLineStatusOnLoad();
			voiceCallset  			= site.getVoiceCallset();
			vscUseHandsets34  		= site.getVscUseHandsets34();
			vscConsoleStartRange1  	= site.getVscConsoleStartRange1();
			vscConsoleCount1  		= site.getVscConsoleCount1();
			vscConsoleStartRange2  	= site.getVscConsoleStartRange2();
			vscConsoleCount2  		= site.getVscConsoleCount2();
			vscConsoleStartRange3 	= site.getVscConsoleStartRange3();
			vscConsoleCount3  		= site.getVscConsoleCount3();
			vscConsoleStartRange4  	= site.getVscConsoleStartRange4();
			vscConsoleCount4  	  	= site.getVscConsoleCount4();
			
			displayPage(out, errors.size());
		}

		else if(action.equals("update")) {
			siteID = Long.parseLong(request.getParameter("siteID"));
			name 					= request.getParameter("name");
			dalUrl					= request.getParameter("dalUrl");
			itslink1 				= request.getParameter("itslink1");
			itslink2 				= request.getParameter("itslink2");
			itslinkPort 			= request.getParameter("itslinkPort");
			itslinkCos 				= request.getParameter("itslinkCos");
			
			if(request.getParameter("lineStatusOnLoad") == null)
				lineStatusOnLoad = "no";
			else
				lineStatusOnLoad = "yes";

			voiceCallset 			= request.getParameter("voiceCallset");
			
			if(request.getParameter("vscUseHandsets34") == null)
				vscUseHandsets34 = "no";
			else
				vscUseHandsets34 = "yes";

			vscConsoleStartRange1 	= request.getParameter("vscConsoleStartRange1");
			vscConsoleCount1 		= request.getParameter("vscConsoleCount1");
			vscConsoleStartRange2 	= request.getParameter("vscConsoleStartRange2");
			vscConsoleCount2 		= request.getParameter("vscConsoleCount2");
			vscConsoleStartRange3 	= request.getParameter("vscConsoleStartRange3");
			vscConsoleCount3 		= request.getParameter("vscConsoleCount3");
			vscConsoleStartRange4 	= request.getParameter("vscConsoleStartRange4");
			vscConsoleCount4 	  	= request.getParameter("vscConsoleCount4");

			if(name.length() < 1 ) {
				errors.put("sitename", "");
				errorMessage = "Please specify Site Name";
			}
			if(itslink1.length() < 1 ) {
				errors.put("itslink1", "");
				errorMessage = "Please specify Primary ITSLink";
			}

			if(errors.isEmpty()) {
				Site site = siteDao.getSiteByID(siteID);
				site.setName(name);
				site.setDalUrl(dalUrl);
				site.setItslink1(itslink1);
				site.setItslink2(itslink2);
				site.setItslinkPort(itslinkPort);
				site.setItslinkCos(itslinkCos);
				site.setLineStatusOnLoad(lineStatusOnLoad);
				site.setVoiceCallset(voiceCallset);
				site.setVscUseHandsets34(vscUseHandsets34);
				site.setVscConsoleStartRange1(vscConsoleStartRange1);
				site.setVscConsoleCount1(vscConsoleCount1);
				site.setVscConsoleStartRange2(vscConsoleStartRange2);
				site.setVscConsoleCount2(vscConsoleCount2);
				site.setVscConsoleStartRange3(vscConsoleStartRange3);
				site.setVscConsoleCount3(vscConsoleCount3);
				site.setVscConsoleStartRange4(vscConsoleStartRange4);
				site.setVscConsoleCount4(vscConsoleCount4);
				
				try {
					siteDao.update(site);
					Log.info("ITS Setings: Database updated site: " + site.getSiteID());
				}
				catch (SQLException e) {
					Log.error(e.getMessage(), e);
				}

				RequestDispatcher rd = request.getRequestDispatcher("its-summary");
				rd.forward(request, response);

			}
			else {
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
			out.println("        <title>ITS Properties</title>");
			out.println("        <meta name=\"pageID\" content=\"ITS-SUMMARY\"/>");
			out.println("    </head>");
			out.println("    <body>");

			if (errorSize > 0) {
				out.println("<div class=\"error\">");
				out.println(errorMessage);
				out.println("</div>");
			}
			out.println("");
			out.println("Use the form below to edit ITS Properties.<br>");
			out.println("</p>");
			out.println("<form action=\"its-settings\" method=\"get\">");

			if(action.equals("edit")) {
				out.println("<input type='hidden' name='action' value='update'>");
				out.println("<input type='hidden' name='siteID' value='" + siteID + "'>");

			} else if(action.equals("top")) {
				out.println("<input type='hidden' name='action' value='add'>");

			} else {
				out.println("<input type='hidden' name='action' value='" + action + "'>");
				out.println("<input type='hidden' name='siteID' value='" + siteID + "'>");
			}
			out.println("");

			out.println("<div class=\"jive-contentBoxHeader\">General</div>");
			out.println("<div class=\"jive-contentBox\">");
			out.println("	 <table>");
			out.println("	 	<tr><td width=\"30%\">Site Name</td><td><input size='20' type='text' name='name' value='" + name + "'></td>");
			out.println("	 		<td>Name of this ITS site</td></tr>");
			out.println("	 	<tr><td width=\"30%\">DAL URL</td><td><input size='20' type='text' name='dalUrl' value='" + dalUrl + "'></td>");
			out.println("	 		<td>Location of DAL(Data Access Layer) service</td></tr>");
			if(lineStatusOnLoad.equals("yes")) {
				out.println("	 	<tr><td width=\"30%\">Line Status On Load</td><td><input size='20' type='checkbox' checked='yes' name='lineStatusOnLoad' value='" + lineStatusOnLoad + "' ></td>");
				out.println("	 		<td>Load Line State when iTrader start up</td></tr>");
			} else {
				out.println("	 	<tr><td width=\"30%\">Line Status On Load</td><td><input size='20' type='checkbox' name='lineStatusOnLoad' value='" + lineStatusOnLoad + "' ></td>");
				out.println("	 		<td>Load Line State when iTrader start up</td></tr>");
			}
			out.println("	 	<tr><td width=\"30%\">Voice Callset</td><td><input size='20' type='text' name='voiceCallset' value='" + voiceCallset + "' ></td>");
			out.println("	 		<td>Callset Number for Remote Handset</td></tr>");
			if(vscUseHandsets34.equals("yes")) {
				out.println("	 	<tr><td width=\"30%\">VSC UseHandsets34</td><td><input size='20' type='checkbox' checked='yes' name='vscUseHandsets34' ></td>");
				out.println("	 		<td>Use Handset3 and 4 for VSC?</td></tr>");
			} else {
				out.println("	 	<tr><td width=\"30%\">VSC UseHandsets34</td><td><input size='20' type='checkbox' name='vscUseHandsets34' ></td>");
				out.println("	 		<td>Use Handset3 and 4 for VSC?</td></tr>");
			}
			out.println("	 	<tr><td width=\"30%\">VSC ConsoleStartRange1</td><td><input size='20' type='text' name='vscConsoleStartRange1' value='" + vscConsoleStartRange1 + "' ></td>");
			out.println("	 		<td>First VSC Console number for VSC Block 1</td></tr>");
			out.println("	 	<tr><td width=\"30%\">VSC ConsoleCount1</td><td><input size='20' type='text' name='vscConsoleCount1' value='" + vscConsoleCount1 + "'></td>");
			out.println("	 		<td>VSC console count for VSC Block 1</td></tr>");
			out.println("	 	<tr><td width=\"30%\">VSC ConsoleStartRange2</td><td><input size='20' type='text' name='vscConsoleStartRange2' value='" + vscConsoleStartRange2 + "'></td>");
			out.println("	 		<td>First VSC Console number for VSC Block 2</td></tr>");
			out.println("	 	<tr><td width=\"30%\">VSC ConsoleCount2</td><td><input size='20' type='text' name='vscConsoleCount2' value='" + vscConsoleCount2 + "' ></td>");
			out.println("	 		<td>VSC console count for VSC Block 2</td></tr>");
			out.println("	 	<tr><td width=\"30%\">VSC ConsoleStartRange3</td><td><input size='20' type='text' name='vscConsoleStartRange3' value='" + vscConsoleStartRange3 + "' ></td>");
			out.println("	 		<td>First VSC Console number for VSC Block 3</td></tr>");
			out.println("	 	<tr><td width=\"30%\">VSC ConsoleCount3</td><td><input size='20' type='text' name='vscConsoleCount3' value='" + vscConsoleCount3 + "'></td>");
			out.println("	 		<td>VSC console count for VSC Block 3</td></tr>");
			out.println("	 	<tr><td width=\"30%\">VSC ConsoleStartRange4</td><td><input size='20' type='text' name='vscConsoleStartRange4' value='" + vscConsoleStartRange4 + "'></td>");
			out.println("	 		<td>First VSC Console number for VSC Block 4</td></tr>");
			out.println("	 	<tr><td width=\"30%\">VSC ConsoleCount4</td><td><input size='20' type='text' name='vscConsoleCount4' value='" + vscConsoleCount4 + "'></td>");
			out.println("	 		<td>VSC console count for VSC Block 4</td></tr>");
			out.println("	 </table>");
			out.println("</div>");
			out.println("");

			out.println("<div class=\"jive-contentBoxHeader\">ITSlink</div>");
			out.println("<div class=\"jive-contentBox\">");
			out.println("	 <table>");
			out.println("	 	<tr><td width=\"30%\">Primary ITSLink</td><td><input size='20' type='text' name='itslink1' value='" + itslink1 + "'></td>");
			out.println("	 		<td>IP Address or Hostname of Primary ITSLink</td></tr>");
			out.println("	 	<tr><td width=\"30%\">Secondly ITSLink</td><td><input size='20' type='text' name='itslink2' value='" + itslink2 + "'></td>");
			out.println("	 		<td>IP Address or Hostname of Secondly ITSLink</td></tr>");
			out.println("	 	<tr><td width=\"30%\">ITSLink Port</td><td><input size='20' type='text' name='itslinkPort' value='" + itslinkPort + "'></td>");
			out.println("	 		<td>ITSLink port number. Default is 3001</td></tr>");
			out.println("	 	<tr><td width=\"30%\">Class Of Service</td><td><input size='20' type='text' name='itslinkCos' value='" + itslinkCos + "'></td>");
			out.println("	 		<td>ITSLink Class Of Service</td></tr>");
			out.println("	 </table>");
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
