package appspot.simulaedp.test;

import java.util.ArrayList;

import appspot.simulaedp.model.Processo;

public abstract class InitialCase {

	protected static final boolean VALIDO = true;
	protected static final boolean INVALIDO = false;

	protected ArrayList<Processo> gerarArrayListDeProcessos(int total, int[] burst, int[] chegada, int[] prioridade) {
		ArrayList<Processo> processos = new ArrayList<Processo>();
		for (int i = 0; i < total; i++) {
			processos.add(gerarProcessoValido((i + 1), ((burst != null) ? burst[i] : 100),
					((chegada != null) ? chegada[i] : 0), ((prioridade != null) ? prioridade[i] : 0)));
		}
		return processos;
	}

	protected ArrayList<Processo> gerarListaDeProcessos(int total, boolean valido) {
		ArrayList<Processo> processos = new ArrayList<Processo>();
		for (int i = 0; i < total; i++) {
			Processo processo = (valido ? gerarProcessoValido(i + 1, 100, 0, 0) : gerarProcessoInvalido(i + 1));
			processos.add(processo);
		}
		return processos;
	}

	protected Processo gerarProcessoValido(int i, int burst, int chegada, int prioridade) {
		Processo processo = new Processo();
		processo.setId(i);
		processo.setBurst(burst);
		processo.setChegada(chegada);
		processo.setPrioridade(prioridade);
		return processo;
	}

	protected Processo gerarProcessoInvalido(int i) {
		Processo processo = new Processo();
		processo.setId(i);
		processo.setBurst(-20);
		processo.setChegada(-20);
		processo.setPrioridade(-20);
		return processo;
	}
}