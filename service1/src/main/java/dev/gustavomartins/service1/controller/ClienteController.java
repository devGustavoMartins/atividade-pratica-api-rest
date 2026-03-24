package dev.gustavomartins.service1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @GetMapping("/{id}")
    public ResponseEntity<?> getCliente(@PathVariable Long id) throws InterruptedException {

        switch (id.intValue()) {
            case 2:
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cliente não encontrado");
            case 3:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Erro ao processar a solicitação");
            case 4:
                Thread.sleep(5000);
                break;
            default:
                break;
        }

        Map<String, Object> cliente = new HashMap<>();
        cliente.put("id", id);
        cliente.put("nome", "Cliente " + id);
        cliente.put("cpf", "000.000.000-0" + id);
        cliente.put("email", "cliente" + id + "@email.com");
        return ResponseEntity.ok(cliente);
    }
}
