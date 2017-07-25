package rfid.com.delfi.rfid;


import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import delfi.com.vn.rfidsample.R;

public class DisplayActivity extends Activity
{
private Button btnRead,btnWrite,btnClear,btnReturn;	
EditText myedittext;
private String dx="";
private static int dsrcvflag=0;
public static final int MESSAGE_CHUFA_READ = 1;
public static int num=0;
Thread rd;
	 @Override
		protected void onCreate(Bundle savedInstanceState)
		    {
		        super.onCreate(savedInstanceState);
		        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		        this.setContentView(R.layout.activity_data_display);
		        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		        FunctionImplementActivity.mt.setText("RFID Demo V1.0");
		        btnRead=(Button)findViewById(R.id.btnread);
		        btnWrite=(Button)findViewById(R.id.btnwrite);
		        btnClear=(Button)findViewById(R.id.btnclear);
		        btnReturn=(Button)findViewById(R.id.btnreturn);		        
		        myedittext =(EditText)findViewById(R.id.edittext1);
		        btnRead.setOnClickListener(new displaybutton_onclick());
		        btnWrite.setOnClickListener(new displaybutton_onclick());
		        btnClear.setOnClickListener(new displaybutton_onclick());
		        btnReturn.setOnClickListener(new displaybutton_onclick());
		        if(FunctionActivity.modeflag==true)
		        {
		        	dsrcvflag=1;  
		        	myrunnablethread rp =new myrunnablethread();
		        	rd= new Thread(rp);
		        	rd.start();
		        }
		    }
	 
	 private class  displaybutton_onclick implements OnClickListener
	 {
		public void onClick(View v)
		{
			 switch(v.getId()) 
	 		 {
			    case R.id.btnread:
			    	 FunctionActivity.readflag=false;
			    	 FunctionImplementActivity.sendcmd ="5504515252AA";
		   			 FunctionActivity.readflag=true;
		   			 FunctionImplementActivity.senddata = new FunctionActivity.mysendcmdThread(FunctionImplementActivity.sendcmd) ;
		   			 FunctionImplementActivity. senddata.start();
		   			 dsrcvflag=2;
		   			 dsrcvdata(8);

				     break;
			    case R.id.btnwrite:
			    	 FunctionActivity.readflag=false;
				     break;
	 		    case R.id.btnclear:    //
	 		    	 myedittext.setText("");
	 		    	 break;
	 		    case R.id.btnreturn: 
	 		    	 dsrcvflag=2;
	    	         finish();
	    	         break;
	 		 }
		}	 
		 
	 } 
//////////////////////////////////////////////////////////////////
	 private final Handler myHandler = new Handler() 
	 {
	        @Override
	        public void handleMessage(Message msg)
	        {
	            switch (msg.what)
	            {
	              case MESSAGE_CHUFA_READ:
	            	               myedittext.append("Num��"+msg.arg1+" "+msg.obj.toString()+"\r\n");
	            	               break;
	            }
	        }
	 };
///////
class myrunnablethread implements Runnable
{
	public myrunnablethread(){}

	public void run() 
	{
		while(dsrcvflag==1)
		{
		  FunctionActivity.endflag=true;
          FunctionActivity.readflag=true;
		  dsrcvdata(16);
		  try {
			   Thread.sleep(10);
		      } 
		  catch (InterruptedException e) {}
		}
		
	}
	
}
////////////////////////////////////////////////////////////////////	

	 private  void dsrcvdata(int rlen)
	 {
	    dx="";
	    try 
	 	 {
	 		FunctionActivity.mmInStream= HomeActivity.mmSocket.getInputStream();
	 	 }
	    catch (IOException e2) {}
	 	int t=0;      
	    while(FunctionActivity.readflag==true)
	 	{
	        if(FunctionActivity.endflag==true)
	 	   {	
	 	     int bytes=0;
	 	    
	 	     try 
	 	       {
	 			   bytes= FunctionActivity.mmInStream.available();
	 		   } 
	 	      catch (IOException e) {break;} 
	 	     
	 	      if(bytes>0)
	 	      {
	 	    	  t=0;
	 	    	  byte[]b=new byte[bytes];
		 	      try 
		 	      {
		 	         FunctionActivity.mmInStream.read(b);
		 		  } catch (IOException e1) {}
		 	     
		 	      dx+= FunctionImplementActivity.bytestohexcode(b);
		 	     if((dx.startsWith("55"))&&(dx.endsWith("AA")))
		       	  {
		              switch(dsrcvflag)
		               {
		       	         case 1:
		       	        	    String  s1=dx.substring(0, 8);
		      	        	    if((s1.equalsIgnoreCase("550E2000"))&&(dx.length()==rlen*2))
		      	        	    {
		      	        	    	num++;
		      	        	    	String uid ="UID�� "+dx.substring(8, 16);
		      	        	    	String cur_seconds =dx.substring(16, 18);
		        	 	   			String cur_minutes =dx.substring(18, 20)+":";
		        	 	   			String cur_hours =dx.substring(20,22)+":";
		        	 	   			String cur_days =dx.substring(22, 24)+" ";
		        	 	   			String cur_months =dx.substring(24, 26)+"-";
		        	 	   		    String cur_year="20"+dx.substring(26, 28)+"-";
		        	 	   			String cur_date="  DATE�� "+cur_year+cur_months+cur_days+cur_hours+cur_minutes+cur_seconds;
		        	 	   		     dx="";
		        	 	   		    FunctionActivity.endflag=false;
		     		        	    FunctionActivity.readflag=false;
		      	        	    	myHandler.obtainMessage(MESSAGE_CHUFA_READ, num, 1, uid+cur_date).sendToTarget();
		      	        	    	break;
		      	        	    }
		      	        	    else
		      	        	    {
		      	        	      dx="";
		      	        	      FunctionActivity.readflag=false;
		      	        	    }
		       	        	    break;
		       	         case 2:
		       	        	    dx="";
		       	        	    FunctionActivity.endflag=false;
	     	        	        FunctionActivity.readflag=false;
		       	        	    break;
		               }
		       	  }
	 	      }
	 	      else
	 	      {
	 	    	  t++;
	 	    	  if(t>10000)
	 	    	  {
	 	    		 FunctionActivity.readflag=false;
	 	    		 FunctionActivity.endflag=false;
	 	    		 dx="";
	 	    	  }
	 	      }
	 	   }
	 	}
	 }
	   @Override 
	   protected void onDestroy() 
	   { 
		   myHandler.removeCallbacks(rd);
		   super.onDestroy();
	   }
}
