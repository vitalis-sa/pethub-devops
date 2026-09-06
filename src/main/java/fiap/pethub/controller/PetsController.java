package fiap.pethub.controller;

import fiap.pethub.dto.request.PetRequest;
import fiap.pethub.dto.response.DeleteResponse;
import fiap.pethub.dto.response.PetResponse;
import fiap.pethub.service.PetService;
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
@RequestMapping("/api/pets")
@RequiredArgsConstructor
@Tag(name = "Pets", description = "Gerenciamento de pets")
public class PetsController {

    private final PetService service;

    @Operation(summary = "Listar pets", description = "Retorna lista paginada, filtrável por nome ou veterinário responsável")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public ResponseEntity<Page<PetResponse>> findAll(
            @Parameter(description = "Filtrar por nome") @RequestParam(required = false) String nome,
            @Parameter(description = "Filtrar por veterinário responsável") @RequestParam(required = false) Long veterinarioId,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findAll(nome, veterinarioId, pageable));
    }

    @Operation(summary = "Listar pets por CPF do responsável")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Responsável não encontrado")
    })
    @GetMapping("/responsavel")
    public ResponseEntity<Page<PetResponse>> findByResponsavelCpf(
            @Parameter(description = "CPF do responsável", required = true) @RequestParam String cpf,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.findByResponsavelCpf(cpf, pageable));
    }

    @Operation(summary = "Buscar pet por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pet encontrado"),
        @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Cadastrar pet", description = "Busca o responsável pelo CPF e vincula ao pet")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pet criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Responsável ou veterinário não encontrado")
    })
    @PostMapping
    public ResponseEntity<PetResponse> create(@Valid @RequestBody PetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @Operation(summary = "Atualizar pet")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> update(@PathVariable Long id, @Valid @RequestBody PetRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @Operation(summary = "Remover pet")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pet não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {
        return ResponseEntity.ok(service.delete(id));
    }
}

