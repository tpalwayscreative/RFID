package rfid.com.delfi.rfid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import delfi.com.vn.rfidsample.R;

public class FunctionImplementActivity extends Activity
{
	ListView function_listview;
    List<String> list_function = new ArrayList<String>();	
	ArrayAdapter<String> functionAdapter; 
	static String sendcmd="";
	static FunctionActivity.mysendcmdThread senddata;
	
	ProgressBar mpbar;
	
	Button  btn_fun2activity_back;
	private static String df="";
	private static int rcvflag =0;
    public  int bar=0;
  //  private Handler mHandler = new Handler();
    private static final int MESSAGE_ERASE_START    = 1;
    private static final int MESSAGE_ERASE_FAILURE  = 2;
    private static final int MESSAGE_ERASE_FINISHED = 3;	
    private static final int MESSAGE_GET_ID         = 4;
    private static final int MESSAGE_GET_DATE       = 5;
    
    static TextView mt;
	 @Override
		protected void onCreate(Bundle savedInstanceState)
		    {
		        super.onCreate(savedInstanceState);
		       //
		        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		        this.setContentView(R.layout.activity_implement_function);
		        //
		        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		        
		        mt=(TextView)findViewById(R.id.x_title);
		        mt.setText("RFID Demo V1.0");
		        function_listview =(ListView)findViewById(R.id.listView1);
		        list_function.clear();
		        list_function.add("Get current date&clock");
		        list_function.add("Get device UID ");
		        list_function.add("Erase all local records");
		        list_function.add("Data display");
		       // list_function.add("����ȫ�����ؼ�¼��Ϣ");
		       // list_function.add("��/д���ݲ���");
	
		        functionAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_function);
		        function_listview.setAdapter(functionAdapter);
		        function_listview.setOnItemClickListener( new myitemclik());
		        
		        btn_fun2activity_back=(Button)findViewById(R.id.button2);
		        btn_fun2activity_back.setOnClickListener(new fun2_activityback_onclick());
		        
		        mpbar =(ProgressBar)findViewById(R.id.progressBar1);
		        mpbar.setMax(501);
				  mpbar.setVisibility(View.VISIBLE);
		       // mpbar.setIndeterminate(false);
			    
			    				  
			    mpbar.setVisibility(View.VISIBLE);
			    mpbar.setEnabled(true);
			    mpbar.setBackgroundColor(Color.GREEN);
			    mpbar.setProgress(0);
			    	        
		    }
	 private class fun2_activityback_onclick implements OnClickListener
	 {

		public void onClick(View v)
		{
	         finish();
		}	 
		 
	 }  

///////////////////////////////////////////////////////////////////
//�ֽ�ת16�����ַ���
public static String bytestohexcode(byte[] abc)
{
StringBuffer s= new StringBuffer(abc.length*2);
for(int i=0;i<abc.length;i++)
{
s.append(FunctionActivity.myco[(abc[i]>>4)&0x0f]);
s.append(FunctionActivity.myco[abc[i]&0x0f]);
}

return s.toString();	
}
//////////////////////////////////////////	 
public  void rcvdata(int rlen)
{
   df="";
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
	     catch (IOException e) {} 
	      if(bytes>0) 
	      {
	    	  
	        t=0;
	        
	        byte[]b=new byte[bytes];
	        try 
	        {
	           FunctionActivity.mmInStream.read(b);
		    }
	        catch (IOException e1) {}
	     
	       df+=bytestohexcode(b);	
	      
	      if((df.startsWith("55"))&&(df.endsWith("AA")))
      	  {
	    	  String s="";
             switch(rcvflag)
              {
      	         case 1://GET DATE & CLOCK
      	        	    String  s1=df.substring(0, 8);
      	        	    if((s1.equalsIgnoreCase("550B0E00"))&&(df.length()==rlen*2))
      	        	    {
      	        	      String cur_seconds =df.substring(8, 10);
      	 	   			  String cur_minutes =df.substring(10, 12)+":";
      	 	   			  String cur_hours =df.substring(12,14)+":";
      	 	   			  String cur_days =df.substring(14, 16)+" ";
      	 	   			  String cur_months =df.substring(16, 18)+"-";
      	 	   			  String cur_year="20"+df.substring(18, 20)+"-";
      	 	   		      FunctionActivity.readflag=false;
 	 	   		          FunctionActivity.endflag=false;
 	 	   		          df="";
      	 	   			  s="Current date��"+cur_year+cur_months+cur_days+cur_hours+cur_minutes+cur_seconds;
      	 	   		      mHandler.obtainMessage(MESSAGE_GET_DATE,s ).sendToTarget();
      	 	   		      break;
      	        	    }
      	        	    else if((df.length()!=rlen*2)||(df.substring(0, 6).equalsIgnoreCase("550B0E")==false))
      	        	    {
      	        	      df="";
      	        	      break;
      	        	    }
      	        	    else
      	        	    {
      	        	      FunctionActivity.readflag=false;
    	        	      FunctionActivity.endflag=false;
      	        	      df="";
      	        	      s="Get the current date/clock failed��";
    	 	   		      mHandler.obtainMessage(MESSAGE_GET_DATE,s ).sendToTarget();
      	        	    }
      	        	    break;
      	         case 2://GET ID
      	        	    String  s2=df.substring(0, 8);
      	        	    
      	        	    if((s2.equalsIgnoreCase("550B0F00"))&&(df.length()==rlen*2))
      	        	    {
      	        	  
      	        	        s= "Device UID��"+df.substring(8, 22);
      	        	    	df="";
          	        	    FunctionActivity.readflag=false;
          	        	    FunctionActivity.endflag=false;
      	        	    	mHandler.obtainMessage(MESSAGE_GET_ID,s ).sendToTarget();
      	        	       
         	        	    break;
      	        	    }
      	        	   else if((df.length()!=rlen*2)||(df.substring(0, 6).equalsIgnoreCase("550B0F")==false))
      	        	   {
      	        		  df="";     	        		 
     	        	      break;  
      	        	   }
      	        	 else
   	        	      {
      	        		 FunctionActivity.readflag=false;
 	        	         FunctionActivity.endflag=false;
    	        	     df="";
      	        		 s= "Get device UID failed";

      	        		 mHandler.obtainMessage(MESSAGE_GET_ID,s ).sendToTarget();        	       
   	        	      }
      	        	    break;
      	       case 3:  //ERASE RECORDS
      	    	        if(df.equalsIgnoreCase("5504100041AA"))
      	    	        {
      	    	        	bar++;
      	    	        	df="";
      	    	        	FunctionActivity.readflag=false;
       	        	        FunctionActivity.endflag=false;
       	        	        
      	    	        }
      	    	        if(df.length()!=rlen*2)
      	    	        {
      	    	        	df="";
      	    	        	break;
      	    	        }
      	    	        else
      	    	        {
      	    	        	df="";
      	    	        	FunctionActivity.readflag=false;
       	        	        FunctionActivity.endflag=false;
       	        	        s="Erase the unfinished, unexpected termination.";
       				        mHandler.obtainMessage(MESSAGE_ERASE_FAILURE,s ).sendToTarget();
      	    	        }  
      	    	        break;
      	        default :
      	        	    df="";
      	        	    break;
      	        	   
               }
      	     }
	      }
	      else
	      {
	    	  t++;
	    	  if(t>10000)
	    	  {
	    		  FunctionActivity.endflag =false;
	    		  FunctionActivity.readflag=false;
	    		  df="";
	    	  }
	      }
	    }
	 } 
}
//////////////////////////////////////////
private final Handler mHandler = new Handler() 
{
       @Override
       public void handleMessage(Message msg)
       {
           switch (msg.what)
           {
             case MESSAGE_ERASE_START:
            	   msgoutput(msg); 
            	   break;
             case MESSAGE_ERASE_FINISHED:
            	   msgoutput(msg);       
           	       break;
             case MESSAGE_GET_DATE:
            	   msgoutput(msg); 
            	   break;
             case MESSAGE_GET_ID:
          	       msgoutput(msg); 
          	       break;	   
             default:
            	   break;
           }
       }
       private void msgoutput(Message msg1)
       {
    	   mt.setText(msg1.obj.toString());
    	   
       }
};
Thread cs;
//////////////////////////////////////////

@Override 
protected void onDestroy() 
{ 
	mHandler.removeCallbacks(cs);
super.onDestroy(); 

}
///////////////////////////////////////////////////////////////////
	 private class myitemclik implements  OnItemClickListener
	   {
		 
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) 
		{
			switch(arg2)
	   		{
	   		case 0://
	   			  sendcmd  ="55030E58AA";	
	   			  FunctionActivity.readflag=true;
	   			  senddata = new  FunctionActivity.mysendcmdThread(sendcmd) ;
	   			  senddata.start(); 
	   			  rcvflag=1;
	   			  
	   			  rcvdata(13);	
	   			  break;
	   		case 1://
	   			  sendcmd ="55030F59AA";	
	   			  FunctionActivity.readflag=true;
	   			  senddata = new FunctionActivity.mysendcmdThread(sendcmd) ;
	   			  senddata.start(); 
	   			  rcvflag=2;

	   			  rcvdata(13); 
	   			 
	   			  break;
	   		case 2://
	   			  sendcmd ="55031046AA";	
	   			  senddata =new FunctionActivity.mysendcmdThread(sendcmd);
				  senddata.start(); 
				  
				  bar=0;
				  rcvflag=3; 
				  
				  String s="Erasing...,Please wait.";
				  mHandler.obtainMessage(MESSAGE_ERASE_START,s ).sendToTarget();
				  
				  cs= new Thread(new Runnable()
				    {            
				    	public void run() 
				    	{                
				    		while (bar <501) 
				    		{                    
				    			FunctionActivity.readflag=true;
				    			FunctionActivity.endflag=true;
							    rcvdata(6);		
				    			mHandler.post(new Runnable() 
				    			{                        
				    				public void run()
				    				{                     
				    					mpbar.setProgress(bar);    
				    				}                   
				    			});	
				    		}
				    		
				    		DisplayActivity.num=0;
				    		String s="Local records have all been erased.";
				    		mHandler.obtainMessage(MESSAGE_ERASE_FINISHED,s ).sendToTarget();
				    		try {
								Thread.sleep(5);
							} catch (InterruptedException e) {}	
				    	} 
				    	
				    });
				  cs.start();

				 
	   			  break;
	   		case 3://
	   			/*
	   			  sendcmd ="55031147AA";	
	   			  senddata =new FunctionActivity.mysendcmdThread(sendcmd);
				  senddata.start(); 				  
				  rcvflag=4; 
	   			break;
	   		case 4:
	   		    */
	   			 Intent intent =new Intent();
		         intent.setClass(FunctionImplementActivity.this, DisplayActivity.class);
		         startActivity(intent);
	   			//
	   			break;
	   		}
			// TODO Auto-generated method stub
			
		}
	   }
	   
}
