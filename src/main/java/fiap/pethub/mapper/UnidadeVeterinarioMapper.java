package fiap.pethub.mapper;

import fiap.pethub.dto.request.UnidadeVeterinarioRequest;
import fiap.pethub.dto.response.UnidadeVeterinarioResponse;
import fiap.pethub.entity.UnidadeVeterinario;
import fiap.pethub.entity.Veterinario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnidadeVeterinarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "veterinarios", ignore = true)
    UnidadeVeterinario toEntity(UnidadeVeterinarioRequest request);

    @Mapping(source = "veterinarios", target = "nomesVeterinarios", qualifiedByName = "toNomes")
    UnidadeVeterinarioResponse toResponse(UnidadeVeterinario unidade);

    List<UnidadeVeterinarioResponse> toResponseList(List<UnidadeVeterinario> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "veterinarios", ignore = true)
    void updateEntity(UnidadeVeterinarioRequest request, @MappingTarget UnidadeVeterinario unidade);

    @Named("toNomes")
    default List<String> toNomes(List<Veterinario> veterinarios) {
        if (veterinarios == null) return List.of();
        return veterinarios.stream().map(Veterinario::getNome).toList();
    }
}
