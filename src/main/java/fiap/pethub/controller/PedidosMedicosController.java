package fiap.pethub.controller;

import fiap.pethub.dto.request.PedidoMedicoRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.PedidoMedicoResponse;
import fiap.pethub.enums.StatusPedidoMedico;
import fiap.pethub.enums.TipoPedidoMedico;
import fiap.pethub.service.PedidoMedicoService;
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
@RequestMapping("/api/pedidos-medicos")
@RequiredArgsConstructor
@Tag(name = "Pedidos Médicos", description = "Exames solicitados e medicações prescritas pelo veterinário para execução em casa")
public class PedidosMedicosController {

    private final PedidoMedicoService service;

    @Operation(summary = "Listar pedidos médicos", description = "Filtrável por petId, status e tipo. Consumido também pelo app via C#.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<PedidoMedicoResponse>> findAll(
            @Parameter(description = "Filtrar por pet") @RequestParam(required = false) Long petId,
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) StatusPedidoMedico status,
            @Parameter(description = "Filtrar por tipo") @RequestParam(required = false) TipoPedidoMedico tipo,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(petId, status, tipo, pageable));
    }

    @Operation(summary = "Buscar pedido médico por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PedidoMedicoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Criar pedido médico", description = "Notifica o responsável via API C# com lembrete do tipo EXAME ou MEDICAMENTO")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Consulta ou pet não encontrado")
    })
    @PostMapping
    public ResponseEntity<PedidoMedicoResponse> create(@Valid @RequestBody PedidoMedicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar pedido médico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PedidoMedicoResponse> update(@PathVariable Long id, @Valid @RequestBody PedidoMedicoRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Remover pedido médico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

