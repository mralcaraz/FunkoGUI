package org.agrosoft.funkogui.client;

import lombok.extern.slf4j.Slf4j;
import org.agrosoft.funkogui.gui.utils.ConfigManager;

@Slf4j
public class ApiClient {
    private final String BASE_URL;
    private final RestClient restClient;

    public ApiClient(RestClient restClient) {
        this.restClient = restClient;
        this.BASE_URL = ConfigManager.get("api.base.url");
    }

    public boolean shutdownBackend() {
        log.info("Requesting shutdown through api");
        return restClient.postVoidCall(BASE_URL.replace("funkos", "shutdown"), "");
    }
}
