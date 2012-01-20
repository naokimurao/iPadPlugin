package com.bt.xmpp.plugin;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SiteDao {

	private static final String GET_SITEBYID = "SELECT * FROM ofipadanywhere WHERE siteID = ? ";

	private static final String GET_ALLSITE = "SELECT * FROM ofipadanywhere";

	private static final String INSERT_SITE =
		"INSERT INTO ofipadanywhere (siteID, name, dalUrl, itslink1, itslink2, itslinkPort, itslinkCos, lineStatusOnLoad," +
		"voiceCallset,  vscUseHandsets34, vscConsoleStartRange1, vscConsoleCount1, vscConsoleStartRange2, vscConsoleCount2, " +
		"vscConsoleStartRange3, vscConsoleCount3, vscConsoleStartRange4, vscConsoleCount4) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String UPDATE_SITE =
		"UPDATE ofipadanywhere SET name = ?, dalUrl = ?, itslink1=?, itslink2=?, itslinkPort=?, itslinkCos=?,  lineStatusOnLoad=?, voiceCallset=? " +
		", vscUseHandsets34=?, vscConsoleStartRange1=?, vscConsoleCount1=?, vscConsoleStartRange2=?, vscConsoleCount2=?, vscConsoleStartRange3=?, vscConsoleCount3=? " +
		", vscConsoleStartRange4=?, vscConsoleCount4=?  WHERE siteID=?";

	private static final String DELETE_SITE = "DELETE FROM ofipadanywhere WHERE siteID = ?";

	private IPadAnywherePlugin plugin;

	public SiteDao() {
		plugin = (IPadAnywherePlugin)XMPPServer.getInstance().getPluginManager().getPlugin("ipadanywhere");
	}

	public Site getSiteByID(long  siteID) {
		Site site = null;
		Connection con = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;

		try {
			con = DbConnectionManager.getConnection();
			psmt = con.prepareStatement(GET_SITEBYID);
			psmt.setLong(1, siteID);
			rs = psmt.executeQuery();

			if (rs.next()) {
				site = read(rs);
			}
		} 
		catch (SQLException e) {
			Log.error(e.getMessage(), e);
		} 
		finally {
			DbConnectionManager.closeConnection(rs, psmt, con);
		}
		
		return site;
	}

	private static Site read(ResultSet rs) {
		Site site = null;
		try {

			long siteID = rs.getLong("siteID");
			String name = rs.getString("name");
			String dalUrl = rs.getString("dalUrl");
			String itslink1 = rs.getString("itslink1");
			String itslink2 = rs.getString("itslink2");
			String itslinkPort = rs.getString("itslinkPort");
			String itslinkCos = rs.getString("itslinkCos");
			String lineStatusOnLoad = rs.getString("lineStatusOnLoad");
			String voiceCallset = rs.getString("voiceCallset");
			String vscUseHandsets34 = rs.getString("vscUseHandsets34");
			String vscConsoleStartRange1 = rs.getString("vscConsoleStartRange1");
			String vscConsoleCount1 = rs.getString("vscConsoleCount1");
			String vscConsoleStartRange2 = rs.getString("vscConsoleStartRange2");
			String vscConsoleCount2 = rs.getString("vscConsoleCount2");
			String vscConsoleStartRange3 = rs.getString("vscConsoleStartRange3");
			String vscConsoleCount3 = rs.getString("vscConsoleCount3");
			String vscConsoleStartRange4 = rs.getString("vscConsoleStartRange4");
			String vscConsoleCount4 = rs.getString("vscConsoleCount4");
			
			site = new Site();
			site.setSiteID(siteID);
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
			
        }
		catch (SQLException e) {
			Log.error(e.getMessage(), e);
		}
		return site;
	}

	public void insert(Site site) throws SQLException {

		Log.info("Adding new site " + site.getName());
		Connection con = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		//site.setSiteID(SequenceManager.nextID(site));
		try {
			con = DbConnectionManager.getConnection();
			psmt = con.prepareStatement(INSERT_SITE);
			psmt.setLong(1, site.getSiteID());
			psmt.setString(2, site.getName());
			psmt.setString(3, site.getDalUrl());
			psmt.setString(4, site.getItslink1());
			psmt.setString(5, site.getItslink2());
			psmt.setString(6, site.getItslinkPort());
			psmt.setString(7, site.getItslinkCos());
			psmt.setString(8, site.getLineStatusOnLoad());
			psmt.setString(9, site.getVoiceCallset());
			psmt.setString(10, site.getVscUseHandsets34());
			psmt.setString(11, site.getVscConsoleStartRange1());
			psmt.setString(12, site.getVscConsoleCount1());
			psmt.setString(13, site.getVscConsoleStartRange2());
			psmt.setString(14, site.getVscConsoleCount2());
			psmt.setString(15, site.getVscConsoleStartRange3());
			psmt.setString(16, site.getVscConsoleCount3());
			psmt.setString(17, site.getVscConsoleStartRange4());
			psmt.setString(18, site.getVscConsoleCount4());
			
            psmt.executeUpdate();
		} 
		catch (SQLException e) {
			Log.error(e.getMessage(), e);
			throw new SQLException(e.getMessage());
		} 
		finally {
			DbConnectionManager.closeConnection(rs, psmt, con);
		}

		plugin.getIPadAnywhereComponent().siteAdded(site);
	}

	public void update(Site site) throws SQLException {

		Log.info("Updating site " + site.getName());
		Connection con = null;
		PreparedStatement psmt = null;
		try {
			con = DbConnectionManager.getConnection();
			psmt = con.prepareStatement(UPDATE_SITE);
			psmt.setString(1, site.getName());
			psmt.setString(2, site.getDalUrl());
			psmt.setString(3, site.getItslink1());
			psmt.setString(4, site.getItslink2());
			psmt.setString(5, site.getItslinkPort());
			psmt.setString(6, site.getItslinkCos());
			psmt.setString(7, site.getLineStatusOnLoad());
			psmt.setString(8, site.getVoiceCallset());
			psmt.setString(9, site.getVscUseHandsets34());
			psmt.setString(10, site.getVscConsoleStartRange1());
			psmt.setString(11, site.getVscConsoleCount1());
			psmt.setString(12, site.getVscConsoleStartRange2());
			psmt.setString(13, site.getVscConsoleCount2());
			psmt.setString(14, site.getVscConsoleStartRange3());
			psmt.setString(15, site.getVscConsoleCount3());
			psmt.setString(16, site.getVscConsoleStartRange4());
			psmt.setString(17, site.getVscConsoleCount4());
			
			psmt.setLong(18, site.getSiteID());
            psmt.executeUpdate();

		} 
		catch (SQLException e) {
			Log.error(e.getMessage(), e);
			throw new SQLException(e.getMessage());
		} 
		finally {
			DbConnectionManager.closeConnection(psmt, con);
		}

		plugin.getIPadAnywhereComponent().siteUpdated(site);
	}

	public void remove(long siteID) {
		Site site = this.getSiteByID(siteID);
		plugin.getIPadAnywhereComponent().siteRemoved(site);

		Log.info("Deleteing site " + siteID);
		Connection con = null;
		PreparedStatement psmt = null;

		try {
			con = DbConnectionManager.getConnection();
			psmt = con.prepareStatement(DELETE_SITE);
            psmt.setLong(1, siteID);
			psmt.executeUpdate();
            psmt.close();

        } 
		catch (SQLException e) {
			Log.error(e.getMessage(), e);
		} 
		finally {
			DbConnectionManager.closeConnection(psmt, con);
		}
	}

	public Collection<Site> getSites() {

		List<Site> sites = new ArrayList<Site>();
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = DbConnectionManager.createScrollablePreparedStatement(con, GET_ALLSITE);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				sites.add(read(rs));
			}
			rs.close();
		}
		catch (SQLException e) {
			Log.error(e);
		} 
		finally {
			try {
				if (pstmt != null) 
					pstmt.close();
			} 
			catch (Exception e) {
				Log.error(e);
			}
			try {
				if (con != null) 
					con.close();
			}
			catch (Exception e) {
				Log.error(e);
			}
		}
		return sites;
	}

	public static Collection<Site> getSites2() {

		List<Site> sites = new ArrayList<Site>();
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = DbConnectionManager.createScrollablePreparedStatement(con, GET_ALLSITE);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				sites.add(read(rs));
			}
			rs.close();
		}
		catch (SQLException e) {
			Log.error(e);
		} 
		finally {
			try {
				if (pstmt != null) 
					pstmt.close();
				
			} 
			catch (Exception e) {
				Log.error(e);
			}
			try {
				if (con != null) 
					con.close();
			} 
			catch (Exception e) {
				Log.error(e);
			}
		}
		return sites;
	}

}
