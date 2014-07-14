package com.example.testbluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.R.string;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Build;

public class MainActivity extends Activity implements OnClickListener{

	TextView display;
	Intent serviceintent;
//	private List<string> devices;
    private List<string> deviceList;
    private ListView listView;
    String getFromService;
    BluetoothAdapter adapter;
    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";   //SPP����UUID��
    BluetoothDevice _device = null;     //�����豸
    BluetoothSocket _socket = null;      //����ͨ��socket
    private InputStream blueStream;    //������������������������
    String devicename = "";
    String deviceaddress = "";
    int SELECT = 0;
    CmnHandler myhandler;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		��ȡ��������еİ�ť
		Button start = (Button) findViewById(R.id.start);
		Button stop = (Button) findViewById(R.id.stop);
		
//		��ȡ��������е�TextView
		display = (TextView) findViewById(R.id.textView1);
		
//		Ϊ������ť���ü����¼�
		start.setOnClickListener(this);
		stop.setOnClickListener(this);
//		��ȡϵͳĬ������
		adapter = BluetoothAdapter.getDefaultAdapter();
		
//		ע��BroadcastReceiver
		ActivityReceiver activityRceciver = new ActivityReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		registerReceiver(activityRceciver, filter);

		myhandler = new CmnHandler();

	}
	
	public class CmnHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				TextView tv = (TextView) findViewById(R.id.textView1);
				tv.setText(msg.obj.toString());
			}
		}
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
		{

//			ֱ�Ӵ�����
			adapter.enable();
//			��ʼ����
			adapter.startDiscovery();
			break;
		}
		case R.id.stop:
		{
//			��ȡϵͳĬ������
			BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//			ֱ�ӹر�����
			adapter.disable();
			break;
		}
		}
		ListView listView = (ListView) findViewById(R.id.listView1);
        listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			      GetInputsteamThread getinputstreamThread = new GetInputsteamThread(myhandler,_socket);
					getinputstreamThread.start();
					
			}
			
		});

	}
	
	
//	�Զ����BroadcastReceiver�����ڸ��������ACTION_FOUND
	public class ActivityReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
//		�ж��Ƿ����㲥
//		Toast.makeText(MainActivity.this, "����broadcastReceiver", Toast.LENGTH_SHORT).show();
//		��ȡ�������ǳ�
		String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//        	�ж��Ƿ����������
//        	Toast.makeText(MainActivity.this, "�����������豸", Toast.LENGTH_SHORT).show();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            devicename = device.getName();
            deviceaddress = device.getAddress();
            getFromService = devicename + "   " + deviceaddress;
        }
		listView = (ListView) findViewById(R.id.listView1);
		
		deviceList = new ArrayList<string>();
		ArrayList<String> devices = new ArrayList<String>();
		devices.add(getFromService);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.layoutstyle,
                devices);
        listView.setAdapter(adapter);
	}
}

		public class GetInputsteamThread extends Thread{
			private final Handler mmhandler;
			private final BluetoothSocket mmSocket;  
		    private final InputStream mmInStream;  
		  
		    public GetInputsteamThread(Handler mhandler,BluetoothSocket socket) { 
		    	mmhandler = mhandler;
		        mmSocket = socket;  
		        InputStream tmpIn = null;  
		        
		        _device = adapter.getRemoteDevice(deviceaddress);
		     // �÷���ŵõ�socket
		  		   try{
		  		   	_socket = _device.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
		  		   }catch(IOException e){
//		  		   	Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
		  		   }
		  		   try
		  			{	
		  				_socket.connect();
//		  				Toast.makeText(this, "����"+_device.getName()+"�ɹ���", Toast.LENGTH_SHORT).show();

		  			} catch (IOException e)
		  			{
		  				
		  				try
		  				{
//		  				Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
		  				_socket.close();
		  				_socket = null;
		  				} catch (IOException e1)
		  				{
		  					// TODO Auto-generated catch block
//		  					Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();	
		  				}            		
		  				// TODO Auto-generated catch block
		  			}
		  		   
		  		   

		  		   //�򿪽����߳�
		  		   try{
		  				blueStream = _socket.getInputStream();   //�õ���������������
		  				//blueoutOutputStream=_socket.getOutputStream();//�õ������������
//		  				Toast.makeText(this, "���������ɹ�", Toast.LENGTH_SHORT).show();
//						TextView show = (TextView) findViewById(R.id.textView1);
//						int num;
//						byte[] bytes=new byte[4];
//						num = blueStream.read(bytes);
//						show.setText(Integer.toString(num));
//		  				GetInputsteamThread getinputstreamThread = new GetInputsteamThread(myhandler,_socket);
//		  				new Thread(getinputstreamThread).start();
		  				}catch(IOException e){
//		  					Toast.makeText(this, "��������ʧ�ܣ�", Toast.LENGTH_SHORT).show();
		  				}
		  
		        // Get the input and output streams, using temp objects because  
		        // member streams are final  
		        try {  
		            tmpIn = socket.getInputStream();  
		        } catch (IOException e) { }  
		  
		        mmInStream = tmpIn;  
		    }  
		  
		    public void run() {  
		        byte[] buffer = new byte[4];  // buffer store for the stream  
//		        int bytes; // bytes returned from read()  
 
		        // Keep listening to the InputStream until an exception occurs  
		        while (true) {  
		            try {  
//		                bytes = blueStream.read(buffer);  
						int num=1234;
						byte[] buffer1 =new byte[4];
						num = blueStream.read(buffer1);
						Message msg = new Message();
				        msg.what = 0x123;
						msg.obj = num;
						mmhandler.sendMessage(msg);		                // Read from the InputStream  
		                // Send the obtained bytes to the UI Activity  
//		                mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)  
//		                        .sendToTarget();  
		            } catch (IOException e) {  
		                break;  
		            }  
		        }  
		    }
		}
}