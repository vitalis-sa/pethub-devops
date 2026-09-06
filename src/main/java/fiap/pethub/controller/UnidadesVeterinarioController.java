package fiap.pethub.controller;

import fiap.pethub.dto.request.UnidadeVeterinarioRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.UnidadeVeterinarioResponse;
import fiap.pethub.service.UnidadeVeterinarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/unidades")
@RequiredArgsConstructor
@Tag(name = "Unidades Veterinárias", description = "Gerenciamento de clínicas e estabelecimentos")
public class UnidadesVeterinarioController {

    private final UnidadeVeterinarioService service;

    @Operation(summary = "Listar unidades", description = "Retorna lista paginada, filtrável por nome ou cidade")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<UnidadeVeterinarioResponse>> findAll(
            @Parameter(description = "Filtrar por nome") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtrar por cidade") @RequestParam(required = false) String cidade,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(nome, cidade, pageable));
    }

    @Operation(summary = "Buscar unidade por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unidade encontrada"),
        @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UnidadeVeterinarioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Cadastrar unidade veterinária")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Unidade criada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    @PostMapping
    public ResponseEntity<UnidadeVeterinarioResponse> create(@Valid @RequestBody UnidadeVeterinarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar unidade veterinária")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UnidadeVeterinarioResponse> update(
            @PathVariable Long id, @Valid @RequestBody UnidadeVeterinarioRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Remover unidade veterinária")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Unidade não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

