package fiap.pethub.mapper;

import fiap.pethub.dto.request.ConsultaRequest;
import fiap.pethub.dto.response.ConsultaResponse;
import fiap.pethub.entity.Consulta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConsultaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "unidade", ignore = true)
    Consulta toEntity(ConsultaRequest request);

    @Mapping(source = "pet.nome", target = "nomePet")
    @Mapping(source = "veterinario.nome", target = "nomeVeterinario")
    @Mapping(source = "unidade.nome", target = "nomeUnidade")
    ConsultaResponse toResponse(Consulta consulta);

    List<ConsultaResponse> toResponseList(List<Consulta> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "veterinario", ignore = true)
    @Mapping(target = "unidade", ignore = true)
    void updateEntity(ConsultaRequest request, @MappingTarget Consulta consulta);
}

