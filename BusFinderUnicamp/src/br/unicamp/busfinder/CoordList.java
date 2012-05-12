package br.unicamp.busfinder;

import java.util.ArrayList;

/** Contem get e set para as coordenadas */
public class CoordList {

	/** Lista de coordenadas */
	private ArrayList<String> coordenada = new ArrayList<String>();

	/** Retorna a lista de coordenadas */
	public ArrayList<String> getCoordenada() {
		return coordenada;
	}

	/** Adiciona a coordenada à lista */
	public void setCoordenada(String coordenada) {
		this.coordenada.add(coordenada);
	}

}