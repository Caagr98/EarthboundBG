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
	
	@Override public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		glViewport(0, 0, width, height);
		float x = width / 2F;
		float y = height / 2F;
		//@off
		float[] verts = {
				-1, 1,	0.5F-x, 0.5F-y,
				-1, -1,	0.5F-x, 0.5F+y,
				1, 1,	0.5F+x, 0.5F-y,
				1, -1,	0.5F+x, 0.5F+y,
		};
		//@on
		vertBuf = ByteBuffer.allocateDirect(verts.length * 4).order(ByteOrder.nativeOrder());
		vertBuf.asFloatBuffer().put(verts).flip();
		
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
	}
	
	@Override public void onDrawFrame(GL10 glUnused) {
		glClear(GL_COLOR_BUFFER_BIT);
		
		if(needSetup) {
			needSetup = false;
			if(layers != null) for(BackgroundLayer l:layers)
				l.delete();
			layers = new ArrayList();
			for(int i:Config.get(context))
				layers.add(new BackgroundLayer(i));
			t = 0;
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
