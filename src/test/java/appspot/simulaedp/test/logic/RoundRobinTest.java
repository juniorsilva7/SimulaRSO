package appspot.simulaedp.test.logic;

import java.util.ArrayList;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import appspot.simulaedp.exception.ProcessosNaoCarregadosException;
import appspot.simulaedp.exception.TempoQuantumException;
import appspot.simulaedp.logic.Escalonador;
import appspot.simulaedp.logic.impl.RoundRobin;
import appspot.simulaedp.model.Processo;
import appspot.simulaedp.test.InitialCase;

public class RoundRobinTest extends InitialCase {

	private static final int QUANTUM_VALIDO = 20;
	private static final int QUANTUM_INVALIDO = 0;

	private static final int[] BURSTS_SIMPLES = { 60, 20, 40, 30 };
	private static final int[] ID_COM_BURSTS_SIMPLES = { 1, 2, 3, 4, 1, 3, 4, 1 };
	private static final int[] TEMPO_RESPOSTA_COM_BURSTS_SIMPLES = { 0, 20, 40, 60 };
	private static final int[] TEMPO_ESPERA_COM_BURSTS_SIMPLES = { 90, 20, 80, 100 };
	private static final int[] TURN_AROUND_COM_BURSTS_SIMPLES = { 150, 40, 120, 130 };

	@Test
	public void deveRetornarResultadoSimples() {
		final int QUANTUM = 20;
		Escalonador roundRobin = new RoundRobin(gerarArrayListDeProcessos(BURSTS_SIMPLES.length, BURSTS_SIMPLES, null,
				null), QUANTUM);
		roundRobin.executar();
		ArrayList<Processo> resultadoGrafico = roundRobin.resultadoGraficoFinal();
		Assert.assertThat(resultadoGrafico, Matchers.notNullValue());
		Assert.assertThat(resultadoGrafico.size(), Matchers.is(ID_COM_BURSTS_SIMPLES.length));
		for (int i = 0; i < resultadoGrafico.size(); i++) {
			Assert.assertThat(resultadoGrafico.get(i).getId(), Matchers.equalTo(ID_COM_BURSTS_SIMPLES[i]));
		}
		ArrayList<Processo> resultado = roundRobin.resultadoFinal();
		Assert.assertThat(resultado, Matchers.notNullValue());
		Assert.assertThat(resultado.size(), Matchers.is(BURSTS_SIMPLES.length));
		for (int i = 0; i < resultado.size(); i++) {
			Assert.assertThat(resultado.get(i).getEspera(), Matchers.equalTo(TEMPO_ESPERA_COM_BURSTS_SIMPLES[i]));
			Assert.assertThat(resultado.get(i).getResposta(), Matchers.equalTo(TEMPO_RESPOSTA_COM_BURSTS_SIMPLES[i]));
			Assert.assertThat(resultado.get(i).getTurnAround(), Matchers.equalTo(TURN_AROUND_COM_BURSTS_SIMPLES[i]));
		}
		double esperaMedia = roundRobin.tempoEsperaMedia();
		double respostaMedia = roundRobin.tempoRespostaMedia();
		double turnAroundMedio = roundRobin.tempoTurnAroundMedio();
		checarResultadoMedio(turnAroundMedio);
		checarResultadoMedio(respostaMedia);
		checarResultadoMedio(esperaMedia);
		Assert.assertTrue(roundRobin.totalProcessos() == BURSTS_SIMPLES.length);
	}

	private static final int[] BURSTS_MEDIO = { 53, 17, 68, 24 };
	private static final int[] ID_COM_BURSTS_MEDIO = { 1, 2, 3, 4, 1, 3, 4, 1, 3, 3 };
	private static final int[] TEMPO_RESPOSTA_COM_BURSTS_MEDIO = { 0, 20, 37, 57 };
	private static final int[] TEMPO_ESPERA_COM_BURSTS_MEDIO = { 81, 20, 94, 97 };
	private static final int[] TURN_AROUND_COM_BURSTS_MEDIO = { 134, 37, 162, 121 };

	@Test
	public void deveRetornarResultadoMedio() {
		final int QUANTUM = 20;
		Escalonador roundRobin = new RoundRobin(
				gerarArrayListDeProcessos(BURSTS_MEDIO.length, BURSTS_MEDIO, null, null), QUANTUM);
		roundRobin.executar();
		ArrayList<Processo> resultadoGrafico = roundRobin.resultadoGraficoFinal();
		Assert.assertThat(resultadoGrafico, Matchers.notNullValue());
		Assert.assertThat(resultadoGrafico.size(), Matchers.is(ID_COM_BURSTS_MEDIO.length));
		for (int i = 0; i < resultadoGrafico.size(); i++) {
			Assert.assertThat(resultadoGrafico.get(i).getId(), Matchers.equalTo(ID_COM_BURSTS_MEDIO[i]));
		}
		ArrayList<Processo> resultado = roundRobin.resultadoFinal();
		Assert.assertThat(resultado, Matchers.notNullValue());
		Assert.assertThat(resultado.size(), Matchers.is(BURSTS_MEDIO.length));
		for (int i = 0; i < resultado.size(); i++) {
			Assert.assertThat(resultado.get(i).getEspera(), Matchers.equalTo(TEMPO_ESPERA_COM_BURSTS_MEDIO[i]));
			Assert.assertThat(resultado.get(i).getResposta(), Matchers.equalTo(TEMPO_RESPOSTA_COM_BURSTS_MEDIO[i]));
			Assert.assertThat(resultado.get(i).getTurnAround(), Matchers.equalTo(TURN_AROUND_COM_BURSTS_MEDIO[i]));
		}
		double esperaMedia = roundRobin.tempoEsperaMedia();
		double respostaMedia = roundRobin.tempoRespostaMedia();
		double turnAroundMedio = roundRobin.tempoTurnAroundMedio();
		checarResultadoMedio(turnAroundMedio);
		checarResultadoMedio(respostaMedia);
		checarResultadoMedio(esperaMedia);
		Assert.assertTrue(roundRobin.totalProcessos() == BURSTS_MEDIO.length);
	}

	private void checarResultadoMedio(double val) {
		Assert.assertTrue(val > 0.0);
	}

	@Test
	public void deveEscalonarComDoisACemProcesso() {
		for (int i = 2; i <= 100; i++) {
			Escalonador roundRobin = new RoundRobin(gerarListaDeProcessos(i, VALIDO), QUANTUM_VALIDO);
			roundRobin.executar();
			ArrayList<Processo> resultadoGrafico = roundRobin.resultadoGraficoFinal();
			Assert.assertThat(resultadoGrafico, Matchers.notNullValue());
		}
	}

	@Test(expected = TempoQuantumException.class)
	public void naoDeveEscalonarProcessosComTempoQuantumNegativo() {
		new RoundRobin(gerarListaDeProcessos(3, VALIDO), QUANTUM_INVALIDO);
	}

	@Test(expected = ProcessosNaoCarregadosException.class)
	public void naoDeveEscalonarProcessosComBurstNegativo() {
		new RoundRobin(gerarListaDeProcessos(3, INVALIDO), QUANTUM_VALIDO);
	}

	@Test(expected = ProcessosNaoCarregadosException.class)
	public void naoDeveEscalonarSemAntesCarregarOsProcessos() {
		new RoundRobin(null, QUANTUM_VALIDO);
	}
}