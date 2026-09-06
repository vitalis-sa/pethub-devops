package fiap.pethub.controller;

import fiap.pethub.dto.request.VeterinarioRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.VeterinarioResponse;
import fiap.pethub.service.VeterinarioService;
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
@RequestMapping("/api/veterinarios")
@RequiredArgsConstructor
@Tag(name = "Veterinários", description = "Gerenciamento de veterinários")
public class VeterinariosController {

    private final VeterinarioService service;

    @Operation(summary = "Listar veterinários", description = "Retorna lista paginada, filtrável por nome ou ativo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Parâmetros inválidos")
    })
    @GetMapping
    public ResponseEntity<Page<VeterinarioResponse>> findAll(
            @Parameter(description = "Filtrar por nome") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtrar apenas ativos") @RequestParam(required = false) Boolean ativo,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(nome, ativo, pageable));
    }

    @Operation(summary = "Buscar veterinário por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Veterinário encontrado"),
        @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> findById(
            @Parameter(description = "ID do veterinário", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Cadastrar veterinário")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Veterinário criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "CRMV ou email já cadastrado")
    })
    @PostMapping
    public ResponseEntity<VeterinarioResponse> create(@Valid @RequestBody VeterinarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar veterinário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> update(
            @Parameter(description = "ID do veterinário", required = true) @PathVariable Long id,
            @Valid @RequestBody VeterinarioRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Remover veterinário")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Veterinário não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(
            @Parameter(description = "ID do veterinário", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

