package com.ajna.hfxclnt;

 

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.ajna.hfxclnt.connection.HfxClient;
import com.ajna.hfxclnt.connection.HfxResponseInterface;
import com.ajna.riskman.dao.DaoController;
import com.ajna.hfxclnt.model.HfxMessage;
import com.ajna.hfxclnt.model.Order;
import com.ajna.riskman.ui.OrderTableModel;
import com.ajna.riskman.util.DateUtil;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;

import javafx.scene.control.MenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.paint.Color;


public class MainApp extends Application implements HfxResponseInterface {

	final int GUI_HEIGHT = 500;
	final int GUI_WIDTH = 1050;
 
	 String mHostName = "13.112.11.23";
	 int mPortNumber =  7788;
	
	int orderSeed = 1;
	final String TITLE_STRING = "High Frequency Trading UI";

	public TableView<OrderTableModel> tableView = new TableView<OrderTableModel>();
	public ObservableList<OrderTableModel> data =  FXCollections.observableArrayList();
	
	GridPane mGridOrderEntry;
	
	HBox mSummaryView;
	
	SimpleDateFormat mSdf1;
	
	HfxClient mHfxConnection;
 
 
	SimpleStringProperty mLoginStatusProperty;
	
	final Button loginButton = new Button("Login");
	
	Label mLoginStatus = new Label("Pls Login");
 
	DecimalFormat formatter = new DecimalFormat("#,###.00");
	private static final SimpleDateFormat mydateformat = new SimpleDateFormat("HHmmss");
	private String myOrderIDPrefix;
	
	String mUserId;
	
	boolean isLoggedIn = false;
	
	static Stage classStage = new Stage();
	
	public MainApp(HfxClient hfxc) {
		
		mHfxConnection = hfxc;
		
		mHostName = hfxc.getHost();
		mPortNumber = hfxc.getPort();
		mUserId = hfxc.getmUserId();
		
		mHfxConnection.setInterface(this);
		
		isLoggedIn = mHfxConnection.isLoggedIn();
		
		mSdf1 = new SimpleDateFormat("ddMMMyyyy");
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	        
	    myOrderIDPrefix = mUserId + mydateformat.format(timestamp);
	}
	@SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
	@Override
	public void start(Stage primaryStage) throws Exception {
		 
		classStage = primaryStage ;
 
		String loginStatus = (mHfxConnection.isLoggedIn() ? " Logged in" : " Not Logged in");
		
		primaryStage.setTitle(TITLE_STRING + "   User ID: " + mUserId + " - Connected to " + mHostName + ":" + mPortNumber + "(" + loginStatus + ")" );
		
		BorderPane borderPane = new BorderPane();
		
		mGridOrderEntry = buildOrderEntry();	
		
		buildOrderTableView();
		
		mSummaryView = buildSummaryView();

		borderPane.setTop(mSummaryView);
		
		borderPane.setCenter(tableView);
		
		borderPane.setBottom(mGridOrderEntry);
		
		final Scene scene = new Scene(borderPane, GUI_WIDTH, GUI_HEIGHT);

		// show the stage.
		primaryStage.setScene(scene);
		primaryStage.show();

		//Thread th = new Thread(() -> updateOrderTableView());
		//th.start();
		
		 
	}
	
	private HBox buildSummaryView() {
		HBox summaryView = new HBox();
		
 
		summaryView.setAlignment(Pos.CENTER_RIGHT);
		summaryView.setPadding(new Insets(15, 12, 15, 12));
		summaryView.setSpacing(10);
	    
 
		
		mLoginStatusProperty = new SimpleStringProperty();
		mLoginStatus.textProperty().bind(mLoginStatusProperty);
		
		Vector<String> allUsers  =  new Vector<String>(Arrays.asList("USER1","USER2","USER3"));
		
		ComboBox<String> loginUserComboBox = new ComboBox<String>();
 
		loginUserComboBox.getItems().clear();
		loginUserComboBox.getItems().addAll(allUsers);
		
		loginUserComboBox.getSelectionModel().selectFirst();
		
		
		loginButton.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent t)
            {
            	try {

        			boolean connected = mHfxConnection.connectToHfx();
        			if(connected){
        				String user = loginUserComboBox.getValue().toString();

        				ByteBuffer bb = HfxMessage.createLoginMessage(user);
        				mHfxConnection.sendMessage(bb);
        			}
 
        		} catch (Exception e) {
 
        			e.printStackTrace();
        		}
            	
            	
            	
            }
        });
 
		
		
		//summaryView.getChildren().addAll(loginUserComboBox, loginButton, mLoginStatus);
		
		return summaryView;
	}

 
	
	private void updateOrderTableView(Order ord) {
		
		boolean found = false;
		OrderTableModel otm = null;
		
		for ( OrderTableModel entry : data ) {
	        if(entry.getOid().equalsIgnoreCase(ord.getOrderID())){
	        	//found one, just update
	        	otm = entry;
	        	found = true;
	        	break;
	        }  
	    }
		
		if (found){
			 
			otm.setStatus(ord.getStatus());
			otm.setFilledQty(ord.getFilledQty());
			otm.setOpenQty(ord.getOpenQty());
			if(ord.getStatus().trim().equalsIgnoreCase("Filled"))
				otm.setAccumFills(ord.getAccumFills());
			 
			
		} else {
			otm = new OrderTableModel(ord.getOrderID(), ord.getSymbol(),ord.getQuantity(), ord.getSide(), ord.getPrice(), ord.getOpenQty(), ord.getFilledQty(), ord.getAccumFills(), ord.getStatus());
			data.add(otm);
		}
		 
		
		tableView.setItems(data);
		
		recheck_table();
	}
	 
	/**
	 * 
	 */
	private void recheck_table() {
		 
		tableView.setRowFactory(new Callback<TableView<OrderTableModel>, TableRow<OrderTableModel>>() {
			@Override
			public TableRow<OrderTableModel> call(TableView<OrderTableModel> paramP) {
				return new TableRow<OrderTableModel>() {

					@Override
					protected void updateItem(OrderTableModel paramT, boolean paramBoolean) {

						super.updateItem(paramT, paramBoolean);
						//System.out.println("recheck_table: " + paramT.getSymbol() + " is a " + paramT.getSide());
						
/*						if (!isEmpty()) {
							String style = "-fx-control-inner-background: #007F0E;"
									+ "-fx-control-inner-background-alt: #007F0E;";	 
							if(paramT != null) {
								
								if( paramT.getSide().equalsIgnoreCase("Buy")) {
									  style = "-fx-control-inner-background: #787F00;"
											+ "-fx-control-inner-background-alt: #787F00;";
								} else {
									style = "-fx-control-inner-background: #7F0040;"
											+ "-fx-control-inner-background-alt: #7F0040;";
								}
								System.out.println("recheck_table: " + paramT.getSymbol() + " is a " + paramT.getSide());
								System.out.println("recheck_table: Style is: " + style);
							}
							 
							setStyle(style);
						}*/
						
					}
				};
			}
		});
	}
	
	
	
	private  void buildOrderTableView() {
		 
		
		TableColumn<OrderTableModel, String> oidColumn = new TableColumn<OrderTableModel, String>("OrdID");
		oidColumn.setCellValueFactory(new PropertyValueFactory<OrderTableModel, String>("oid"));
		//oidColumn.setPrefWidth(10);
 		
		TableColumn<OrderTableModel, String> symColumn = new TableColumn<OrderTableModel, String>("Symbol");
		symColumn.setCellValueFactory(new PropertyValueFactory<OrderTableModel, String>("symbol"));
		//symColumn.setPrefWidth(25);
		
		TableColumn<OrderTableModel, Double> qtyColumn = new TableColumn<OrderTableModel, Double>("Quantity");
		qtyColumn.setCellValueFactory(new PropertyValueFactory<OrderTableModel, Double>("quantity"));
		//qtyColumn.setPrefWidth(10);
		
		TableColumn<OrderTableModel, String> sideColumn = new TableColumn<OrderTableModel, String>("Side");
		sideColumn.setCellValueFactory(new PropertyValueFactory<OrderTableModel, String>("side"));
		//sideColumn.setPrefWidth(5);
		
		TableColumn<OrderTableModel, Double> prcColumn = new TableColumn<OrderTableModel, Double>("Price");
		prcColumn.setCellValueFactory(new PropertyValueFactory<OrderTableModel, Double>("price"));
		//prcColumn.setPrefWidth(8);
		
		TableColumn<OrderTableModel, Integer> oqtyColumn = new TableColumn<OrderTableModel, Integer>("OpenQty");
		oqtyColumn.setCellValueFactory(new PropertyValueFactory<OrderTableModel, Integer>("openQty"));
		
		TableColumn<OrderTableModel, Integer> fqtyColumn = new TableColumn<OrderTableModel, Integer>("FilledQty");
		fqtyColumn.setCellValueFactory(new PropertyValueFactory<OrderTableModel, Integer>("filledQty"));
		
		TableColumn<OrderTableModel, Integer> aqtyColumn = new TableColumn<OrderTableModel, Integer>("AccumFillQty");
		aqtyColumn.setCellValueFactory(new PropertyValueFactory<OrderTableModel, Integer>("accumFills"));
		
		TableColumn<OrderTableModel, String> statusColumn = new TableColumn<OrderTableModel, String>("Status");
		statusColumn.setCellValueFactory(new PropertyValueFactory<OrderTableModel, String>("status"));
		
  
		tableView.getColumns().addAll(oidColumn, symColumn, qtyColumn, sideColumn, prcColumn, oqtyColumn, fqtyColumn, aqtyColumn, statusColumn );
		
		MenuItem mi1 = new MenuItem("Cancel");
		
		mi1.setOnAction((ActionEvent event) -> {
		    OrderTableModel item = (OrderTableModel) tableView.getSelectionModel().getSelectedItem();
		    System.out.println("Selected item: " + item.getOid());
		     
			Order ord2send = new  Order( item.getOid(),  mUserId, item.getOpenQty(), item.getSymbol());
			
			ByteBuffer bb = ord2send.getHfxString(HfxMessage.HFX_MSGTYP_CXL_ORDER_REQUEST);
					
			mHfxConnection.sendMessage(bb);
		    
		});

		ContextMenu menu = new ContextMenu();
		menu.getItems().add(mi1);
		tableView.setContextMenu(menu);
	}

	public GridPane buildOrderEntry() {
		
		GridPane mGridOrderEntry = new GridPane();
		
		mGridOrderEntry.setPadding(new Insets(15, 12, 15, 12));
 
		Vector<String> allSyms  = 
				new Vector<String>(Arrays.asList("6758","6752","9983","AUROPHARMA","AXISBANK","BANKBARODA",
						"BHEL","BPCL","BHARTIARTL","CIPLA","COALINDIA","GAIL","HINDALCO",
						"ITC","ICICIBANK","LICHSGFIN","NTPC","ONGC","POWERGRID","TATAMOTORS"
						,"TATAPOWER","TATASTEEL","TECHM","WIPRO","INFY","SBIN","IDEA","ADANIPORTS"));
		
		
		
		//1. Symbol
		Label symLabel = new Label("Symbol:   ");
		//TextField  symTextField = new TextField();
		ComboBox<String> symComboBox = new ComboBox<String>();
		symComboBox.setEditable(true);
		Collections.sort(allSyms);
		symComboBox.getItems().clear();
		symComboBox.getItems().addAll(allSyms);
		symComboBox.getSelectionModel().selectFirst();
		HBox symbolContainer = new HBox();
		symbolContainer.getChildren().addAll(symLabel, symComboBox);
		
		//2. Quantiy
		Label qtyLabel = new Label("Quantity: ");
		TextField qtyTextField = new TextField("1000");
		HBox qtyContainer = new HBox();
		qtyContainer.getChildren().addAll(qtyLabel, qtyTextField);
		
		//3. Side
		Label sideLabel = new Label("Side:       ");
		ComboBox sideComboBox = new ComboBox();
		sideComboBox.getItems().addAll(
				"Buy",
				"Sell"
				);
		sideComboBox.getSelectionModel().selectFirst();
		
		HBox sideContainer = new HBox();
		sideContainer.getChildren().addAll(sideLabel, sideComboBox);
		
		//4. Price
		Label prcLabel = new Label("Price:    ");
		TextField prcTextField = new TextField("100");
		HBox prcContainer = new HBox();
		prcContainer.getChildren().addAll(prcLabel, prcTextField);
				
		//5. OptionType
		Label optTypeLabel = new Label("OptnType:");
		ComboBox optTypeComboBox = new ComboBox();
		optTypeComboBox.getItems().addAll(
				"Call",
				"Put"
				);
		optTypeComboBox.getSelectionModel().selectFirst();
		HBox optypeContainer = new HBox();
		optypeContainer.getChildren().addAll(optTypeLabel, optTypeComboBox);
		
		//6. Expiry
		Label expiryLabel = new Label("Expiry:");
		ComboBox expiryComboBox  = new ComboBox();
		expiryComboBox.getItems().addAll(new DateUtil().getNext3Expiries());
 
		expiryComboBox.getSelectionModel().selectFirst();
		HBox expiryContainer = new HBox();
		expiryContainer.getChildren().addAll(expiryLabel, expiryComboBox);
		
		
		//7. Strike
		Label strikeLabel = new Label("Strike:   ");
		TextField strikeTextField = new TextField();
		HBox strikeContainer = new HBox();
		strikeContainer.getChildren().addAll(strikeLabel, strikeTextField);

		//. Add button
		Button addButton = new Button("      Add      ");
		addButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				int buy_side = 1;
				String sym = symComboBox.getValue().toString();
				int qty = Integer.parseInt(qtyTextField.getText());
				String side = sideComboBox.getValue().toString(); 
				if(side.equalsIgnoreCase("BUY")) buy_side = 1; else buy_side = 2;
				double price = Double.parseDouble(prcTextField.getText());
				
				if(mUserId == null || mUserId.isEmpty()){
					System.out.println("User not logged in. Pls login before sending orders ");
					return;
				}
				Order ord2send = new  Order( (myOrderIDPrefix + ++orderSeed),  mUserId, buy_side,  qty,   sym,  price);
				
				System.out.println("Order seed: [" + orderSeed + "]");
				
				ByteBuffer bb = ord2send.getHfxString(HfxMessage.HFX_MSGTYP_NEW_ORDER_REQUEST);
				mHfxConnection.sendMessage(bb);
				 
			}
		});
		
		//. Clear button
				Button clrButton = new Button("      Clear      ");
				clrButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent e) {
						symComboBox.getSelectionModel().selectFirst();
						qtyTextField.clear();
						sideComboBox.getSelectionModel().selectFirst();
						prcTextField.clear();
						optTypeComboBox.getSelectionModel().selectFirst();
						expiryComboBox.getSelectionModel().selectFirst();
						strikeTextField.clear();
					}
				});
				
		HBox btnContainer = new HBox();
		btnContainer.setSpacing(10);
		btnContainer.getChildren().addAll(addButton, clrButton);
				
		VBox vbox_1 = new VBox();
		vbox_1.setPadding(new Insets(15, 12, 15, 12));
		vbox_1.setSpacing(10);
		vbox_1.getChildren().addAll(symbolContainer, qtyContainer);
		
		VBox vbox_2 = new VBox();
		vbox_2.setPadding(new Insets(15, 12, 15, 12));
		vbox_2.setSpacing(10);
		vbox_2.getChildren().addAll(sideContainer, prcContainer);
		
		VBox vbox_3 = new VBox();
		vbox_3.setPadding(new Insets(15, 12, 15, 12));
		vbox_3.setSpacing(10);
		//vbox_3.getChildren().addAll(strikeContainer, optypeContainer );
		
		VBox vbox_4 = new VBox();
		vbox_4.setPadding(new Insets(15, 12, 15, 12));
		vbox_4.setSpacing(10);
		//vbox_4.getChildren().addAll(expiryContainer, btnContainer);
		vbox_4.getChildren().addAll( btnContainer);
		
		mGridOrderEntry.setHgap(10);
		mGridOrderEntry.setVgap(12);
		
		mGridOrderEntry.add(vbox_1, 0, 0);
		mGridOrderEntry.add(vbox_2, 1, 0);
		mGridOrderEntry.add(vbox_3, 2, 0);
		mGridOrderEntry.add(vbox_4, 3, 0);

		return mGridOrderEntry;

	}
	
	@Override
	public void onHfxUpdate(int type, ByteBuffer msg) {
		 switch (type) {

		 case HfxMessage.HFX_MSGTYP_ORDER_BOOK_RESPONSE: //order response
			 System.out.println("OrderBook Response [" + new String(msg.array()) + "]");
			 
			 
			 break;
			 
		 case HfxMessage.HFX_MSGTYP_NEW_ORDER_RESPONSE: //actual order response with all fields
			 Order ord1 = new Order(msg);
			 System.out.println("OrderStatus Response [" + ord1.toString() + "]");
			 updateOrderTableView(ord1);
			 break;
		 case  HfxMessage.HFX_MSGTYP_LOGIN_RESPONSE: // login response

			 int status = msg.getInt(8);
			 
			 byte[] user  = new byte[8]; 
			 msg.get(user, 0, 8);
			 String userID = new String(user);
			 
			
			
			 System.out.println("Login Respone status : [" + status + "], UserID: [" + userID + "]");
			 
			 
			 if(status == HfxMessage.HFX_STATUS_LOGIN_SUCCESS){
				 isLoggedIn = true;
				 mUserId = userID.trim();
				 System.out.println("User " + mUserId + " logged in successfully!");
				 
				 
			        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			        
			        myOrderIDPrefix = mUserId + mydateformat.format(timestamp);
			        
			        System.out.println("OrderID prefix:  " + myOrderIDPrefix);
				
			 } else {
				 System.out.println("User " + mUserId + " log-in attempt failed!");
			 }
			 break;
		
		 default: //unknown
			 System.out.println("Unknown type received: " + type);
			 break;
				 
				 
			 
		 }
		
	}
	/**
	public static void main(String[] args) throws InterruptedException {
		launch(args);
	}
	**/
	
	 
}
