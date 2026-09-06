package fiap.pethub.controller;

import fiap.pethub.dto.request.ConsultaRequest;
import fiap.pethub.dto.response.ConsultaResponse;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.enums.StatusConsulta;
import fiap.pethub.service.ConsultaService;
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
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas", description = "Gerenciamento de consultas veterinárias")
public class ConsultasController {

    private final ConsultaService service;

    @Operation(summary = "Listar consultas", description = "Filtrável por petId, veterinarioId e status")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<ConsultaResponse>> findAll(
            @Parameter(description = "Filtrar por pet") @RequestParam(required = false) Long petId,
            @Parameter(description = "Filtrar por veterinário") @RequestParam(required = false) Long veterinarioId,
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) StatusConsulta status,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(petId, veterinarioId, status, pageable));
    }

    @Operation(summary = "Buscar consulta por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta encontrada"),
        @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Criar consulta", description = "Cria a consulta e notifica o responsável via API C#")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Consulta criada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Pet, veterinário ou unidade não encontrado")
    })
    @PostMapping
    public ResponseEntity<ConsultaResponse> create(@Valid @RequestBody ConsultaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar consulta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponse> update(@PathVariable Long id, @Valid @RequestBody ConsultaRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Remover consulta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

