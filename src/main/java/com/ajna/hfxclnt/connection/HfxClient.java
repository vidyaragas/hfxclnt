package com.ajna.hfxclnt.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

import com.ajna.hfxclnt.model.HfxMessage;

public class HfxClient {
	
	String mHost;
	
	int mPort;
	
	String mUserId;
	
	boolean isLoggedIn = false;
	
	//Socket mSocket;
	SocketChannel mSocketChannel;

	private boolean socketConnected;
	
	HfxResponseInterface mInterface;

	public HfxClient(String host, int port)   {
		 mHost = host;
		 mPort = port;
		 socketConnected = false;
	}
	
	public HfxClient() {
		// TODO Auto-generated constructor stub
	}

	public boolean connectToHfx() throws Exception{
		
		boolean success = false;
		
		System.out.println("Attempting to Connect  " + mHost + ":" + mPort);
		
		success =  openSocket(mHost, mPort);
		 
		 socketConnected = success;
		 
		 Thread th = new Thread(() -> readFromSocketThread());
		  th.start();
		  
		  return success;
		  
	}
	public void setInterface(HfxResponseInterface hfxr){
		this.mInterface = hfxr;
	}
	
	public HfxResponseInterface getInterface() {
		return mInterface;
	}
	private void readFromSocketThread() {
		HfxMessage recvdMsg;
		while(socketConnected) {
			try {
				recvdMsg = readFromSocket();
				mInterface.onHfxUpdate(recvdMsg.getMsgType(), recvdMsg.getMsgPayload());
				//System.out.println(recvdMsg.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private  boolean openSocket(String server, int port) throws Exception
	  {
	     
	    boolean connected = false;
	    // create a socket with a timeout
	    try
	    {
 
	      InetSocketAddress hostAddress = new InetSocketAddress( server, port);
	      
	      mSocketChannel = SocketChannel.open(hostAddress);

	      mSocketChannel.configureBlocking(true);
	      connected = true;
	       
	    } 
	    catch (SocketTimeoutException ste) 
	    {
	      System.err.println("Timed out waiting for the socket.");
	      ste.printStackTrace();
	      throw ste;
	    }
	    
	    return connected;
	  }
	
	//to be called in a separate thread to keep listening
	private HfxMessage readFromSocket() throws IOException{

		HfxMessage hfxm = null;
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(4);
 
		int bytesCount = mSocketChannel.read(byteBuffer);
		
		if(bytesCount > 0){
		
		byteBuffer.flip();
		int sizevalue = byteBuffer.getInt(0);
		ByteBuffer typeBuffer = ByteBuffer.allocate(4);
		
		
		bytesCount = 0;
		
		bytesCount = mSocketChannel.read(typeBuffer);
		
		typeBuffer.flip();
		int methodType = typeBuffer.getInt(0);
		
		if(sizevalue > 2048){
			System.out.println("payload size: " + sizevalue + ".... skipping");
			return hfxm;
		}
		
		 
		
		
		ByteBuffer payloadBuffer = ByteBuffer.allocate(sizevalue - 8); // we have read 8 bytes already

		
		bytesCount = 0;
		bytesCount =  mSocketChannel.read(payloadBuffer);
		//System.out.println("received payload size: " + bytesCount + ",  total msg size: " + sizevalue);
		payloadBuffer.flip();
		
		
		hfxm = new HfxMessage(sizevalue, methodType, payloadBuffer);
		 
		
		} else {
			//handle disconnect
			socketConnected = false;
		}
		
		return  hfxm;
		
	}
	

	public void sendMessage(ByteBuffer hfxMessage ) {
		// TODO Auto-generated method stub
		try {
			hfxMessage.flip();
			 
			while(hfxMessage.hasRemaining()) {
				mSocketChannel.write(hfxMessage);
			}
			
			 
 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	 
	
	
	public String getmUserId() {
		return mUserId;
	}

	public void setmUserId(String mUserId) {
		this.mUserId = mUserId;
	}

	public String getHost() {
		return mHost;
	}

	public void setHost(String mHost) {
		this.mHost = mHost;
	}

	public int getPort() {
		return mPort;
	}

	public void setPort(int mPort) {
		this.mPort = mPort;
	}

 

	public boolean isConnected() {
		return socketConnected;
	}

 

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}

	public static void main(String[] args) throws InterruptedException {
		 try {
			HfxClient hfxc = new HfxClient("10.0.1.15", 7777);
			//hfxc.sendMessage( );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
	
	 
