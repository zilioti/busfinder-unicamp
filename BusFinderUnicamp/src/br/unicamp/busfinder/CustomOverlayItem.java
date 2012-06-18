/***
 * Copyright (c) 2011 readyState Software Ltd
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

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class CustomOverlayItem extends OverlayItem {

	protected String msnippet2;
	protected boolean mfavorito;
	
	public CustomOverlayItem(GeoPoint ponto, String title, String snippet, String snippet2, boolean favorito) {
		super(ponto, title, "");
		//aqui faz a requisicao do servidor
		String tempo = null;
		
		double latponto = (double) (ponto.getLatitudeE6()/1E6);
		double longponto = (double) (ponto.getLongitudeE6()/1E6);
		String response = null;
		
		
	/*	try {
			WebAssyncTask z = new WebAssyncTask();
			response = z.readcircular("http://mc933.lab.ic.unicamp.br:8011/previsao/linha2?latponto="+String.valueOf(latponto)+"&longponto="+String.valueOf(longponto));

			 try{
				  //Seta a resposta como um objeto JSON para acessar as 
				  JSONObject o=new JSONObject(response);
				  Log.d("bbbbbbbbbbbbb",response);
				  tempo= String.valueOf(o.get("previsao"));
				  Log.d("aaaaaaaaaaa",tempo);
					
				    		
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d("sem internet", "erro");
		}
		
		*/
		
		
		//msnippet2 = tempo;
		msnippet2 = "valor";
		mfavorito = favorito;
		
		
	}

	public String getSnippet2() {
		return msnippet2;
	}

	public void setSnippet2(String snippet2) {
		this.msnippet2 = snippet2;
	}
	
	public boolean getFavorito() {
		return mfavorito;
	}

	public void setFavorito(boolean favorito) {
		this.mfavorito = favorito;
	}

}
