/***
 * Copyright (c) 2010 readyState Software Ltd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package br.unicamp.busfinder;

import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * An abstract extension of ItemizedOverlay for displaying an information balloon
 * upon screen-tap of each marker overlay.
 * 
 * @author Jeff Gilfelt
 */
public abstract class BalloonItemizedOverlay<Item extends CustomOverlayItem> extends ItemizedOverlay<Item> {

	private static final long BALLOON_INFLATION_TIME = 300;
	private static Handler handler = new Handler();
	
	private MapView mapView;
	private BalloonOverlayView<Item> balloonView;
	
	private View clickRegion;
	private View closeRegion;
	private View buttonRegion;
	
	private int viewOffset;
	final MapController mc;
	private Item currentFocusedItem;
	private int currentFocusedIndex;
	
	private boolean showClose = true;
	private boolean showDisclosure = false;
	private boolean snapToCenter = true;
	
	private static boolean isInflating = false;
	
	private Context context;
	
	/**
	 * Create a new BalloonItemizedOverlay
	 * 
	 * @param defaultMarker - A bounded Drawable to be drawn on the map for each item in the overlay.
	 * @param mapView - The view upon which the overlay items are to be drawn.
	 */
	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker);
		this.mapView = mapView;
		viewOffset = 0;
		mc = mapView.getController();
		this.context = mapView.getContext();
	}
	
	/**
	 * Set the horizontal distance between the marker and the bottom of the information
	 * balloon. The default is 0 which works well for center bounded markers. If your
	 * marker is center-bottom bounded, call this before adding overlay items to ensure
	 * the balloon hovers exactly above the marker. 
	 * 
	 * @param pixels - The padding between the center point and the bottom of the
	 * information balloon.
	 */
	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}
	public int getBalloonBottomOffset() {
		return viewOffset;
	}
	
	/**
	 * Override this method to handle a "tap" on a balloon. By default, does nothing 
	 * and returns false.
	 * 
	 * @param index - The index of the item whose balloon is tapped.
	 * @param item - The item whose balloon is tapped.
	 * @return true if you handled the tap, otherwise false.
	 */
	protected boolean onBalloonTap(int index, Item item) {
		return false;
	}
	
	/**
	 * Override this method to perform actions upon an item being tapped before 
	 * its balloon is displayed.
	 * 
	 * @param index - The index of the item tapped.
	 */
	protected void onBalloonOpen(int index) {}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#onTap(int)
	 */
	@Override
	//protected final boolean onTap(int index) {
	public final boolean onTap(int index) {
		
		
		
		
		
		handler.removeCallbacks(finishBalloonInflation);
		isInflating = true;
		handler.postDelayed(finishBalloonInflation, BALLOON_INFLATION_TIME);
		
		currentFocusedIndex = index;
		currentFocusedItem = createItem(index);
		setLastFocusedIndex(index);
		
		double latponto = (double) (currentFocusedItem.getPoint().getLatitudeE6()/1E6);
		double longponto = (double) (currentFocusedItem.getPoint().getLongitudeE6()/1E6);
		String response = null;
		String tempo = "NAO CALCULADO";
	
		try {
			WebAssyncTask z = new WebAssyncTask();
			response = z.readprevisao("http://mc933.lab.ic.unicamp.br:8011/previsao/linha2?latponto="+String.valueOf(latponto)+"&longponto="+String.valueOf(longponto));
			 try{
				  //Seta a resposta como um objeto JSON para acessar as 
				  JSONObject o=new JSONObject(response);
				  tempo= String.valueOf(o.get("previsao"));	
				    		
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast toast = Toast.makeText(context, "Problema com a conexao.", Toast.LENGTH_SHORT);
   		 	toast.show();
		}
		
		
		
		
		currentFocusedItem.setSnippet2("Proximo onibus em "+tempo+" minutos");
		
		
		onBalloonOpen(index);
		createAndDisplayBalloonOverlay();
		
		if (snapToCenter) {
			mc.animateTo(currentFocusedItem.getPoint());
		}
		
		
	        
		 if(currentFocusedItem.mfavorito){
             buttonRegion.setBackgroundResource(android.R.drawable.btn_star_big_on);
             
             
        	 }
        	 else {
        		 buttonRegion.setBackgroundResource(android.R.drawable.btn_star_big_off);
        	
        		
        	 }
		
		return true;
	}

	/**
	 * Creates the balloon view. Override to create a sub-classed view that
	 * can populate additional sub-views.
	 */
	protected BalloonOverlayView<Item> createBalloonOverlayView() {
		return new BalloonOverlayView<Item>(getMapView().getContext(), getBalloonBottomOffset());
	}
	
	/**
	 * Expose map view to subclasses.
	 * Helps with creation of balloon views. 
	 */
	protected MapView getMapView() {
		return mapView;
	}
	
	/**
	 * Sets the visibility of this overlay's balloon view to GONE and unfocus the item. 
	 */
	public void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
		currentFocusedItem = null;
	}
	
	/**
	 * Hides the balloon view for any other BalloonItemizedOverlay instances
	 * that might be present on the MapView.
	 * 
	 * @param overlays - list of overlays (including this) on the MapView.
	 */
	private void hideOtherBalloons(List<Overlay> overlays) {
		
		for (Overlay overlay : overlays) {
			if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this) {
				((BalloonItemizedOverlay<?>) overlay).hideBalloon();
			}
		}
		
	}
	
	public void hideAllBalloons() {
		if (!isInflating) {
			List<Overlay> mapOverlays = mapView.getOverlays();
			if (mapOverlays.size() > 1) {
				hideOtherBalloons(mapOverlays);
			}
			hideBalloon();
		}
	}
	
	/**
	 * Sets the onTouchListener for the balloon being displayed, calling the
	 * overridden {@link #onBalloonTap} method.
	 */
	private OnTouchListener createBalloonTouchListener() {
		return new OnTouchListener() {
			
			float startX;
			float startY;
			
			public boolean onTouch(View v, MotionEvent event) {
				
				View l =  ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
				Drawable d = l.getBackground();
				
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int[] states = {android.R.attr.state_pressed};
					if (d.setState(states)) {
						d.invalidateSelf();
					}
					startX = event.getX();
					startY = event.getY();
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					int newStates[] = {};
					if (d.setState(newStates)) {
						d.invalidateSelf();
					}
					if (Math.abs(startX - event.getX()) < 40 && 
							Math.abs(startY - event.getY()) < 40 ) {
						// call overridden method
						onBalloonTap(currentFocusedIndex, currentFocusedItem);
					}
					return true;
				} else {
					return false;
				}
				
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#getFocus()
	 */
	@Override
	public Item getFocus() {
		return currentFocusedItem;
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.ItemizedOverlay#setFocus(Item)
	 */
	@Override
	public void setFocus(Item item) {
		super.setFocus(item);	
		currentFocusedIndex = getLastFocusedIndex();
		currentFocusedItem = item;
		if (currentFocusedItem == null) {
			hideBalloon();
		} else {
			createAndDisplayBalloonOverlay();
		}	
	}
	
	/**
	 * Creates and displays the balloon overlay by recycling the current 
	 * balloon or by inflating it from xml. 
	 * @return true if the balloon was recycled false otherwise 
	 */
	private boolean createAndDisplayBalloonOverlay(){
		boolean isRecycled;
		if (balloonView == null) {
			balloonView = createBalloonOverlayView();
			clickRegion = (View) balloonView.findViewById(R.id.balloon_inner_layout);
			clickRegion.setOnTouchListener(createBalloonTouchListener());
			closeRegion = (View) balloonView.findViewById(R.id.balloon_close);
			buttonRegion = (View) balloonView.findViewById(R.id.button1);
			

			if (closeRegion != null) {
				if (!showClose) {
					closeRegion.setVisibility(View.GONE);
				} else {
					closeRegion.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							hideBalloon();	
						}
					});
					buttonRegion.setOnClickListener(new View.OnClickListener() {
			             public void onClick(View v) {
			            	 if(!currentFocusedItem.mfavorito){ 
			            		 PontosFavoritos2 bancodedados = new PontosFavoritos2(mapView.getContext());
			            		 bancodedados.salvar(currentFocusedIndex, "nome do ponto"+String.valueOf(currentFocusedIndex));
			            		 buttonRegion.setBackgroundResource(android.R.drawable.btn_star_big_on);
			            		 currentFocusedItem.mfavorito = true;
			            		 bancodedados.fechar();
			            		 Toast toast = Toast.makeText(context, "Ponto adicionado aos favoritos.", Toast.LENGTH_SHORT);
			            		 toast.show();
			            	 }
			            	 else {
			            		 PontosFavoritos2 bancodedados = new PontosFavoritos2(mapView.getContext());
			            		 bancodedados.deletar(currentFocusedIndex);
			            		 buttonRegion.setBackgroundResource(android.R.drawable.btn_star_big_off);
			            		 currentFocusedItem.mfavorito = false;
			            		 bancodedados.fechar();
			            		 Toast toast = Toast.makeText(context, "Ponto removido dos favoritos.", Toast.LENGTH_SHORT);
			            		 toast.show();
			            		
			            	 }
			             }
					});
					
				}
			}
			if (showDisclosure && !showClose) {
				View v = balloonView.findViewById(R.id.balloon_disclosure);
				if (v != null) {
					v.setVisibility(View.VISIBLE);
				}
			}
			isRecycled = false;
		} else {
			isRecycled = true;
		}
	
		balloonView.setVisibility(View.GONE);
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			hideOtherBalloons(mapOverlays);
		}
		
		if (currentFocusedItem != null)
			balloonView.setData(currentFocusedItem);
		
		GeoPoint point = currentFocusedItem.getPoint();
		MapView.LayoutParams params = new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, point,
				MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;
		
		balloonView.setVisibility(View.VISIBLE);
		
		if (isRecycled) {
			balloonView.setLayoutParams(params);
		} else {
			mapView.addView(balloonView, params);
		}
		
		return isRecycled;
	}
	
	public void setShowClose(boolean showClose) {
		this.showClose = showClose;
	}

	public void setShowDisclosure(boolean showDisclosure) {
		this.showDisclosure = showDisclosure;
	}

	public void setSnapToCenter(boolean snapToCenter) {
		this.snapToCenter = snapToCenter;
	}

	public static boolean isInflating() {
		return isInflating;
	}
	
	private static Runnable finishBalloonInflation = new Runnable() {
		public void run() {
			isInflating = false;
		}
	};
	
}