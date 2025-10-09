package org.agrosoft.funkogui.client;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.agrosoft.funkogui.gui.utils.ConfigManager;
import org.agrosoft.funkogui.model.FunkoDto;
import org.agrosoft.funkogui.model.StringResponseDto;

import java.util.List;
import java.util.Map;

@Slf4j
public class FunkoClient {
    private final String BASE_URL;
    private final RestClient restClient;

    public FunkoClient(RestClient restClient) {
        this.restClient = restClient;
        this.BASE_URL = ConfigManager.get("api.base.url");
    }

    public List<FunkoDto> getAll() {
        log.info("Get all funkos method started");
        return restClient.getCall(
                BASE_URL + "/todos",
                Map.of("presentDto", "true"),
                new TypeReference<List<FunkoDto>>(){}
        );
    }

    public boolean saveFunko(FunkoDto toSave) {
        log.info("Save Funko method started");
        return restClient.postCall(
                BASE_URL + "/guardarDto",
                toSave,
                new TypeReference<StringResponseDto>(){}
        ).getMensaje().matches(".*\\d$");
    }

    public boolean updateFunko(FunkoDto toSave) {
        log.info("Update Funko method started");
        return restClient.patchCall(
                BASE_URL + "/editarFunko",
                toSave,
                new TypeReference<StringResponseDto>(){}
        ).getMensaje().matches(".*\\d$");
    }

    public byte[] exportToExcel() {
        log.info("Exporting Excel from Backend");
        return restClient.getFile(BASE_URL + "/exportarAExcel");
    }
}
