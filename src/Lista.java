// Conteúdo para src/Lista.java

import java.util.function.Function;
import java.util.function.Predicate;

public class Lista<E>{

	private Celula<E> primeiro; // Célula sentinela
	private Celula<E> ultimo;   // Última célula real da lista
	private int tamanho;

    /** Cria uma lista vazia com elemento sentinela */
	public Lista() {
		Celula<E> sentinela = new Celula<>(); // Cria a célula sentinela
		this.primeiro = sentinela; // 'primeiro' sempre aponta para a sentinela
		this.ultimo = sentinela;   // No início, 'ultimo' também aponta para a sentinela
		this.tamanho = 0;
	}

    /**
     * Indica se a lista está vazia ou não
     * @return TRUE/FALSE conforme a lista esteja vazia ou não
     */
	public boolean vazia() {
		// A lista está vazia se 'primeiro' (sentinela) e 'ultimo' apontam para a mesma célula
		return (this.primeiro == this.ultimo);
	}

    /**
     * Insere um elemento na posição final da lista.
     * @param elemento Elemento a ser inserido.
     */
    public void inserir(E elemento) {
        // Chama o inserir na posição 'tamanho', que é sempre o final
        inserir(elemento, tamanho);
    }

    /**
     * Insere um novo elemento na posição indicada. A posição 0 é após a sentinela.
     * A posição máxima válida para inserção é 'tamanho' (insere no final).
     * @param novo Elemento a ser inserido.
     * @param posicao Posição de referência para inserção (0 <= posicao <= tamanho).
     * @throws IndexOutOfBoundsException em caso de posição inválida
     */
	public void inserir(E novo, int posicao) {
		Celula<E> anterior, novaCelula, proximaCelula;

		// Valida a posição
		if ((posicao < 0) || (posicao > this.tamanho))
			throw new IndexOutOfBoundsException("Não foi possível inserir o item na lista: "
					+ "a posição " + posicao + " é inválida para uma lista de tamanho " + this.tamanho + "!");

		// Encontra a célula ANTERIOR à posição de inserção
		anterior = this.primeiro; // Começa da sentinela
		for (int i = 0; i < posicao; i++) {
			anterior = anterior.getProximo();
        }

		// Cria a nova célula com o item
		novaCelula = new Celula<>(novo);

		// Conecta a nova célula na lista
		proximaCelula = anterior.getProximo(); // Guarda a referência para a célula seguinte
		anterior.setProximo(novaCelula);      // 'anterior' agora aponta para 'novaCelula'
		novaCelula.setProximo(proximaCelula); // 'novaCelula' aponta para a 'proximaCelula'

		// Atualiza 'ultimo' se a inserção foi no final
		if (posicao == this.tamanho) {
			this.ultimo = novaCelula;
        }

		this.tamanho++; // Incrementa o tamanho
	}

    /**
     * Remove o último elemento da lista.
     * @return O elemento removido
     * @throws IllegalStateException em caso de lista vazia
     */
	public E remover() {
        // Chama remover passando a posição do último elemento (tamanho - 1)
        if (vazia()) { // Verifica antes para dar a mensagem correta
             throw new IllegalStateException("Não foi possível remover o último item: a lista está vazia!");
        }
        return remover(tamanho - 1);
     }


     /**
      * Remove um elemento da posição indicada. Se a lista estiver vazia ou a posição for inválida (<0 ou >=tamanho),
      * gera exceções. A posição 0 é o primeiro elemento real (após a sentinela).
      * @param posicao Posição do elemento a ser retirado (0 <= posicao < tamanho).
      * @return Elemento removido da lista.
      * @throws IllegalStateException se a lista estiver vazia.
      * @throws IndexOutOfBoundsException em caso de posição inválida.
      */
	public E remover(int posicao) {
		Celula<E> anterior, celulaRemovida, proximaCelula;

		// Verifica se está vazia
		if (vazia())
			throw new IllegalStateException("Não foi possível remover o item da lista: "
					+ "a lista está vazia!");

		// Valida a posição (para remoção, vai de 0 até tamanho-1)
		if ((posicao < 0) || (posicao >= this.tamanho ))
			throw new IndexOutOfBoundsException("Não foi possível remover o item da lista: "
					+ "a posição " + posicao + " é inválida para uma lista de tamanho " + this.tamanho + "!");

		// Encontra a célula ANTERIOR à que será removida
		anterior = this.primeiro; // Começa da sentinela
		for (int i = 0; i < posicao; i++) {
			anterior = anterior.getProximo();
        }

		// Identifica a célula a ser removida e a próxima
		celulaRemovida = anterior.getProximo();
		proximaCelula = celulaRemovida.getProximo();

		// Remove a célula da lista, ligando a anterior à próxima
		anterior.setProximo(proximaCelula);
		celulaRemovida.setProximo(null); // Desconecta a célula removida (ajuda GC)

		// Atualiza 'ultimo' se a célula removida era a última
		if (celulaRemovida == this.ultimo) {
			this.ultimo = anterior;
        }

		this.tamanho--; // Decrementa o tamanho

		return (celulaRemovida.getItem()); // Retorna o item da célula removida
	}

    /**
     * Retorna, sem retirar, um elemento na posição indicada pelo parâmetro. A posição 0
     * é o primeiro elemento real (após a sentinela) e a última é (tamanho-1).
     * Lança exceções para lista vazia ou posições inválidas.
     * @param posicao Posição do elemento a ser consultado (0 <= posicao < tamanho).
     * @return O elemento da posição indicada na lista.
     * @throws IllegalStateException se a lista estiver vazia.
     * @throws IndexOutOfBoundsException em caso de posição inválida.
     */
    public E elementoNaPosicao(int posicao) {
		Celula<E> aux;

		// Verifica se está vazia
		if (vazia())
			throw new IllegalStateException("Não foi possível consultar o item da lista: "
					+ "a lista está vazia!");

		// Valida a posição (para consulta, vai de 0 até tamanho-1)
		if ((posicao < 0) || (posicao >= this.tamanho ))
			throw new IndexOutOfBoundsException("Não foi possível consultar o item da lista: "
					+ "a posição " + posicao + " é inválida para uma lista de tamanho " + this.tamanho + "!");

		// Navega até a célula na posição desejada
		aux = primeiro.getProximo(); // Começa do primeiro elemento real
		for (int i = 0; i < posicao; i++) {
			aux = aux.getProximo();
        }

		return (aux.getItem()); // Retorna o item da célula encontrada
	}

    /**
     * Localiza um elemento na lista de acordo com um Predicado. Retorna o primeiro elemento na lista
     * que atende àquele predicado ou nulo caso não exista.
     * @param condicional Predicado com a condição para encontrar um elemento.
     * @return O primeiro elemento encontrado que atenda à condição ou null, caso não haja.
     * @throws IllegalStateException se a lista estiver vazia (opcional, pode retornar null direto).
     */
    public E localizar(Predicate<E> condicional) {
		Celula<E> aux;

		if (vazia()) {
             // Pode lançar exceção ou simplesmente retornar null se a lista vazia não deve ser um erro aqui.
             // Vamos retornar null para simplificar o uso em App.java
             // throw new IllegalStateException("Não foi possível localizar o item na lista: a lista está vazia!");
             return null;
        }

        // Percorre a lista a partir do primeiro elemento real
        aux = primeiro.getProximo();
        while (aux != null) {
            // Testa a condição para o item da célula atual
            if(condicional.test(aux.getItem())) {
                return aux.getItem(); // Retorna o item se a condição for atendida
            }
            aux = aux.getProximo(); // Vai para a próxima célula
        }
        return null; // Retorna null se não encontrou nenhum item que atenda à condição
	}

    /**
     * Conta quantos elementos na lista atendem à condição estabelecida pelo predicado (Tarefa 1).
     * @param condicional Predicado com a condição para verificação de elementos na lista.
     * @return Inteiro com a quantidade de elementos que atendem ao predicado (0 se a lista for vazia).
     */
    public int contarRepeticoes(Predicate<E> condicional){
        int contador = 0;
        Celula<E> aux = this.primeiro.getProximo(); // Começa do primeiro elemento real após o sentinela

        // Percorre a lista
        while (aux != null) {
            // Se o item da célula atual atende à condição, incrementa o contador
            if (condicional.test(aux.getItem())) {
                contador++;
            }
            aux = aux.getProximo(); // Vai para a próxima célula
        }
        return contador; // Retorna a contagem final
	}

    /**
	 * Calcula e retorna o valor total de um determinado atributo numérico (Double) dos elementos da lista,
	 * utilizando uma função de extração fornecida.
	 * @param extrator Uma função que recebe um elemento (E) e retorna seu valor (Double).
	 * @return O valor total (soma) dos atributos extraídos. Retorna 0.0 se a lista estiver vazia.
	 */
	public double calcularValorTotal(Function<E, Double> extrator) {
		double soma = 0.0;
		Celula<E> aux = primeiro.getProximo(); // Começa do primeiro elemento real

		// Percorre a lista
		while (aux != null) {
            // Extrai o valor do item atual usando a função 'extrator' e soma ao total
			soma += extrator.apply(aux.getItem());
			aux = aux.getProximo(); // Vai para a próxima célula
		}
		return soma; // Retorna a soma total (será 0.0 se a lista estava vazia)
	}

    /**
     * Retorna a quantidade atual de elementos na lista.
     * @return Inteiro não negativo com a quantidade atual de elementos na lista.
     */
    public int tamanho(){
        return tamanho;
    }

    /**
     * Retorna uma string com informação detalhada de cada elemento da lista.
     * A string indica as posições dos elementos, iniciando-se em 0.
     * Retorna "A Lista está vazia!" se a lista não contiver elementos.
     * @return Uma string com as informações de cada elemento da lista.
     */
    @Override
	public String toString() {
		Celula<E> aux;
		StringBuilder listaString = new StringBuilder(); 

	    if(vazia()){
            return "A Lista está vazia!"; 
        } else {
            int contador = 0;
			aux = primeiro.getProximo(); 
			while (aux != null) {
                String dado = String.format("Posição %d: %s\n", contador, aux.getItem().toString());
				listaString.append(dado);
				aux = aux.getProximo(); 
                contador++;
			}
            if (listaString.length() > 0) {
                 listaString.setLength(listaString.length() - 1);
            }
		}
        return listaString.toString(); 
	}
}
