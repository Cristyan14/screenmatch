package br.estudos.services;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
public class ConsumoApi {
	public String obterDados(String endereco) {
	    HttpClient client = HttpClient.newHttpClient();
	    HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(endereco))
	            .build();
	    HttpResponse<String> response = null;
	    try {
	        response = client
	                .send(request, HttpResponse.BodyHandlers.ofString());
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    } catch (InterruptedException e) {
	        throw new RuntimeException(e);
	    }

	    String json = response.body();
	    return json;
	}
}
