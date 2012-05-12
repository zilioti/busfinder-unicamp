package br.unicamp.busfinder;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KMLHandler extends DefaultHandler {

	Boolean currentElement = false;
	String currentValue = null;
	
	public static CoordList coordList = null;

	public static CoordList getSitesList() {
		return coordList;
	}

	public static void setSitesList(CoordList coordList) {
		KMLHandler.coordList = coordList;
	}

	/** Chamada para quando a tag de coordenada inicia */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

			currentElement = true;

			if (localName.equals("kml")){
				coordList = new CoordList();
			} 

	}

	/** Chamada para quando a tag de coordenada encerra */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

			currentElement = false;

			if (localName.equalsIgnoreCase("coordinates"))
				coordList.setCoordenada(currentValue);

	}

	
	/** Chamada para coletar os dados na tag */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (currentElement) {
			currentValue = new String(ch, start, length);
			currentElement = false;
		}

	}

}