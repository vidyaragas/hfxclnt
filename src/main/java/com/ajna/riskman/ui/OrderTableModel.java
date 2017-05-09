package com.ajna.riskman.ui;

import javafx.beans.property.SimpleStringProperty;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.ajna.hfxclnt.MainApp;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
 
public class OrderTableModel  {
	//1. Symbol		String
	//2. Quantiy 	int
	//3. Side		String
	//4. Price		doube
	//5. OptionType	String
	//6. Expiry		String
	//7. Strike		String
	DecimalFormat formatter = new DecimalFormat("#,###.00");
	String mdKey;
	SimpleStringProperty oid = new SimpleStringProperty();
	SimpleStringProperty entry = new SimpleStringProperty();
	SimpleStringProperty symbol = new SimpleStringProperty();
	SimpleIntegerProperty quantity = new SimpleIntegerProperty();
	SimpleStringProperty side = new SimpleStringProperty();
	SimpleDoubleProperty price  = new SimpleDoubleProperty();
	SimpleDoubleProperty ltprice  = new SimpleDoubleProperty();
	SimpleStringProperty optionType  = new SimpleStringProperty();
	SimpleStringProperty expiry  = new SimpleStringProperty();
	SimpleDoubleProperty strike   = new SimpleDoubleProperty();
	SimpleDoubleProperty spot   = new SimpleDoubleProperty();
	
	SimpleDoubleProperty dayPL   = new SimpleDoubleProperty();
	SimpleDoubleProperty posPL   = new SimpleDoubleProperty();
	
	SimpleStringProperty dayPLStr   = new SimpleStringProperty();
	SimpleStringProperty posPLStr  = new SimpleStringProperty();
	
	SimpleIntegerProperty filledQty = new SimpleIntegerProperty();
	SimpleIntegerProperty openQty = new SimpleIntegerProperty();
	SimpleIntegerProperty accumFills = new SimpleIntegerProperty();
	SimpleStringProperty status  = new SimpleStringProperty();
	
	
	public OrderTableModel( Integer mOid, String mEntry, String mSymbol, Integer mQuantity, String mSide,
			Double mPrice, String mOptionType, String mExpiry, Double mStrike) {
		//super();
		 
		this.oid.set(""+mOid);
		this.entry.set(mEntry);
		this.symbol.set(mSymbol.toUpperCase());
		this.quantity.set(mQuantity);
		this.side.set(mSide);
		this.price.set(mPrice);
		this.optionType.set(mOptionType);
		this.expiry.set(mExpiry.toUpperCase());
		this.strike.set(mStrike);
		
		this.ltprice.set(0);
		this.spot.set(0);
		
		this.dayPL.set(0);
		this.posPL.set(0);
		
		this.dayPLStr.set("0.00");
		this.posPLStr.set("0.00");
		
		mdKey = mSymbol.toUpperCase() + "_" + mExpiry.toUpperCase() + "_" + mSide + "_" + mOptionType + "_"  + mStrike ;
 
	}
 

	public OrderTableModel(String orderID, String symbol2, int quantity2, String side2, double price2, int openQty2,
			int filledQty2, int accumFil, String status2) {
		// TODO Auto-generated constructor stub
		
		this.oid.set(orderID);
		this.symbol.set(symbol2);
		this.quantity.set(quantity2);
		this.side.set(side2);
		this.price.set(price2);
		this.openQty.set(openQty2);
		this.filledQty.set(filledQty2);
		this.accumFills.set(accumFil);
		this.status.set(status2);
	}









	public String getMdKey() {
		return mdKey;
	}



	public void setMdKey(String mdKey) {
		this.mdKey = mdKey;
	}



	public String getOid() {
		return oid.get();
	}

	public void setOid(String oid) {
		this.oid.set(oid);
	}

	public String getStatus() {
		return status.get();
	}

	public void setStatus(String sta) {
		this.status.set(sta);
	}
	public Integer getOpenQty() {
		return openQty.get();
	}

	public void setOpenQty(Integer quantity) {
		this.openQty.set(quantity);
	}
	
	public Integer getAccumFills() {
		return accumFills.get();
	}

	public void setAccumFills(Integer quantity) {
		this.accumFills.set(quantity);
	}
	
	public Integer getFilledQty() {
		return filledQty.get();
	}

	public void setFilledQty(Integer quantity) {
		this.filledQty.set(quantity);
	}
	public String getEntry() {
		return entry.get();
	}

	public void setEntry(String entry) {
		this.entry.set(entry);
	}

	public String getSymbol() {
		return symbol.get();
	}

	public void setSymbol(String symbol) {
		this.symbol.set(symbol);
	}

	public Integer getQuantity() {
		return quantity.get();
	}

	public void setQuantity(Integer quantity) {
		this.quantity.set(quantity);
	}

	public String getSide() {
		return side.get();
	}

	public void setSide(String side) {
		this.side.set(side);
	}

	public Double getPrice() {
		return price.get();
	}

	public void setPrice(Double price) {
		this.price.set(price);
	}

	public String getOptionType() {
		return optionType.get();
	}

	public void setOptionType(String optionType) {
		this.optionType.set(optionType);
	}

	public String getExpiry() {
		return expiry.get();
	}

	public void setExpiry(String expiry) {
		this.expiry.set(expiry);
	}

	public Double getStrike() {
		return strike.get();
	}

	public void setStrike(Double strike) {
		this.strike.set(strike);
	}

	public Double getLtprice() {
		return ltprice.get();
	}

	public void setLtprice(Double ltprice) {
		this.ltprice.set(ltprice);
	}

	public Double getSpot() {
		return spot.get();
	}

	public void setSpot(Double spot) {
		this.spot.set(spot);
	}


	public Double getDayPL() {
		return dayPL.get();
	}



	public void setDayPL(Double dayPL) {
		this.dayPL.set(dayPL);
		this.setDayPLStr(formatter.format(dayPL));
	}



	public Double getPosPL() {
		return posPL.get();
	}



	public void setPosPL(Double posPL) {
		this.posPL.set(posPL);
		this.setPosPLStr(formatter.format(posPL));
	}
 


	public String getDayPLStr() {
		return dayPLStr.get();
	}



	public void setDayPLStr(String dayPLStr) {
		this.dayPLStr.set(dayPLStr);
	}



	public String getPosPLStr() {
		return posPLStr.get();
	}



	public void setPosPLStr(String posPLStr) {
		this.posPLStr.set(posPLStr);
	}

	
	public void computePL() {
		 if(side.get().equalsIgnoreCase("Buy")){
			 
			 this.setDayPL(quantity.get() * ( ltprice.get() - price.get()));
			 
			 if(optionType.get().equalsIgnoreCase("Call")){
				 
				 this.setPosPL(quantity.get() * (spot.get() - strike.get() - price.get() ));
				 
			 } else { //Put
				  
				 this.setPosPL(quantity.get() * (strike.get() - spot.get() - price.get() ));
			 }
		 } else { //Sell
			 
			 this.setDayPL(quantity.get() * (price.get() - ltprice.get() ));
			 
			 double prem = quantity.get() * price.get();
			 double factor, redu;
			 
			 if(optionType.get().equalsIgnoreCase("Call")){
				 factor = (strike.get() - spot.get() ); 
			 } else { //Put				
				 factor = (spot.get() - strike.get());  
			 }
			 if(factor < 0){
					redu = quantity.get() * factor;
					prem += redu;
				 }
			 this.setPosPL(prem);
		 }
		
		
	}

}
