package fiap.pethub.mapper;

import fiap.pethub.dto.request.LeituraWearableRequest;
import fiap.pethub.dto.response.LeituraWearableResponse;
import fiap.pethub.entity.LeituraWearable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeituraWearableMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "consumoDiarioAcumulado", ignore = true)
    @Mapping(target = "percentualMeta", ignore = true)
    @Mapping(target = "alertaGerado", ignore = true)
    @Mapping(target = "tipoAlerta", ignore = true)
    @Mapping(target = "descricaoAlerta", ignore = true)
    LeituraWearable toEntity(LeituraWearableRequest request);

    @Mapping(source = "pet.nome", target = "nomePet")
    LeituraWearableResponse toResponse(LeituraWearable leitura);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "consumoDiarioAcumulado", ignore = true)
    @Mapping(target = "percentualMeta", ignore = true)
    @Mapping(target = "alertaGerado", ignore = true)
    @Mapping(target = "tipoAlerta", ignore = true)
    @Mapping(target = "descricaoAlerta", ignore = true)
    void updateEntity(LeituraWearableRequest request, @MappingTarget LeituraWearable entity);

    List<LeituraWearableResponse> toResponseList(List<LeituraWearable> list);
}
