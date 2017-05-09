package com.ajna.riskman.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

import com.ajna.hfxclnt.model.Order;
import com.ajna.hfxclnt.model.OrderDao;

public class DaoController {

	  String mDriver = "org.apache.derby.jdbc.EmbeddedDriver";

	  //String dbName="W:\\work\\db-derby\\database\\TradeDB";
	  String dbName="database\\TradeDB";
	  String connectionURL = "jdbc:derby:" + dbName + ";create=true";

	  Connection mConn = null;
	  
	  OrderDao mOrdersDao;
	  
	  public DaoController() throws SQLException {
		  mConn = DriverManager.getConnection(connectionURL);		 
          System.out.println("Connected to database " + dbName);
          
          mOrdersDao = new OrderDao(mConn);
          
          this.showOrders();
	  }
	  
	  public void addOrder(String sym, int qty, String side,double prc, String optype, Date expryDate, double strkPrc){
		  try{

			  mOrdersDao.insertOrder( sym,  qty,  side, prc,  optype,  expryDate,  strkPrc);

		  } catch (SQLException e) {
				 e.printStackTrace();
			}	
	  }
	  
	  public void removeOrder(int oid) {
		  try {
			  mOrdersDao.deleteOrder(oid);
		  } catch (SQLException e) {
			  e.printStackTrace();
		  }
	  }
	  
	  public Vector<Order> getOrders() {
		  Vector<Order> ret = new Vector<Order>();
		  try {
			 ret = mOrdersDao.getOrders(); 
		  } catch (SQLException e) {
				 e.printStackTrace();
			}	
		  return ret;
	  }
	  
	  
	  public void showOrders() {
		  try {
			  mOrdersDao.showTable(); 
		  } catch (SQLException e) {
				 e.printStackTrace();
			}	
	  }
	  public Connection getDBConn(){
		  return mConn;
	  }
	      
}
