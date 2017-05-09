package com.ajna.hfxclnt.model;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.sql.Date;
import java.util.Arrays;

public class Order {

	//1. Symbol		String
	//2. Quantiy 	int
	//3. Side		String
	//4. Price		doube
	//5. OptionType	String
	//6. Expiry		String
	//7. Strike		String
	
	
	String orderID;
	String userID;
	Date createdDate;
	String symbol;
	int quantity;
	String side;
	int side_int;
	double price;
	String optType;
	Date	expiryDate;
	double	strike;
	
	double stopPrice;
	byte aon;
	byte ioc;
	int	filledQty;
	int accumFills;
	int	openQty;
	String status;
	//--------------
	
	//HfxClnt
	public Order(String ordID, String uID, int buy_side, int qty, String symbol, double price){
	 super();
	 this.orderID = ordID;
	 this.userID = uID;
	 this.side_int = buy_side;
	 this.quantity = qty;
	 this.price = price;
	 this.symbol = symbol;
	}
	
	//use this for cancel order
	
	public Order(String ordID, String mUserId, int qty, String symb) {
		 super();
		 this.orderID = ordID;
		 this.userID = mUserId;
		 this.quantity = qty;
		 this.symbol = symb;
		 
	}
	
	public ByteBuffer getHfxString(int type) {
 	
		/**
	char orderID[16];
	char userID[8];
	int  side;
	int  quantity;
	double price;
	char price_str[16];
	double stopPrice;
	char symbol[16];
	bool aon;
	bool ioc;
	int	filledQty;
	int	openQty;
	char status[16];
		 */
		int len = 88;
		ByteBuffer sendMsg = ByteBuffer.allocate(len);

		sendMsg.order(ByteOrder.LITTLE_ENDIAN);
		int curPos = 0;
		

		String oid = ""+ this.orderID;
		
		sendMsg.putInt(len);

		sendMsg.putInt(type);

		curPos = sendMsg.position();
		sendMsg.put(oid.getBytes());
		 
		curPos = curPos + 16;
		sendMsg.position(curPos);

		sendMsg.put(userID.getBytes());
		curPos = curPos + 8;
		sendMsg.position(curPos);
	
		sendMsg.putInt(side_int);

		sendMsg.putInt(quantity);

		sendMsg.putDouble(price);
		
		//hack to coverup 16bytes
		sendMsg.position(16+sendMsg.position());

		sendMsg.putDouble(0);

		curPos = sendMsg.position();
		sendMsg.put(symbol.getBytes());
		curPos = curPos + 16;
		sendMsg.position(curPos);
		//fillup another 26 bytes
		
		 
		return sendMsg ;
	}
	

	
	public Order(String orderID, Date createdDate, String symbol, int quantity, String side, double price, String optType,
			Date expiryDate, double strike) {
		super();
		this.orderID = orderID;
		this.createdDate = createdDate;
		this.symbol = symbol;
		this.quantity = quantity;
		this.side = side;
		this.price = price;
		this.optType = optType;
		this.expiryDate = expiryDate;
		this.strike = strike;
	}
	
	
	

	public Order(ByteBuffer bb){
		/**
		char orderID[16];  => 0
		char userID[8]; 
		int  side;
		int  quantity;
		double price;
		double stopPrice;
		char symbol[16];
		bool aon;
		bool ioc;
		int	filledQty;
		int	openQty;
		char status[16];
		//--------------------------------------------------------------//		
	int orderID;
	String userID;
	Date createdDate;
	String symbol;
	int quantity;
	String side;
	int side_int;
	double price;
	String optType;
	Date	expiryDate;
	double	strike;
	double stopPrice;
	boolean aon;
	boolean ioc;
	int	filledQty;
	int	openQty;
	String status;
	
	
		**/
 	
		//System.out.println( "Byte order is " + bb.order().toString());
		 
		
	
		
		//System.out.println("1. Position: " + bb.position() + ", Limit: " + bb.limit() );
		byte[] bytes16 = new byte[16];
		bb.get(bytes16, 0, 16);
		this.orderID = new String(bytes16);
		//System.out.println("OrderID is : " + orderID);
		//System.out.println("2. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		byte[] bytes8 = new byte[8];
		//System.out.println("Size of bytearray: " + bytes8.length);
		bb.get(bytes8);//, 16, 8);
		this.userID = new String(bytes8); 
		//System.out.println("userID is : " + userID);
		//System.out.println("3. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		int sideInt = bb.getInt();
		this.side = (sideInt==1 ? "BUY" : "SELL");
		//System.out.println("Side is: " + side);
		//System.out.println("4. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		this.quantity = bb.getInt();
		//System.out.println("quantity: " + quantity);
		//System.out.println("5. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		this.price = bb.getDouble();
		
		
		byte[] bytes10 = new byte[16];
		bb.get(bytes10); //, 48, 16);
		String price_str = new String(bytes10);
		this.price = Double.parseDouble(price_str);
		
		//System.out.println("price: " + price);
		//System.out.println("6. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		
		this.stopPrice = bb.getDouble();
		//System.out.println("Stop Price: " + stopPrice);
		//System.out.println("7. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		byte[] bytes9 = new byte[16];
		bb.get(bytes9); //, 48, 16);
		this.symbol = new String(bytes9);
		//System.out.println("Symbol: "+ symbol);
		//System.out.println("8. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		this.aon = bb.get();
		//System.out.println("aon: " + aon);
		//System.out.println("9. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		this.ioc = bb.get();
		//test this hack
		bb.position(bb.position()+2);
		//System.out.println("ioc: " + ioc);
		//System.out.println("10. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		this.filledQty = bb.getInt();
		//System.out.println("filledQty: " + filledQty);
		//System.out.println("11. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		this.openQty = bb.getInt();
		this.accumFills = this.quantity - this.openQty;
		
		//System.out.println("openQty: " + openQty);
		//System.out.println("12. Position: " + bb.position() + ", Limit: " + bb.limit() + ", remaining: " + bb.remaining());
		byte[] bytes7 = new byte[16];
		bb.get(bytes7);//, 74, 16);
		this.status = new String(bytes7);
		//System.out.println("status: " + status);
		
		//System.out.println("Integer at 79: " + bb.getInt(79) );
		//System.out.println("Integer at 80: " + bb.getInt(80) );
		//System.out.println("Integer at 81: " + bb.getInt(81) );
		//System.out.println("Integer at 82: " + bb.getInt(82) );
		//System.out.println("Integer at 83: " + bb.getInt(83) );
		//System.out.println("Integer at 84: " + bb.getInt(84) );
		//System.out.println("Integer at 85: " + bb.getInt(85) );
		//System.out.println("Integer at 86: " + bb.getInt(86) );
		//System.out.println("Integer at 87: " + bb.getInt(87) );
		//System.out.println("Integer at 88: " + bb.getInt(88) );
		//System.out.println("Integer at 89: " + bb.getInt(89) );
	}	
	


	public int getAccumFills() {
		return accumFills;
	}

	public void setAccumFills(int accumFills) {
		this.accumFills = accumFills;
	}

	public int getFilledQty() {
		return filledQty;
	}

	public void setFilledQty(int filledQty) {
		this.filledQty = filledQty;
	}

	public int getOpenQty() {
		return openQty;
	}

	public void setOpenQty(int openQty) {
		this.openQty = openQty;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	/**********************************************************************************
	 * 
	 * @return
	 ***********************************************************************************/
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getOptType() {
		return optType;
	}
	public void setOptType(String optType) {
		this.optType = optType;
	}
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public double getStrike() {
		return strike;
	}
	public void setStrike(double strike) {
		this.strike = strike;
	}

	@Override
	public String toString() {
		return "Order [orderID=" + orderID + ", userID=" + userID + ", symbol=" + symbol + ", quantity=" + quantity
				+ ", side=" + side + ", price=" + price + ", stopPrice=" + stopPrice + ", aon=" + aon + ", ioc=" + ioc
				+ ", filledQty=" + filledQty + ", openQty=" + openQty + ", status=" + status + "]";
	}


 
	
	
}
