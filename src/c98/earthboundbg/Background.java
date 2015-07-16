package c98.earthboundbg;

import java.io.*;

public class Background {
	public int frames;
	private int[] pal;
	public int palSpeed;
	
	public byte[] pixels;
	
	public int animType;
	public int animSpeed;
	public int animFreq, animFreqA;
	public int animAmpl, animAmplA;
	public int animComp, animCompA;
	
	public void palette(int[] out, int frame) {
		System.arraycopy(pal, frame * 16, out, 0, 16);
	}
	
	public void drawThumb(int[] out, int frame) {
		int[] pal = new int[16];
		palette(pal, frame);
		for(int i = 0; i < 32; i++)
			for(int j = 0; j < 32; j++)
				out[i + j * 32] = pal[pixels[(i * 8 + 128) / 2 + (j * 8 + 128) / 2 * 256]] | 0xFF000000;
	}
	
	private void read(DataInputStream in, int id) throws IOException {
		frames = in.readUnsignedByte();
		pal = new int[frames * 16];
		for(int i = 0; i < pal.length; i++)
			pal[i] = in.readInt();
		palSpeed = in.readUnsignedByte();
		
		int numTiles = in.readUnsignedShort();
		byte[] tilePixels = new byte[numTiles * 8 * 8];
		for(int i = 0; i < tilePixels.length; i++)
			tilePixels[i] = in.readByte();
		short[] tiles = new short[32 * 32];
		for(int i = 0; i < tiles.length; i++)
			tiles[i] = in.readShort();
		pixels = new byte[256 * 256];
		for(int i = 0; i < 32; i++)
			for(int j = 0; j < 32; j++) {
				short tileData = tiles[i + j * 32];
				int tile = tileData & 0x3FF;
				boolean vflip = (tileData & 0x8000) != 0;
				boolean hflip = (tileData & 0x4000) != 0;
				int xor = 0;
				if(hflip) xor |= 7 * 8;
				if(vflip) xor |= 7;
				for(int i_ = 0; i_ < 8; i_++)
					for(int j_ = 0; j_ < 8; j_++) {
						int idx = i_ + j_ * 8 ^ xor;
						byte pixel = tilePixels[tile * 64 + idx];
						pixels[(i * 8 + i_) * 256 + (j * 8 + j_) * 1] = pixel;
					}
			}
		
		animType = in.readByte();
		animSpeed = in.readByte();
		animFreq = in.readShort();
		animFreqA = in.readShort();
		animAmpl = in.readShort();
		animAmplA = in.readShort();
		animComp = in.readShort();
		animCompA = in.readShort();
	}
	
	public static Background readBackground(int i) {
		try {
			InputStream is = getInputStream(i);
			if(is.read() == 0) return null;
			Background bg = new Background();
			bg.read(new DataInputStream(new BufferedInputStream(is)), i);
			return bg;
		} catch(Exception e) {
			throw new RuntimeException("Reading background " + i, e);
		}
	}
	
	private static InputStream getInputStream(int i) throws IOException {
		return EBRenderer.context.getAssets().open("bg-" + i + ".dat");
	}
	
	public static boolean exists(int i) {
		try {
			InputStream is = getInputStream(i);
			return is.read() != 0;
		} catch(IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
