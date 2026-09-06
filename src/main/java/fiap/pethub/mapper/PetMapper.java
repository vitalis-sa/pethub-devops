package fiap.pethub.mapper;

import fiap.pethub.dto.request.PetRequest;
import fiap.pethub.dto.response.PetResponse;
import fiap.pethub.entity.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "veterinarioResponsavel", ignore = true)
    Pet toEntity(PetRequest request);

    @Mapping(source = "responsavel.nome", target = "nomeResponsavel")
    @Mapping(source = "veterinarioResponsavel.nome", target = "nomeVeterinarioResponsavel")
    PetResponse toResponse(Pet pet);

    List<PetResponse> toResponseList(List<Pet> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "veterinarioResponsavel", ignore = true)
    void updateEntity(PetRequest request, @MappingTarget Pet pet);
}
