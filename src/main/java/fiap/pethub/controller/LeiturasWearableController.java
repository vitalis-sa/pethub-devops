package fiap.pethub.controller;

import fiap.pethub.dto.request.LeituraWearableRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.LeituraWearableResponse;
import fiap.pethub.service.LeituraWearableService;
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
@RequestMapping("/api/leituras-wearable")
@RequiredArgsConstructor
@Tag(name = "Leituras Wearable", description = "Monitoramento de consumo de água de felinos — gera métricas e alertas de saúde automaticamente")
public class LeiturasWearableController {

    private final LeituraWearableService service;

    @Operation(
        summary = "Listar leituras de hidratação",
        description = "Retorna leituras paginadas. Filtrável por petId e apenasAlertas (leituras que geraram alerta de saúde)"
    )
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<LeituraWearableResponse>> findAll(
            @Parameter(description = "Filtrar por pet") @RequestParam(required = false) Long petId,
            @Parameter(description = "Retornar apenas leituras que geraram alertas de saúde") @RequestParam(required = false) Boolean apenasAlertas,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(petId, apenasAlertas, pageable));
    }

    @Operation(summary = "Buscar leitura por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Leitura encontrada"),
        @ApiResponse(responseCode = "404", description = "Leitura não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LeituraWearableResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(
        summary = "Registrar leitura de hidratação",
        description = "Recebe dados do dispositivo wearable. Calcula automaticamente o consumo diário acumulado, " +
                      "percentual da meta e gera alertas (DESIDRATACAO_CRITICA, BAIXO_CONSUMO, META_ATINGIDA, CONSUMO_EXCESSIVO)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Leitura registrada com métricas calculadas"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @PostMapping
    public ResponseEntity<LeituraWearableResponse> create(@Valid @RequestBody LeituraWearableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar leitura de hidratação", description = "Recalcula métricas e alertas após a atualização")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Leitura não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LeituraWearableResponse> update(@PathVariable Long id, @Valid @RequestBody LeituraWearableRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Remover leitura de hidratação")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Leitura não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}
