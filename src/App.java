import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.Scanner;

public class App {

    static String nomeArquivoDados;
    static Scanner teclado;
    static Lista<Produto> produtosCadastrados;
    static Lista<Pedido> listaPedidos = new Lista<>();

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("\nDigite enter para continuar...");
        if (teclado.hasNextLine()) {
             String pending = teclado.nextLine();
             if (!pending.isEmpty()) {
                 // Simplified handling
             }
        }
        teclado.nextLine();
    }

    static void cabecalho() {
        limparTela();
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }

    @SuppressWarnings("unchecked")
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
    	T valor = null;
        System.out.print(mensagem + " ");
    	String linha = teclado.nextLine().trim();

        if (linha.isEmpty()) {
            System.err.println("Entrada vazia detectada.");
            linha = "-1";
        }

        try {
            valor = (T)classe.getMethod("valueOf", String.class).invoke(null, linha.replace(",", "."));
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException | SecurityException e) {
             System.err.println("Formato numérico inválido ou erro interno: '" + linha + "'. Usando valor de erro. Detalhe: " + e.getMessage());
             try {
                if (classe == Double.class) valor = (T)Double.valueOf(-1.0);
                else if (classe == Float.class) valor = (T)Float.valueOf(-1.0f);
                else valor = (T)Integer.valueOf(-1);
            } catch (ClassCastException ignored) {
                 valor = null;
                 System.err.println("Erro inesperado no cast do valor de erro.");
            }
        }
        return valor;
    }

    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar por um produto, por código");
        System.out.println("3 - Procurar por um produto, por nome");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido atual");
        System.out.println("6 - Repeticoes de um produto em um pedido finalizado");
        System.out.println("0 - Sair");
        System.out.println("-----------------------------");
        int opcao = lerOpcao("Digite sua opção: ", Integer.class);
        return opcao;
    }

    static Lista<Produto> lerProdutos(String nomeArquivo) {
    	Scanner arquivo = null;
    	String linha;
    	Produto produto;
    	Lista<Produto> produtosLidos = new Lista<>();

    	try {
            File file = new File(nomeArquivo);
            System.out.println("Tentando ler arquivo: " + file.getAbsolutePath());
    		arquivo = new Scanner(file, Charset.forName("UTF-8"));

            if (arquivo.hasNextLine()) {
                arquivo.nextLine();
            }

    		while (arquivo.hasNextLine()) {
    			linha = arquivo.nextLine();
                if (!linha.trim().isEmpty()) {
                    try {
                        produto = Produto.criarDoTexto(linha);
                        if (produto != null) {
                            produtosLidos.inserir(produto);
                        }
                    } catch (Exception e) {
                        System.err.println("ERRO ao processar linha do arquivo: '" + linha + "'. Detalhe: " + e.getMessage());
                    }
                }
    		}
            System.out.println(produtosLidos.tamanho() + " produtos lidos do arquivo.");

    	} catch (IOException excecaoArquivo) {
            System.err.println("ERRO GRAVE ao abrir ou ler o arquivo de produtos: " + nomeArquivo);
            System.err.println("Verifique se o arquivo existe no local correto (raiz do projeto) e tem permissão de leitura.");
            System.err.println("Detalhes do erro: " + excecaoArquivo.getMessage());
    		produtosLidos = null;
    	} finally {
    		if (arquivo != null) {
                arquivo.close();
            }
    	}
    	return produtosLidos;
    }

    static Produto localizarProduto() {
        Produto produtoEncontrado = null;
    	cabecalho();
        if (produtosCadastrados == null || produtosCadastrados.vazia()) {
            System.out.println("Não há produtos cadastrados para localizar.");
            return null;
        }
    	System.out.println("Localizando um produto por código...");
        int idProduto = lerOpcao("Digite o ID do produto desejado:", Integer.class);
        if (idProduto <= 0) {
            System.out.println("Código inválido inserido.");
            return null;
        }
        try {
            produtoEncontrado = produtosCadastrados.localizar(prod -> prod.hashCode() == idProduto);
        } catch (IllegalStateException e) {
             System.out.println("Erro interno: " + e.getMessage());
             produtoEncontrado = null;
        }
        if (produtoEncontrado == null) {
            System.out.println("Produto com ID " + idProduto + " não encontrado.");
        }
        return produtoEncontrado;
    }

    static Produto localizarProdutoDescricao() {
    	Produto produtoEncontrado = null;
         if (produtosCadastrados == null || produtosCadastrados.vazia()) {
            System.out.println("Não há produtos cadastrados para localizar.");
            return null;
        }
    	System.out.println("Localizando um produto por nome/descrição...");
    	System.out.print("Digite o nome ou a descrição do produto desejado: ");
        String descricao = teclado.nextLine().trim();
        if (descricao.isEmpty()) {
             System.out.println("Descrição não pode ser vazia para busca.");
             return null;
        }
        try {
            produtoEncontrado = produtosCadastrados.localizar(prod -> prod.descricao.equalsIgnoreCase(descricao));
        } catch (IllegalStateException e) {
             System.out.println("Erro interno: " + e.getMessage());
             produtoEncontrado = null;
        } catch (NullPointerException e) {
             System.err.println("Erro: Produto inválido encontrado durante a busca.");
             produtoEncontrado = null;
        }
        return produtoEncontrado;
    }

    private static void mostrarProduto(Produto produto) {
        String mensagem;
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto.toString());
        } else {
            mensagem = "Produto não encontrado ou dados inválidos.";
        }
        System.out.println(mensagem);
    }

    static void listarTodosOsProdutos() {
        cabecalho();
        System.out.println("\n--- PRODUTOS CADASTRADOS ---");
        if (produtosCadastrados != null && !produtosCadastrados.vazia()) {
            System.out.println(produtosCadastrados.toString());
        } else {
            System.out.println("Nenhum produto cadastrado ou erro na leitura do arquivo.");
        }
        System.out.println("----------------------------");
    }

    public static Pedido iniciarPedido() {
    	Pedido novoPedido = new Pedido();
    	Produto produtoEncontrado;
    	int numProdutosAIncluir;
        cabecalho();
        if (produtosCadastrados == null || produtosCadastrados.vazia()) {
            System.out.println("Não há produtos cadastrados no sistema para iniciar um pedido.");
            return null;
        }
    	listarTodosOsProdutos();
    	System.out.println("\n--- Iniciando Novo Pedido ---");
    	numProdutosAIncluir = lerOpcao("Quantos tipos de produtos diferentes deseja incluir neste pedido? (0 para cancelar):", Integer.class);
        if (numProdutosAIncluir <= 0) {
            System.out.println("Criação de pedido cancelada.");
            return null;
        }
        for (int i = 0; i < numProdutosAIncluir; i++) {
            System.out.printf("\n--- Incluindo Produto %d de %d ---\n", i + 1, numProdutosAIncluir);
        	produtoEncontrado = localizarProdutoDescricao();
        	if (produtoEncontrado == null) {
        		System.out.println("Produto não encontrado. Tente novamente para este item.");
        		i--;
            } else {
                mostrarProduto(produtoEncontrado);
                String confirma = "";
                while (!confirma.equalsIgnoreCase("S") && !confirma.equalsIgnoreCase("N")) {
                     System.out.print("Confirmar inclusão deste produto no pedido (S/N)? ");
                     confirma = teclado.nextLine();
                }
                if (confirma.equalsIgnoreCase("S")) {
                     novoPedido.incluirProduto(produtoEncontrado);
                     System.out.println("'" + produtoEncontrado.descricao + "' incluído com sucesso.");
                } else {
                     System.out.println("Inclusão cancelada pelo usuário.");
                }
            }
        }
        System.out.println("\n--- Fim da Inclusão de Produtos ---");
        System.out.println("Resumo do Pedido Atual:");
        System.out.println(novoPedido.resumo());
    	return novoPedido;
    }

    public static boolean finalizarPedido(Pedido pedido) {
        cabecalho();
        boolean finalizado = false;
        if (pedido == null) {
            System.out.println("Nenhum pedido ativo para finalizar.");
        } else if (pedido.getProdutos() == null || pedido.getProdutos().vazia()) {
            System.out.println("O pedido atual está vazio. Não pode ser finalizado.");
        } else {
            listaPedidos.inserir(pedido);
            System.out.println("Pedido finalizado com sucesso e adicionado à lista de pedidos concluídos.");
            System.out.println("\nResumo do Pedido Finalizado:");
            System.out.println(pedido.resumo());
            finalizado = true;
        }
        return finalizado;
    }

    public static void repeticoesDeProdutoNoPedido() {
        cabecalho();
        int tamanhoLista = listaPedidos.tamanho();
        if (tamanhoLista == 0) {
            System.out.println("Ainda não há pedidos finalizados na lista para consultar.");
            return;
        }
        System.out.println("--- Consulta de Repetições em Pedidos Finalizados ---");
        System.out.println("Pedidos disponíveis:");
        for(int i = 0; i < tamanhoLista; i++) {
            try {
                Pedido p = listaPedidos.elementoNaPosicao(i);
                System.out.printf("  %d: %s\n", i + 1, p.resumo());
            } catch (Exception e) {
                 System.out.printf("  Erro ao obter resumo do pedido na posição %d\n", i + 1);
            }
        }
        System.out.println("-----------------------------------------------------");
        String mensagem = String.format("Digite a posição do pedido na lista (de 1 a %d) para verificar:", tamanhoLista);
        int N = lerOpcao(mensagem, Integer.class);
        if (N < 1 || N > tamanhoLista) {
            System.out.println("Posição inválida. Operação cancelada.");
            return;
        }
        try {
            Pedido qualPedido = listaPedidos.elementoNaPosicao(N - 1);
            System.out.print("Informe o nome/descrição do produto a ser contado no pedido " + N + ": ");
            String descricao = teclado.nextLine().trim();
             if (descricao.isEmpty()) {
                 System.out.println("Descrição do produto não pode ser vazia. Operação cancelada.");
                 return;
             }
            Produto produtoAContar = new ProdutoNaoPerecivel(descricao, 0.01);
            int quantidade = qualPedido.repeticoes(produtoAContar);
            System.out.printf("\nResultado: O produto '%s' aparece %d vez(es) no pedido %d.\n", descricao, quantidade, N);
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
             System.err.println("Erro ao acessar o pedido selecionado: " + e.getMessage());
        } catch (IllegalArgumentException e) {
             System.err.println("Erro com a descrição fornecida: " + e.getMessage());
        } catch (Exception e) {
             System.err.println("Ocorreu um erro inesperado ao contar as repetições: " + e.getMessage());
             e.printStackTrace();
        }
    }

	public static void main(String[] args) {
        try {
             teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        } catch (Exception e) {
             System.err.println("UTF-8 não suportado, usando codificação padrão do sistema.");
             teclado = new Scanner(System.in);
        }
        nomeArquivoDados = "produtos.txt";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        if (produtosCadastrados == null) {
             System.out.println("\nERRO CRÍTICO: Falha ao carregar a lista de produtos do arquivo '" + nomeArquivoDados + "'.");
             System.out.println("Verifique se o arquivo existe na raiz do projeto e se o programa tem permissão para lê-lo.");
             System.out.println("O programa será encerrado.");
             pausa();
             teclado.close();
             return;
        }
        System.out.println("Arquivo de produtos lido com sucesso.");
        System.out.println(produtosCadastrados.tamanho() + " produtos foram carregados para o catálogo.");
        pausa();
        Pedido pedidoAtual = null;
        int opcao = -1;
        do{
            opcao = menu();
            switch (opcao) {
                case 1:
                    listarTodosOsProdutos();
                    break;
                case 2:
                    mostrarProduto(localizarProduto());
                    break;
                case 3:
                    mostrarProduto(localizarProdutoDescricao());
                    break;
                case 4:
                    if (pedidoAtual != null) {
                        System.out.println("ERRO: Já existe um pedido em andamento.");
                        System.out.println("Finalize o pedido atual (opção 5) antes de iniciar um novo.");
                    } else {
                        pedidoAtual = iniciarPedido();
                        if (pedidoAtual == null) {
                             System.out.println("Início de pedido cancelado ou falhou.");
                        } else {
                            System.out.println("Novo pedido iniciado.");
                        }
                    }
                    break;
                case 5:
                    boolean foiFinalizado = finalizarPedido(pedidoAtual);
                    if (foiFinalizado) {
                        pedidoAtual = null;
                    }
                    break;
                case 6:
                    repeticoesDeProdutoNoPedido();
                    break;
                case 0:
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("Opção inválida ("+ opcao +"). Por favor, tente novamente.");
                    break;
            }
            if (opcao != 0) {
                 pausa();
            }
        } while(opcao != 0);
        System.out.println("\nPrograma encerrado.");
        teclado.close();
    }
}
