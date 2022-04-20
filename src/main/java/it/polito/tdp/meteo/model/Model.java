package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.List;


import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	MeteoDAO meteoDao = new MeteoDAO();
	List<Citta> cit = meteoDao.getAllLocalita();
	List<Citta> best;
	double bestCosto = 0.0;
	
	
	public double getBestCosto() {
		return bestCosto;
	}
	
	public Model() {

	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		List<Media> m = meteoDao.getUmiditaMedia(mese);
		String s = "";
		for (Media r : m) {
			s += r.toString() + "\n";
		}
		return s;
	}
	
	// of course you can change the String output with what you think works best
	public List<Citta> trovaSequenza(int mese) {
		List<Citta> parziale = new ArrayList<>();
		this.best = null;

		for (Citta c : cit) {
			c.setRilevamenti(meteoDao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}

		cerca(parziale, 0);

		return best;
	}
	
	public void cerca(List<Citta> parziale, int livello) {
		if (livello == NUMERO_GIORNI_TOTALI) {
			if (best == null || calcolaCosto(best) > calcolaCosto(parziale)) {
				best = new ArrayList<Citta>(parziale);
				bestCosto = calcolaCosto(parziale);
			}
		} else {
			for (Citta c : cit) {
				if (isValid(parziale, c)) {
					parziale.add(c);
					cerca(parziale, livello + 1);
					parziale.remove(parziale.size() - 1);
				}
			}
		}
	}

	private double calcolaCosto(List<Citta> parziale) {
		double costo = 0.0;
		int i = 0;
		for (Citta c : parziale) {
			costo += c.getRilevamenti().get(i++).getUmidita();
			if(i != 1) {
				if(c.equals(parziale.get(i-2)) == false) {
					costo += COST;
				}
			}
		}
		return costo;
	}

	private boolean isValid(List<Citta> parziale, Citta prova) {
		int conta = 0;

		if (parziale.size() == 0) {
			return true;
		}

		for (Citta c : parziale) {
			if (c.equals(prova)) {
				conta++;
			}
		}

		if (conta >= NUMERO_GIORNI_CITTA_MAX) {
			return false;
		}

		if (parziale.size() == 1 || parziale.size() == 2) {
			if (parziale.get(parziale.size() - 1).equals(prova)) {
				return true;
			}
			return false;
		}

		if (parziale.get(parziale.size() - 1).equals(prova)) {
			return true;
		}
		if (parziale.get(parziale.size() - 1).equals(parziale.get(parziale.size() - 2))
				&& parziale.get(parziale.size() - 2).equals(parziale.get(parziale.size() - 3))) {
			return true;
		}

		return false;
	}
	

}
