package appspot.simulaedp.test.logic;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import appspot.simulaedp.exception.ProcessosNaoCarregadosException;
import appspot.simulaedp.logic.Escalonador;
import appspot.simulaedp.logic.impl.Prioridade;
import appspot.simulaedp.model.Processo;
import appspot.simulaedp.test.InitialCase;

public class PrioridadeTest extends InitialCase {

	private static final int[] BURSTS = { 100, 20, 50, 10, 40 };
	private static final int[] PRIORIDADE = { 1, 5, 3, 4, 2 };
	private static final int[] ID_PREVISTO_POR_PRIORIDADE = { 1, 5, 3, 4, 2 };
	private static final int[] TEMPO_ESPERA_PREVISTA_POR_BURSTS = { 0, 100, 140, 190, 200 };
	private static final int[] TEMPO_RESPOSTA_PREVISTA_POR_BURSTS = { 100, 140, 190, 200, 220 };
	private static final int[] TURN_AROUND_PREVISTA_POR_BURSTS = { 100, 140, 190, 200, 220 };

	@Test
	public void deveRetornarResultadoOrdenadoPorPrioridade() {
		Escalonador prioridade = new Prioridade(gerarArrayListDeProcessos(BURSTS.length, BURSTS, null, PRIORIDADE));

		prioridade.executar();
		List<Processo> resultado = prioridade.resultadoFinal();
		List<Processo> resultadoGrafico = prioridade.resultadoGraficoFinal();
		double esperaMedia = prioridade.tempoEsperaMedia();
		double respostaMedia = prioridade.tempoRespostaMedia();
		double turnAroundMedio = prioridade.tempoTurnAroundMedio();
		checarResultadoMedio(turnAroundMedio);
		checarResultadoMedio(respostaMedia);
		checarResultadoMedio(esperaMedia);
		checarResultado(resultado);
		checarResultado(resultadoGrafico);
		Assert.assertTrue(prioridade.totalProcessos() == BURSTS.length);
	}

	private void checarResultadoMedio(double val) {
		Assert.assertTrue(val > 0.0);
	}

	private void checarResultado(List<Processo> resultado) {
		Assert.assertThat(resultado, Matchers.notNullValue());
		Assert.assertThat(resultado.size(), Matchers.is(PRIORIDADE.length));
		for (int i = 0; i < resultado.size(); i++) {
			Assert.assertThat(resultado.get(i).getId(), Matchers.equalTo(ID_PREVISTO_POR_PRIORIDADE[i]));
			Assert.assertThat(resultado.get(i).getEspera(), Matchers.equalTo(TEMPO_ESPERA_PREVISTA_POR_BURSTS[i]));
			Assert.assertThat(resultado.get(i).getResposta(), Matchers.equalTo(TEMPO_RESPOSTA_PREVISTA_POR_BURSTS[i]));
			Assert.assertThat(resultado.get(i).getTurnAround(), Matchers.equalTo(TURN_AROUND_PREVISTA_POR_BURSTS[i]));
		}
	}

	@Test
	public void deveEscalonarComUmAMilProcesso() {
		for (int i = 1; i <= 1000; i++) {
			Escalonador prioridade = new Prioridade(gerarListaDeProcessos(i, VALIDO));
			prioridade.executar();
			List<Processo> resultado = prioridade.resultadoFinal();
			Assert.assertThat(resultado, Matchers.notNullValue());
			Assert.assertThat(resultado.size(), Matchers.is(i));
		}
	}

	@Test(expected = ProcessosNaoCarregadosException.class)
	public void naoDeveEscalonarProcessosComBurstNegativo() {
		new Prioridade(gerarListaDeProcessos(3, INVALIDO));
	}

	@Test(expected = ProcessosNaoCarregadosException.class)
	public void naoDeveGerarResultadoSemEscalonarOsProcessosAntes() {
		new Prioridade(null);
	}
}