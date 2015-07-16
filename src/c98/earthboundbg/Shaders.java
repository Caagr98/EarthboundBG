package c98.earthboundbg;

import static android.opengl.GLES20.*;

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
			+ "\n	#define M_PI 3.1415926535897932384626433832795"
			+ "\n	varying vec2 uv;"
			+ "\n	uniform sampler2D texture;"
			+ "\n	uniform sampler2D palette;"
			+ "\n	uniform float t;"
			+ "\n	uniform float alpha;"
			+ "\n	uniform float ampl;" //TODO those don't need to be uniforms, they can be constants
			+ "\n	uniform float freq;"
			+ "\n	uniform float comp;"
			+ "\n	uniform float amplA;"
			+ "\n	uniform float freqA;"
			+ "\n	uniform float compA;"
			+ "\n	uniform float speed;"
			+ "\n	float offset() {"
			+ "\n		float y = uv.y * 256.0;"
			+ "\n		"
			+ "\n		float C1 = 1.0 / 512.0;"
			+ "\n		float C2 = 8.0 * M_PI / (1024.0 * 256.0);"
			+ "\n		float C3 = M_PI / 60.0;"
			+ "\n		"
			+ "\n		float amplitude = ampl + amplA * t * 2.0;"
			+ "\n		float frequency = freq + freqA * t * 2.0;"
			+ "\n		float compression = comp + compA * t * 2.0;"
			+ "\n		"
			+ "\n		float S = (C1 * amplitude * sin(C2 * frequency * y + C3 * speed * t));"
			+ "\n		"
			+ "\n		return $value;"
			+ "\n	}"
			+ "\n	void main() {"
			+ "\n		float S = offset() / 256.0;"
			+ "\n		vec2 uv2 = uv;"
			+ "\n		$transform;"
			+ "\n		float idx = texture2D(texture, uv2).x * 16.0;"
			+ "\n		vec3 rgb = texture2D(palette, vec2(idx, 0)).bgr;"
			+ "\n	//	rgb = vec3(idx, idx, idx);"
			+ "\n		gl_FragColor = vec4(rgb, alpha);"
			+ "\n	}";
	//@on
	private static String frag(int type) { //Basically a shitty preprocessor
		String f = frag;
		if(type == 0) f = f.replace("$value", "S");
		if(type == 1) f = f.replace("$value", "mod(y, 2.0) < 1.0 ? -S : S");
		if(type == 2) f = f.replace("$value", "y * (1.0 + compression / 256.0) + S");
		
		if(type == 2) f = f.replace("$transform", "uv2.y = S");
		if(type != 2) f = f.replace("$transform", "uv2.x += S");
		return f;
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
	
	public static int getProgram(int type) {
		return loadShaders(vert, frag(type));
	}
}
