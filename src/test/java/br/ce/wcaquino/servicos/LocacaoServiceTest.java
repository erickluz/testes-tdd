package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Date;

import org.junit.Assert;
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

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testeLocacao() throws Exception {
		// cenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 1, 5.0);

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
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		// acao
		service.alugarFilme(usuario, filme);
	}

	@Test // Forma mais robusta de se realizar um teste(BEM ESPECIFICO, TRATANDO TODOS OS
			// ERROS ESPECIFICAMENTE)
	public void testeFilmeSemEstoque2() {
		// scenario
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		// action
		try {
			service.alugarFilme(usuario, filme);
			Assert.fail("Nao poderia funcionar"); // Nao pode funcionar com valor incorreto
		} catch (Exception e) {
			assertThat(e.getMessage(), is("Filme sem estoque")); // Tem que dar erro
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
		LocacaoService service = new LocacaoService();
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);

		exception.expect(Exception.class);
		exception.expectMessage("Filme sem estoque");

		// acao
		service.alugarFilme(usuario, filme);
	}

	@Test
	public void testeLocacaoUsuarioVazio() {
		// scenario
		LocacaoService service = new LocacaoService();
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
		LocacaoService service = new LocacaoService();
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