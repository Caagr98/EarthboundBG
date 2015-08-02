package c98.earthboundbg.settings;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import c98.earthboundbg.*;

public class EBSettingsActivity extends Activity {
	private static class BGAdapter extends BaseAdapter {
		private List<Integer> list;
		
		public BGAdapter(List<Integer> list) {
			this.list = list;
		}
		
		@Override public View getView(int position, View convertView, ViewGroup parent) {
			ViewGroup v = convertView != null ? (ViewGroup)convertView : (ViewGroup)LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_item, parent, false);
			((TextView)v.findViewById(R.id.name)).setText("" + list.get(position));
			Thumbnail.getThumbnail((ImageView)v.findViewById(R.id.preview), list.get(position), 0);
			return v;
		}
		
		@Override public long getItemId(int position) {
			return 0;
		}
		
		@Override public Object getItem(int position) {
			return null;
		}
		
		@Override public int getCount() {
			return list.size();
		}
	}
	
	public static final int COUNT = 326 + 1;
	private EBRenderer renderer;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Preset.init(this);
		setContentView(R.layout.settings);
		
		((Button)findViewById(R.id.add)).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				final List<Integer> allValues = new ArrayList();
				for(int i = 1; i < COUNT; i++)
					if(Background.exists(i)) allValues.add(i);
				AlertDialog.Builder b = new AlertDialog.Builder(EBSettingsActivity.this);
				b.setAdapter(new BGAdapter(allValues), new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						add(allValues.get(which));
					}
				});
				b.create().show();
			}
		});
		((Button)findViewById(R.id.presets)).setOnClickListener(new OnClickListener() {
			@Override public void onClick(View v) {
				AlertDialog.Builder b = new AlertDialog.Builder(EBSettingsActivity.this);
				b.setAdapter(new Preset.PresetAdapter(), new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Preset.add(EBSettingsActivity.this, which);
					}
				});
				b.create().show();
			}
		});
		
		GLSurfaceView gl = getGL();
		gl.setEGLContextClientVersion(2);
		renderer = new EBRenderer(this);
		gl.setRenderer(renderer);
		
		ListView v = (ListView)findViewById(R.id.list);
		v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				remove(position);
			}
		});
		update();
	}
	
	private void update() {
		((ListView)findViewById(R.id.list)).setAdapter(new BGAdapter(Config.get(this)));
		renderer.update();
	}
	
	private GLSurfaceView getGL() {
		return (GLSurfaceView)findViewById(R.id.gl);
	}
	
	@Override protected void onResume() {
		super.onResume();
		getGL().onResume();
	}
	
	@Override protected void onPause() {
		super.onPause();
		getGL().onPause();
	}
	
	@Override protected void onStop() {
		super.onStop();
		Thumbnail.clearCache();
	}
	
	public void add(Integer id) {
		List<Integer> list = Config.get(this);
		list.add(id);
		Config.set(this, list);
		update();
	}
	
	public void remove(int pos) {
		List<Integer> list = Config.get(this);
		list.remove(pos);
		Config.set(this, list);
		update();
	}
}
