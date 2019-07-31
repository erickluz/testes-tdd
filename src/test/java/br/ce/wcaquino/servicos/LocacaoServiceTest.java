package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.matchers.MatchersProprios.caiNumaSegunda;
import static br.ce.wcaquino.utils.DataUtils.isMesmaData;
import static br.ce.wcaquino.utils.DataUtils.obterDataComDiferencaDias;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.ce.wcaquino.builders.UsuarioBuilder;
import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoServiceTest {
	
	@InjectMocks
	private LocacaoService service;
	
	@Mock
	private SPCService spcService;
	
	@Mock
	private LocacaoDAO dao;
	
	@Mock
	private EmailService emailService;

	@Rule
	public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
////		System.out.println("Before, contador:" + (i++));
//		service = new LocacaoService();		
//		dao = Mockito.mock(LocacaoDAO.class); 
//		service.setLocacaoDAO(dao);
//		spcService = Mockito.mock(SPCService.class);
//		service.setSpcService(spcService);
//		emailService = Mockito.mock(EmailService.class);
//		service.setEmailService(emailService);
	}

	@After
	public void tearDown() {
//		System.out.println("After");
	}

	@BeforeClass
	public static void setupClass() {
//		System.out.println("Before Class");
	}

	@AfterClass
	public static void tearDownClass() {
//		System.out.println("After Class");
	}

	@Test
	public void deveAlugarFilmeComSucesso() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		// cenario
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Filme filme = new Filme("Filme 1", 1, 5.0);
		Filme filme2 = new Filme("Filme 1", 1, 5.0);

		// acao
		Locacao locacao = service.alugarFilme(usuario, Arrays.asList(filme, filme2));

		// verificacao
		error.checkThat(locacao.getValor(), is(equalTo(10.0)));
		error.checkThat(isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
		error.checkThat(isMesmaData(locacao.getDataRetorno(), obterDataComDiferencaDias(1)), is(true));

	}

	@Test(expected = FilmeSemEstoqueException.class) // Forma mais elegante e mais especifica
	public void naoDeveAlugarFilmeSemEstoque() throws Exception {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 1, 5.0);
		Filme filme2 = new Filme("Filme 1", 0, 5.0);
		// acao
		service.alugarFilme(usuario, Arrays.asList(filme, filme2));
	}

	@Test // Forma mais robusta de se realizar um teste(BEM ESPECIFICO, TRATANDO TODOS OS
			// ERROS ESPECIFICOS)
	public void naoDeveAlugarFilmeSemEstoque2() {
		// scenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		Filme filme2 = new Filme("Filme 1", 2, 5.0);

		// action
		try {
			service.alugarFilme(usuario, Arrays.asList(filme, filme2));
			Assert.fail("Nao poderia funcionar"); // Nao pode funcionar com valor incorreto
		} catch (FilmeSemEstoqueException e) {
			// Tem q dar erro de filme sem estoque
		} catch (LocadoraException e) {
			Assert.fail("Nao deveria dar erro de Locadora"); // Nao pode funcionar com valor incorreto
		}

		filme = new Filme("Filme 1", 1, 5.0);

		// action
		try {
			service.alugarFilme(usuario, Arrays.asList(filme, filme2));
		} catch (Exception e) {
			Assert.fail("Valor correto e mesmo assim deu erro"); // Nao pode funcionar com valor correto
		}
	}

	@Test // Uma forma diferente de se testar
	public void naoDeveAlugarFilmeSemEstoque3() throws Exception {
		// cenario
		Usuario usuario = new Usuario("Usuario 1");
		Filme filme = new Filme("Filme 1", 0, 5.0);
		Filme filme2 = new Filme("Filme 1", 0, 5.0);

		exception.expect(FilmeSemEstoqueException.class);

		// acao
		service.alugarFilme(usuario, Arrays.asList(filme, filme2));
	}

	@Test
	public void naoDeveAlugarFilmeSemUsuario() {
		// scenario
		Filme filme = new Filme("Filme 1", 1, 5.0);
		Filme filme2 = new Filme("Filme 1", 1, 5.0);

		try {
			service.alugarFilme(null, Arrays.asList(filme, filme2));
			Assert.fail("Nao era pra ter dado certo");
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Usuario nao pode ser nulo"));
		} catch (FilmeSemEstoqueException e) {
			e.printStackTrace();
			Assert.fail("Nao deveria dar erro de filme");
		}

		Usuario usuario = new Usuario("Usuario 1");
		try {
			service.alugarFilme(usuario, Arrays.asList(filme, filme2));
		} catch (LocadoraException e) {
			Assert.fail("Nao era pra ter dado errado");
		} catch (FilmeSemEstoqueException e) {
			e.printStackTrace();
			Assert.fail("Nao deveria dar erro de filme");
		}

	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() {
		Usuario usuario = new Usuario("Usuario 1");

		try {
			service.alugarFilme(usuario, null);
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), is("Filme nao pode ser nulo"));
		} catch (FilmeSemEstoqueException e) {
			Assert.fail("Nao era pra dar erro de filme sem estoque");
		}

		Filme filme = new Filme("Filme 1", 1, 5.0);
		Filme filme2 = new Filme("Filme 1", 1, 5.0);
		try {
			service.alugarFilme(usuario, Arrays.asList(filme, filme2));
		} catch (LocadoraException e) {
			Assert.fail("Nao era pra ter dado erro");
		} catch (FilmeSemEstoqueException e) {
			Assert.fail("Nao era pra dar erro de filme sem estoque");
		}

	}

//	@Test
//	public void devePagar75PctNoFilme() throws LocadoraException, FilmeSemEstoqueException {
//		// scenario
//		Usuario usuario = new Usuario("usuario 1");
//		List<Filme> filmes = Arrays.asList(new Filme("filme1", 2, 4.0), new Filme("filme2", 2, 4.0),
//				new Filme("filme3", 2, 4.0));
//
//		// action
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//
//		// verificacao
//		// 4+4+3=11
//		assertThat(resultado.getValor(), is(11.0));
//	}
//
//	@Test
//	public void devePagar50PctNoFilme() throws LocadoraException, FilmeSemEstoqueException {
//		// scenario
//		Usuario usuario = new Usuario("usuario 1");
//		List<Filme> filmes = Arrays.asList(new Filme("filme1", 2, 4.0), new Filme("filme2", 2, 4.0),
//				new Filme("filme3", 2, 4.0), new Filme("filme4", 2, 4.0));
//
//		// action
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//
//		// verificacao
//		// 4+4+3+2=14
//		assertThat(resultado.getValor(), is(13.0));
//
//	}
//
//	@Test
//	public void devePagar25PctNoFilme() throws LocadoraException, FilmeSemEstoqueException {
//		// scenario
//		Usuario usuario = new Usuario("usuario 1");
//		List<Filme> filmes = Arrays.asList(new Filme("filme1", 2, 4.0), new Filme("filme2", 2, 4.0),
//				new Filme("filme3", 2, 4.0), new Filme("filme4", 2, 4.0), new Filme("filme5", 2, 4.0));
//
//		// action
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//
//		// verificacao
//		// 4+4+3+2+1=14
//		assertThat(resultado.getValor(), is(14.0));
//
//	}
//
//	@Test
//	public void devePagar0PctNoFilme() throws LocadoraException, FilmeSemEstoqueException {
//		// scenario
//		Usuario usuario = new Usuario("usuario 1");
//		List<Filme> filmes = Arrays.asList(new Filme("filme1", 2, 4.0), new Filme("filme2", 2, 4.0),
//				new Filme("filme3", 2, 4.0), new Filme("filme4", 2, 4.0), new Filme("filme5", 2, 4.0),
//				new Filme("filme6", 2, 4.0));
//
//		// action
//		Locacao resultado = service.alugarFilme(usuario, filmes);
//
//		// verificacao
//		// 4+4+3+2+1+0=14
//		assertThat(resultado.getValor(), is(14.0));
//
//	}

	@Test
	public void deveDevolverNaSegundaCasoAlugarNoSabado() throws LocadoraException, FilmeSemEstoqueException {
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		// scenario
		Usuario usuario = new Usuario("usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("filme1", 2, 4.0));
		
		// acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
		

//		boolean ehSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.SATURDAY);
		assertThat(locacao.getDataRetorno(), caiNumaSegunda());
		
	}

	
	@Test
	public void naoDeveAlugarFilmeParaNegativadosSPC() throws FilmeSemEstoqueException {
		Usuario usuario = UsuarioBuilder.umUsuario().agora();
		Usuario usuario2 = UsuarioBuilder.umUsuario().agora();
		usuario2.setNome("User 2");
		
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 2, 4.0));
		
		Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);
		
		try {
			service.alugarFilme(usuario, filmes);
			Assert.fail("Nao deveria estar positivado!");
		}catch (LocadoraException e) {
			assertEquals(e.getMessage(), "Usuario Negativado!");
		}
		
		Mockito.verify(spcService).possuiNegativacao(usuario);
		
	}
	
	@Test
	public void deveEnviarEmailParaLocacaoAtrasadas() {
		//cenario
		Usuario usuario1 = new Usuario("usuario 1");
		Usuario usuario2 = new Usuario("usuario 2");
		Locacao locacao1 = new Locacao();
		Locacao locacao2 = new Locacao();
		locacao1.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
		locacao1.setUsuario(usuario1);
		locacao2.setDataRetorno(DataUtils.obterDataComDiferencaDias(-2));
		locacao2.setUsuario(usuario2);
		
		List<Locacao> locacoes = Arrays.asList(locacao1, locacao2);
		
		Mockito.when(dao.obtertLocacoesPendentes()).thenReturn(locacoes);
		
		//acao
		service.notificarAtrasos();
		
		//verificacao
		Mockito.verify(emailService).notificaAtraso(usuario1);
		Mockito.verify(emailService).notificaAtraso(usuario2);
		
	}
	
}
