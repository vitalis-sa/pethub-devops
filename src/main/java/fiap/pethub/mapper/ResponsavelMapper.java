package fiap.pethub.mapper;

import fiap.pethub.dto.request.ResponsavelContatoRequest;
import fiap.pethub.dto.request.ResponsavelEnderecoRequest;
import fiap.pethub.dto.request.ResponsavelRequest;
import fiap.pethub.dto.response.ResponsavelContatoResponse;
import fiap.pethub.dto.response.ResponsavelEnderecoResponse;
import fiap.pethub.dto.response.ResponsavelResponse;
import fiap.pethub.entity.Responsavel;
import fiap.pethub.entity.ResponsavelContato;
import fiap.pethub.entity.ResponsavelEndereco;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ResponsavelMapper {

    ResponsavelResponse toResponse(Responsavel responsavel);
    ResponsavelEnderecoResponse toEnderecoResponse(ResponsavelEndereco endereco);
    ResponsavelContatoResponse toContatoResponse(ResponsavelContato contato);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "enderecos", ignore = true)
    @Mapping(target = "contatos", ignore = true)
    Responsavel toEntity(ResponsavelRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "enderecos", ignore = true)
    @Mapping(target = "contatos", ignore = true)
    void updateEntity(ResponsavelRequest request, @MappingTarget Responsavel responsavel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    ResponsavelContato contatoToEntity(ResponsavelContatoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    void updateContato(ResponsavelContatoRequest request, @MappingTarget ResponsavelContato contato);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    ResponsavelEndereco enderecoToEntity(ResponsavelEnderecoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "responsavel", ignore = true)
    void updateEndereco(ResponsavelEnderecoRequest request, @MappingTarget ResponsavelEndereco endereco);
}

