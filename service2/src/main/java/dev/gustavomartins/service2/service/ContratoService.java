package dev.gustavomartins.service2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ContratoService {

    @Autowired
    private RestTemplate restTemplate;

    public Map<String, Object> buscarContrato(Long id) {

        Map<String, Object> contrato = new HashMap<>();
        contrato.put("id", id);
        contrato.put("descricao", "Contrato " + id);
        contrato.put("valorMensal", 150.0 * id);
        contrato.put("clienteId", id);

        String url = "http://cliente-service:8081/clientes/" + id;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            contrato.put("cliente", response.getBody());

        } catch (HttpClientErrorException.NotFound e) {
            contrato.put("cliente", null);
            contrato.put("erroCliente", "Cliente não encontrado (404)");

        } catch (HttpServerErrorException e) {
            contrato.put("cliente", null);
            contrato.put("erroCliente", "Erro interno no serviço de clientes (" + e.getStatusCode().value() + ")");

        } catch (ResourceAccessException e) {
            contrato.put("cliente", null);
            contrato.put("erroCliente", "Serviço de clientes indisponível: " + e.getMostSpecificCause().getMessage());
        }

        return contrato;
    }
}
