package br.unicamp.busfinder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


	public class WebAssyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
		          
		          WebService webService = new WebService(url);
		          
		          //passa parametros para o servidor se preciso...
		          Map params = new HashMap();
		          
		          //Pega a resposta do servidor

		          response = webService.webGet("", params);
		            	

			}
			return response;
		}
		
		public String readcircular(String url) {
			return this.doInBackground(url);

		}

	}

