package br.com.desafios.controledeorcamentofamiliar.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.desafios.controledeorcamentofamiliar.dto.ReceitaDto;
import br.com.desafios.controledeorcamentofamiliar.modelo.Receita;
import br.com.desafios.controledeorcamentofamiliar.repository.ReceitaRepository;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/receita")
public class ReceitaController {

	@Autowired
	private ReceitaRepository receitaRepository;
	private Receita receita = new Receita();
	private ReceitaDto receitaDto = new ReceitaDto();

	/**
	 * Para criar minha URI, o meu UriComponentsBuilder usa o caminho do parametro
	 * do metodo completo. O metodo retorna status 201 caso seja criado sem
	 * problemas. Se o objeto ja estiver no banco de dados, ele nao aceita a cração
	 * 
	 * @param receitaDto
	 * @param uriBuilder
	 * @return
	 */
	@PostMapping
	@Transactional
	public ResponseEntity<ReceitaDto> cadastro(@RequestBody ReceitaDto receitaDto, UriComponentsBuilder uriBuilder) {
		List<ReceitaDto> lista = listagemDeReceita();
		if (!lista.contains(receitaDto)) {
			Receita receita = receitaDto.converteEmReceita(receitaDto);
			receitaRepository.save(receita);

			URI uri = uriBuilder.path("/receita/{id}").buildAndExpand(receita.getId()).toUri();
			return ResponseEntity.created(uri).body(new ReceitaDto(receita));
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping
	public List<ReceitaDto> listagemDeReceita() {
		List<Receita> receitas = receitaRepository.findAll();
		return ReceitaDto.converte(receitas);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ReceitaDto> detalhe(@PathVariable("id") Long id) {
		Optional<Receita> receita = receitaRepository.findById(id);
		return ResponseEntity.ok(new ReceitaDto(receita.get()));
	}

	@PutMapping("/{id}")
	@Transactional
	public ResponseEntity<ReceitaDto> atualizar(@PathVariable Long id, @RequestBody ReceitaDto receitaDto) {
		Receita receita = receitaRepository.findById(id).orElseThrow();

		receita.setDescricao(receitaDto.getDescricao());
		receita.setValor(receitaDto.getValor());
		receita.setData(receitaDto.getData());

		ReceitaDto receitaDtoAtualizada = new ReceitaDto(receitaRepository.save(receita));
		return ResponseEntity.ok(receitaDtoAtualizada);
	}

	@DeleteMapping("/{id}")
	public void deletar(@PathVariable Long id) {
		receita = receitaRepository.findById(id).orElseThrow();
		receitaRepository.delete(receita);

	}

}
