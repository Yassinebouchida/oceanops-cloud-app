package com.oceanopscloud.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanopscloud.model.ShipRequest;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

import java.util.HashMap;
import java.util.List;

@Service
public class AiService {

    private final OpenAIClient client;

    public AiService(@Value("${openai.api.key}") String apiKey) {
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public HashMap<String, Object> analyzeRequest(ShipRequest req) {
        try {
            String itemsSummary = req.getItems() == null ? "No items"
                    : req.getItems().stream()
                    .map(i -> i.getItemName() + " x" + i.getQuantity() + " (" + i.getUnit() + ")")
                    .toList()
                    .toString();

            String prompt = """
                    You are an experienced shipchandler and maritime procurement manager.

                    Analyze this ship supply request before approval.
                    Current Date: %s
                    Ship Name: %s
                    Port: %s
                    Urgency: %s
                    ETA: %s
                    Requested Delivery Date: %s
                    Items: %s
                    Notes: %s

                    Evaluate:
                    1. If the request should be approved, rejected, or reviewed manually.
                    2. Whether quantities look realistic.
                    3. Missing information needed from the captain/client.
                    4. Delivery risks based on ETA, port, and urgency.
                    5. Extra items the shipchandler may suggest.

                    Return ONLY valid JSON. Do not use markdown.

                    {
                      "decision": "APPROVE",
                      "confidence": 90,
                      "summary": "string",
                      "risks": ["string"],
                      "missing_information": ["string"],
                      "recommended_items": ["string"],
                      "urgency_rating": "LOW",
                      "notes": "string"
                    }
                    """.formatted(
                    LocalDate.now(),
                    req.getShipName(),
                    req.getPort(),
                    req.getUrgencyLevel(),
                    req.getEta(),
                    req.getRequestedDeliveryDate(),
                    itemsSummary,
                    req.getNotes()
            );

            ResponseCreateParams params = ResponseCreateParams.builder()
                    .model(ChatModel.GPT_4O_MINI)
                    .input(prompt)
                    .build();

            Response response = client.responses().create(params);

            String json = response.output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .flatMap(content -> content.outputText().stream())
                    .map(outputText -> outputText.text())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No AI text returned"))
                    .trim();

            json = json.replace("```json", "")
                    .replace("```", "")
                    .trim();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, HashMap.class);

        } catch (Exception e) {
            e.printStackTrace();

            HashMap<String, Object> fallback = new HashMap<>();
            fallback.put("decision", "MANUAL_REVIEW");
            fallback.put("confidence", 60);
            fallback.put("summary", "The request requires manual verification before approval.");
            fallback.put("risks", List.of(
                    "External AI service unavailable",
                    "Manual stock and delivery verification required"
            ));
            fallback.put("missing_information", List.of(
                    "AI verification unavailable"
            ));
            fallback.put("recommended_items", List.of(
                    "Confirm quantities",
                    "Verify ETA",
                    "Check stock availability"
            ));
            fallback.put("urgency_rating", req.getUrgencyLevel() != null ? req.getUrgencyLevel().toString() : "MEDIUM");
            fallback.put("notes", "Fallback analysis generated because OpenAI could not be reached.");

            return fallback;
        }
    }
}