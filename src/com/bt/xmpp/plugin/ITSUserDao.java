package com.bt.xmpp.plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.util.Log;

public class ITSUserDao {
	
	private static final String GET_USERS = "SELECT ofipadanywhereuser.username, ofipadanywhereuser.siteID, ofipadanywhere.name from ofipadanywhereuser INNER JOIN ofipadanywhere ON ofipadanywhereuser.siteID = ofipadanywhere.siteID ";
	
	private static final String GET_USERSBYSITE = "SELECT ofipadanywhereuser.username, ofipadanywhereuser.siteID, ofipadanywhere.name from ofipadanywhereuser INNER JOIN ofipadanywhere ON ofipadanywhereuser.siteID = ofipadanywhere.siteID where ofipadanywhere.siteID = ?";
	
	private static final String GET_USER  = "SELECT ofipadanywhereuser.username, ofipadanywhereuser.siteID, ofipadanywhere.name from ofipadanywhereuser INNER JOIN ofipadanywhere ON ofipadanywhereuser.siteID = ofipadanywhere.siteID " +
											" where ofipadanywhereuser.username = ?";
	
	private static final String INSERT_USER =  "INSERT INTO ofipadanywhereuser (username, siteID) VALUES (?,?)";
	
	private static final String DELETE_BYUSER = "DELETE FROM ofipadanywhereuser WHERE username = ?";
	
	private static final String DELETE_BYSITE = "DELETE FROM ofipadanywhereuser WHERE siteID = ?";
	
	private IPadAnywherePlugin plugin;
	
	public ITSUserDao() {
		plugin = (IPadAnywherePlugin)XMPPServer.getInstance().getPluginManager().getPlugin("ipadanywhere");
	}
	
	public ITSUser getUserByName(String username) {
		ITSUser user = null;
		Connection con = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		
		try {
			con = DbConnectionManager.getConnection();
			psmt = con.prepareStatement(GET_USER);
			psmt.setString(1, username);
			rs = psmt.executeQuery();

			if (rs.next()) {
				user = read(rs);
			}

		} catch (SQLException e) {
			Log.error(e.getMessage(), e);
		} finally {
			DbConnectionManager.closeConnection(rs, psmt, con);
		}
		
		return user;
	}
	
	private static ITSUser read(ResultSet rs) {
		ITSUser user = null;
		try {
			String username = rs.getString("username");
			long siteID 	= rs.getLong("siteID");
			String siteName = rs.getString("name");
						
			user = new ITSUser();
			user.setUsername(username);
			user.setSiteID(siteID);
			user.setSiteName(siteName);
        } 
		catch (SQLException e) {
			Log.error(e.getMessage(), e);
		}
		return user;
	}
	
	public void insert(ITSUser user) throws SQLException {	
		Log.info("Adding new user Mapping " + user.getUsername());
		Connection con = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		
		try {
			con = DbConnectionManager.getConnection();
			psmt = con.prepareStatement(INSERT_USER);
			psmt.setString(1, user.getUsername());
			psmt.setLong(2, user.getSiteID());	
            psmt.executeUpdate();

		} catch (SQLException e) {
			Log.error(e.getMessage(), e);
			throw new SQLException(e.getMessage());
		} finally {
			DbConnectionManager.closeConnection(rs, psmt, con);
		}
		
		plugin.getIPadAnywhereComponent().ITSUserAdded(user);
	}
	
	public void removeByUsername(String username) {
		Log.info("Deleteing user " + username);
		ITSUser user = this.getUserByName(username);
		plugin.getIPadAnywhereComponent().ITSUserRemoved(user);
		
		Connection con = null;
		PreparedStatement psmt = null;
		
		try {
			con = DbConnectionManager.getConnection();
			psmt = con.prepareStatement(DELETE_BYUSER);
			psmt.setString(1, username);
            psmt.executeUpdate();
            psmt.close();

        } catch (SQLException e) {
			Log.error(e.getMessage(), e);
		} finally {
			DbConnectionManager.closeConnection(psmt, con);
		}
	}
	
	public void removeBySite(long siteID) {
		Log.info("Deleteing users by site " + siteID);
		Collection<ITSUser> tmpUsers = this.getUsersBySite(siteID);
		
		Connection con = null;
		PreparedStatement psmt = null;
		
		try {
			con = DbConnectionManager.getConnection();
			psmt = con.prepareStatement(DELETE_BYSITE);
            psmt.setLong(1, siteID);
			psmt.executeUpdate();
            psmt.close();
            
            plugin.getIPadAnywhereComponent().ITSUserRemovedBySite(tmpUsers);

        } catch (SQLException e) {
			Log.error(e.getMessage(), e);
		} finally {
			DbConnectionManager.closeConnection(psmt, con);
		}		
	}
	
	public Collection<ITSUser> getUsers() {	
		List<ITSUser> users = new ArrayList<ITSUser>();
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = DbConnectionManager.createScrollablePreparedStatement(con, GET_USERS);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				users.add(read(rs));
			}
			rs.close();
		} 
		catch (SQLException e) {
			Log.error(e);
		} 
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} 
			catch (Exception e) {
				Log.error(e);
			}
			try {
				if (con != null) {
					con.close();
				}
			} 
			catch (Exception e) {
				Log.error(e);
			}
		}
		return users;
	}
	
	public Collection<ITSUser> getUsersBySite(long siteID) {
		List<ITSUser> users = new ArrayList<ITSUser>();
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = DbConnectionManager.createScrollablePreparedStatement(con, GET_USERSBYSITE);
			pstmt.setLong(1, siteID);
			ResultSet rs = pstmt.executeQuery();
			
			while (rs.next()) {
				users.add(read(rs));
			}
			rs.close();
		} 
		catch (SQLException e) {
			Log.error(e);
		} 
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} 
			catch (Exception e) {
				Log.error(e);
			}
			try {
				if (con != null) {
					con.close();
				}
			} 
			catch (Exception e) {
				Log.error(e);
			}
		}
		return users;
	}

}

