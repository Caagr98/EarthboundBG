package c98.earthboundbg.settings;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import c98.earthboundbg.R;

public class EBFragment extends Fragment {
	public static interface EBListener {
		void click(int layer);
	}
	
	private List<Layer> presets = new ArrayList();
	private ListView list;
	private EBSettingsActivity parent;
	private EBListener listener;
	private int tabIndex;
	
	public EBFragment(EBSettingsActivity parent, EBListener listener, int idx) {
		this.parent = parent;
		this.listener = listener;
		tabIndex = idx;
	}
	
	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.settings_list, container, false);
		list = (ListView)rootView.findViewById(R.id.list);
		list.setAdapter(new BaseAdapter() {
			@SuppressWarnings("deprecation") @Override public View getView(int position, View convertView, ViewGroup parent) {
				ViewGroup v = convertView != null ? (ViewGroup)convertView : (ViewGroup)LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_item, parent, false);
				Layer p = presets.get(position);
				((TextView)v.findViewById(R.id.name)).setText(p.name);
				Thumbnail.getThumbnail((ImageView)v.findViewById(R.id.preview), p.a);
				Thumbnail.getThumbnail((ImageView)v.findViewById(R.id.preview2), p.b);
				((ImageView)v.findViewById(R.id.preview2)).setAlpha(127); //Can't use XML, doesn't work in API 8
				return v;
			}
			
			@Override public long getItemId(int position) {
				return 0;
			}
			
			@Override public Object getItem(int position) {
				return presets.get(position);
			}
			
			@Override public int getCount() {
				return presets.size();
			}
		});
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
				Layer p = presets.get(position);
				if(p.a != 0) listener.click(p.a);
				if(p.b != 0) listener.click(p.b);
			}
		});
		list.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override public boolean onItemLongClick(AdapterView<?> parentView, View view, int position, long id) {
				int idx = tabIndex * 1000000 + position;
				if(parent.preview == idx) parent.update();
				else {
					Layer p = presets.get(position);
					List<Integer> layerInts = new ArrayList(2);
					if(p.a != 0) layerInts.add(p.a);
					if(p.b != 0) layerInts.add(p.b);
					parent.getRenderer().previewLayers(layerInts);
					parent.preview = idx;
				}
				return true;
			}
		});
		return rootView;
	}
	
	public void setLayers(List<Layer> layers) {
		presets = layers;
		if(list != null) ((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
	}
}
