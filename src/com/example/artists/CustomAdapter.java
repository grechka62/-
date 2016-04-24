package com.example.artists;

import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

// адаптер для работы с разнотипной информацией
public class CustomAdapter extends BaseAdapter {
	private Context cont;
	private LayoutInflater inflater;
	private JSONArray jsonAr;
	private float dens;
	private int wp;
	
	public CustomAdapter(Context context, String string) throws JSONException {
		cont = context;
		inflater = (LayoutInflater) context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		
		// полученная по сети строка преобразуется в JSONArray
		JSONArray jsonAr = new JSONArray(string);
		this.jsonAr = jsonAr;
		
		// получаем размеры экрана, чтобы адаптировать размер шрифтов
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		dens = metrics.density;
		wp = metrics.widthPixels;
	}
	
	@Override
	public int getCount() {
		return jsonAr.length();
	}
	
	@Override
	public String getItem(int position) {
		try {
			return jsonAr.getJSONObject(position).toString();
		} catch (JSONException e) {
			return "Объект поврежден";
		}
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		JSONObject item;
		
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.row, null);
		}
		ImageView smallpic = (ImageView)view.findViewById(R.id.smallpic);
		TextView name = (TextView)view.findViewById(R.id.name);
		TextView genres = (TextView)view.findViewById(R.id.genres);
		TextView tracks = (TextView)view.findViewById(R.id.tracks);
		
		try {
			item = jsonAr.getJSONObject(position);
			
			// картинку получаем по сети в отдельном потоке
			DownloadWebpageTask dit = new DownloadWebpageTask();
			JSONObject imageAsObject = item.getJSONObject("cover");
			dit.execute(imageAsObject.getString("small"));
			Bitmap bm = dit.get();
			if (bm != null) {
				smallpic.setImageBitmap(bm);
			} else {
				smallpic.setImageBitmap(BitmapFactory.decodeResource(cont.getResources(), R.drawable.ic_launcher));
			}
			
			name.setText(item.getString("name"));
			changeTextSize(name, 20);
			
			JSONArray genresArray = item.getJSONArray("genres");
			String genresString = genresArray.getString(0);
			for (int i = 1; i < genresArray.length(); i++) {
				genresString = genresString + ", " + genresArray.getString(i);
			}
			genres.setText(genresString);
			changeTextSize(genres, 16);
			
			String tracksString = String.valueOf(item.getInt("albums"));
			String[] albums = {" альбомов", " альбом", " альбома"};
			tracksString = verifyStrings.verifyTracks(tracksString, albums);
			tracksString = tracksString + ", " + String.valueOf(item.getInt("tracks"));
			String[] tracksAr = {" песен", " песня", " песни"};
			tracksString = verifyStrings.verifyTracks(tracksString, tracksAr);
			tracks.setText(tracksString);
			changeTextSize(tracks, 16);
			
		} catch (JSONException e) {
		} catch (InterruptedException e) {
			smallpic.setImageBitmap(BitmapFactory.decodeResource(cont.getResources(), R.drawable.ic_launcher));
			name.setText("Данные не получены.");
		} catch (ExecutionException e) {
			smallpic.setImageBitmap(BitmapFactory.decodeResource(cont.getResources(), R.drawable.ic_launcher));
			name.setText("Данные повреждены.");
		}
		
		return view;
	}
	
	// изменение размера шрифта, если текст не помещается в одну строку
	private void changeTextSize (TextView tv, int size) {
		if (tv.length() > (wp-160*dens)/(size*dens)*9/5) {
			tv.setTextSize((wp-160*dens)*9/5/tv.length()/dens);
	    } else {
	    	tv.setTextSize(size);
	    }
	}
}
