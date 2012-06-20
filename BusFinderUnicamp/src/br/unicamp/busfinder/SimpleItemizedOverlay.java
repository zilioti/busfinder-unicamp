package br.unicamp.busfinder;


import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import com.google.android.maps.MapView;
import br.unicamp.busfinder.BalloonItemizedOverlay;

public class SimpleItemizedOverlay extends BalloonItemizedOverlay<CustomOverlayItem> {

	private ArrayList<CustomOverlayItem> m_overlays = new ArrayList<CustomOverlayItem>();
	private Context c;

	public SimpleItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
	}

	public void addOverlay(CustomOverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}
	
	protected CustomOverlayItem saveItem(int i, CustomOverlayItem overlay) {
		
		m_overlays.set(i, overlay);
		
		return m_overlays.get(i);
		
	}
	

	@Override
	protected CustomOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}
	
	
	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, CustomOverlayItem item) {
		Toast.makeText(c, "onBalloonTap for overlay index " + index,
				Toast.LENGTH_LONG).show();
		return true;
	}

}