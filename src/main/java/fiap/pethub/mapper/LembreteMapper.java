package fiap.pethub.mapper;

import fiap.pethub.dto.request.LembreteRequest;
import fiap.pethub.dto.response.LembreteResponse;
import fiap.pethub.entity.Lembrete;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LembreteMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Lembrete toEntity(LembreteRequest request);

    @Mapping(source = "responsavel.id", target = "responsavelId")
    @Mapping(source = "responsavel.nome", target = "nomeResponsavel")
    @Mapping(source = "pet.id", target = "petId")
    @Mapping(source = "pet.nome", target = "nomePet")
    LembreteResponse toResponse(Lembrete lembrete);

    List<LembreteResponse> toResponseList(List<Lembrete> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(LembreteRequest request, @MappingTarget Lembrete lembrete);
}
