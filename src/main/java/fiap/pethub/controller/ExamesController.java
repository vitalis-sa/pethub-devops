package fiap.pethub.controller;

import fiap.pethub.dto.request.ExameRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.ExameResponse;
import fiap.pethub.service.ExameService;
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
@RequestMapping("/api/exames")
@RequiredArgsConstructor
@Tag(name = "Exames", description = "Gerenciamento de exames clínicos")
public class ExamesController {

    private final ExameService service;

    @Operation(summary = "Listar exames", description = "Filtrável por petId ou consultaId")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<ExameResponse>> findAll(
            @Parameter(description = "Filtrar por pet") @RequestParam(required = false) Long petId,
            @Parameter(description = "Filtrar por consulta") @RequestParam(required = false) Long consultaId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(petId, consultaId, pageable));
    }

    @Operation(summary = "Buscar exame por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Exame encontrado"),
        @ApiResponse(responseCode = "404", description = "Exame não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExameResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Registrar exame")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Exame registrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Consulta ou pet não encontrado")
    })
    @PostMapping
    public ResponseEntity<ExameResponse> create(@Valid @RequestBody ExameRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar exame")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Exame não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ExameResponse> update(@PathVariable Long id, @Valid @RequestBody ExameRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Remover exame")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Exame não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

