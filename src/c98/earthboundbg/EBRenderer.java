package c98.earthboundbg;

import static android.opengl.GLES20.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.opengl.GLU;

public class EBRenderer implements android.opengl.GLSurfaceView.Renderer {
	public static Context context;
	
	public EBRenderer(Context context) {
		EBRenderer.context = context;
	}
	
	private int t = 0;
	private List<BackgroundLayer> layers;
	
	private boolean needSetup;
	
	@Override public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		layers = null;
		update();
		
		glClearColor(0, 0, 0, 1);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE);
	}
	
	public void update() {
		needSetup = true;
	}
	
	private ByteBuffer vertBuf;
	
	private int x, y, w, h;
	private boolean scroll;
	
	@Override public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		glViewport(0, 0, width, height);
		w = width;
		h = height;
		createBuffer();
		
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
	}
	
	public void setOffset(int offX, int offY) {
		if(scroll) {
			x = offX;
			y = offY;
		} else x = y = 0;
		createBuffer();
	}
	
	private void createBuffer() {
		float X = w / 2F;
		float Y = h / 2F;
		//@off
		float[] verts = {
				-1, 1,	128-X, 128-Y,
				-1, -1,	128-X, 128+Y,
				1, 1,	128+X, 128-Y,
				1, -1,	128+X, 128+Y,
		};
		//@on
		for(int i = 0; i < verts.length; i += 4) {
			verts[i + 2] -= x + X;
			verts[i + 3] -= y + 32;
		}
		vertBuf = ByteBuffer.allocateDirect(verts.length * 4).order(ByteOrder.nativeOrder());
		vertBuf.asFloatBuffer().put(verts).flip();
	}
	
	@Override public void onDrawFrame(GL10 glUnused) {
		glClear(GL_COLOR_BUFFER_BIT);
		
		if(needSetup) {
			needSetup = false;
			if(layers != null) for(BackgroundLayer l:layers)
				l.delete();
			Config c = new Config(context);
			layers = new ArrayList();
			for(int i:c.layers)
				layers.add(new BackgroundLayer(i, c));
			t = 0;
			scroll = c.doScroll;
		}
		
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 16, vertBuf.position(0));
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 16, vertBuf.position(8));
		
		for(BackgroundLayer layer:layers)
			layer.render(t, layers.size());
		glError("Post render");
		Sync.sync(30);
		t++;
	}
	
	public static void glError(String where) {
		int error = glGetError();
		if(error != 0) throw new RuntimeException(where + ": " + GLU.gluErrorString(error));
	}
}
