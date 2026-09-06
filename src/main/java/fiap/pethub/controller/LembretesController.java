package fiap.pethub.controller;

import fiap.pethub.dto.request.LembreteRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.LembreteResponse;
import fiap.pethub.enums.StatusLembrete;
import fiap.pethub.enums.TipoLembrete;
import fiap.pethub.service.LembreteService;
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
@RequestMapping("/api/lembretes")
@RequiredArgsConstructor
@Tag(name = "Lembretes", description = "Gerenciamento de lembretes do responsável sobre consultas, vacinas, exames e medicamentos")
public class LembretesController {

    private final LembreteService service;

    @Operation(summary = "Listar lembretes", description = "Filtrável por responsavelId, petId, status e tipo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<LembreteResponse>> findAll(
            @Parameter(description = "Filtrar por responsável") @RequestParam(required = false) Long responsavelId,
            @Parameter(description = "Filtrar por pet") @RequestParam(required = false) Long petId,
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) StatusLembrete status,
            @Parameter(description = "Filtrar por tipo") @RequestParam(required = false) TipoLembrete tipo,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(responsavelId, petId, status, tipo, pageable));
    }

    @Operation(summary = "Buscar lembrete por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LembreteResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Criar lembrete")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Responsável ou pet não encontrado")
    })
    @PostMapping
    public ResponseEntity<LembreteResponse> create(@Valid @RequestBody LembreteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar lembrete")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LembreteResponse> update(@PathVariable Long id, @Valid @RequestBody LembreteRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Atualizar status do lembrete", description = "Permite marcar como ENVIADO ou FALHOU")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status atualizado"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<LembreteResponse> updateStatus(
            @PathVariable Long id,
            @Parameter(description = "Novo status") @RequestParam StatusLembrete status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @Operation(summary = "Remover lembrete")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

