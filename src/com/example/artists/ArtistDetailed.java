package com.example.artists;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class ArtistDetailed extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_artist_detailed);
		
		// ��������� ����� App Bar
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		int myColor = getResources().getColor(R.color.myColor);
		ColorDrawable color = new ColorDrawable(myColor);
		actionBar.setBackgroundDrawable(color);
		
		// �������� ��������� ��������� ����������
		View linLay = (View) findViewById(R.id.linLay);
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.set);
		linLay.startAnimation(animation);
		
		String jsonStr = getIntent().getStringExtra("item");
		ImageView bigpic = (ImageView)findViewById(R.id.bigpic);
		TextView genres = (TextView)findViewById(R.id.genres);
		TextView tracks = (TextView)findViewById(R.id.tracks);
		TextView description = (TextView)findViewById(R.id.description);
		
		try {
			JSONObject item = new JSONObject(jsonStr);
			actionBar.setTitle(item.getString("name"));

			// �������� ����������� �� ���� � ������ ������
			DownloadWebpageTask dit = new DownloadWebpageTask();
			JSONObject imageAsObject = item.getJSONObject("cover");
			dit.execute(imageAsObject.getString("big"));
			Bitmap bm = dit.get();
			if (bm == null) {
				bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
			}
			bigpic.setImageBitmap(bm);
			
			// ��������� �����
			JSONArray genresArray = item.getJSONArray("genres");
			String genresString = genresArray.getString(0);
			for (int i = 1; i < genresArray.length(); i++) {
				genresString = genresString + ", " + genresArray.getString(i);
			}
			genres.setText(genresString);
			
			// ��������� ���������� �������� � �����
			String tracksString = String.valueOf(item.getInt("albums"));
			String[] albums = {" ��������", " ������", " �������"};
			tracksString = verifyStrings.verifyTracks(tracksString, albums);
			tracksString = tracksString + ", " + String.valueOf(item.getInt("tracks"));
			String[] tracksAr = {" �����", " �����", " �����"};
			tracksString = verifyStrings.verifyTracks(tracksString, tracksAr);
			tracks.setText(tracksString);
			
			description.setText(item.getString("description"));
		
			//���� ��������� ����������, �� ������ ����� �������� ���������
		} catch (JSONException e) {
			bigpic.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			genres.setText("������ ����������.");
		} catch (InterruptedException e) {
			bigpic.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			genres.setText("������ �� ��������.");
		} catch (ExecutionException e) {
			bigpic.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
			genres.setText("������ ����������.");
		}
		
	}
	
	// ���������� ������� �� ������ �������� �� App Bar
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
}
