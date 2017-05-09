package com.ajna.hfxclnt.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HfxMessage {

	
	public static final  int HFX_MSGTYP_NEW_ORDER_REQUEST = 1;
	public static final  int HFX_MSGTYP_NEW_ORDER_RESPONSE = 2;
	public static final int HFX_MSGTYP_CXL_ORDER_REQUEST = 3;
	public static final int HFX_MSGTYP_CXL_ORDER_RESPONE = 4;
	public static final int HFX_MSGTYP_LOGIN_REQUEST = 91;
	public static final int HFX_MSGTYP_LOGIN_RESPONSE = 92;
	public static final int HFX_MSGTYP_LOGOUT_REQUEST = 93;
	public static final int HFX_MSGTYP_LOGOUT_RESPONSE = 94;
	public static final int HFX_MSGTYP_ORDER_BOOK_RESPONSE = 99;
	
	public static final int HFX_STATUS_LOGIN_REJECT = 0;
	public static final int HFX_STATUS_LOGIN_SUCCESS = 1;
	public static final int HFX_STATUS_LOGOUT_REJECT = 2;
	public static final int HFX_STATUS_LOGOUT_SUCCESS = 3;
	
	int msgSize;
	int msgType;
	ByteBuffer msgPayload;
	
   
	
	public HfxMessage(int msgSize, int msgType, ByteBuffer msgPayload) {
		super();
		this.msgSize = msgSize;
		this.msgType = msgType;
		this.msgPayload = msgPayload;
	}
	

	public static ByteBuffer createLoginMessage(String user){
			
	    int status = 1;
		
		int type = HfxMessage.HFX_MSGTYP_LOGIN_REQUEST; //Login request
		
		int len = 4 + 4 + 8 + 4 ;
		
		ByteBuffer sendMsg = ByteBuffer.allocate(len);

		sendMsg.order(ByteOrder.LITTLE_ENDIAN);
		
		int curPos = 0;
		 
		sendMsg.putInt(len);
		
		curPos = sendMsg.position();

		
		sendMsg.putInt(type);
		 
		curPos = sendMsg.position();

		
		curPos = sendMsg.position();
		
		sendMsg.put(user.getBytes());

		curPos = curPos + 8;
		
		int dummy = 0;

		
		sendMsg.position(curPos);
		
		
		sendMsg.putInt(dummy);
		
		//System.out.println("Current Position 6: " +  sendMsg.position());
		
		return sendMsg ;
	}
	
	 
	
	public int getMsgSize() {
		return msgSize;
	}
	public void setMsgSize(int msgSize) {
		this.msgSize = msgSize;
	}
	public int getMsgType() {
		return msgType;
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public ByteBuffer getMsgPayload() {
		return msgPayload;
	}
	public void setMsgPayload(ByteBuffer msgPayload) {
		this.msgPayload = msgPayload;
	}

	@Override
	public String toString() {
		return "HfxMessage [msgSize=" + msgSize + ", msgType=" + msgType + ", msgPayload=" + msgPayload + "]";
	}
	
	 
}


