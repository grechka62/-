package com.example.artists;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements OnItemClickListener, OnClickListener {
	
	private CustomAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // меняем цвет App Bar
	    ActionBar actionBar = getSupportActionBar();
	    int myColor = getResources().getColor(R.color.myColor);
		ColorDrawable color = new ColorDrawable(myColor);
		actionBar.setBackgroundDrawable(color);
		
		startListView();
	}
	
	// метод обеспечивает вывод на экран информации об исполнителях
	public void startListView() {
		try {
			// проверка наличия подключения к сети
			ConnectivityManager connMgr = (ConnectivityManager)
					getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			
			// если подключение возможно
			if (networkInfo != null && networkInfo.isConnected()) {
				setContentView(R.layout.activity_main);
				
				// строка в формате JSON будет получена в другом потоке
				DownloadWebpageTask dwt = new DownloadWebpageTask();
				dwt.execute(getString(R.string.jsonUrl));
				String jsonString = dwt.get();
				
				// с помощью адаптера устанавливается ListView
				try {
					adapter = new CustomAdapter(this, jsonString);
					ListView listView = (ListView)findViewById(R.id.listView);
					listView.setAdapter(adapter);
					listView.setOnItemClickListener(this);
					
				} catch (JSONException e) {
					// устанавливается другой layout с предложением повторить попытку
					anyException();
					TextView text = (TextView) findViewById(R.id.connText);
					text.setText("Полученный объект поврежден.");
				}
			} else {
				anyException();
			}
		} catch (InterruptedException e) {
			anyException();
			TextView text = (TextView) findViewById(R.id.connText);
			text.setText("Данные не получены.");
		} catch (ExecutionException e) {
			anyException();
			TextView text = (TextView) findViewById(R.id.connText);
			text.setText("Полученный объект поврежден.");
		}
	}
	
	// в отдельном потоке получаем JSON-строку из сети
	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		
		@Override
	  	protected String doInBackground(String... urls) {
	        try {
	    	   	return downloadUrl(urls[0]);
			} catch (IOException e) {
				return "Unable to retrieve web page. URL may be invalid.";
			}
		}
	}
	
	// загружаем строку по полученному URL.
	// если будет выброшено исключение,
	// оно будет обработано в методе doInBackGround
	private String downloadUrl(String myurl) throws IOException {
		
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
			
			// преобразуем полученный поток в строку
			String contentAsString = readIt(is);
			return contentAsString;
		} finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}
	
	// Преобразование полученного потока данных в троку
	public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = stream.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		return result.toString("UTF-8");
	}

	// при нажатии на пункт списка переходим создается Activity с подробной информацией
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String item = adapter.getItem(position);
		Intent intent = new Intent(MainActivity.this, ArtistDetailed.class);
		intent.putExtra("item",item);
		startActivity(intent);
	}

	// обработчик нажатия на кнопку "Повторить попытку"
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.connButton) {
			startListView();
		}
	}
	
	// Устанавливается layout с сообщением и кнопкой
	private void anyException() {
		setContentView(R.layout.activity_repeat_conn);
		Button btn = (Button) findViewById(R.id.connButton);
		btn.setOnClickListener(this);
	}
}