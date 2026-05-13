package org.agrosoft.funkogui.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.agrosoft.funkogui.gui.utils.ConfigManager;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class RestClient {
    private final OkHttpClient client;
    private final ObjectMapper mapper;
    private final int maxRetries;

    public RestClient() {
        this.client = new OkHttpClient();
        this.mapper = new ObjectMapper();
        this.maxRetries = ConfigManager.getInt("max.retries", 3);
    }

    public <T> T getCall(String url, Map<String, String> queryParams, TypeReference<T> type) {
        log.info("GET calling [{}]", url);
        int attempt = 0;
        T result = null;
        while (attempt < this.maxRetries && Objects.isNull(result)) {
            attempt++;
            try {
                HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
                if(queryParams != null) {
                    queryParams.forEach(urlBuilder::addQueryParameter);
                }
                Request request = new Request.Builder()
                        .url(urlBuilder.build())
                        .get()
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if(!response.isSuccessful()) {
                        log.error("Exception caught while calling Get. Error: {}, Message: {}",
                                response.code(), response.message());
                        throw new RuntimeException("Http error: " + response.code());
                    } else if(response.code() == 204 || Objects.isNull(response.body())) {
                        log.info("Response is OK(204) but no content is fetched from this call");
                        break;
                    } else if (response.code() == 400) {
                        log.info("Response is BAD REQUEST: {}", response.message());
                        break;
                    }
                    String body = response.body().string();
                    result = this.mapper.readValue(body, type);
                }
            } catch (Exception e) {
                log.warn("Failed <{}> attempt to reach GET[{}]", attempt, url, e);
                if(attempt < this.maxRetries) {
                    try { Thread.sleep(2500L * attempt ); } catch (InterruptedException ignore) {}
                }
            }
        }
        return result;
    }

    public byte[] getFile(String url) {
        log.info("GET (file) calling [{}]", url);
        int attempt = 0;
        byte[] result = null;

        while(attempt < this.maxRetries && Objects.isNull(result)) {
            attempt++;
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()){
                    if(!response.isSuccessful()) {
                        log.error("Exception caught while calling Get(File). Error: {}, Message: {}",
                                response.code(), response.message());
                        throw new RuntimeException("HTTP Error: " + response.code());
                    } else if (response.code() == 204) {
                        log.info("Response is OK(204) but no content was fetched from this call");
                        break;
                    } else if (response.code() == 400) {
                        log.error("Response is BAD REQUEST: {}", response.message());
                        break;
                    }
                    if(Objects.nonNull(response.body())) {
                        InputStream inputStream = response.body().byteStream();
                        result = inputStream.readAllBytes();
                    }
                }
            } catch (Exception e) {
                log.warn("Failed <{}> attempt to reach GET(File)[{}]", attempt, url, e);
                if (attempt < this.maxRetries) {
                    try { Thread.sleep(2500L * attempt); } catch (InterruptedException ignore) {}
                }
            }
        }
        return result;
    }

    public <T> T postCall(String url, Object bodyObj, TypeReference<T> type) {
        int attempt = 0;
        T result = null;
        log.info("POST calling [{}]", url);
        while (attempt < this.maxRetries && Objects.isNull(result)) {
            attempt++;
            try {
                String jsonBody = this.mapper.writeValueAsString(bodyObj);
                RequestBody requestBody = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                try(Response response = client.newCall(request).execute()){
                    if(response.isSuccessful() || Objects.nonNull(response.body())) {
                        log.info("Successful call POST[{}]", url);
                        String body = response.body().string();
                        //log.info(body);
                        result = this.mapper.readValue(body, type);
                    } else if (response.code() == 400) {
                        log.error("Response for POST is BAD REQUEST: {}", response.message());
                        break;
                    } else {
                        log.warn("Server error. Code {}, Message: {}", response.code(), response.message());
                    }
                }
            } catch(Exception e) {
                log.warn("Failed <{}> attempt to reach POST[{}]", attempt, url, e);
                if(attempt < this.maxRetries) {
                    try { Thread.sleep(2500L * attempt ); } catch (InterruptedException ignore) {}
                }
            }
        }
        return result;
    }

    public <T> T patchCall(String url, Object bodyObj, TypeReference<T> type) {
        int attempt = 0;
        T result = null;
        log.info("PATCH calling [{}]", url);
        while (attempt < this.maxRetries && Objects.isNull(result)) {
            attempt++;
            try {
                String jsonBody = this.mapper.writeValueAsString(bodyObj);
                RequestBody requestBody = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
                Request request = new Request.Builder()
                        .url(url)
                        .method("PATCH", requestBody)
                        .build();
                try(Response response = client.newCall(request).execute()){
                    if(response.isSuccessful() || Objects.nonNull(response.body())) {
                        log.info("Successful call PATCH[{}]", url);
                        String body = response.body().string();
                        //log.info(body);
                        result = this.mapper.readValue(body, type);
                    } else if (response.code() == 400) {
                        log.error("Response for PATCH is BAD REQUEST: {}", response.message());
                        break;
                    } else {
                        log.warn("Server error. Code {}, Message: {}", response.code(), response.message());
                    }
                }
            } catch(Exception e) {
                log.warn("Failed <{}> attempt to reach PATCH[{}]", attempt, url, e);
                if(attempt < this.maxRetries) {
                    try { Thread.sleep(2500L * attempt ); } catch (InterruptedException ignore) {}
                }
            }
        }
        return result;
    }

    public boolean postVoidCall(String url, Object bodyObj) {
        int attempt = 0;
        log.info("POST (void) calling [{}]", url);
        boolean result = false;

        while (attempt < this.maxRetries && !result) {
            attempt++;
            try {
                String jsonBody = this.mapper.writeValueAsString(bodyObj);
                RequestBody requestBody = RequestBody.create(
                        jsonBody,
                        MediaType.get("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() || response.code() == 204) {
                        log.info("Successful POST (void) [{}] - code {}", url, response.code());
                        result = true;
                    } else if (response.code() == 400) {
                        log.error("Bad request POST [{}]: {}", url, response.message());
                    } else {
                        log.warn("Server error POST [{}]: {} {}", url, response.code(), response.message());
                    }
                }

            } catch (Exception e) {
                log.warn("Failed <{}> attempt POST (void) [{}]", attempt, url, e);
                if (attempt < this.maxRetries) {
                    try {
                        Thread.sleep(2500L * attempt);
                    } catch (InterruptedException ignore) {}
                }
            }
        }

        return result;
    }
}
