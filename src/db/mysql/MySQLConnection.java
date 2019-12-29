package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterClient;

public class MySQLConnection implements DBConnection {
	
	private Connection conn;
	
	
	public MySQLConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
			//创建一个connection
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void close() {
		//检查：如果不是null就把它close
		if(conn != null) {
			try {
				conn.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}


	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		if(conn == null) {
			System.err.println("DB connection failed");
			
			return;
		}
		
		try {
			System.out.println(userId);
			//String sql =  "INSERT IGNORE INTO history(user_id, item_id) VALUES (?, ?)";
			String sql =  "INSERT INTO history(user_id,item_id) VALUES (?,?)";
			//INSERT INTO `table_name`(column_1,column_2,...) VALUES (value_1,value_2,...);
			//Statement statement = conn.createStatement();
			//statement.execute(sql);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);//按住userId
			
			for(String itemId : itemIds) {
				ps.setString(2, itemId);
				System.out.println(itemId);
				ps.execute();
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		if(conn == null) {
			System.err.println("DB connection failed");
			return;
		}
		
		try {
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			//两个primary 
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			for(String itemId : itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		

	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		
		Set<String> favoriteItems = new HashSet<>();
		
		try {
			String sql = "SELECT  item_id FROM history WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				String itemId = rs.getString("item_id");
				favoriteItems.add(itemId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return favoriteItems;

	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> itemIds = getFavoriteItemIds(userId);
		
		try {
			String sql = "SELECT * FROM items WHERE item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String itemId : itemIds) {
				stmt.setString(1, itemId);
				
				ResultSet rs = stmt.executeQuery();
				
				ItemBuilder builder = new ItemBuilder();
				
				while (rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(itemId));
					builder.setDistance(rs.getDouble("distance"));
					builder.setRating(rs.getDouble("rating"));
					
					favoriteItems.add(builder.build());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;



	}

	@Override
	public Set<String> getCategories(String itemId) {
		if (conn == null) {
			return null;
		}
		Set<String> categories = new HashSet<>();
		try {
			String sql = "SELECT category from categories WHERE item_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, itemId);

			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String category = rs.getString("category");
				categories.add(category);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return categories; 
	}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		//simply copied from what we've had in SearchItem.java
		TicketMasterClient ticketMasterClient = new TicketMasterClient();
		List<Item> items = ticketMasterClient.search(lat,lon,term);
		for(Item item:items) {
			saveItem(item);
		}
		return items;
	}

	@Override
	public void saveItem(Item item) {
        if (conn == null) {
  		   System.err.println("DB connection failed");
  		   return;
  	         }
  	
  	 try {
  		 String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
  		 PreparedStatement ps = conn.prepareStatement(sql);
  		 ps.setString(1, item.getItemId());
  		 ps.setString(2, item.getName());
  		 ps.setDouble(3, item.getRating());
  		 ps.setString(4, item.getAddress());
  		 ps.setString(5, item.getImageUrl());
  		 ps.setString(6, item.getUrl());
  		 ps.setDouble(7, item.getDistance());
  		 ps.execute();

  		 sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
  		 ps = conn.prepareStatement(sql);
                              // itemed 123
  		 ps.setString(1, item.getItemId());
                              // pop, music
  		 for(String category : item.getCategories()) {
  			 ps.setString(2, category);
  			 ps.execute();
  		 }
  		
  	 } catch (Exception e) {
  		 e.printStackTrace();
  	 }


	}

	@Override
	public String getFullname(String userId) {
		if(conn == null) {
			return "";
		}
		String name = "";
		try {
			String sql = "SELECT first_name, last_name FROM users WHERE user_id = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet resultSet = statement.executeQuery();
			while(resultSet.next()) {
				name = resultSet.getString("first_name")+" "+resultSet.getString("last_name");
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return name;
	}



	@Override
	public boolean verifyLogin(String userId, String password) {
		if(conn == null) {
			return false;
		}
		
		try {
			String sql = "SELECT user_id FROM users WHERE user_id = ? AND password = ?";
			PreparedStatement statement = conn.prepareStatement(sql);
			//准备一个sql语句，后面setString将问好替换掉，然后后面执行这个语句
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();
			if(resultSet.next()) {
				//看执行的语句是否有返回结果，如果有，返回result，false表示查询返回为空
				
				return true;
			}
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	@Override
	//注册用户的实现
	public boolean registerUser(String userId, String password, String firstName, String lastName) {
		if(conn == null) {
			System.err.println("db connection failed");
			return false;
		}
		
		String sql = "INSERT INTO users VALUES(?,?,?,?)";
		try {
			PreparedStatement statement  = conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			statement.setString(3, firstName);
			statement.setString(4, lastName);
			return statement.executeUpdate()==1;
			//executeUpdate返回值是1，正常添加，0则不正常
		} catch (SQLException e) {
			e.printStackTrace();
			
		}
		return false;
	}
	



}
