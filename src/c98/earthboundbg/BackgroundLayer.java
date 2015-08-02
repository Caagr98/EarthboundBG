package c98.earthboundbg;

import static android.opengl.GLES20.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class BackgroundLayer {
	private int program;
	private int palSpeed;
	private int texture;
	private int[] palettes;
	public int id;
	
	public BackgroundLayer(int id) {
		this.id = id;
		Background bg = Background.readBackground(id);
		
		IntBuffer intbuf = IntBuffer.allocate(bg.frames);
		glGenTextures(bg.frames, intbuf);
		palettes = intbuf.array();
		intbuf = IntBuffer.allocate(1);
		glGenTextures(1, intbuf);
		texture = intbuf.get();
		EBRenderer.glError("Post gen textures");
		
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, 256, 256, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, ByteBuffer.wrap(bg.pixels));
		EBRenderer.glError("Post texture");
		
		int[] pal = new int[16];
		for(int frame = 0; frame < bg.frames; frame++) {
			glBindTexture(GL_TEXTURE_2D, palettes[frame]);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			
			bg.palette(pal, frame);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 16, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, IntBuffer.wrap(pal));
			
			EBRenderer.glError("Post palette " + frame);
		}
		
		program = Shaders.getProgram(bg);
		glUseProgram(program);
		EBRenderer.glError("Post shader");
		uniformI("texture", 0);
		uniformI("palette", 1);
		palSpeed = bg.palSpeed;
	}
	
	private void uniform(String string, float v) {
		int pos = glGetUniformLocation(program, string);
		if(pos != -1) glUniform1f(pos, v);
		EBRenderer.glError("Set uniform " + string);
	}
	
	private void uniformI(String string, int v) {
		int pos = glGetUniformLocation(program, string);
		if(pos != -1) glUniform1i(pos, v);
		EBRenderer.glError("Set uniform " + string);
	}
	
	public void render(int t, int num) {
		t *= 2;
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		glActiveTexture(GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, palettes[palSpeed == 0 ? 0 : t / palSpeed % palettes.length]);
		glUseProgram(program);
		uniform("t", t);
		uniform("alpha", 1F / num);
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
	}
	
	public void delete() {
		glDeleteTextures(1, IntBuffer.wrap(new int[] {texture}));
		glDeleteTextures(palettes.length, IntBuffer.wrap(palettes));
		glDeleteProgram(program);
	}
}
