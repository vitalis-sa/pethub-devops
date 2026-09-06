package fiap.pethub.controller;

import fiap.pethub.dto.request.DiagnosticoRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.DiagnosticoResponse;
import fiap.pethub.service.DiagnosticoService;
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
@RequestMapping("/api/diagnosticos")
@RequiredArgsConstructor
@Tag(name = "Diagnósticos", description = "Gerenciamento de diagnósticos clínicos (gerados pelo veterinário durante a consulta)")
public class DiagnosticosController {

    private final DiagnosticoService service;

    @Operation(summary = "Listar diagnósticos", description = "Filtrável por petId ou consultaId")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<DiagnosticoResponse>> findAll(
            @Parameter(description = "Filtrar por pet") @RequestParam(required = false) Long petId,
            @Parameter(description = "Filtrar por consulta") @RequestParam(required = false) Long consultaId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(petId, consultaId, pageable));
    }

    @Operation(summary = "Buscar diagnóstico por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Diagnóstico encontrado"),
        @ApiResponse(responseCode = "404", description = "Diagnóstico não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DiagnosticoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Registrar diagnóstico", description = "Diagnóstico deve estar vinculado a uma consulta. Campos de ML/GenAI são preenchidos pelo assistente de IA durante a consulta.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Diagnóstico registrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Consulta ou pet não encontrado")
    })
    @PostMapping
    public ResponseEntity<DiagnosticoResponse> create(@Valid @RequestBody DiagnosticoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar diagnóstico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Diagnóstico não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DiagnosticoResponse> update(@PathVariable Long id, @Valid @RequestBody DiagnosticoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Remover diagnóstico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Diagnóstico não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

