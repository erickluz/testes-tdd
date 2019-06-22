package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

public class LocacaoServiceTest {

	private LocacaoService service;
	private static Integer i = 0;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() {
		System.out.println("Before, contador:" + (i++));
		service = new LocacaoService();
	}
	
	@After
	public void tearDown() {
		System.out.println("After");
	}
	
	@BeforeClass
	public static void setupClass() {
		System.out.println("Before Class");
	}
	
	@AfterClass
	public static void tearDownClass() {
		System.out.println("After Class");
	}
	
	@Test
	public void testeLocacao() throws Exception {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 1, 5.0);

		System.out.println("Teste!");
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filme);

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

	}

	@Test(expected = FilmeSemEstoqueException.class) // Forma mais elegante e mais especifica
	public void testeFilmeSemEstoque() throws Exception {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		// acao
		service.alugarFilme(usuario, filme);
	}

	@Test // Forma mais robusta de se realizar um teste(BEM ESPECIFICO, TRATANDO TODOS OS
			// ERROS ESPECIFICAMENTE)
	public void testeFilmeSemEstoque2() {
		// scenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		// action
		try {
			service.alugarFilme(usuario, filme);
			Assert.fail("Nao poderia funcionar"); // Nao pode funcionar com valor incorreto
		} catch (FilmeSemEstoqueException e) {
			// Tem q dar erro de filme sem estoque
		} catch (LocadoraException e) {
			Assert.fail("Nao deveria dar erro de Locadora"); // Nao pode funcionar com valor incorreto
		}

		filme = new Filme("Filme 1", 1, 5.0);

		// action
		try {
			service.alugarFilme(usuario, filme);
		} catch (Exception e) {
			Assert.fail("Valor correto e mesmo assim deu erro"); // Nao pode funcionar com valor correto
		}
	}

	@Test // Uma forma diferente de se testar
	public void testeFilmeSemEstoqu3() throws Exception {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		exception.expect(FilmeSemEstoqueException.class);

		// acao
		service.alugarFilme(usuario, filme);
	}

	@Test
	public void testeLocacaoUsuarioVazio() {
		// scenario
		Filme filme = new Filme("Filme 1", 1, 5.0);

		try {
			service.alugarFilme(null, filme);
			Assert.fail("Nao era pra ter dado certo");
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario nao pode ser nulo"));
		} catch (FilmeSemEstoqueException e) {
			e.printStackTrace();
			Assert.fail("Nao deveria dar erro de filme");
		}
		
		Usuario usuario = new Usuario("Usuario 1");
		try {
			service.alugarFilme(usuario, filme);
		} catch (LocadoraException e) {
			Assert.fail("Nao era pra ter dado errado");
		} catch (FilmeSemEstoqueException e) {
			e.printStackTrace();
			Assert.fail("Nao deveria dar erro de filme");
		}
		
	}
	
	@Test
	public void testeLocacaoFilmeVazio() {
		Usuario usuario = new Usuario("Usuario 1");
		
		try {
			service.alugarFilme(usuario, null);
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Filme nao pode ser nulo"));
		} catch (FilmeSemEstoqueException e) {
			Assert.fail("Nao era pra dar erro de filme sem estoque");
		}
		
		Filme filme = new Filme("Filme 1", 1, 5.0);
		try {
			service.alugarFilme(usuario, filme);
		} catch (LocadoraException e) {
			Assert.fail("Nao era pra ter dado erro");
		} catch (FilmeSemEstoqueException e) {
			Assert.fail("Nao era pra dar erro de filme sem estoque");
		}
		
	}

}