package br.unicamp.busfinder;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class BusFinderUnicampActivity extends MapActivity {
   
	/* Latitude e Longitude do CB da Unicamp */
	private static final int CENTER_LATITUDE = (int) (-22.817055 * 1E6);
	private static final int CENTER_LONGITUDE = (int) (-47.069729 * 1E6);
	
	private String[] linhas = { "Linha 1", "Linha 2"};
	
	
	/* Lista para as coordenadas */
	CoordList coordList = null;
	ArrayList<String> coordenada = null;
	
	private MapView map;
	private MapController controller;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Spinner combo = (Spinner) findViewById(R.id.linhas);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, linhas);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_item);
        combo.setAdapter(adaptador);
        
        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	
        	public void onItemSelected(AdapterView parent,View v, int posicao, long id){
        		
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
			public void onNothingSelected(AdapterView parent) {
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
		return false;
	}
	
	
public void DesenhaPontosOnibus(String arquivo) {
    	
        
        /* Leitura e extracao das coordenadas do arquivo .kml */
        try {

        	/** Handling XML */
        	SAXParserFactory spf = SAXParserFactory.newInstance();
        	SAXParser sp = spf.newSAXParser();
        	XMLReader xr = sp.getXMLReader();

        	InputSource is = new InputSource(getAssets().open(arquivo));
        	
        	KMLHandler myXMLHandler = new KMLHandler();
        	xr.setContentHandler(myXMLHandler);
        	xr.parse(is);

        	} catch (Exception e) {
        	System.out.println("XML Pasing Excpetion = " + e);
        }
        
        /* Lista de coordenadas */
        coordList = KMLHandler.coordList;
        coordenada = coordList.getCoordenada();
        
        controller = map.getController();
        
        
        /* Definicao do ponto centra da cidade */
        GeoPoint pointCB = new 
        		GeoPoint(CENTER_LATITUDE, CENTER_LONGITUDE);
        ArrayList<GeoPoint> Points = new ArrayList<GeoPoint>();
        
        /* Definicao da lista de pontos ao redor */
        int x;
        int y;
        for (String p : coordenada){
        	String c[] = p.split("\\,");
        	x = (int)(Double.parseDouble(c[0]) * 1E6);
        	y = (int)(Double.parseDouble(c[1]) * 1E6);
        	GeoPoint point = new GeoPoint(y, x);
        	Points.add(point);
        	
        }
        
        /* Centralizacao no ponto centra da cidade */
        controller.setCenter(pointCB);
        controller.animateTo(pointCB);
        controller.setZoom(17);
        
        /* Marcacao da lista de pontos */
        Overlay overlayp;
        for (GeoPoint o : Points){
        	overlayp = new PointOverlay(o, map);
        	map.getOverlays().add(overlayp);
        } 
    }
}