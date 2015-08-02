package c98.earthboundbg.settings;

import static c98.earthboundbg.settings.EBSettingsActivity.COUNT;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import c98.earthboundbg.Background;

public class Thumbnail {
	public static class ThumbTask extends AsyncTask<Void, Void, Bitmap> {
		private ImageView v;
		private int id1, id2;
		
		public ThumbTask(ImageView view, int i, int j) {
			v = view;
			id1 = i;
			id2 = j;
		}
		
		@Override protected Bitmap doInBackground(Void... params) {
			Bitmap b1 = getThumbnail(id1);
			if(id2 == 0) return b1;
			int[] rgb1 = new int[32 * 32];
			b1.getPixels(rgb1, 0, 32, 0, 0, 32, 32);
			Bitmap b2 = getThumbnail(id2);
			int[] rgb2 = new int[32 * 32];
			b2.getPixels(rgb2, 0, 32, 0, 0, 32, 32);
			
			Bitmap b = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
			int[] rgb = new int[32 * 32];
			for(int i = 0; i < rgb.length; i++) {
				int rgb1_ = (rgb1[i] & 0xFEFEFE) >> 1;
				int rgb2_ = (rgb2[i] & 0xFEFEFE) >> 1;
				rgb[i] = rgb1_ + rgb2_ | 0xFF000000;
			}
			b.setPixels(rgb, 0, 32, 0, 0, 32, 32);
			
			thumbnails[id1 + id2 * COUNT] = b;
			
			return b;
		}
		
		private static Bitmap getThumbnail(int num) {
			if(thumbnails[num] != null) return thumbnails[num];
			Bitmap bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
			int[] rgb = new int[32 * 32];
			Background bg = Background.readBackground(num);
			bg.drawThumb(rgb, bg.frames / 2);
			bitmap.setPixels(rgb, 0, 32, 0, 0, 32, 32);
			thumbnails[num] = bitmap;
			return bitmap;
		}
		
		@Override protected void onPostExecute(Bitmap result) {
			if(v.getContentDescription().equals(id1 + "," + id2)) v.setImageBitmap(result);
		}
	}
	
	private static volatile Bitmap[] thumbnails;
	static {
		clearCache();
	}
	
	public static void getThumbnail(ImageView view, int i, int j) {
		if(thumbnails[i + j * COUNT] != null) {
			view.setImageBitmap(thumbnails[i + j * COUNT]);
			return;
		}
		view.setContentDescription(i + "," + j);
		view.setImageDrawable(null);
		new ThumbTask(view, i, j).execute();
	}
	
	public static void clearCache() {
		thumbnails = new Bitmap[COUNT * COUNT];
	}
}
