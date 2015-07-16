package c98.earthboundbg;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public abstract class GLWallpaperService extends WallpaperService {
	public class GLEngine extends Engine {
		public class WallpaperGLSurfaceView extends GLSurfaceView {
			WallpaperGLSurfaceView(Context context) {
				super(context);
			}
			
			@Override public SurfaceHolder getHolder() {
				return getSurfaceHolder();
			}
			
			public void onDestroy() {
				super.onDetachedFromWindow();
			}
		}
		
		public WallpaperGLSurfaceView surfaceView;
		private boolean rendererHasBeenSet;
		
		@Override public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			surfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
		}
		
		@Override public void onVisibilityChanged(boolean visible) {
			super.onVisibilityChanged(visible);
			if(rendererHasBeenSet) if(visible) surfaceView.onResume();
			else surfaceView.onPause();
		}
		
		@Override public void onDestroy() {
			super.onDestroy();
			surfaceView.onDestroy();
		}
		
		protected void setRenderer(Renderer renderer) {
			surfaceView.setRenderer(renderer);
			rendererHasBeenSet = true;
		}
		
		protected void setEGLContextClientVersion(int version) {
			surfaceView.setEGLContextClientVersion(version);
		}
	}
}
