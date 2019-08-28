package com.example.youtube;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.viaviweb.recipebox.R;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class YoutubePlay extends YouTubeFailureRecoveryActivity {


	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube);
		Bundle b = getIntent().getExtras();
		id = (String) b.get("id");
		Log.e("id", id);
		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView
				.initialize(
						getString(R.string.youtube_api_key),
						this);
	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		// TODO Auto-generated method stub
		if (!wasRestored) {
			player.loadVideo(id);
		}
		 
	}

	@Override
	protected Provider getYouTubePlayerProvider() {
		// TODO Auto-generated method stub
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}
}