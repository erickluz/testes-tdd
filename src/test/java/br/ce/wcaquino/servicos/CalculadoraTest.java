package br.ce.wcaquino.servicos;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.ce.wcaquino.exceptions.NumeroDivididoPorZeroException;

// Classe criada para 
public class CalculadoraTest {

	public Calculadora calculadora;
	
	@Before
	public void setup() {
		this.calculadora = new Calculadora();
	}
	
	@Test
	public void deveSomarDoisNumeros() {
		
		int a = 5;
		int b = 6;
		
		Assert.assertEquals(11, calculadora.somaDoisNumeros(a, b));
		
	}
	
	@Test
	public void deveSubtrairNumeros() {
		int a = 21;
		int b = 14;
		
		Assert.assertEquals(7, calculadora.subtraiNumeros(a, b));
		
		a = 14;
		b = 21;
		Assert.assertEquals(-7, calculadora.subtraiNumeros(a, b));
		
	}
	
	@Test
	public void deveDividirNumeros() throws NumeroDivididoPorZeroException {
		double a = 34;
		double b = 11;
	
		Assert.assertEquals(3.09, calculadora.dividirNumeros(a, b), 0.01);
		Assert.assertEquals(0.32, calculadora.dividirNumeros(b, a), 0.01);
		
		b = 0;
		try {
			calculadora.dividirNumeros(a, b);
			Assert.fail("Numero deveria ser dividido por zero");
		}catch (NumeroDivididoPorZeroException e) {
			
		}
		
		
	}
	
}
