package c98.earthboundbg.settings;

public class Layer {
	public String name;
	public int a, b;
	
	public Layer(String string, int i, int j) {
		name = string;
		a = i;
		b = j;
		if(a < b) {
			int tmp = b;
			b = a;
			a = tmp;
		}
	}
	
	public Layer(int i) {
		this("" + i, i, 0);
	}
}
