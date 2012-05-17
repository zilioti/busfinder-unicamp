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

public class RouteOverlay extends Overlay {
	
	private Paint paint = new Paint();
	private GeoPoint point1, point2;
	private MapView map;

	
	public RouteOverlay (GeoPoint point1, GeoPoint point2, MapView mapView) {
		this.point1 = point1;
		this.point2 = point2;
		this.map = mapView;
	}
	
	/* Desenha a marcacao */
	@Override
	public void draw(Canvas canvas, MapView mapView, 
			boolean shadow) {
		super.draw(canvas, mapView, shadow);
	
		//A PARTIR DAQUI TO TENTANDO FAZER ALGO AI aueheuhhaauhaeuheauhea
		 Paint   paint = new Paint();
	        paint.setDither(true);
	        paint.setColor(Color.BLUE);
	        paint.setStyle(Paint.Style.FILL_AND_STROKE);
	        paint.setStrokeJoin(Paint.Join.ROUND);
	        paint.setStrokeCap(Paint.Cap.ROUND);
	        paint.setStrokeWidth(5);
	        
	        Point p1 = new Point();
	        Point p2 = new Point();
	        Path path = new Path();
	        
	        map.getProjection().toPixels(point1, p1);
	        map.getProjection().toPixels(point2, p2);

	        path.moveTo(p2.x, p2.y);
	        path.lineTo(p1.x,p1.y);

	        canvas.drawPath(path, paint);
	
	        
	}
	
	
}