package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Date;
import java.util.List;

import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;

public class LocacaoService {
	
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
				
		Locacao locacao = new Locacao();
		locacao.setFilmes(filmes);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());

		filmes.forEach((filme) -> {
			valor += filme.getPrecoLocacao();
		});
		
		locacao.setValor(valor);
		
		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		//TODO adicionar método para salvar
		
		return locacao;
	}
}