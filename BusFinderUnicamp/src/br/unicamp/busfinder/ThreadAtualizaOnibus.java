package br.unicamp.busfinder;



import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import android.os.Handler;
import android.widget.Toast;


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
			try {
				DesenhaOnibus(linha);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
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
				return;
			}
		} catch (Exception e) {
			Toast toast = Toast.makeText(map.getContext(), "Problema com a conexao.", Toast.LENGTH_SHORT);
   		 	toast.show();
			return;
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
		
	}
}
