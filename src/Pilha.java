import java.util.NoSuchElementException;

public class Pilha<E> {

	private Celula<E> topo;
	private Celula<E> fundo;

	public Pilha() {

		Celula<E> sentinela = new Celula<E>();
		fundo = sentinela;
		topo = sentinela;

	}

	public boolean vazia() {
		return fundo == topo;
	}

	public void empilhar(E item) {

		topo = new Celula<E>(item, topo);
	}

	public E desempilhar() {

		E desempilhado = consultarTopo();
		topo = topo.getProximo();
		return desempilhado;

	}

	public E consultarTopo() {

		if (vazia()) {
			throw new NoSuchElementException("Nao há nenhum item na pilha!");
		}

		return topo.getItem();

	}

	private void inverter() {
		
		Pilha<E> pilha = new Pilha<>();
		
		while (!vazia()) {
			pilha.empilhar(desempilhar());
		}
		
		this.fundo = pilha.fundo;
		this.topo = pilha.topo;
	}
	
	public Pilha<E> subPilha(int numItens) {
		
		Pilha<E> subPilha = new Pilha<>();
		Celula<E> aux = topo;
		int i = 0;
		
		while (aux != fundo && i < numItens) {
		
			subPilha.empilhar(aux.getItem());
			aux = aux.getProximo();
			i++;
		}
		
		if (i < numItens) {
			throw new IllegalArgumentException("Não há essa quantidade de itens na pilha!");
		}
		
		subPilha.inverter();
		return subPilha;
	}

	public void imprimir() {
		
		Celula<E> aux;
		
		if (vazia())
			System.out.println("A pilha está vazia!");
		else {
			aux = this.topo;
			while (aux != fundo) {
				System.out.println(aux.getItem());
				aux = aux.getProximo();
			}
		} 	
	}
}