package br.unicamp.busfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class PointOverlay extends Overlay {
	
	private Paint paint = new Paint();
	private GeoPoint point;
	private MapView map;
	private String imagem;
	private String tema;

	
	public PointOverlay (GeoPoint point, MapView mapView, String imagem, String tema) {
		this.point = point;
		this.map = mapView;
		this.imagem = imagem;
		this.tema = tema;
	}
	
	/* Desenha a marcacao */
	@Override
	public void draw(Canvas canvas, MapView mapView, 
			boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Point p = map.getProjection().toPixels(point, null);
		Bitmap pin = null;
		
		if (imagem.equals("onibus")){
			if(tema.equals("pattern")){
				pin = BitmapFactory.decodeResource(
						map.getResources(), R.drawable.bus1);
			}
			else if(tema.equals("mario")){
				pin = BitmapFactory.decodeResource(
						map.getResources(), R.drawable.bus2);
			}
		}
		else if (imagem.equals("pessoa")){
			if(tema.equals("pattern")){
				pin = BitmapFactory.decodeResource(
						map.getResources(), R.drawable.you1);
			}
			else if(tema.equals("mario")){
				pin = BitmapFactory.decodeResource(
						map.getResources(), R.drawable.you2);
			}
		}
		
		/* Centraliza a marcacao */
		Rect rectOverlay = 
				new Rect(p.x - (pin.getWidth())/2, 
						p.y - pin.getHeight(), p.x + (pin.getWidth())/2, p.y);
		canvas.drawBitmap(pin, null, rectOverlay, paint);
	        
	}
	
	
}