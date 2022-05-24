package it.polito.tdp.crimes.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.crimes.db.EventsDao;

public class Model {
	
	//private Graph<String, DefaultWeightedEdge> grafo;
	private List<String> best;
	private Graph<String, DefaultWeightedEdge> grafo;
	private EventsDao dao;
	
	public Model() {
		
		dao = new EventsDao();
	}
	
	public void creaGrafo(String categoria, int mese) {
		
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//aggiunta vertici
		Graphs.addAllVertices(this.grafo, dao.getVertici(categoria, mese));
		
		//aggiunta archi
		for(Adiacenza a : dao.getArchi(categoria, mese)) {
			Graphs.addEdgeWithVertices(this.grafo, a.getV1(), a.getV2(), a.getPeso());
		}
		System.out.println("Grafo creato");
		System.out.println("#Vertici: "+this.grafo.vertexSet().size());
		System.out.println("#Archi: "+this.grafo.edgeSet().size());
	}
	
	public List<Adiacenza> getArchiMaggioriPesoMedio(){
		//scorro gli arch del grafo e calcolo il peso medio
		double peso = 0.0;
		for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
			peso += this.grafo.getEdgeWeight(e);
					
		}
		double avg = peso / this.grafo.edgeSet().size();
		//riscorro tutti gli archi e prendo quelli maggiori di avg
		
	
	List<Adiacenza> result = new ArrayList<>();
	
	for(DefaultWeightedEdge e : this.grafo.edgeSet()) {
		if(this.grafo.getEdgeWeight(e) > avg)
			result.add(new Adiacenza(this.grafo.getEdgeSource(e), 
					this.grafo.getEdgeTarget(e), 
					(int) this.grafo.getEdgeWeight(e)));
	}
	return result;
	}
	
	
	public List<String> calcolaPercorso(String sorgente, String destinazione){
		
		//preparo la ricorsione
		best = new LinkedList<>();
		List<String> parziale = new LinkedList<>();
		//metto la sorgente in parziale, perche so che sara il mio punto di partenza
		parziale.add(sorgente);
		//richiamo il metodo ricorsivo
		cerca(parziale, destinazione);
		return best;
		
	}

	private void cerca(List<String> parziale, String destinazione) {
		
		//condizione di terminazione
		if(parziale.get(parziale.size()-1).equals(destinazione)) {
			
			//controllo che sia la soluzione migliore
			if(parziale.size()>best.size()) {
				best = new LinkedList<>(parziale);
			}
			return;
			}
		
		//scorro i vicini dell'ultimo inserito e provo le varie "strade"
		for(String v: Graphs.neighborListOf(this.grafo, parziale.get(parziale.size()-1))) {
			
			if(!parziale.contains(v)) {
			parziale.add(v);
			cerca(parziale, destinazione);
			parziale.remove(parziale.size()-1);
		}}
			
			
		
		
		
		
	}
	
}
