package br.com.empowerlearn.empowerlearn_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CepService {

    private static final String API_URL = "https://viacep.com.br/ws/";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode buscarEnderecoPorCep(String cep) throws IOException, InterruptedException {
        // Remove caracteres não numéricos do CEP
        cep = cep.replaceAll("[^0-9]", "");

        if (cep.length() != 8) {
            return null; // CEP inválido
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + cep + "/json/"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonNode rootNode = objectMapper.readTree(response.body());
            if (rootNode.has("erro") && rootNode.get("erro").asBoolean()) {
                return null; // CEP não encontrado
            }
            return rootNode;
        }
        return null;
    }
}