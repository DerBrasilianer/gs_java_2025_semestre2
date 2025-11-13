package com.fiap.gestaoltakn.controller.message;

import com.fiap.gestaoltakn.service.message.CacheSyncProducer;
import com.fiap.gestaoltakn.service.message.RelatorioProducer;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/teste-mensageiria")
public class TesteMensageiriaController {

    private final CacheSyncProducer cacheSyncProducer;
    private final RelatorioProducer relatorioProducer;

    public TesteMensageiriaController(CacheSyncProducer cacheSyncProducer, RelatorioProducer relatorioProducer) {
        this.cacheSyncProducer = cacheSyncProducer;
        this.relatorioProducer = relatorioProducer;
    }

    @GetMapping
    public String paginaTeste(Model model) {
        return "teste-mensageiria";
    }

    @PostMapping("/cache-sync")
    public String testarCacheSync(RedirectAttributes redirectAttributes) {
        try {
            cacheSyncProducer.enviarInvalidacaoDepartamento(1L);
            cacheSyncProducer.enviarInvalidacaoFuncionario(1L);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Mensagens de sincronização de cache enviadas com sucesso! Verifique os logs.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erro ao enviar mensagens: " + e.getMessage());
        }
        return "redirect:/teste-mensageiria";
    }

    @PostMapping("/relatorio")
    public String testarRelatorio(Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            String usuario = authentication != null ? authentication.getName() : "usuario-teste";
            relatorioProducer.solicitarRelatorioFuncionariosRisco(usuario);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Relatório solicitado com sucesso! Processamento assíncrono iniciado. Verifique os logs.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Erro ao solicitar relatório: " + e.getMessage());
        }
        return "redirect:/teste-mensageiria";
    }

}
