package br.unicamp.busfinder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class PointOverlay extends Overlay {
	
	private Paint paint = new Paint();
	private GeoPoint point;
	private MapView map;

	
	public PointOverlay (GeoPoint point, MapView mapView) {
		this.point = point;
		this.map = mapView;
	}
	
	/* Desenha a marcacao */
	@Override
	public void draw(Canvas canvas, MapView mapView, 
			boolean shadow) {
		super.draw(canvas, mapView, shadow);
		Point p = map.getProjection().toPixels(point, null);
		Bitmap pin = BitmapFactory.decodeResource(
				map.getResources(), R.drawable.mapa_pin);
		
		/* Centraliza a marcacao */
		Rect rectOverlay = 
				new Rect(p.x - (pin.getWidth())/2, 
						p.y - pin.getHeight(), p.x + (pin.getWidth())/2, p.y);
		canvas.drawBitmap(pin, null, rectOverlay, paint);
		
		//canvas.drawLine(p.x - (pin.getWidth())/2, p.y - pin.getHeight(),p.x - (pin.getWidth()), p.y - pin.getHeight(),paint);
	
		//A PARTIR DAQUI TO TENTANDO FAZER ALGO AI aueheuhhaauhaeuheauhea
		 Paint   mPaint = new Paint();
	        mPaint.setDither(true);
	        mPaint.setColor(Color.RED);
	        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	        mPaint.setStrokeJoin(Paint.Join.ROUND);
	        mPaint.setStrokeCap(Paint.Cap.ROUND);
	        mPaint.setStrokeWidth(2);
	        
	        GeoPoint gP1 = new GeoPoint(19240000,-99120000);
	        GeoPoint gP2 = new GeoPoint(37423157, -122085008);

	        Point p1 = new Point();
	        Point p2 = new Point();
	        Path path = new Path();

	        
	        map.getProjection().toPixels(gP1, p1);
	        map.getProjection().toPixels(gP2, p2);

	        path.moveTo(p2.x, p2.y);
	        path.lineTo(p1.x,p1.y);

	        canvas.drawPath(path, mPaint);
	        MapController controller;
	        controller = map.getController();	        
	        controller.setCenter(gP1);
	        
	}
	
	
}