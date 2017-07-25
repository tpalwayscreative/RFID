package rfid.com.delfi.rfid;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import delfi.com.vn.rfidsample.R;


public class FunctionActivity extends Activity
{

    RadioButton btnradio_zidongzhence,btnradio_anniuchufa,btnradio_buzzeron,btnradio_buzzeroff;
    Button  btnset_dateAndclock,btnotherfunction,btnactivityback;
	
	ArrayAdapter<String> YearArray,MonthArray,DayArray,HourArray,MinuteArray,SecondArray,WeekArray; 
    Spinner year_spinner,month_spinner,day_spinner,hour_spinner,minute_spinner,second_spinner,week_spinner;
	private static String year,month,day,hour,minute,second,week;
	private static int iy,imo,ida,ih,imi,isec,iw;


	public static OutputStream mmOutStream=null;
	public static InputStream mmInStream=null;
	
	public static mysendcmdThread senddata; //���÷��������߳�
	
	public static boolean  endflag=false;
	public  static boolean readflag=false;	
	public final static char[] myco={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};	
	public static String ds="";
	private String sendcommand="";
    private static int rcvflag=0; //����ID��

	public static boolean modeflag=false;
	 @Override
	protected void onCreate(Bundle savedInstanceState)
	    {
	        super.onCreate(savedInstanceState);

	        //
	        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	        this.setContentView(R.layout.activity_function);
          //
	        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
	        
	      
	        //
	        btnradio_zidongzhence =(RadioButton)findViewById(R.id.radioButton1);
	        btnradio_zidongzhence.setOnClickListener(new zidongzhence_onclick());
	        btnradio_anniuchufa=(RadioButton)findViewById(R.id.radioButton2);
	        btnradio_anniuchufa.setOnClickListener(new anniuchufa_onclick());
	        //
	        btnradio_buzzeron=(RadioButton)findViewById(R.id.radioButton3);
	        btnradio_buzzeron.setOnClickListener(new buzzeron_onclick());
	        btnradio_buzzeroff=(RadioButton)findViewById(R.id.radioButton4);
	        btnradio_buzzeroff.setOnClickListener(new buzzeroff_onclick());
	        
	        //
	        year_spinner =(Spinner)findViewById(R.id.spinner1);
	        String[] mItems_year = getResources().getStringArray(R.array.yearArray);
	        YearArray=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems_year); 	   
	        year_spinner.setAdapter(YearArray);
	        year_spinner.setOnItemSelectedListener(new YearItemSelectedlisterner());
	        //
	        //
	        month_spinner =(Spinner)findViewById(R.id.spinner2);
	        String[] mItems_month = getResources().getStringArray(R.array.monthArray);
	        MonthArray=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems_month); 	   
	        month_spinner.setAdapter(MonthArray);
	        month_spinner.setOnItemSelectedListener(new MonthItemSelectedlisterner());
	        //
	        //
	        day_spinner =(Spinner)findViewById(R.id.spinner3);
	        String[] mItems_day = getResources().getStringArray(R.array.dayArray);
	        DayArray=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems_day); 	   
	        day_spinner.setAdapter(DayArray);
	        day_spinner.setOnItemSelectedListener(new DayItemSelectedlisterner());
	        //
	      //
	        hour_spinner =(Spinner)findViewById(R.id.spinner4);
	        String[] mItems_hour = getResources().getStringArray(R.array.hourArray);
	        HourArray=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems_hour); 	   
	        hour_spinner.setAdapter(HourArray);
	        hour_spinner.setOnItemSelectedListener(new HourItemSelectedlisterner());
	        //
	      //
	        minute_spinner =(Spinner)findViewById(R.id.spinner5);
	        String[] mItems_minute = getResources().getStringArray(R.array.minuteArray);
	        MinuteArray=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems_minute); 	   
	        minute_spinner.setAdapter(MinuteArray);
	        minute_spinner.setOnItemSelectedListener(new MinuteItemSelectedlisterner());
	        // 
	      //
	        second_spinner =(Spinner)findViewById(R.id.spinner6);
	        String[] mItems_sencond = getResources().getStringArray(R.array.secondArray);
	        SecondArray=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems_sencond); 	   
	        second_spinner.setAdapter(SecondArray);
	        second_spinner.setOnItemSelectedListener(new SecondItemSelectedlisterner());
	        // 
	      //
	        week_spinner =(Spinner)findViewById(R.id.spinner7);
	        String[] mItems_week = getResources().getStringArray(R.array.weekArray);
	        WeekArray=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, mItems_week); 	   
	        week_spinner.setAdapter(WeekArray);
	        week_spinner.setOnItemSelectedListener(new WeekItemSelectedlisterner());
	        //
	       //
	        btnset_dateAndclock=(Button)findViewById(R.id.button1);
	        btnset_dateAndclock.setOnClickListener(new set_dateAndclock_onclick());
	       //
	        btnotherfunction=(Button)findViewById(R.id.button2);
	        btnotherfunction.setOnClickListener(new otherfunction_onclick());
	        //
	        //
	        btnactivityback=(Button)findViewById(R.id.button3);
	        btnactivityback.setOnClickListener(new activityback_onclick());
	        iy=imo=ida=ih=imi=isec=iw=0;

	    }

static class mysendcmdThread extends Thread
{ 
		private boolean BluetoothFlag;
	    byte[] msgBuffer=new byte[128];

	 	public mysendcmdThread(String mystring)
	 	{
	 		msgBuffer=HexString2Bytes(mystring);		
	 		BluetoothFlag=true;		
	 		endflag=false;
	 	}
	 	public void run()
	 	{  		  
	 		  while(BluetoothFlag)
	 		  {
	 			  try {
	 				    mmOutStream = HomeActivity.mmSocket.getOutputStream();
	 			     } 
	 			 catch (IOException e) 
	 		        {
	 			     try 
	 			       {
	 			    	HomeActivity.mmSocket.close();
	 				   } 
	 			      catch (IOException e1) {}
	 			     break;
	 		        }	 

	              try 
	                {
	 				  mmOutStream.write(msgBuffer);
	 			   } 
	              catch (IOException e2) 
	              {
	             	 try 
	         	       {
	             		HomeActivity.mmSocket.close();
	         		   } 
	         	       catch (IOException e3) {} 
	             	break; 
	              }
	              
	              try 
	              {
					mmOutStream.flush();
				  } 
	              catch (IOException e) {}
	              BluetoothFlag=false;
	              endflag=true;
	 		  }	 		  
	 	    }    
	 	 //
	 	   public  byte[] HexString2Bytes(String src)   
	 	   {
	 	      
	 	       byte[] tmp = src.getBytes();
	 	       byte[] ret = new byte[tmp.length/2];
	 	       for(int i=0; i<ret.length; i++){
	 	         ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
	 	       }
	 	       return ret;
	 	     }
	 	   public  byte uniteBytes(byte src0, byte src1)
	 	   {
	 	       byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
	 	       _b0 = (byte)(_b0 << 4);
	 	       byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
	 	       byte ret = (byte)(_b0 | _b1);
	 	       return ret;
	 	   }
	 }

///////////////////////////////////////////////////////////////////
//
public static String bytestohexcode(byte[] abc)
{
	StringBuffer s= new StringBuffer(abc.length*2);
	for(int i=0;i<abc.length;i++)
	{
	  s.append(myco[(abc[i]>>4)&0x0f]);
	  s.append(myco[abc[i]&0x0f]);
	}
	
  return s.toString();	
}
////////////////////////////////////////////////////////
public  void rec_data(int l)
{
	ds="";
	//
	 try  
		{
			mmInStream= HomeActivity.mmSocket.getInputStream();
		} 
		catch (IOException e) {}
	 //
	 int t=0;
	  while(readflag==true)
     {
		//
	    if(endflag==true)
	     {		    	
       	   int bytes=0;
       	   //
       	  try 
       	  {
			bytes= mmInStream.available();
		  } 
       	  catch (IOException e) {} 
       		
       	   if(bytes>0)
       	   {
       		 t=0;  
       	     byte[]b=new byte[bytes];
             int count=0;
             //
             while(count<bytes)
             {
       	      try 
       	        {
       		       count+= mmInStream.read(b, count, bytes-count);
			    }
               catch (IOException e1) {}
             }
             //
             ds+=bytestohexcode(b);
             //
             if((ds.startsWith("55"))&&(ds.endsWith("AA")))
        	 {
                     switch(rcvflag)
                     {
        	           case 1:
        		             if(ds.equalsIgnoreCase("5504400011AA"))
        		               {       			   
        			             Toast.makeText(FunctionActivity.this, "Reader the current mode to automatic mode.", Toast.LENGTH_SHORT).show();
                	             readflag=false; 
                	             endflag=false;
                	             btnradio_zidongzhence.setTextColor(Color.GREEN);
                	             btnradio_anniuchufa .setTextColor(Color.WHITE);
        		               }
        		               ds="";
        		               break;
        	           case 2:
        	        	      if(ds.equalsIgnoreCase("5504400011AA"))
    		                  {       			   
    			                 Toast.makeText(FunctionActivity.this, "Reader the current mode to key trigger mode��", Toast.LENGTH_SHORT).show();
            	                  readflag=false;
            	                  endflag=false;
            	                  btnradio_anniuchufa.setTextColor(Color.GREEN);
            	                  btnradio_zidongzhence.setTextColor(Color.WHITE);
            	                  
    		                  }
    		                   ds="";
        	        	       break;
        	           case 3:
        	        	      if(ds.equalsIgnoreCase("55040C005DAA"))
 		                      {       			   
 			                     Toast.makeText(FunctionActivity.this, "Reader the current buzzer status is on��", Toast.LENGTH_SHORT).show();
 			                     btnradio_buzzeron.setTextColor(Color.GREEN);
 			                     btnradio_buzzeroff.setTextColor(Color.WHITE);
         	                     readflag=false; 
         	                     endflag=false;
 		                      }
 		                      ds="";
        	        	      break;
        	           case 4:
        	        	      if(ds.equalsIgnoreCase("55040C005DAA"))
		                      {       			   
			                     Toast.makeText(FunctionActivity.this, "Reader the current buzzer status is off��", Toast.LENGTH_SHORT).show();
			                     btnradio_buzzeroff .setTextColor(Color.GREEN);
			                     btnradio_buzzeron.setTextColor(Color.WHITE);
      	                         readflag=false; 
      	                         endflag=false;
		                      }
		                      ds="";
     	        	          break; 
        	           case 5:
        	        	     if(ds.equalsIgnoreCase("55040D005CAA"))
		                      {       			   
			                     Toast.makeText(FunctionActivity.this, "Reader the current date/clock setting success��", Toast.LENGTH_SHORT).show();
			                     endflag=false;
		                      }
        	        	     else
        	        	     {
        	        	    	 Toast.makeText(FunctionActivity.this, "Reader the current date/clock setting failed��", Toast.LENGTH_SHORT).show();
        	        	    	 endflag=false;
        	        	     }
        	        	      readflag=false; 
		                      ds="";
        	        	      break;
     	        	   default :
     	        		      ds="";
     	        		      break;
                     }
        	 }
          
		 }
       	   else
       	   {
       		   t++;
       		   if(t>10000)  
       		   {
       			   endflag=false;
       			   readflag=false;
       			   ds="";
       		   }
       	   }
	     }   
       	   
	  } 

}

////////////////////////////////////////////////////////
//�Զ�����
private class zidongzhence_onclick implements OnClickListener
	 {
		public void onClick(View v)
		{ 
		   if(btnradio_zidongzhence.isSelected()==false)	
		     {			  
			  btnradio_zidongzhence.setChecked(true); 
			  btnradio_anniuchufa.setChecked(false);
			  
			  readflag=true;
			  sendcommand  ="5504400011AA";	
			  senddata =new mysendcmdThread(sendcommand);  
			  senddata.start(); 
			  try 
			   {
				 Thread.sleep(20);
			   } 
			  catch (InterruptedException e) {}
			  rcvflag=1;
			  rec_data(6);
			  modeflag=true;
		     }		
		}	  
	 }
	 //��ť����
	 private class anniuchufa_onclick implements OnClickListener
	 {
		public void onClick(View v)
		{
	
		  if(btnradio_anniuchufa.isSelected()==false)	
		  {
			  btnradio_anniuchufa.setChecked(true);
			  btnradio_zidongzhence.setChecked(false);
			  
			  readflag=true;
			  sendcommand  ="5504400110AA";	
			  senddata =new mysendcmdThread(sendcommand);
	          senddata.start(); 
	          try {
					Thread.sleep(20);
				} catch (InterruptedException e) {}
	          rcvflag=2;
	          rec_data(6);
	          modeflag=true;
	          
		  }			
		}	  
	 }


	 private class buzzeron_onclick implements OnClickListener
	 {
		public void onClick(View v)
		{
		  if(btnradio_buzzeron.isSelected()==false)	
		  {
			     btnradio_buzzeron.setChecked(true);
			     btnradio_buzzeroff.setChecked(false);
	
			      readflag=true;
			      sendcommand  ="55040C015CAA";	 
				  senddata =new mysendcmdThread(sendcommand);	
                  senddata.start(); 
                  try {
      				Thread.sleep(20);
      			} catch (InterruptedException e) {}
                  rcvflag=3;
				  rec_data(6);
				  				
		  }   

		}	 
		 
	 }
	 //������ʾ�ر�
	 private class buzzeroff_onclick implements OnClickListener
	 {
		public void onClick(View v)
		{

		  if(btnradio_buzzeroff.isSelected()==false)	
		  {
			  btnradio_buzzeroff.setChecked(true);
			  btnradio_buzzeron.setChecked(false);
			  
			  readflag=true;
			  sendcommand  ="55040C025FAA";	
			 
			  senddata =new mysendcmdThread(sendcommand);				  
			  senddata.start(); 
			  try {
					Thread.sleep(20);
				} catch (InterruptedException e) {}
			  rcvflag=4;
			  rec_data(6);			  			
		  }
			
		}	 
		 
	 }
	 
	 //��ѡ��
	 private class YearItemSelectedlisterner  implements OnItemSelectedListener
	 {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) 
		{
			iy=arg2+12;
			
			if(iy>15)
			{
				year=String.format("%x",iy).toUpperCase();
			}
			else
				year="0"+String.format("%x",iy).toUpperCase();
	
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			if(iy>15)
			{
				year=String.format("%x",iy).toUpperCase();
			}
			else
				year="0"+String.format("%x",iy).toUpperCase();
			
		}

	 }
	 //��ѡ��
	 private class MonthItemSelectedlisterner  implements OnItemSelectedListener
	 {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			 imo=arg2+1;	
			  month="0"+String.format("%x",imo).toUpperCase();
	
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			month="0"+String.format("%x",imo).toUpperCase();
		}

	 }
	 //��ѡ��
	 private class DayItemSelectedlisterner  implements OnItemSelectedListener
	 {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			ida=arg2+1;
			if(ida>15)
			{
				day=String.format("%x",ida).toUpperCase();
			}
			else
				day="0"+String.format("%x",ida).toUpperCase();
	
		}

		public void onNothingSelected(AdapterView<?> arg0)
		{
			if(ida>15)
			{
				day=String.format("%x",ida).toUpperCase();
			}
			else
				day="0"+String.format("%x",ida).toUpperCase();		
		}

	 }
	 //ʱѡ��
	 private class HourItemSelectedlisterner  implements OnItemSelectedListener
	 {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
		
			ih=arg2;
			if(ih>15)
			{
				hour=String.format("%x",ih).toUpperCase();
			}
			else
				hour="0"+String.format("%x",ih).toUpperCase();
		}

		public void onNothingSelected(AdapterView<?> arg0) 
		{
			if(ih>15)
			{
				hour=String.format("%x",ih).toUpperCase();
			}
			else
				hour="0"+String.format("%x",ih).toUpperCase();
		}

	 }
	 //��ѡ��
	 private class MinuteItemSelectedlisterner  implements OnItemSelectedListener
	 {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			imi=arg2;
			if(imi>15)
			{
				minute=String.format("%x",imi).toUpperCase();
			}
			else
				minute="0"+String.format("%x",imi).toUpperCase();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			if(imi>15)
			{
				minute=String.format("%x",imi).toUpperCase();
			}
			else
				minute="0"+String.format("%x",imi).toUpperCase();
			
		}

	 }
	 //��ѡ��
	 private class SecondItemSelectedlisterner  implements OnItemSelectedListener
	 {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			isec=arg2;
			if(isec>15)
			{
				second=String.format("%x",isec).toUpperCase();
			}
			else
				second="0"+String.format("%x",isec).toUpperCase();
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			if(isec>15)
			{
				second=String.format("%x",isec).toUpperCase();
			}
			else
				second="0"+String.format("%x",isec).toUpperCase();
			
		}

	 }
	 //����ѡ��
	 private class WeekItemSelectedlisterner  implements OnItemSelectedListener
	 {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
		{
			iw=arg2;
			if(iw>0)  week="0"+String.format("%x",iw);
			   week="07";
	
						  
		}
 
		public void onNothingSelected(AdapterView<?> arg0) 
		{
			if(iw>0)  week="0"+String.format("%x",iw);
			   week="07";
		}

	 }

	 private class set_dateAndclock_onclick implements OnClickListener
	 {

		public void onClick(View v)
		{					  		  
			  sendcommand ="550A0D"+second+minute+hour+day+month+week+year;	
			  senddata =new mysendcmdThread(sendcommand);
			  sendcommand=sendcommand+byte2HexString(checksum_jisuan())+"AA";
		  
			  readflag=true;
	
			  senddata =new mysendcmdThread(sendcommand);	
              senddata.start(); 
              try {
  				    Thread.sleep(20);
  			      } 
              
              catch (InterruptedException e) {}
              rcvflag=5;
              rec_data(6);

		}	 
		 
	 }
	 private class otherfunction_onclick implements OnClickListener
	 {
		public void onClick(View v)
		{
			 Intent intent =new Intent();
	         intent.setClass(FunctionActivity.this, FunctionImplementActivity.class);
	         startActivity(intent);
		}
	 } 
	 private class activityback_onclick implements OnClickListener
	 {
		public void onClick(View v)
		{
	         finish();
		}
	 }  
	 public static String byte2HexString(byte[] b) 
	 {
	        char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7',
	                      '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	        char[] newChar = new char[b.length * 2];
	        for(int i = 0; i < b.length; i++) {
	            newChar[2 * i] = hex[(b[i] & 0xf0) >> 4];
	            newChar[2 * i + 1] = hex[b[i] & 0xf];
	        }
	        return new String(newChar);
	 }
	 public byte[] checksum_jisuan()	 
	 { 
		  byte[] mybytes=senddata.msgBuffer;
		  byte n_mybyte=0;
		  for(int k=0;k<mybytes.length;k++)
			  n_mybyte^=mybytes[k];
		  byte[] checksumbyte =new byte[1];
		  checksumbyte[0]=n_mybyte;
		  return checksumbyte;
	 }

}
