package br.unicamp.busfinder;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;



public class FavoritosActivity extends ListActivity {
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		/* faz o PARSE do ARQUIVO utilizando DOM */
		Document doc = null;
 		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 		DocumentBuilder db;
 		try {
 			
			db = dbf.newDocumentBuilder();
			doc = db.parse(new InputSource(getAssets().open("Linha2.kml")));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
 		Log.d("aaaaaaa",String.valueOf(doc.getElementsByTagName("Placemark").getLength()));
		PontosFavoritos2 bancodedados = new PontosFavoritos2(this);
		
		ArrayList<String> itens = new ArrayList<String>();

	   
		for (int i = 0; i < doc.getElementsByTagName("Placemark").getLength() - 1; i++) {
			String x = doc.getElementsByTagName("Placemark").item(i).getChildNodes().item(1).getTextContent();
			if (bancodedados.buscarPonto(i) == true) {
				itens.add(x);
			}

		}
		bancodedados.fechar();
		// Array de Strings para visualizar na Lista
		
		// Utiliza o adaptador ArrayAdapter, para exibir o array de Strings na tela.
		ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, itens);
		setListAdapter(adaptador);
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Pega o item naquela posição
		Object o = this.getListAdapter().getItem(position);

		String item = o.toString();

		//Exibe um alerta
		Toast.makeText(this, "Você selecionou: " + item, Toast.LENGTH_SHORT).show();
	}
}
