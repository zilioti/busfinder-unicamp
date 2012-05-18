package br.unicamp.busfinder;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class BusFinderUnicampActivity extends MapActivity implements LocationListener, OnSharedPreferenceChangeListener {
   

	
	
	/* Latitude e Longitude do CB da Unicamp */
	private static final int CENTER_LATITUDE = (int) (-22.817055 * 1E6);
	private static final int CENTER_LONGITUDE = (int) (-47.069729 * 1E6);
	
	SharedPreferences prefs;
	
	private String[] linhas = { "Linha 1", "Linha 2"};
	
	
	/* Lista para as coordenadas */
	CoordList coordList = null;
	ArrayList<String> coordenada = null;
	ArrayList<String> rota = null;
	
	private MapView map;
	private MapController controller;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
       
        
        
        
        
        
        rota = new ArrayList<String>();
        coordenada = new ArrayList<String>();
        
     // get the kml (XML) doc. And parse it to get the coordinates(direction
    	// route).
    	Document doc = null;
    	HttpURLConnection urlConnection = null;
    	URL url = null;

    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		DocumentBuilder db;
    		try {
    			db = dbf.newDocumentBuilder();
    			doc = db.parse(new InputSource(getAssets().open("Linha1.kml")));
    		} catch (ParserConfigurationException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			Log.d("aaaaaaXXXXXX", "aaaaaaXXXXX");
    		} catch (SAXException e) {
    			// TODO Auto-generated catch block
    			Log.d("aaaaaaXXXXXX", "aaaaaaXXXXX");

    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			Log.d("aaaaaaXXXXXX", "aaaaaaXXXXX");

    		}
    		

    		for (int i=0;i< doc.getElementsByTagName("coordinates").getLength()-1; i++){
    			String x = doc.getElementsByTagName("coordinates").item(i).getTextContent();
    			String a = x.replaceFirst("\n","").replaceAll(" ", "");
    			coordenada.add(a);        		
    		}

    		String x = doc.getElementsByTagName("coordinates").item(doc.getElementsByTagName("coordinates").getLength()-1).getTextContent();
    		String a = x.replaceFirst("\n","").replaceAll(" ", "");
			String c[] = a.split("\n");
    		for (String y : c){
    			rota.add(y);
    		} 
			
    		
        
        
        
        
        
        
        
        
        
        
        
        
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener((OnSharedPreferenceChangeListener) this);
        
        Location loc = getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
        getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        
        
        final Spinner combo = (Spinner) findViewById(R.id.linhas);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, linhas);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_item);
        combo.setAdapter(adaptador);
        
        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	
        	public void onItemSelected(AdapterView<?> parent,View v, int posicao, long id){
        		
        		if (posicao == 0) {
        		map.setSatellite(false);
                map.setTraffic(true);
                map.setStreetView(false);
                map.getOverlays().clear();
                map.invalidate();
                DesenhaPontosOnibus("Linha1.kml");
        		} else if (posicao == 1){
        			map.setSatellite(true);
                    map.setTraffic(false);
                    map.setStreetView(false);
                    map.getOverlays().clear();
                    map.invalidate();
                    
                    DesenhaPontosOnibus("Linha2.kml");		
        		}
        		
        	}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
        	
		});
        
        map = (MapView) findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        
        /* Mapa como de tr�nsito */
        map.setSatellite(true);
        map.setTraffic(false);
        map.setStreetView(false);
       
        DesenhaPontosOnibus("Linha1.kml");
    }
    
    
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
        
             
    }
    
    
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.file:
	        	startActivity(new Intent(this, PrefsActivity.class));
	            return true;
	        case R.id.create_new:
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
public void DesenhaPontosOnibus(String arquivo) {
    	
        
      
        controller = map.getController();
        
        /* Definicao do ponto centra da cidade */
        GeoPoint pointCB = new 
        		GeoPoint(CENTER_LATITUDE, CENTER_LONGITUDE);
        ArrayList<GeoPoint> Points = new ArrayList<GeoPoint>();
        ArrayList<GeoPoint> Route = new ArrayList<GeoPoint>();
        
        /* Definicao da lista de pontos ao redor */
        int x;
        int y;
        
        
        List<OverlayItem> pontos = new ArrayList<OverlayItem>();
        		
        for (String p : coordenada){
        	String c[] = p.split("\\,");
        	x = (int)(Double.parseDouble(c[0]) * 1E6); 
        	y = (int)(Double.parseDouble(c[1]) * 1E6); // ta com pau no ponto 14 na coord x
        	GeoPoint point = new GeoPoint(y, x);
        	pontos.add(new OverlayItem(point,"p","xtotal"));
        }
        
        Drawable imagem = getResources().getDrawable(R.drawable.mapa_pin);
        ImagensOverlay pontosOverlay = new ImagensOverlay(this,pontos,imagem);
        map.getOverlays().add(pontosOverlay);
        
        
        for(String r : rota ){
        	String c[] = r.split("\\,");
        	x = (int)(Double.parseDouble(c[0]) * 1E6); 
        	y = (int)(Double.parseDouble(c[1]) * 1E6); // ta com pau no ponto 14 na coord x
        	GeoPoint point = new GeoPoint(y, x);
        	Route.add(point);   
   	
        } 
        
        
        
        
        /* Centralizacao no ponto centra da cidade */
        //controller.setCenter(pointCB);
        controller.animateTo(pointCB);
        controller.setZoom(17);
        
        /* Marcacao da lista de pontos */
       Overlay overlayp;
        /*for (GeoPoint o : Points){
        	overlayp = new PointOverlay(o, map);
        	map.getOverlays().add(overlayp);
        } 
        */
        ListIterator<GeoPoint> i = Route.listIterator();
        
        while (i.hasNext()){
        	try {
        		
        		GeoPoint s = i.previous();
        		i.next();
        		GeoPoint e = i.next();
        		
        		Log.d("bbbb",s.toString());
				
        		overlayp = new RouteOverlay(s, e, map);
				map.getOverlays().add(overlayp);
				
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				i.next();
			}
        }
        
  
    }




private LocationManager getLocationManager() {
	LocationManager locationManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
	return locationManager;
}

@Override
public void onLocationChanged(Location location) {
	// TODO Auto-generated method stub
	int lat = (int)(location.getLatitude() * 1E6);
	int longitude = (int)(location.getLongitude() * 1E6);
	GeoPoint pointooo = new GeoPoint(lat, longitude);
	controller.animateTo(pointooo);
	
}



@Override
public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
	
}



@Override
public void onProviderEnabled(String provider) {
	// TODO Auto-generated method stub
	
}



@Override
public void onStatusChanged(String provider, int status, Bundle extras) {
	// TODO Auto-generated method stub
	
}



@Override
public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
	// TODO Auto-generated method stub
	
}
	

}
