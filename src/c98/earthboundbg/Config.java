package c98.earthboundbg;

import java.util.*;
import android.content.*;
import android.content.SharedPreferences.Editor;

public class Config {
	private static final String CFG_LIST = "EBBGList";
	private static final String KEY_COUNT = "count";
	private static final String KEY_VAL = "val";
	
	private static final String CFG_MISC = "EBBGSettings";
	private static final String KEY_INTERLACE = "smoothInterlace";
	private static final String KEY_SCALE = "scale";
	private static final String KEY_SCROLL = "scroll";
	
	private Context c;
	
	public List<Integer> layers = new ArrayList(Arrays.asList(230));
	public boolean smoothInterlace = false;
	public float scale = 1;
	public boolean doScroll = true;
	
	public Config(Context context) {
		c = context.getApplicationContext();
		SharedPreferences listPrefs = c.getSharedPreferences(CFG_LIST, 0);
		if(listPrefs.contains(KEY_COUNT)) {
			layers.clear();
			int count = listPrefs.getInt(KEY_COUNT, 0);
			for(int i = 0; i < count; i++)
				layers.add(listPrefs.getInt(KEY_VAL + i, 0));
		}
		
		SharedPreferences miscPrefs = c.getSharedPreferences(CFG_MISC, 0);
		smoothInterlace = miscPrefs.getBoolean(KEY_INTERLACE, smoothInterlace);
		scale = miscPrefs.getFloat(KEY_SCALE, scale);
		doScroll = miscPrefs.getBoolean(KEY_SCROLL, doScroll);
	}
	
	public void save() {
		Editor listPrefs = c.getSharedPreferences(CFG_LIST, 0).edit();
		listPrefs.putInt(KEY_COUNT, layers.size());
		for(int i = 0; i < layers.size(); i++)
			listPrefs.putInt(KEY_VAL + i, layers.get(i));
		listPrefs.commit();
		
		Editor miscPrefs = c.getSharedPreferences(CFG_MISC, 0).edit();
		miscPrefs.putBoolean(KEY_INTERLACE, smoothInterlace);
		miscPrefs.putFloat(KEY_SCALE, scale);
		miscPrefs.putBoolean(KEY_SCROLL, doScroll);
		miscPrefs.commit();
	}
}
