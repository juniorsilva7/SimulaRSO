package com.appspot.simularso.logic.process;

import java.util.ArrayList;
import java.util.List;

import com.appspot.simularso.logic.EscalonadorProcesso;
import com.appspot.simularso.model.Processo;
import com.appspot.simularso.model.ProcessoDTO;
import com.appspot.simularso.model.ProcessoVO;

public class SRT extends EscalonadorProcessoBase implements EscalonadorProcesso {

	private boolean executando;
	private int index;
	private int tempoDeCorte;

	public SRT(ArrayList<Processo> processos, int tempoQuantum, int tempoContexto) {
		super(processos, tempoQuantum = 0, tempoContexto);
		utilizarBurstOrder(true);
		ordernarProcessos();
		executar();
	}

	private void executar() {
		iniciar();
		while (estiverEmExecucao()) {
			if (totalDeProcessos() > 0) {
				executarProcesso();
			} else {
				finalizar();
			}
		}
	}

	private void executarProcesso() {
		definirTempoDeCorte();
		Processo processo = clonarProcesso(index);
		processo.executar();
		processo.setResposta(atualizarTempoResposta(processo));
		processo.setBurstAtual(atualizarBurstAtual(processo));
		processo.setBurstTotal(atualizarBurstTotal(processo));
		adicionarResultadoGrafico(processo);
		if (totalDeProcessos() > 1) {
			atualizarTempoTotal(processo.getBurstAtual() + tempoContexto());
			processo.setTurnAround(tempoTotal() - tempoContexto());
			processo.setEspera(atualizarTempoEspera(processo) - tempoContexto());
		} else {
			atualizarTempoTotal(processo.getBurstAtual());
			processo.setTurnAround(tempoTotal());
			processo.setEspera(atualizarTempoEspera(processo));
		}
		atualizarProcessos(processo);
	}

	private void definirTempoDeCorte() {
		Processo processoAtual = clonarProcesso(index);
		Processo processoFuturo = clonarProcesso(index + 1);
		int burstAtual = processoAtual.getBurstTotal();
		atualizarTempoDeCorte(burstAtual);
		if (processoFuturo != null) {
			int burstFuturo = processoFuturo.getBurstTotal();
			if (burstFuturo < burstAtual) {
				int tempoChegadaAtual = processoAtual.getChegada();
				int tempoChegadaFuturo = processoFuturo.getChegada();
				int diferenca = Math.abs(tempoChegadaFuturo - tempoChegadaAtual);
				if (tempoDeCorte > diferenca) {
					atualizarTempoDeCorte(diferenca);
				}
			}
		}
	}

	private void atualizarProcessos(Processo processo) {
		if (processo.terminou()) {
			processo.finalizar();
			adicionarResultadoFinal(processo);
		} else {
			processo.esperar();
			atualizarProcesso(processo);
		}
		removerProcesso(index);
		atualizarIndex();
	}

	private int atualizarBurstAtual(Processo processo) {
		int burstTotal = processo.getBurstTotal();
		return (burstTotal < tempoDeCorte) ? burstTotal : tempoDeCorte;
	}

	private int atualizarBurstTotal(Processo processo) {
		int novoBurst = processo.getBurstTotal() - tempoDeCorte;
		return (novoBurst < 0) ? 0 : novoBurst;
	}

	private int atualizarTempoEspera(Processo processo) {
		int espera = processo.getTurnAround() - processo.getBurst();
		return (espera < 0) ? 0 : espera;
	}

	private int atualizarTempoResposta(Processo processo) {
		boolean firstRun = processo.isFirstRun();
		int tempoResposta = processo.getResposta();
		return firstRun ? tempoTotal() : tempoResposta;
	}

	private void iniciar() {
		this.executando = true;
		this.index = 0;
	}

	private void finalizar() {
		this.executando = false;
	}

	private void atualizarIndex() {
		index = (index >= totalDeProcessos()) ? (0) : (index);
	}

	private void atualizarTempoDeCorte(int val) {
		tempoDeCorte = val;
	}

	private boolean estiverEmExecucao() {
		return this.executando;
	}

	@Override
	public int tempoExecucaoTotal() {
		return tempoTotal();
	}

	@Override
	public List<ProcessoVO> resultadoFinal() {
		return resultado();
	}

	@Override
	public List<ProcessoDTO> resultadoGraficoFinal() {
		return resultadoGrafico();
	}

	@Override
	public double tempoEsperaMedia() {
		return calcularEsperaMedia();
	}

	@Override
	public double tempoRespostaMedia() {
		return calcularRespostaMedia();
	}

	@Override
	public double tempoTurnAroundMedio() {
		return calcularTurnAroundMedio();
	}

	@Override
	public int totalProcessos() {
		return totalProcessosExecutados();
	}

	@Override
	public String algoritmoNome() {
		return EscalonadorProcessoAlgoritmo.SRT.getNome();
	}
}
