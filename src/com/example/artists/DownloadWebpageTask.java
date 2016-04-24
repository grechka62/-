package com.example.artists;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

// ��������� ����� ��� �������� ����������� �� ����
public class DownloadWebpageTask extends AsyncTask<String, Void, Bitmap> {
	
	@Override
  	protected Bitmap doInBackground(String... urls) {
              
        try {
    	   	return downloadUrl(urls[0]);
		} catch (IOException e) {
			return null;
		}
    }
	
	// ��������� �������� �� ����������� URL.
	// ���� ����� ��������� ����������,
	// ��� ����� ���������� � ������ doInBackGround
	private Bitmap downloadUrl(String myurl) throws IOException {
		
		InputStream is = null;
		try{
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(7000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();
			is = conn.getInputStream();
			
			// ����������� ���������� ����� � Bitmap
			Bitmap bm = BitmapFactory.decodeStream(is);
        	return bm;
		} finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
}
