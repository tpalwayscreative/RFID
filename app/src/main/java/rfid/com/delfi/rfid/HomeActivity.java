package rfid.com.delfi.rfid;


import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import delfi.com.vn.rfidsample.R;

public class HomeActivity extends Activity
{
	public static final String MY_NAME ="BT_DEMO";
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final int STATE_NONE = 0;       
    public static final int STATE_LISTEN = 1;    
    public static final int STATE_CONNECTING = 2; 
    public static final int STATE_CONNECTED = 3;
    public static BluetoothSocket mmSocket=null;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private static final int REQUEST_ENABLE_BT = 2;
    private ListView mItemView;   
    private Button btnopen,btnscan,btnkejianxing,btnenter,btnexit;
    private ProgressBar progressBar;    
    private ArrayAdapter<String> DevicesArrayAdapter;
    private List<String> lstDevices = new ArrayList<String>();
    private BluetoothAdapter mBluetoothAdapter=null;
    private myconnectThread n;
    public  boolean connectedflag=false;
    private TextView mt;
    private boolean opstatus;

    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
        mt=(TextView)findViewById(R.id.x_title);
        mt.setText("RFID Demo V1.0");
        btnopen = (Button) findViewById(R.id.button_open); 
    	btnscan = (Button) findViewById(R.id.button_scan); 
        btnkejianxing =(Button) findViewById(R.id.button_kejianxing);  
        btnenter= (Button) findViewById(R.id.button_enter); 
        btnexit=(Button) findViewById(R.id.button_exit); 
        
        mItemView= (ListView) findViewById(R.id.listView1 );              
        DevicesArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstDevices);
        mItemView.setAdapter(DevicesArrayAdapter); 

        progressBar= (ProgressBar) findViewById(R.id.progressBar1); 
        progressBar.setVisibility(View.GONE);
        
        btnopen.setOnClickListener(new btn_onclick());     
        btnscan.setOnClickListener(new  btn_onclick());     
        btnkejianxing.setOnClickListener(new btn_onclick());               
        btnenter.setOnClickListener(new  btn_onclick());  
        btnexit.setOnClickListener(new  btn_onclick());        
        mItemView.setOnItemClickListener( new onitemclik()); 
        
        IntentFilter intentfilter = new IntentFilter();
    	intentfilter.addAction(BluetoothDevice.ACTION_FOUND); 
    	intentfilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentfilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);        
    	intentfilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    	intentfilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
    	intentfilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);       
        this.registerReceiver(searchDevices, intentfilter);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }    
    }

    private final BroadcastReceiver searchDevices = new BroadcastReceiver() 
    {
    	public void onReceive(Context context, Intent intent)
    	{
    		String action = intent.getAction(); 
    		BluetoothDevice device=null;
    		
    		if(BluetoothDevice.ACTION_FOUND.equals(action))
    		{
    			device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 
    			//
    			if (device.getBondState() != BluetoothDevice.BOND_BONDED) 
    			{
    				lstDevices.add("name: "+device.getName() + "\n" +"mac: "+ device.getAddress());
    				mItemView.setAdapter(DevicesArrayAdapter);
                } 				  
    		 }
    		else if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action))
    		{
    			device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); 
    			switch (device.getBondState()) 
    			{
    			case BluetoothDevice.BOND_BONDING: 	
    				          mt.setText("Pairing...");
                              break;
    			case BluetoothDevice.BOND_BONDED:
    				          mt.setText("Pairing is complete.");
    				          break;
    			case BluetoothDevice.BOND_NONE:   			
    				          mt.setText("Unpaired.");
                              break; 				          
    			}
    		}
    		else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
    		{
    			mt.setText("Scanning...");  
    			progressBar.setVisibility(View.VISIBLE);
    		}
    		else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
 		     {
    		   mt.setText("The scan is complete.");				 
 			   progressBar.setVisibility(View.GONE);
 			   if(opstatus==true)	
 				   mt.setText("The Bluetooth has closed.");		
 			      opstatus=false; 		 
 			 }
    		else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action))
    		{
    			switch (mBluetoothAdapter.getState())
    			{
    			    case BluetoothAdapter.STATE_TURNING_ON:
    			    	 mt.setText("The Bluetooth is starting..."); 
    			    	 break;
    				case BluetoothAdapter.STATE_ON:
    					mt.setText("Bluetooth has opened.");
     		        	lstDevices.clear(); 
     					      //
     					Set<BluetoothDevice>  pairedDevices=mBluetoothAdapter.getBondedDevices();	  		
     					if(pairedDevices.size()>0)				  
     					 {
     					        //
     					   for (BluetoothDevice devicess : pairedDevices) 
     						    {
     							   lstDevices.add("NAME: "+devicess.getName() + "\n"+"MAC: "+devicess.getAddress());  		   
     						    }
     						  mItemView.setAdapter(DevicesArrayAdapter); 
     					  }
    					break;
    				case BluetoothAdapter.STATE_TURNING_OFF:
   					     mt.setText("The Bluetooth is closing..."); 
   					     break;	
    				case BluetoothAdapter.STATE_OFF:
    					 mt.setText("The Bluetooth is Unopened.");
    					 break;
    			}
    		}
    	}
   };
/////////////////////////////////////////////////////////////// 
   private class btn_onclick implements  OnClickListener
   {
 	  public void onClick(View v) 
 	  {
 		 switch(v.getId()) 
 		 {
 		     case R.id.button_open:
            //
 			     if (!mBluetoothAdapter.isEnabled())
 			     {

 			    	mBluetoothAdapter.enable();
 			      }

 			     else 
 			     {
 			    	 if(mBluetoothAdapter.isDiscovering())
 	 			     {
 			    	   opstatus=true; 		 
 	 				   mBluetoothAdapter.cancelDiscovery();	 				  	
 	 			     }
 			    	 else
 			    	   mt.setText("The Bluetooth has closed.");	
 			    	 mBluetoothAdapter.disable();
 			    	 connectedflag=false; 		    	
 		         }				
 			     break;
 			
 		 case R.id.button_scan:
 			     if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) 
 			     { 
 				       String t="Please open the Bluetooth.";
 				       Toast.makeText(getApplicationContext(), t, 1000).show();
 			     }
 			    else
 			     {
 			    	if(mBluetoothAdapter.isDiscovering())
 			    	{
 			    		mBluetoothAdapter.cancelDiscovery(); 			    		
 			    	}
 			    	else mBluetoothAdapter.startDiscovery();
 			     }
 			    break;
 		 case R.id.button_kejianxing:
 			    if(mBluetoothAdapter.isDiscovering())
			     {
				   mBluetoothAdapter.cancelDiscovery();
			     }
 				 if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) 
 		           {
 		              Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
 		              discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
 		              startActivity(discoverableIntent);
 		           }
 			    break;
 		 case R.id.button_enter:
 			 if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) 
 			  {
 				   String t="Please open the Bluetooth.";
			       Toast.makeText(getApplicationContext(), t, 1000).show();
 			  }
 			 else
 			  {	 
 				 if(mBluetoothAdapter.isDiscovering())
 			     {
 				   mBluetoothAdapter.cancelDiscovery();
 			     }
 				if(connectedflag==false)
 	 			 {
 	 		
 	 				   String t="Please connect the remote Bluetooth device";
 				       Toast.makeText(getApplicationContext(), t, 1000).show();
 	 			 }
 				else
 				{
 				  Intent intent =new Intent();
 		          intent.setClass(HomeActivity.this, FunctionActivity.class);
 		          startActivity(intent);
 				}
 			  }
          	    
 			    break;
 		 case R.id.button_exit: //
 			     mBluetoothAdapter.disable();		     
 			     finish();	
 		 default :
 			     break;   			 
 		 }
 	  }		  
  } 
/////////////////////////////////////////////////////////////// 
   private synchronized void setState(int state) {
       mhandler.obtainMessage(HomeActivity.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
   }
///////////////////////////////////////////////////////////////  
  private class onitemclik implements  OnItemClickListener
   {
   	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  long arg3) 
   	{
   		//
   		if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) 
		  {
   			   String t="Please open the Bluetooth.";
		       Toast.makeText(getApplicationContext(), t, 1000).show();
		       return;
		  }
   		//
       if(mBluetoothAdapter.isDiscovering())       	
   		    mBluetoothAdapter.cancelDiscovery();
 
   		//
       String clickeditem = lstDevices.get(arg2);
   	   String address =clickeditem.substring((clickeditem.length()-17));
   	
   	  BluetoothDevice tdevice = mBluetoothAdapter.getRemoteDevice(address); 
      //
      connect(tdevice);	       
   	 
   	}	   
  } 
  
  public synchronized void connect(BluetoothDevice xdevice)
  { 
	  if(n!=null)
   	  {
   		n.cancel();
   		n=null;
   	  }
   	  n=new myconnectThread(xdevice);
   	  n.start();
   	  setState(STATE_CONNECTING);
  }  
///////////////////////////////////////////////////////////////   
private class myconnectThread extends Thread
{
	int x=0;
	public myconnectThread(BluetoothDevice mydevice)
	{
		mmSocket=null;
		connectedflag=false;
		
		try 
		{
			mmSocket=mydevice.createRfcommSocketToServiceRecord(MY_UUID);
		} 
		catch (IOException e) {}
     
	}
	 @Override	
	public void run()
	{	
		x=10000;
		//mBluetoothAdapter.cancelDiscovery();
		while(connectedflag==false)
		{
		  	
		  try 
		   {
			 mmSocket.connect();
		   } 
		 catch (IOException e) 
		  {	
			 if(delayxtime(x))
			 {
		      cancel();	
		      connectionFailed();
		      connectedflag=false;
		      break;
			 }
		    return;
		  } 
	    
	     setState(STATE_CONNECTED);
	     connectedflag=true;
		}
	}
	public  void cancel()
	{
		try 
		{
			mmSocket.close();
		}
		catch (IOException e){}
	}
	public boolean delayxtime(int mx) 
	{
		boolean aflag=false;
		if(mx>0)
		{
			mx--;	
			aflag=true;
		}
		return aflag;
	}
}
private void connectionFailed() 
{
       setState(STATE_LISTEN);
       Message msg = mhandler.obtainMessage(HomeActivity.MESSAGE_TOAST);
       Bundle bundle = new Bundle();
       bundle.putString(HomeActivity.TOAST, "Connection failed!");
       msg.setData(bundle);
       mhandler.sendMessage(msg);
}

private final Handler mhandler =new Handler()
{
 @Override
     public void handleMessage(Message msg)
    {
         switch (msg.what) 
         {
          case MESSAGE_STATE_CHANGE:
             switch (msg.arg1) 
             {
               case HomeActivity.STATE_CONNECTED:
                    mt.setText("The Bluetooth device is successfully connected.");
                    break;
             case  HomeActivity.STATE_CONNECTING:
                   mt.setText("Connecting to...");
                   break;
             case HomeActivity.STATE_LISTEN:
             case HomeActivity.STATE_NONE:
                   mt.setText("Not connected.");
                 break;
             }
             break;
         case MESSAGE_TOAST:
              Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
             break;
         }
    }
};
///////////////////////////////////////////////////////////////////
   @Override 
   protected void onDestroy() 
   { 
	  this.unregisterReceiver(searchDevices);
	  if(n!=null)
	  {
		  n.cancel();
		  n=null;
	  }
	  mhandler.removeCallbacks(n);
	  connectedflag=false; 
	  super.onDestroy(); 
	  
   }


   public void onActivityResult(int requestCode, int resultCode, Intent data)
   {  
       switch (requestCode) 
       {
       case REQUEST_ENABLE_BT:
           if (resultCode == Activity.RESULT_OK) 
           {
        	   mt.setText("Bluetooth has opened.");
        	   lstDevices.clear();
			      Set<BluetoothDevice>  pairedDevices=mBluetoothAdapter.getBondedDevices();	  		
			     if(pairedDevices.size()>0)				  
			     {
				    for (BluetoothDevice device : pairedDevices) 
				    {
					   lstDevices.add("NAME: "+device.getName() + "\n"+"MAC: "+device.getAddress());  		   
				    }
				  mItemView.setAdapter(DevicesArrayAdapter); 
			     }
           } 
           else 
           {
        	  mt.setText("Bluetooth is not open.");
           }
       }
   }
}
  		

