package br.senac.tads.dsw.dadospessoais.seguranca;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

	private final AuthenticationManager authenticationManager;

	public LoginController(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
		try {
			authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
					loginDto.getUsername(), loginDto.getSenha()
				)
			);

			return ResponseEntity.ok(Map.of("token", "token-fixo-provisorio-sem-significado"));

		} catch (BadCredentialsException e) {
			return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(Map.of("erro", "Usuário ou senha inválidos"));
		}
	}
}
