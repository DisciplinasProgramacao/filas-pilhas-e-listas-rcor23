import java.util.Locale;

public class ProdutoNaoPerecivel extends Produto {

    public ProdutoNaoPerecivel(String descricao, double precoCusto, double margemLucro){
        super(descricao, precoCusto, margemLucro);
    }

    public ProdutoNaoPerecivel(String descricao, double precoCusto){
        super(descricao, precoCusto);
    }

    @Override
    public double valorDeVenda(){
        return precoCusto * (1 + margemLucro);
    }

    @Override
    public String gerarDadosTexto() {
        String precoFormatado = String.format(Locale.US, "%.2f", precoCusto);
        String margemFormatada = String.format(Locale.US, "%.2f", margemLucro);
        return String.format("1;%s;%s;%s", descricao, precoFormatado, margemFormatada);
    }
}
