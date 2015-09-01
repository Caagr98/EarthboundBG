package c98.earthboundbg.settings;

import static c98.earthboundbg.settings.EBSettingsActivity.COUNT;
import java.io.*;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import c98.earthboundbg.Background;
import c98.earthboundbg.EBRenderer;

public class Thumbnail {
	public static class ThumbTask extends AsyncTask<Void, Void, Bitmap> {
		private ImageView v;
		private int id;
		
		public ThumbTask(ImageView view, int i) {
			v = view;
			id = i;
		}
		
		@Override protected Bitmap doInBackground(Void... params) {
			init();
			return thumbnails[id];
		}
		
		@Override protected void onPostExecute(Bitmap result) {
			if(v.getContentDescription().equals(id + "")) v.setImageBitmap(result);
		}
	}
	
	private static volatile Bitmap[] thumbnails;
	
	public static void getThumbnail(ImageView view, int i) {
		view.setImageDrawable(null);
		if(thumbnails != null && thumbnails[i] != null) {
			view.setImageBitmap(thumbnails[i]);
			return;
		}
		view.setContentDescription(i + "");
		new ThumbTask(view, i).execute();
	}
	
	public static synchronized void init() {
		if(thumbnails == null) {
			thumbnails = new Bitmap[COUNT];
			int[] rgb = new int[32 * 32];
			try {
				DataInputStream dis = new DataInputStream(new BufferedInputStream(EBRenderer.context.getAssets().open("thumbnails.dat")));
				for(int i = 1; i < COUNT; i++) {
					if(!Background.exists(i)) continue;
					for(int j = 0; j < rgb.length; j++)
						rgb[j] = dis.readInt();
					Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
					bitmap.setPixels(rgb, 0, 32, 0, 0, 32, 32);
					thumbnails[i] = bitmap;
				}
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void clearCache() {
		thumbnails = null;
	}
}
