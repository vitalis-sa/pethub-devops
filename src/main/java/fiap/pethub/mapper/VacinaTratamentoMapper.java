package fiap.pethub.mapper;

import fiap.pethub.dto.request.VacinaTratamentoRequest;
import fiap.pethub.dto.response.VacinaTratamentoResponse;
import fiap.pethub.entity.VacinaTratamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VacinaTratamentoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "consulta", ignore = true)
    VacinaTratamento toEntity(VacinaTratamentoRequest request);

    @Mapping(source = "pet.nome", target = "nomePet")
    @Mapping(source = "veterinario.nome", target = "nomeVeterinario")
    VacinaTratamentoResponse toResponse(VacinaTratamento vacinaTratamento);

    List<VacinaTratamentoResponse> toResponseList(List<VacinaTratamento> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "consulta", ignore = true)
    void updateEntity(VacinaTratamentoRequest request, @MappingTarget VacinaTratamento vacinaTratamento);
}

