package com.futstack.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.futstack.entities.Deposito;
import com.futstack.entities.Movimentacao;
import com.futstack.entities.Produto;

@Path("/produto")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutoResource {
	Deposito deposito = new Deposito();
	List<Produto> p1 = new ArrayList<Produto>();
	
	@Path("/lista")
	@GET
	public List<Produto> buscar(){
		return Produto.listAll();
	}
	
	@Path("/buscaPorId/{id}")
	@GET
	public Optional<Produto> buscarPorId(@PathParam("id") int id){
		Optional<Produto> produto = Produto.findByIdOptional(id);
		if(produto.isEmpty()) {
			throw new NotFoundException("Não encontrei este ID....");
		}
		return produto;
	}

	@Path("/cria")
	@POST
	@Transactional
	public Response adicionar(Produto dto) {
		dto.persist();
		List<Produto> p = new ArrayList<Produto>();
		p.add(dto);
		
		
		
		deposito.setProduto(p);
		//deposito.persist();
		
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setTipo_movimentacao("Adicionado por Nota Fiscal");
		movimentacao.setQuantidade_movimentacao(dto.quantidade_produto);
		movimentacao.setProduto(dto);
		movimentacao.persist();
		
		Response retorno = Response.status(Status.CREATED).build();
		return retorno;
		
		
	}
	
	@Path("/{id_produto}/addDeposito/{id_deposito}")
	@GET
	@Transactional
	public Response adicionarDeposito(@PathParam("id_produto") int id_produto, @PathParam("id_deposito") int id_deposito) {
		
		Optional<Produto> produto = Produto.findByIdOptional(id_produto);
		Produto p = produto.get();
		
		Optional<Deposito> deposito = Produto.findByIdOptional(id_deposito);
		Deposito d = deposito.get();
		
		
		
		p1.add(p);
		
		d.addProdutoDeposito(p);
		
		d.persist();
		
		
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setTipo_movimentacao("Transferindo produto: ("+ p.nome_produto + ") para o Deposito: (" + d.nome_deposito + ")");
		movimentacao.setQuantidade_movimentacao(p.quantidade_produto);
		movimentacao.setProduto(p);
		movimentacao.persist();
		
		Response retorno = Response.status(Status.CREATED).build();
		return retorno;
		
		
	}
	
	@PUT
	@Path("/altera/{id}")
	@Transactional
	public String alterar(@PathParam("id") int id, Produto dto) {
		Optional<Produto> produtoOp = Produto.findByIdOptional(id);
		if (produtoOp.isEmpty()) {
			throw new NotFoundException();
		}
		Produto produto = produtoOp.get();
		
		
		produto.nome_produto = dto.nome_produto;
		produto.preco = dto.preco;
		produto.pontoReposicao_produto = dto.pontoReposicao_produto;
		
		produto.persist();
		
		List<Produto> p = new ArrayList<Produto>();
		p.add(dto);
		Deposito deposito = new Deposito();
		deposito.setProduto(p);
		deposito.persist();
		
		Movimentacao movimentacao = new Movimentacao();
		movimentacao.setTipo_movimentacao("Alterando dados do produto...");
		movimentacao.setProduto(dto);
		movimentacao.persist();
		
		return "Alteração feita com sucesso!";
	}
	
	@DELETE
	@Path("/exclui/{id}")
	@Transactional
	public String remover(@PathParam("id") int id) {
		Optional<Produto> restaurantOp = Produto.findByIdOptional(id);
		restaurantOp.ifPresentOrElse(Produto::delete, () -> {throw new NotFoundException();});
		
		
		return "Removido com sucesso!";
		
		
	}
}
