package br.senac.tads.dsw.dadospessoais;

import java.util.List;
import java.util.Optional;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/pessoas")
public class PessoaController {

	private final PessoaService pessoaService;

	public PessoaController(PessoaService pessoaService) {
		this.pessoaService = pessoaService;
	}

	@GetMapping
	public List<PessoaDto> obterPessoas() {
		return pessoaService.obterPessoas();
	}

	@PostMapping("/sem-validacao")
	public ResponseEntity<?> incluirNovo(@RequestBody PessoaDto pessoaDto) {
		pessoaService.incluirNovaPessoa(pessoaDto);

		URI location = ServletUriComponentsBuilder
			.fromCurrentContextPath()
			.path("/pessoas/{username}")
			.buildAndExpand(pessoaDto.getUsername())
			.toUri();

		return ResponseEntity.created(location).build();
	}

	@PostMapping
	public ResponseEntity<?> incluirNovoComValidacao(@RequestBody @Valid PessoaDto pessoaDto) {

		pessoaService.incluirNovaPessoa(pessoaDto);
		URI location = ServletUriComponentsBuilder
			.fromCurrentContextPath()
			.path("/pessoas/{username}")
			.buildAndExpand(pessoaDto.getUsername())
			.toUri();

		return ResponseEntity.created(location).build();
	}

	@GetMapping("/{username}")
	public PessoaDto obterPessoa(@PathVariable("username") String username) {
		Optional<PessoaDto> optPessoa = pessoaService.obterPessoa(username);

		if (optPessoa.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		return optPessoa.get();
	}

	@GetMapping("/busca")
	public ResponseEntity<List<PessoaDto>> buscarPessoas(@RequestParam("q") String termo) {
		return ResponseEntity.ok(pessoaService.buscarPessoas(termo));
	}

	@PutMapping("/{username}")
	public ResponseEntity<?> atualizar(@PathVariable("username") String username,
									   @RequestBody @Valid PessoaAlteracaoDto pessoa) {

		PessoaDto pessoaDtoAlterada = pessoaService.alterarPessoa(username, pessoa);
		return ResponseEntity.ok().body(pessoaDtoAlterada);
	}

	@DeleteMapping("/{username}")
	public ResponseEntity<?> remover(@PathVariable("username") String username) {
		pessoaService.removerPessoa(username);
		return ResponseEntity.noContent().build(); // HTTP 204
	}

	@ExceptionHandler(NaoEncontradoException.class)
	public ResponseEntity<ProblemDetail> tratarExcecao(NaoEncontradoException ex) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(
			HttpStatus.valueOf(404), ex.getMessage());

		return ResponseEntity.of(pd).build();
	}
}
