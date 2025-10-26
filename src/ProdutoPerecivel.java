import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class ProdutoPerecivel extends Produto{

    private static final double DESCONTO = 0.25;
    private static final int PRAZO_DESCONTO = 7;
    private LocalDate dataDeValidade;
    private static final DateTimeFormatter FORMATADOR_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ProdutoPerecivel(String descricao, double precoCusto, double margemLucro, LocalDate validade){
        super(descricao, precoCusto, margemLucro);
        if(validade == null || validade.isBefore(LocalDate.now())) {
             throw new IllegalArgumentException("Data de validade inválida (nula ou anterior ao dia de hoje)!");
        }
        this.dataDeValidade = validade;
    }

    @Override
    public double valorDeVenda() {
        double valorSemDesconto = precoCusto * (1 + margemLucro);
        double descontoAplicado = 0d;
        long diasValidade = ChronoUnit.DAYS.between(LocalDate.now(), dataDeValidade);

        if(diasValidade <= PRAZO_DESCONTO) {
            descontoAplicado = DESCONTO;
        }
        return valorSemDesconto * (1 - descontoAplicado);
    }

    @Override
    public String toString(){
        String dadosSuper = super.toString();
        dadosSuper += " - Válido até " + FORMATADOR_DATA.format(dataDeValidade);
        return dadosSuper;
    }

    @Override
    public String gerarDadosTexto() {
        String precoFormatado = String.format(Locale.US, "%.2f", precoCusto);
        String margemFormatada = String.format(Locale.US, "%.2f", margemLucro);
        String dataFormatada = FORMATADOR_DATA.format(dataDeValidade);
        return String.format("2;%s;%s;%s;%s", descricao, precoFormatado, margemFormatada, dataFormatada);
    }
}
