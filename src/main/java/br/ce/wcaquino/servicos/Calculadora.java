package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NumeroDivididoPorZeroException;

public class Calculadora {

	public int somaDoisNumeros(int a, int b) {
		return a + b;
	}

	public int subtraiNumeros(int a, int b) {
		return a-b;
	}

	public double dividirNumeros(double a, double b) throws NumeroDivididoPorZeroException {
		if (b == 0) {
			throw new NumeroDivididoPorZeroException("O Numero pode existir divisao por zero");
		}
		return a / b;
	}
	
}
