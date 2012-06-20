package br.unicamp.busfinder;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class PontosFavoritos2 {

	// Nome do banco
	private static final String NOME_BANCO = "pontosfavoritos";
	// Nome da tabela
	public static final String NOME_TABELA = "pontoslinha2";

	protected SQLiteDatabase db;

	public PontosFavoritos2(Context ctx) {
		// Abre o banco de dados já existente
		db = ctx.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + NOME_TABELA + " (id INTEGER PRIMARY KEY AUTOINCREMENT , nome TEXT NOT NULL);");
	
	}


	// Insere um novo carro
	public long salvar(int id, String nome) {
		ContentValues values = new ContentValues();
		values.put("id", id);
		values.put("nome", nome);
		long valorinserido = db.insert(NOME_TABELA, "", values);
		return valorinserido;
	}



	// Deleta o carro com os argumentos fornecidos
	public int deletar(long id) {
		
		String where = "id = ?";
		String _id = String.valueOf(id);
		String[] whereArgs = new String[] { _id };
		int count = db.delete(NOME_TABELA, where, whereArgs);
		return count;
	}

	// Busca o carro pelo id
	public boolean buscarPonto(long id) {
		// select * from carro where _id=?
		Cursor c = db.query(true, NOME_TABELA, new String[] { "id"}, "id" + "=" + id, null, null, null, null, null);
		if (c.getCount() > 0) {
			c.close();
			return true;

		}
		c.close();
		return false;
	}

	
	// Fecha o banco
	public void fechar() {
		// fecha o banco de dados
		if (db != null) {
			db.close();
		}
	}
}
