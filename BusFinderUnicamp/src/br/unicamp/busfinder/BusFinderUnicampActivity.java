package br.unicamp.busfinder;


import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.xml.sax.InputSource;


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
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class BusFinderUnicampActivity extends MapActivity implements LocationListener, OnSharedPreferenceChangeListener {
   

	List<Overlay> mapOverlays;
	Drawable drawable;
	SimpleItemizedOverlay itemizedOverlay;
	
	String arquivo;
	Spinner combo;
	MyLocationOverlay ondeEstou;
	int i;
	
	/* Latitude e Longitude do CB da Unicamp */
	private static final int CENTER_LATITUDE = (int) (-22.817055 * 1E6);
	private static final int CENTER_LONGITUDE = (int) (-47.069729 * 1E6);
	
	SharedPreferences prefs;
	
	boolean rota_ativada;
	
	private String[] linhas = { "Linha 1 : Sentido Anti-Hor�rio", 
								"Linha 2 : Sentido Hor�rio", 
								"Linha 2 - Via FEC : Sentido Hor�rio", 
								"Linha 2 - Via Museu : Sentido Hor�rio", 
								"Linha Noturna : Sentido Hor�rio"
							   };
	
	
	/* Lista para as coordenadas */
	ArrayList<PontoOnibus> coordenada = null;
	ArrayList<String> rota = null;
	
	private MapView map;
	private MapController controller;
	
   
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        /* Inicializa o mapa */
		map = (MapView) findViewById(R.id.map);
        map.setBuiltInZoomControls(true);
        mapOverlays = map.getOverlays();
        controller = map.getController();
        controller.setZoom(17);
        GeoPoint pointCB = new GeoPoint(CENTER_LATITUDE, CENTER_LONGITUDE);
        drawable = getResources().getDrawable(R.drawable.busstop1);
        
        /* inicializa o spinner */
        combo = (Spinner) findViewById(R.id.linhas);
        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, linhas);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        combo.setAdapter(adaptador);
        
        /* inicializa as preferencias do usuario, obs: em tempo real */
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        inicializarListaPrefs();
        
        /* GPS */
        Location loc = getLocationManager().getLastKnownLocation(LocationManager.GPS_PROVIDER);
        ondeEstou = new MyLocationOverlay(this,map);
        map.getOverlays().add(ondeEstou);
        getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
        
        
       /* Funcao que faz o Parse dos arquivos KML e desenha os pontos de onibus no mapa */
        DesenhaPontosOnibus(arquivo); 
        
        
        
        /* SPINNER com as linhas de onibus quando clicado */  
        combo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        	public void onItemSelected(AdapterView<?> parent,View v, int posicao, long id){
        		
        		switch (posicao) {
        		case 0:
                    map.getOverlays().clear();
                    map.invalidate();
                    arquivo = "Linha1.kml";
                    DesenhaPontosOnibus(arquivo);
    	            break;
    	            
        		case 1:
                    map.getOverlays().clear();
                    map.invalidate();
                    arquivo = "Linha2.kml";
                    DesenhaPontosOnibus(arquivo);	
                    break;
        		case 2:
                    break;
        		case 3:
                    break;
        		case 4:
                    break;
                
    	       
    	        default:
                    map.getOverlays().clear();
                    map.invalidate();
                    DesenhaPontosOnibus("Linha1.kml");
    	            break;
        		
        		}
    	            
        	
        	}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				if (arquivo.equals("Linha1.kml")) combo.setSelection(0);
				if (arquivo.equals("Linha2.kml")) combo.setSelection(1);
			}	
		});
        
        /* verificar aqui oque faz!!!!!!!!!!!!!!!!! */
        if (savedInstanceState == null) {

			controller = map.getController();
			controller.animateTo(pointCB);
			controller.setZoom(17);

		} else {

			// example restoring focused state of overlays
			int focused;
			focused = savedInstanceState.getInt("focused_1", -1);
			if (focused >= 0) {
				itemizedOverlay.setFocus(itemizedOverlay.getItem(focused));
			}
			focused = savedInstanceState.getInt("focused_2", -1);

		}
        
        /* escuta a funcao quando alguma preferencia eh alterada */
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }
    
    

    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart(); 
             
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume(); 
    	ondeEstou.enableMyLocation();
             
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause(); 
    	ondeEstou.disableMyLocation();
             
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy(); 
    	getLocationManager().removeUpdates(this);
             
    }
    
	@Override
	protected boolean isRouteDisplayed() {
		/* verdadeiro pois a aplicacao mostra a rota */
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menumapa, menu);
	    return true;
	}
	
	/* MENU da aplicacao */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    
	    switch (item.getItemId()) {
	        
	    case R.id.pref:
	        	startActivity(new Intent(this, PrefsActivity.class));
	            return true;

	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	public void DesenhaPontosOnibus(String arquivo) {
    	
		rota = new ArrayList<String>(); //lista com as coordenadas da rota
		coordenada = new ArrayList<PontoOnibus>(); //lista com as coordenadas dos pontos de onibus
     
		
		/* faz o PARSE do ARQUIVO utilizando DOM */
		Document doc = null;
 		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 		DocumentBuilder db;
 		try {
 			
			db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(getAssets().open(arquivo)));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
 			
		for (int i = 0; i < doc.getElementsByTagName("Placemark").getLength() - 1; i++) {
			String x = doc.getElementsByTagName("Placemark").item(i).getChildNodes().item(1).getTextContent();
			String y = doc.getElementsByTagName("Placemark").item(i).getChildNodes().item(7).getTextContent();
			String a = y.replaceFirst("\n", "").replaceAll(" ", "");
			PontoOnibus b = new PontoOnibus(a,x,false);
			
			coordenada.add(b);
		}

 		String z = doc.getElementsByTagName("coordinates").item(doc.getElementsByTagName("coordinates").getLength()-1).getTextContent();
 		String a = z.replaceFirst("\n","").replaceAll(" ", "");
			String c[] = a.split("\n");
 		for (String y : c){
 			rota.add(y);
 		}  
 		doc = null;
 		/*FIM do PARSE devolvendo a lista de coordenadas e rotas com as latitudes e longitudes dos pontos de onibus */
        
        
 		
        ArrayList<GeoPoint> Route = new ArrayList<GeoPoint>();
 		itemizedOverlay = new SimpleItemizedOverlay(drawable, map);       
        List<OverlayItem> pontos = new ArrayList<OverlayItem>();
        	
        /* adiciona os pontos na lista itemizedOverlay */
        for (PontoOnibus p : coordenada) {
			String k[] = p.coordenada.split("\\,");
			int x = (int) (Double.parseDouble(k[0]) * 1E6);
			int y = (int) (Double.parseDouble(k[1]) * 1E6);
			GeoPoint point = new GeoPoint(y, x);
			// pontos.add(new OverlayItem(point,p,"xtotal"));
			itemizedOverlay.addOverlay(new CustomOverlayItem(point, p.nomeponto,
					"7 min próximo ônibus", "10 min ônibus seguinte",p.favorito));
        }
        
        mapOverlays.add(itemizedOverlay);
        
       /*s� desenha a rota se estiver ativada nas preferencias */
       if (rota_ativada){
        
    	   /*adiciona as coordenadas da rota em ROUTE */
    	   for(String r : rota ){
    		   String k[] = r.split("\\,");
    		   int x = (int)(Double.parseDouble(k[0]) * 1E6); 
    		   int y = (int)(Double.parseDouble(k[1]) * 1E6); 
    		   GeoPoint point = new GeoPoint(y, x);
    		   Route.add(point);   
    	   } 
    	   Overlay overlayp;
    	   ListIterator<GeoPoint> i = Route.listIterator();
    	   /*percorre as coordenadas da rota e desenha uma reta entre 2 */
    	   while (i.hasNext()){
    		   try {

        		GeoPoint s = i.previous();
        		i.next();
        		GeoPoint e = i.next();
				
        		overlayp = new RouteOverlay(s, e, map);
				map.getOverlays().add(overlayp);
    		   } catch (Exception e) {
				i.next();
    		   }
    	   }
       }
        
  
    }/*DesenhaPontosOnibus*/



	/* funcao do GPS que pega a localizacao */
	private LocationManager getLocationManager() {
		LocationManager locationManager = (LocationManager) getSystemService (Context.LOCATION_SERVICE);
		return locationManager;
	}

	/* funcao do GPS que eh chamada quando muda a localizacao do usuario */
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		int lat = (int)(location.getLatitude() * 1E6);
		int longitude = (int)(location.getLongitude() * 1E6);
		GeoPoint pointooo = new GeoPoint(lat, longitude);
		controller.setCenter(pointooo);
		map.invalidate();
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

@Override
protected void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);

}

public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    // your stuff or nothing
}


	/* Chama inicializarListaPrefs toda vez que o usuario mudar a preferencia*/
	private OnSharedPreferenceChangeListener prefListener = new OnSharedPreferenceChangeListener() {
		
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
    	
			inicializarListaPrefs();
			if (key.equals("routepreference") || key.equals("linepreferences")) {
				map.getOverlays().clear();
				DesenhaPontosOnibus(arquivo);
			}
		}
	};


	/*funcao que inicializa as preferencias do usuario para as variaveis da activity */
	private void inicializarListaPrefs() {
	
		/*MAPA*/
		String tipo_mapa = prefs.getString("mappreferences", "traffic");
    
		if (tipo_mapa.equalsIgnoreCase("traffic")) {
			/* Mapa como de transito*/
			map.setSatellite(false);
			map.setTraffic(true);
			map.setStreetView(false);
		} else if (tipo_mapa.equalsIgnoreCase("satellite")) {
			/* Mapa como de satelite*/
			map.setSatellite(true);
			map.setTraffic(false);
			map.setStreetView(false);
		}
		
		
		/*OPCOES DO MAPA */
		rota_ativada = prefs.getBoolean("routepreference", true);
		
		
		/*nao esta funcionando A ESCALA */
		
		if (!prefs.getBoolean("zoompreference", true)) map.setBuiltInZoomControls(false);
		
		
		/*OPCOES DO FOCO*/
		
		
		/*OPCOES da Linha de Onibus */
		
		String preflinha = prefs.getString("linepreferences", "line1");
		
		if (preflinha.equals("line1")) {
			arquivo = "Linha1.kml";
			combo.setSelection(0);
		}
		if (preflinha.equals("line2")) {
			arquivo = "Linha2.kml";
			combo.setSelection(1);
		}
		if (preflinha.equals("line2fec")) {
			arquivo = "Linha1.kml";
			combo.setSelection(2);
		}
		if (preflinha.equals("line2museum")) {
			arquivo = "Linha1.kml";
			combo.setSelection(3);
		}
		if (preflinha.equals("linenight")) {
			arquivo = "Linha1.kml";
			combo.setSelection(4);
		}
		
		
		/*OPCOES de Tema dos Pinos */		
		
		
	}
	
	public class PontoOnibus {

		String coordenada;
		String nomeponto;
		boolean favorito;

		PontoOnibus(String coord, String ponto, boolean fav) {
			this.coordenada = coord;
			this.nomeponto = ponto;
			this.favorito = fav;
		}

	}



}/*fim da activity*/
