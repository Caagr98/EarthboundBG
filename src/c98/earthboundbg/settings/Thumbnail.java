package c98.earthboundbg.settings;

import static c98.earthboundbg.settings.EBSettingsActivity.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;
import c98.earthboundbg.Background;

public class Thumbnail {
	public static class ThumbTask implements Runnable {
		private ImageView v;
		private int id1, id2;
		
		public ThumbTask(ImageView view, int i, int j) {
			v = view;
			id1 = i;
			id2 = j;
		}
		
		@Override public void run() {
			Activity a = (Activity)v.getContext();
			Bitmap b1 = getThumbnail(id1);
			int[] rgb1 = new int[32 * 32];
			b1.getPixels(rgb1, 0, 32, 0, 0, 32, 32);
			if(id2 != 0) {
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
				
				b1 = b;
			}
			
			final Bitmap b = b1;
			a.runOnUiThread(new Runnable() {
				@Override public void run() {
					if(v.getContentDescription().equals("" + id1 + "," + id2)) v.setImageBitmap(b);
				}
			});
		}
	}
	
	private static volatile Bitmap[] thumbnails = new Bitmap[COUNT * COUNT];
	private static ExecutorService exec = Executors.newSingleThreadExecutor();
	
	public static void getThumbnail(ImageView view, int i, int j) {
		if(thumbnails[i + j * COUNT] != null) {
			view.setImageBitmap(thumbnails[i + j * COUNT]);
			return;
		}
		view.setContentDescription("" + i + "," + j);
		view.setImageDrawable(null);
		exec.execute(new ThumbTask(view, i, j));
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
	
}
