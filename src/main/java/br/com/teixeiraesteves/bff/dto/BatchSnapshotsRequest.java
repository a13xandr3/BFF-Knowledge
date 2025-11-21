package br.com.teixeiraesteves.bff.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Requisição para snapshots em lote")
public record BatchSnapshotsRequest(
        @NotEmpty
        @ArraySchema(arraySchema = @Schema(description = "IDs de arquivos a consultar"))
        List<Long> ids,

        @Schema(description = "Se true, inclui base64 no retorno", defaultValue = "false")
        Boolean includeBase64
) {}
