package br.ce.wcaquino.servicos;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDAO;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {
	
	private LocacaoDAO dao;
	
	private SPCService spcService;
	
	private Double valor = 0.0;
	
	public Locacao alugarFilme(Usuario usuario, List<Filme> filmes)throws LocadoraException, FilmeSemEstoqueException {
		
		if (usuario == null) {
			throw new LocadoraException("Usuario nao pode ser nulo");
		}
		
		if(filmes == null || filmes.isEmpty()) {
			throw new LocadoraException("Filme nao pode ser nulo");
		}
		
		for(int i = 0; i <filmes.size();i++) {
			if (filmes.get(i).getEstoque() == 0) {
				throw new FilmeSemEstoqueException();
			}
		}
		
		if(spcService.possuiNegativacao(usuario)) {
			throw new LocadoraException("Usuario Negativado!");
		}

		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());

		
		
		double valorFilme = 0;
		for (int i=0; i<filmes.size();i++) {
			valorFilme = filmes.get(i).getPrecoLocacao();
			
			if (i == 2) {
				valorFilme = valorFilme * 0.75;
			}else if(i == 3) {
				valorFilme = valorFilme * 0.50;
			}else if(i == 4) {
				valorFilme = valorFilme * 0.25;
			} else if(i == 5) {
				valorFilme = 0; 
			}
			
			valor += valorFilme;
		}
		
		locacao.setValor(valor);
		
		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = DataUtils.adicionarDias(dataEntrega, 1);
		if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
			dataEntrega = DataUtils.adicionarDias(dataEntrega, 1);
		}
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		dao.salvar(locacao);
		
		return locacao;
	}
	
	
//	injecao de dependencia
	public void setLocacaoDAO(LocacaoDAO dao) {
		this.dao = dao;
	}
	
	public void setSpcService(SPCService spcService) {
		this.spcService = spcService;
	}
	
}