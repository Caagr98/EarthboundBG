package c98.earthboundbg;

import static android.opengl.GLES20.*;
import java.util.Locale;

public class Shaders {
	// @off
	private static final String vert = ""
			+ "  	attribute vec4 pos;"
			+ "\n	attribute vec2 uv_;"
			+ "\n	varying vec2 uv;"
			+ "\n	void main() {"
			+ "\n		uv = uv_;"
			+ "\n		gl_Position = pos;"
			+ "\n	}";
	private static final String frag = ""
			+ "  	precision mediump float;"
			+ "\n	varying vec2 uv;"
			+ "\n	uniform sampler2D texture;"
			+ "\n	uniform sampler2D palette;"
			+ "\n	uniform float t;"
			+ "\n	uniform float alpha;"
			+ "\n	void main() {"
			+ "\n		vec2 uv2 = uv / $scale;"
			+ "\n		$calc;"
			+ "\n		float idx = texture2D(texture, uv2 / 256.0).x;"
			+ "\n		vec3 rgb = texture2D(palette, vec2(idx * 16.0, 0)).bgr;"
			+ "\n		gl_FragColor = vec4(rgb, alpha);"
			+ "\n	}";
	//@on
	private static String frag(Background bg, Config conf) {
		//0: no animation, only palette
		//1: horizontal
		//2: interlaced
		//3: vertical
		//4: interlaced again?
		String a = get(bg.animAmpl, bg.animAmplA, 1 / 512.0);
		String f = get(bg.animFreq, bg.animFreqA, Math.PI * 2 / 256 / 256);
		String c = get(bg.animComp, bg.animCompA, 1);
		String p = get(0, bg.animSpeed, Math.PI * 2 / 120.0);
		String y = "uv2.y";
		
		String S = String.format(Locale.US, "%s * sin(%s * %s + %s)", a, f, y, p);
		
		String frag = Shaders.frag.replace("$scale", "" + conf.scale);
		
		switch(bg.animType) {
			case 0:
				return frag.replace("$calc", "");
			case 1:
				return frag.replace("$calc", "uv2.x += " + S);
			case 2:
			case 4:
				return frag.replace("$calc", "uv2.x += " + S + " * sign(sin(" + y + " * " + Math.PI + "))");
			case 3:
				return frag.replace("$calc", "uv2.y += " + S + " + " + c + " * " + y + " / 256.0");
		}
		
		return null;
	}
	
	private static String get(float anim, float animA, double mult) {
		float multf = (float)mult;
		String a = "" + anim * multf;
		String b = "t * " + animA * multf;
		if(anim == 0 && animA == 0) return "0.0";
		if(anim != 0 && animA == 0) return a;
		if(anim == 0 && animA != 0) return b;
		return a + " + " + b;
	}
	
	private static int loadShaders(String vert, String frag) {
		int pid = glCreateProgram();
		
		int vid = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vid, vert);
		glCompileShader(vid);
		glAttachShader(pid, vid);
		
		int fid = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fid, frag);
		glCompileShader(fid);
		glAttachShader(pid, fid);
		
		glBindAttribLocation(pid, 0, "pos");
		glBindAttribLocation(pid, 1, "uv_");
		glLinkProgram(pid);
		
		//Sorry, no glGetShaderInfoLog, it's buggy
		
		glDeleteShader(vid);
		glDeleteShader(fid);
		
		return pid;
	}
	
	public static int getProgram(Background bg, Config conf) {
		return loadShaders(vert, frag(bg, conf));
	}
}
