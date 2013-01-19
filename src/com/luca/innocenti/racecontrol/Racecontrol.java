package com.luca.innocenti.racecontrol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Racecontrol extends Activity {


	private String chk;
	private String cel;
	private int conta;
	private Button btLectorQR;

	@SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_racecontrol);
        TextView check = (TextView)findViewById(R.id.check);
        TextView cell = (TextView)findViewById(R.id.cell);
        conta = 0;
        
        // legge il nome del checkpoint da file
        StringBuilder checkpoint = new StringBuilder();
        File sdcard = Environment.getExternalStorageDirectory();
        File ch = new File(sdcard,"checkpoint.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(ch));
            String line;

            while ((line = br.readLine()) != null) {
                checkpoint.append(line);
                checkpoint.append('\n');
            }
            br.close();
            chk = checkpoint.toString();
        }
        catch (IOException e) {
        	Log.d("t","Errore");
        }
        check.setText(checkpoint);
        
        
        // legge il numero di telefono da file
        StringBuilder cellulare = new StringBuilder();
        File ce = new File(sdcard,"cellulare.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(ce));
            String line;

            while ((line = br.readLine()) != null) {
                cellulare.append(line);
                cellulare.append('\n');
            }
            br.close();
            cel = cellulare.toString();
        }
        catch (IOException e) {
        	Log.d("t","Errore");
        }
        cell.setText(cel);
        
        
        if (cel.length() == 2) cell.setText("No Tramissìone");

		btLectorQR = (Button) findViewById(R.id.button1);
		btLectorQR.setText("Nr. Passaggi : "+Integer.toString(conta));
		btLectorQR.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
				startActivityForResult(intent, 0);
			}
		});
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				//String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				
				conta++;
				btLectorQR.setText("Nr. Passaggi : "+Integer.toString(conta));

				
				
				SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
			    String stringa_tempo = formatter.format(new Date(System.currentTimeMillis()));
			
			    
			    String dati = contents+";"+stringa_tempo+";"+chk;
				
				Toast toast=Toast.makeText(this,dati,Toast.LENGTH_LONG);
				toast.show();
				
				// Alessio = 3473520198
				// Loredana = 3407302184
				if (cel.length() > 6)
				{
					SmsManager sms = SmsManager.getDefault();
					sms.sendTextMessage(cel, null, contents, null, null);
				}
		
				
    	        File root = Environment.getExternalStorageDirectory();
    	        if (root.canWrite()){
    	            File data_file = new File(root, "passaggi.txt");
    	            FileWriter data_file_writer = null;
					try {
						data_file_writer = new FileWriter(data_file,true);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    	            BufferedWriter out = new BufferedWriter(data_file_writer);
    	            try {
						out.write(dati);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				    try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
    	            // suona per conferma
    	            //Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    	            //Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
    	            //r.play();
    	        }
					
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
			}
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_racecontrol, menu);
        return true;
    }
    
}
