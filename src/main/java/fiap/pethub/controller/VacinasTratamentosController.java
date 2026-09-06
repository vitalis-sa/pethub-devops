package fiap.pethub.controller;

import fiap.pethub.dto.request.VacinaTratamentoRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.VacinaTratamentoResponse;
import fiap.pethub.enums.TipoVacinaTratamento;
import fiap.pethub.service.VacinaTratamentoService;
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
@RequestMapping("/api/vacinas-tratamentos")
@RequiredArgsConstructor
@Tag(name = "Vacinas e Tratamentos", description = "Gerenciamento de vacinas, medicamentos e procedimentos")
public class VacinasTratamentosController {

    private final VacinaTratamentoService service;

    @Operation(summary = "Listar vacinas/tratamentos", description = "Filtrável por petId e tipo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<VacinaTratamentoResponse>> findAll(
            @Parameter(description = "Filtrar por pet") @RequestParam(required = false) Long petId,
            @Parameter(description = "Filtrar por tipo") @RequestParam(required = false) TipoVacinaTratamento tipo,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(petId, tipo, pageable));
    }

    @Operation(summary = "Buscar vacina/tratamento por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Encontrado"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<VacinaTratamentoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Registrar vacina/tratamento", description = "Se proximaDose for preenchida, notifica o responsável via API C#")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Pet, veterinário ou consulta não encontrado")
    })
    @PostMapping
    public ResponseEntity<VacinaTratamentoResponse> create(@Valid @RequestBody VacinaTratamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar vacina/tratamento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<VacinaTratamentoResponse> update(@PathVariable Long id, @Valid @RequestBody VacinaTratamentoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Remover vacina/tratamento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

