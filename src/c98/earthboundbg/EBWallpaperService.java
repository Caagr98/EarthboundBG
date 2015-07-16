package c98.earthboundbg;

import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;

public class EBWallpaperService extends GLWallpaperService {
	public class EBEngine extends GLWallpaperService.GLEngine {
		@Override public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			
			setEGLContextClientVersion(2);
			setRenderer(new EBRenderer(EBWallpaperService.this));
			surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
	}
	
	@Override public Engine onCreateEngine() {
		return new EBEngine();
	}
}
