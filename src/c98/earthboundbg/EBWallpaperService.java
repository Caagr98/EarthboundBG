package c98.earthboundbg;

import android.opengl.GLSurfaceView;
import android.view.SurfaceHolder;

public class EBWallpaperService extends GLWallpaperService {
	public class EBEngine extends GLWallpaperService.GLEngine {
		private EBRenderer renderer;
		
		@Override public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);
			
			setEGLContextClientVersion(2);
			setRenderer(renderer = new EBRenderer(EBWallpaperService.this));
			surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}
		
		@Override public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
			renderer.setOffset(xPixelOffset, yPixelOffset);
		}
	}
	
	@Override public Engine onCreateEngine() {
		return new EBEngine();
	}
}
