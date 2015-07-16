package c98.earthboundbg.settings;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import c98.earthboundbg.R;

public class Preset {
	
	public static class PresetAdapter extends BaseAdapter {
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			ViewGroup v = convertView != null ? (ViewGroup)convertView : (ViewGroup)c.getLayoutInflater().inflate(R.layout.settings_item, parent, false);
			Preset p = presets.get(position);
			((TextView)v.findViewById(R.id.name)).setText(p.name);
			Thumbnail.getThumbnail((ImageView)v.findViewById(R.id.preview), p.a, p.b);
			return v;
		}
		
		@Override public int getCount() {
			return presets.size();
		}
		
		@Override public Object getItem(int position) {
			return null;
		}
		
		@Override public long getItemId(int position) {
			return 0;
		}
	}
	
	private static EBSettingsActivity c;
	private static List<Preset> presets = new ArrayList();
	
	public static void init(EBSettingsActivity c) {
		presets.clear();
		Preset.c = c;
		for(String line:c.getResources().getStringArray(R.array.presets)) {
			String[] parts = line.split(";");
			Preset p = new Preset();
			p.name = parts[0];
			p.a = Integer.parseInt(parts[1]);
			p.b = Integer.parseInt(parts[2]);
			if(p.a < p.b) {
				int tmp = p.b;
				p.b = p.a;
				p.a = tmp;
			}
			presets.add(p);
		}
	}
	
	private String name;
	private int a, b;
	
	public static void add(int id) {
		Preset p = presets.get(id);
		if(p.a != 0) c.add(p.a);
		if(p.b != 0) c.add(p.b);
	}
}
