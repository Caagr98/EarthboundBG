package c98.earthboundbg;

import java.util.*;
import android.content.*;
import android.content.SharedPreferences.Editor;

public class Config {
	private static SharedPreferences getPrefs(Context c) {
		return c.getSharedPreferences("EBBGList", 0);
	}
	
	public static List<Integer> get(Context c) {
		SharedPreferences p = getPrefs(c);
		if(!p.contains("count")) set(p, Arrays.asList(230));
		int count = p.getInt("count", 0);
		List<Integer> list = new ArrayList(count);
		for(int i = 0; i < count; i++)
			list.add(p.getInt("val" + i, 0));
		Collections.sort(list);
		return list;
	}
	
	public static void set(Context c, List<Integer> list) {
		set(getPrefs(c), list);
	}
	
	private static void set(SharedPreferences p, List<Integer> list) {
		Editor e = p.edit();
		e.clear();
		e.putInt("count", list.size());
		for(int i = 0; i < list.size(); i++)
			e.putInt("val" + i, list.get(i));
		e.commit();
	}
}
