package c98.earthboundbg.settings;

import java.util.ArrayList;
import java.util.List;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import c98.earthboundbg.*;
import c98.earthboundbg.settings.EBFragment.EBListener;

public class EBSettingsActivity extends FragmentActivity {
	public static final int COUNT = 326 + 1;
	private static List<Layer> presets = new ArrayList();
	private static List<Layer> layers = new ArrayList();
	
	public void initLayerLists() {
		presets.clear();
		for(String line:getResources().getStringArray(R.array.presets)) {
			String[] parts = line.split(";");
			Layer p = new Layer(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
			presets.add(p);
		}
		layers.clear();
		for(int i = 1; i < COUNT; i++)
			if(Background.exists(i)) layers.add(new Layer(i));
	}
	
	private EBRenderer renderer;
	private EBFragment mainPage;
	public int preview;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLayerLists();
		setContentView(R.layout.settings);
		
		final ViewPager p = (ViewPager)findViewById(R.id.pager);
		EBListener add = new EBListener() {
			@Override public void click(int layer) {
				add(layer);
				p.setCurrentItem(1, true);
			}
		};
		EBListener remove = new EBListener() {
			@Override public void click(int layer) {
				remove(layer);
				p.setCurrentItem(1, true);
			}
		};
		mainPage = new EBFragment(this, remove, 0);
		final EBFragment layersPage = new EBFragment(this, add, 1);
		layersPage.setLayers(layers);
		final EBFragment presetsPage = new EBFragment(this, add, 2);
		presetsPage.setLayers(presets);
		p.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			@Override public int getCount() {
				return 3;
			}
			
			@Override public Fragment getItem(int arg0) {
				if(arg0 == 0) return layersPage;
				if(arg0 == 1) return mainPage;
				if(arg0 == 2) return presetsPage;
				return null;
			}
			
			@Override public CharSequence getPageTitle(int position) {
				if(position == 0) return "Layers";
				if(position == 1) return "Active";
				if(position == 2) return "Presets";
				return "";
			}
		});
		p.setCurrentItem(1);
		
		GLSurfaceView gl = getGL();
		gl.setEGLContextClientVersion(2);
		renderer = new EBRenderer(this);
		gl.setRenderer(getRenderer());
		
		update();
	}
	
	private List<Layer> getLayers() {
		List<Integer> layerInt = new Config(this).layers;
		List<Layer> layers = new ArrayList();
		for(int i:layerInt)
			layers.add(new Layer(i));
		return layers;
	}
	
	public void update() {
		preview = -1;
		mainPage.setLayers(getLayers());
		getRenderer().update();
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
		Config c = new Config(this);
		c.layers.add(id);
		c.save();
		update();
	}
	
	public void remove(Integer id) {
		Config c = new Config(this);
		c.layers.remove(id);
		c.save();
		update();
	}
	
	public EBRenderer getRenderer() {
		return renderer;
	}
}
