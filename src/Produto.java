import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale; // Adicionado baseado no uso de NumberFormat

public abstract class Produto implements Comparable<Produto> {
    private static final double MARGEM_PADRAO = 0.2;
    private static int ultimoID = 10_000;

    protected int idProduto;
    protected String descricao;
    protected double precoCusto;
    protected double margemLucro;

    private void init(String desc, double precoCusto, double margemLucro){
        if(desc.length()<3 ||precoCusto<=0||margemLucro<=0)
            throw new IllegalArgumentException("Valores inválidos para o produto");
        descricao = desc;
        this.precoCusto = precoCusto;
        this.margemLucro = margemLucro;
        idProduto = ultimoID++;
    }

    protected Produto(String desc, double precoCusto, double margemLucro){
        init(desc, precoCusto, margemLucro);
    }

    protected Produto(String desc, double precoCusto){
        init(desc, precoCusto, MARGEM_PADRAO);
    }

    public abstract double valorDeVenda();

    @Override
    public int hashCode(){
        return idProduto;
    }

    @Override
    public String toString(){
        NumberFormat moeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")); // Especifica Locale aqui
        return String.format("%05d - %s: %s", idProduto, descricao, moeda.format(valorDeVenda())); // Usa 5 dígitos para ID como na versão corrigida
    }

    @Override
    public int compareTo(Produto outro){
        // Mantém a comparação original (case-sensitive) do seu código
        if (outro == null) return 1;
        return this.descricao.compareTo(outro.descricao);
    }

    @Override
    public boolean equals(Object obj){
        try{
            Produto outro = (Produto)obj;
            return this.hashCode() == outro.hashCode();
        }catch (ClassCastException ex){
            return false;
        }
    }

    static Produto criarDoTexto(String linha) throws IllegalArgumentException, DateTimeParseException { // Adiciona throws
        Produto novoProduto = null;
        String[] detalhes = linha.split(";");
        if (detalhes.length < 4 || detalhes.length > 5) { // Validação básica do número de campos
            throw new IllegalArgumentException("Formato inválido da linha: " + linha);
        }
        try {
            String tipo = detalhes[0].trim();
            String descr = detalhes[1].trim();
            double precoCusto = Double.parseDouble(detalhes[2].trim().replace(",", "."));
            double margem = Double.parseDouble(detalhes[3].trim().replace(",", "."));
            if(tipo.equals("1")){
                 if (detalhes.length != 4) throw new IllegalArgumentException("Formato inválido (esperado 4 campos para tipo 1): " + linha);
                novoProduto = new ProdutoNaoPerecivel(descr, precoCusto, margem);
            }
            else if (tipo.equals("2")){
                 if (detalhes.length != 5) throw new IllegalArgumentException("Formato inválido (esperado 5 campos para tipo 2): " + linha);
                LocalDate dataValidade =
                    LocalDate.parse(detalhes[4].trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                novoProduto = new ProdutoPerecivel(descr, precoCusto, margem, dataValidade);
            } else {
                 throw new IllegalArgumentException("Tipo de produto inválido '" + tipo + "' na linha: " + linha);
            }
        } catch (NumberFormatException e) {
             throw new IllegalArgumentException("Erro ao converter número na linha: " + linha, e);
        } catch (DateTimeParseException e) {
             throw new DateTimeParseException("Erro ao converter data (use dd/MM/yyyy) na linha: " + linha, e.getParsedString(), e.getErrorIndex(), e);
        } catch (IllegalArgumentException e) { // Captura exceções dos construtores
             throw new IllegalArgumentException("Erro nos dados do produto: " + e.getMessage() + " Linha: " + linha, e);
        }
        return novoProduto;
    }

    public abstract String gerarDadosTexto();
}
