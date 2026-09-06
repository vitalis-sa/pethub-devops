package fiap.pethub.mapper;

import fiap.pethub.dto.request.PedidoMedicoRequest;
import fiap.pethub.dto.response.PedidoMedicoResponse;
import fiap.pethub.entity.PedidoMedico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMedicoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "consulta", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PedidoMedico toEntity(PedidoMedicoRequest request);

    @Mapping(source = "pet.nome", target = "nomePet")
    PedidoMedicoResponse toResponse(PedidoMedico pedidoMedico);

    List<PedidoMedicoResponse> toResponseList(List<PedidoMedico> list);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "consulta", ignore = true)
    @Mapping(target = "pet", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(PedidoMedicoRequest request, @MappingTarget PedidoMedico pedidoMedico);
}

