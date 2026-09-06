package fiap.pethub.mapper;

import fiap.pethub.dto.request.DiagnosticoRequest;
import fiap.pethub.dto.response.DiagnosticoResponse;
import fiap.pethub.entity.Diagnostico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DiagnosticoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "consulta", ignore = true)
    Diagnostico toEntity(DiagnosticoRequest request);

    @Mapping(source = "pet.nome", target = "nomePet")
    DiagnosticoResponse toResponse(Diagnostico diagnostico);

    List<DiagnosticoResponse> toResponseList(List<Diagnostico> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "consulta", ignore = true)
    void updateEntity(DiagnosticoRequest request, @MappingTarget Diagnostico diagnostico);
}

