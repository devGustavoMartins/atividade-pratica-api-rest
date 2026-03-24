package dev.gustavomartins.service2.controller;

import dev.gustavomartins.service2.service.ContratoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/contratos")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getContrato(@PathVariable Long id) {
        Map<String, Object> contrato = contratoService.buscarContrato(id);
        return ResponseEntity.ok(contrato);
    }
}
