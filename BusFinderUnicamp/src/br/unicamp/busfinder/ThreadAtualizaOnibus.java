package br.unicamp.busfinder;



import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/**
 * Este exemplo demonstra como utilizar um Handler para agendar tarefas com
 * delay...
 * 
 * Para evitar a utilização do Thread.sleep, conforme a recomendação do Android
 * 
 * @author ricardo
 * 
 */
public class ThreadAtualizaOnibus implements Runnable {

	private Handler handler;
	Overlay onibus;
	Overlay onibusantigo;
	MapView map;
	int linha;
	boolean ON;
	MapController controller;
	String preffoco;


	public ThreadAtualizaOnibus(MapView map, Overlay onibus, Overlay onibusantigo, int linha, Handler handler, String preffoco, MapController controller){
		this.map = map;
		this.onibus = onibus;
		this.onibusantigo = onibusantigo;
		this.linha = linha;
		this.handler = handler;
		this.controller = controller;
		this.preffoco = preffoco;
		this.ON = true;
		
	}

	public void run() {	
		
		if (ON){

			handler.postDelayed(this, 30000);
			DesenhaOnibus(linha);
		}
	}

	public void kill(){
		this.ON = false;
	}
	
	
	public void DesenhaOnibus(int linha){
		// TODO Auto-generated method stub
	
	  double latonibus = 0;
	  double longitudeonibus = 0;

		  String response = null;
		try {
			WebAssyncTask z = new WebAssyncTask();
			response = z.readcircular("http://mc933.lab.ic.unicamp.br:8011/circular/"+linha);

	
      try{
    	  //Seta a resposta como um objeto JSON para acessar as 
    	  JSONObject o=new JSONObject(response);
    	  latonibus=Double.parseDouble(o.get("lat").toString());
    	  longitudeonibus=Double.parseDouble(o.get("long").toString());  		
  		} catch (JSONException e1) {
  			e1.printStackTrace();
  		}
  		
		GeoPoint pointbus = new GeoPoint((int) (latonibus * 1E6), (int) (longitudeonibus * 1E6));
        onibus = new PointOverlay(pointbus, map, "onibus");
        map.getOverlays().remove(onibusantigo);
        map.getOverlays().add(onibus);
        onibusantigo = onibus;
        if (preffoco.equals("bus")){
        	controller.setCenter(pointbus);
        	controller.animateTo(pointbus);
        }
		map.invalidate();
	} catch (Exception e) {
		Log.d("seminternet", "seminternet");
	}
}
}
