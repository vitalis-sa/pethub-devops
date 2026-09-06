package fiap.pethub.controller;

import fiap.pethub.dto.request.LoginRequest;
import fiap.pethub.dto.request.ResponsavelRequest;
import fiap.pethub.dto.request.VeterinarioRequest;
import fiap.pethub.dto.response.LoginResponse;
import fiap.pethub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login e cadastro de veterinários e responsáveis")
public class AuthController {

    private final AuthService service;

    @SecurityRequirements
    @Operation(summary = "Autenticar",
            description = "Devolve o token JWT a ser enviado no cabeçalho Authorization: Bearer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticado"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas ou cadastro inativo")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.autenticar(request));
    }

    @SecurityRequirements
    @Operation(summary = "Cadastrar responsável",
            description = "Cria o tutor e já devolve o token, dispensando um login em seguida")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ou CPF já cadastrado")
    })
    @PostMapping("/registrar/responsavel")
    public ResponseEntity<LoginResponse> registrarResponsavel(
            @Valid @RequestBody ResponsavelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarResponsavel(request));
    }

    @SecurityRequirements
    @Operation(summary = "Cadastrar veterinário",
            description = "Cria o veterinário e já devolve o token, dispensando um login em seguida")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cadastrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Email ou CRMV já cadastrado")
    })
    @PostMapping("/registrar/veterinario")
    public ResponseEntity<LoginResponse> registrarVeterinario(
            @Valid @RequestBody VeterinarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrarVeterinario(request));
    }
}
