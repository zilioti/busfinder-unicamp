package br.unicamp.busfinder;


import java.util.HashMap;
import java.util.Map;
import android.os.AsyncTask;



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
		
		public String readprevisao(String url) {
			return this.doInBackground(url);

		}


	}

