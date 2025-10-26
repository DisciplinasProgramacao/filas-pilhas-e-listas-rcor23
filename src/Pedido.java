import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;

public class Pedido {

	/** Lista para armazenar os produtos do pedido */
	private Lista<Produto> produtos;

	/** Data de criação do pedido */
	private LocalDate dataPedido;

	/** Indica a quantidade total de produtos no pedido até o momento */
	private int quantProdutos = 0; // Controla a quantidade total de itens adicionados

	/** Construtor do pedido.
	 * Cria a lista de produtos do pedido e armazena a data atual do sistema. */
	public Pedido() {
		produtos = new Lista<Produto>(); // Cria a lista interna
		quantProdutos = 0;             // Inicializa contador
		dataPedido = LocalDate.now();  // Define a data do pedido
	}

	/**
     * Inclui um produto neste pedido e incrementa a quantidade total de produtos. (Tarefa 2)
     * @param novo O produto a ser incluído no pedido.
     * @return A nova quantidade total de produtos no pedido após a inclusão.
     */
	public int incluirProduto(Produto novo) {
        if (novo != null) {
		    produtos.inserir(novo); // Usa o método inserir da Lista (insere no final)
		    quantProdutos++;        // Incrementa o contador de itens
        } else {
             System.err.println("Tentativa de incluir produto nulo no pedido."); // Aviso de erro
        }
		return quantProdutos;
	}

    /**
     * Retorna a lista interna de produtos deste pedido.
     * @return A Lista<Produto> contendo os produtos do pedido.
     */
	public Lista<Produto> getProdutos() {
		return produtos;
	}

	/**
     * Calcula e retorna o valor final do pedido (soma do valor de venda de todos os produtos). (Tarefa 2)
     * @return Valor final do pedido (double). Retorna 0.0 se o pedido estiver vazio.
     */
	public double valorFinal() {
        if (produtos.vazia()) {
            return 0.0;
        }
		// Usa o método calcularValorTotal da Lista, passando uma referência ao método
        // valorDeVenda da classe Produto como a função extratora.
        return produtos.calcularValorTotal(Produto::valorDeVenda);
        // Alternativa usando lambda:
        // return produtos.calcularValorTotal(produto -> produto.valorDeVenda());
	}

	/**
     * Representação, em String, do pedido.
     * Contém um cabeçalho com sua data e depois, em cada linha, a descrição de cada produto (usando o toString da Lista).
     * Ao final, mostra o valor a ser pago pelo pedido, formatado.
     * @return Uma string contendo dados do pedido.
     */
    @Override
	public String toString() {
		StringBuilder stringPedido = new StringBuilder();
		DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		stringPedido.append("Pedido na data " + formatoData.format(dataPedido) + "\n");

        // Verifica se a lista de produtos está vazia antes de chamar toString nela
        if (!produtos.vazia()) {
            stringPedido.append("Produtos:\n");
			stringPedido.append(produtos.toString()); // Chama o toString da Lista interna
		} else {
            stringPedido.append("Pedido vazio.\n");
        }
        // Adiciona o valor final formatado
		stringPedido.append("\nValor a pagar: R$ " + String.format("%.2f", valorFinal()));

		return stringPedido.toString();
	}

	/**
	 * Conta as repetições de um produto dentro do pedido, comparando pela descrição (ignorando maiúsculas/minúsculas). (Tarefa 2)
	 * @param produto Objeto "Produto" cuja descrição será usada para a contagem.
	 * @return Quantidade de repetições deste produto (por descrição) no pedido.
	 */
	public int repeticoes(Produto produto){
        if (produto == null || produto.descricao == null) return 0; // Segurança

		// Cria um predicado (condição) que compara a descrição do produto atual (p)
        // com a descrição do produto fornecido, ignorando maiúsculas/minúsculas.
        Predicate<Produto> comparaDescricaoIgnoreCase = p -> p != null && p.descricao != null && p.descricao.equalsIgnoreCase(produto.descricao);

		// Usa o método contarRepeticoes da Lista interna, passando o predicado criado.
        return produtos.contarRepeticoes(comparaDescricaoIgnoreCase);
	}

	/**
	 * Retorna uma descrição resumida do pedido em uma única linha.
	 * Formato: "Pedido com XX produtos em DD/MM/AAAA, valor total de R$ XX,XX"
	 * @return Uma string com o resumo do pedido.
	 */
	public String resumo() {
		DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		return ("Pedido com " + quantProdutos + " produtos em " + formatoData.format(dataPedido)
		+ ", valor total de R$ " + String.format("%.2f", valorFinal()));
	}
}
