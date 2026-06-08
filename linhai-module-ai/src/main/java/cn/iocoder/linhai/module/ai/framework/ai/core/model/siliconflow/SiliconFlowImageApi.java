/*
 * Copyright 2023-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.iocoder.linhai.module.ai.framework.ai.core.model.siliconflow;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.ai.openai.api.OpenAiImageApi;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * SiliconFlow Image API.
 */
public class SiliconFlowImageApi {

    private final WebClient webClient;

    public SiliconFlowImageApi(String aiToken) {
        this(SiliconFlowApiConstants.DEFAULT_BASE_URL, aiToken);
    }

    public SiliconFlowImageApi(String baseUrl, String openAiToken) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    headers.setBearerAuth(openAiToken);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .build();
    }

    public ResponseEntity<OpenAiImageApi.OpenAiImageResponse> createImage(
            SiliconflowImageRequest siliconflowImageRequest) {
        Assert.notNull(siliconflowImageRequest, "Image request cannot be null.");
        Assert.hasLength(siliconflowImageRequest.prompt(), "Prompt cannot be empty.");
        return this.webClient.post()
                .uri("/v1/images/generations")
                .bodyValue(siliconflowImageRequest)
                .retrieve()
                .toEntity(OpenAiImageApi.OpenAiImageResponse.class)
                .block();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SiliconflowImageRequest(
            @JsonProperty("prompt") String prompt,
            @JsonProperty("model") String model,
            @JsonProperty("batch_size") Integer batchSize,
            @JsonProperty("negative_prompt") String negativePrompt,
            @JsonProperty("seed") Integer seed,
            @JsonProperty("num_inference_steps") Integer numInferenceSteps,
            @JsonProperty("guidance_scale") Float guidanceScale,
            @JsonProperty("image") String image) {

        public SiliconflowImageRequest(String prompt, String model) {
            this(prompt, model, null, null, null, null, null, null);
        }
    }

}
