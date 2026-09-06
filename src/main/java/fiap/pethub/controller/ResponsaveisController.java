package fiap.pethub.controller;

import fiap.pethub.dto.request.ResponsavelContatoRequest;
import fiap.pethub.dto.request.ResponsavelEnderecoRequest;
import fiap.pethub.dto.request.ResponsavelRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.ResponsavelContatoResponse;
import fiap.pethub.dto.response.ResponsavelEnderecoResponse;
import fiap.pethub.dto.response.ResponsavelResponse;
import fiap.pethub.service.ResponsavelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@RequestMapping("/api/responsaveis")
@RequiredArgsConstructor
@Tag(name = "Responsáveis", description = "Gerenciamento de responsáveis, contatos e endereços")
public class ResponsaveisController {

    private final ResponsavelService service;

    // ─── Responsavel ──────────────────────────────────────────────────────────

    @Operation(summary = "Listar responsáveis", description = "Filtrável por nome e status ativo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<ResponsavelResponse>> findAll(
            @Parameter(description = "Filtrar por nome (parcial)") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtrar por status ativo") @RequestParam(required = false) Boolean ativo,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(nome, ativo, pageable));
    }

    @Operation(summary = "Buscar responsável por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Responsável encontrado"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponsavelResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Buscar responsável por CPF")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Responsável encontrado"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @GetMapping("/buscar")
    public ResponseEntity<ResponsavelResponse> findByCpf(
            @Parameter(description = "CPF do responsável", required = true)
            @RequestParam @NotBlank String cpf) {
        return ResponseEntity.ok(service.findByCpf(cpf));
    }

    @Operation(summary = "Cadastrar responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    public ResponseEntity<ResponsavelResponse> create(@Valid @RequestBody ResponsavelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ResponsavelResponse> update(@PathVariable Long id, @Valid @RequestBody ResponsavelRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Remover responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }

    // ─── Contatos ─────────────────────────────────────────────────────────────

    @Operation(summary = "Listar contatos do responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contatos retornados"),
        @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    @GetMapping("/{responsavelId}/contatos")
    public ResponseEntity<Page<ResponsavelContatoResponse>> findContatos(
            @PathVariable Long responsavelId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findContatos(responsavelId, pageable));
    }

    @Operation(summary = "Buscar contato por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contato encontrado"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @GetMapping("/{responsavelId}/contatos/{contatoId}")
    public ResponseEntity<ResponsavelContatoResponse> findContatoById(
            @PathVariable Long responsavelId, @PathVariable Long contatoId) {
        return ResponseEntity.ok(service.findContatoById(responsavelId, contatoId));
    }

    @Operation(summary = "Adicionar contato ao responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Contato adicionado"),
        @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    @PostMapping("/{responsavelId}/contatos")
    public ResponseEntity<ResponsavelContatoResponse> addContato(
            @PathVariable Long responsavelId, @Valid @RequestBody ResponsavelContatoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addContato(responsavelId, request));
    }

    @Operation(summary = "Atualizar contato do responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @PutMapping("/{responsavelId}/contatos/{contatoId}")
    public ResponseEntity<ResponsavelContatoResponse> updateContato(
            @PathVariable Long responsavelId, @PathVariable Long contatoId,
            @Valid @RequestBody ResponsavelContatoRequest request) {
        return ResponseEntity.ok(service.updateContato(responsavelId, contatoId, request));
    }

    @Operation(summary = "Remover contato do responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @DeleteMapping("/{responsavelId}/contatos/{contatoId}")
    public ResponseEntity<DeleteResponse> deleteContato(
            @PathVariable Long responsavelId, @PathVariable Long contatoId) {
        return ResponseEntity.ok(service.deleteContato(responsavelId, contatoId));
    }

    // ─── Endereços ────────────────────────────────────────────────────────────

    @Operation(summary = "Listar endereços do responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Endereços retornados"),
        @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    @GetMapping("/{responsavelId}/enderecos")
    public ResponseEntity<Page<ResponsavelEnderecoResponse>> findEnderecos(
            @PathVariable Long responsavelId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findEnderecos(responsavelId, pageable));
    }

    @Operation(summary = "Buscar endereço por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Endereço encontrado"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @GetMapping("/{responsavelId}/enderecos/{enderecoId}")
    public ResponseEntity<ResponsavelEnderecoResponse> findEnderecoById(
            @PathVariable Long responsavelId, @PathVariable Long enderecoId) {
        return ResponseEntity.ok(service.findEnderecoById(responsavelId, enderecoId));
    }

    @Operation(summary = "Adicionar endereço ao responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Endereço adicionado"),
        @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    @PostMapping("/{responsavelId}/enderecos")
    public ResponseEntity<ResponsavelEnderecoResponse> addEndereco(
            @PathVariable Long responsavelId, @Valid @RequestBody ResponsavelEnderecoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addEndereco(responsavelId, request));
    }

    @Operation(summary = "Atualizar endereço do responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @PutMapping("/{responsavelId}/enderecos/{enderecoId}")
    public ResponseEntity<ResponsavelEnderecoResponse> updateEndereco(
            @PathVariable Long responsavelId, @PathVariable Long enderecoId,
            @Valid @RequestBody ResponsavelEnderecoRequest request) {
        return ResponseEntity.ok(service.updateEndereco(responsavelId, enderecoId, request));
    }

    @Operation(summary = "Remover endereço do responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
    })
    @DeleteMapping("/{responsavelId}/enderecos/{enderecoId}")
    public ResponseEntity<DeleteResponse> deleteEndereco(
            @PathVariable Long responsavelId, @PathVariable Long enderecoId) {
        return ResponseEntity.ok(service.deleteEndereco(responsavelId, enderecoId));
    }
}

