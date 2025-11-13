package com.fiap.gestaoltakn.ai.service;

import com.fiap.gestaoltakn.ai.dto.OpenAIRequest;
import com.fiap.gestaoltakn.ai.dto.OpenAIResponse;
import com.fiap.gestaoltakn.entity.FuncionarioEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    private final WebClient openaiWebClient;

    @Value("${openai.api.key:}")
    private String openaiApiKey;

    public AIService(WebClient openaiWebClient) {
        this.openaiWebClient = openaiWebClient;
    }

    public String analisarBemEstarFuncionario(FuncionarioEntity funcionario) {
        logger.info("Iniciando análise de bem-estar para funcionário: {}", funcionario.getNome());

        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            logger.warn("API Key não configurada, usando análise mock");
            return gerarAnaliseMock(funcionario);
        }

        try {
            String prompt = String.format("""
                Analise o bem-estar do funcionário com base nos dados abaixo e forneça recomendações concisas:
                
                Nome: %s
                Departamento: %s
                Horas trabalhadas no último mês: %d
                Limite máximo de horas do departamento: %d
                Status atual: %s
                
                Forneça:
                1. Uma breve análise do nível de risco
                2. 2-3 recomendações específicas para melhorar o bem-estar
                3. Sugestões para o gestor do departamento
                
                Seja objetivo e use no máximo 300 palavras. Responda em português brasileiro.
                """,
                    funcionario.getNome(),
                    funcionario.getDepartamento().getNome(),
                    funcionario.getHorasTrabalhadasUltimoMes(),
                    funcionario.getDepartamento().getNumeroHorasMaximas(),
                    funcionario.getStatus().name()
            );

            logger.info("Prompt criado para funcionário: {}", funcionario.getNome());

            OpenAIRequest request = new OpenAIRequest(
                    "llama-3.1-8b-instant",
                    List.of(new OpenAIRequest.Message("user", prompt)),
                    0.7,
                    null
            );

            logger.info("Enviando requisição para Groq API... Modelo: {}", request.getModel());
            logger.info("Request payload: model={}, temperature={}, max_tokens={}",
                    request.getModel(), request.getTemperature(), request.getMaxTokens());

            OpenAIResponse response = openaiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .doOnSuccess(r -> logger.info("Resposta recebida da API Groq"))
                    .doOnError(e -> logger.error("Erro na chamada da API Groq: {}", e.getMessage()))
                    .block();

            if (response != null &&
                    response.getChoices() != null &&
                    !response.getChoices().isEmpty() &&
                    response.getChoices().get(0).getMessage() != null &&
                    response.getChoices().get(0).getMessage().getContent() != null) {

                String resultado = response.getChoices().get(0).getMessage().getContent();
                logger.info("Resposta da IA recebida com sucesso! Tamanho: {} caracteres", resultado.length());
                logger.debug("Conteúdo da resposta: {}", resultado);
                return resultado;
            } else {
                logger.warn("Resposta vazia ou inválida da API Groq, usando análise mock");
                if (response != null) {
                    logger.warn("Estrutura da resposta: choices={}",
                            response.getChoices() != null ? response.getChoices().size() : "null");
                }
                return gerarAnaliseMock(funcionario);
            }

        } catch (WebClientResponseException e) {
            logger.error("Erro HTTP {} na chamada da API Groq: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "Erro na comunicação com o serviço de IA: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("Erro inesperado ao analisar bem-estar do funcionário via IA: {}", e.getMessage(), e);
            return gerarAnaliseMock(funcionario);
        }
    }

    public String gerarRecomendacoesDepartamento(String nomeDepartamento, Integer horasMaximas,
                                                 Long totalFuncionarios, Long funcionariosEmRisco) {
        logger.info("Gerando recomendações para departamento: {}", nomeDepartamento);

        if (openaiApiKey == null || openaiApiKey.isEmpty()) {
            logger.warn("API Key não configurada, usando recomendações mock");
            return gerarRecomendacoesMock(nomeDepartamento, horasMaximas, totalFuncionarios, funcionariosEmRisco);
        }

        try {
            String prompt = String.format("""
                Com base nos dados do departamento abaixo, forneça recomendações para melhorar a gestão de carga de trabalho:
                
                Departamento: %s
                Limite máximo de horas: %d
                Total de funcionários: %d
                Funcionários em situação de risco: %d
                
                Forneça:
                1. Análise da situação atual
                2. Recomendações para redistribuição de carga
                3. Sugestões de políticas preventivas
                4. Indicações de quando considerar contratações
                
                Seja prático e objetivo, com foco em ações implementáveis.
                Use no máximo 400 palavras. Responda em português brasileiro.
                """,
                    nomeDepartamento,
                    horasMaximas,
                    totalFuncionarios,
                    funcionariosEmRisco
            );

            logger.info("Prompt departamento criado para: {}", nomeDepartamento);

            OpenAIRequest request = new OpenAIRequest(
                    "llama-3.1-8b-instant",
                    List.of(new OpenAIRequest.Message("user", prompt)),
                    0.7,
                    null
            );

            logger.info("Enviando requisição para Groq API (departamento)... Modelo: {}", request.getModel());

            OpenAIResponse response = openaiWebClient.post()
                    .uri("/chat/completions")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class)
                    .doOnSuccess(r -> logger.info("Resposta departamento recebida da API Groq"))
                    .doOnError(e -> logger.error("Erro na chamada departamento da API Groq: {}", e.getMessage()))
                    .block();

            if (response != null &&
                    response.getChoices() != null &&
                    !response.getChoices().isEmpty() &&
                    response.getChoices().get(0).getMessage() != null &&
                    response.getChoices().get(0).getMessage().getContent() != null) {

                String resultado = response.getChoices().get(0).getMessage().getContent();
                logger.info("Resposta da IA para departamento recebida com sucesso! Tamanho: {} caracteres", resultado.length());
                return resultado;
            } else {
                logger.warn("Resposta vazia ou inválida da API Groq para departamento, usando recomendações mock");
                return gerarRecomendacoesMock(nomeDepartamento, horasMaximas, totalFuncionarios, funcionariosEmRisco);
            }

        } catch (WebClientResponseException e) {
            logger.error("Erro HTTP {} na chamada departamento da API Groq: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "Erro na comunicação com o serviço de IA: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("Erro inesperado ao gerar recomendações para departamento via IA: {}", e.getMessage(), e);
            return gerarRecomendacoesMock(nomeDepartamento, horasMaximas, totalFuncionarios, funcionariosEmRisco);
        }
    }

    private String gerarAnaliseMock(FuncionarioEntity funcionario) {
        return String.format("""
            **Análise de Bem-Estar - %s** [MOCK]
            
            **Situação Atual:** 
            Funcionário trabalhou %d horas no último mês, com limite departamental de %d horas.
            Status: %s
            
            **Recomendações:**
            1. Monitorar carga de trabalho regularmente
            2. Considerar redistribuição de tarefas se necessário
            3. Promover pausas regulares durante a jornada
            
            **Para o Gestor:**
            - Acompanhar indicadores de carga mensalmente
            - Oferecer suporte quando horas ultrapassarem 80%% do limite
            """,
                funcionario.getNome(),
                funcionario.getHorasTrabalhadasUltimoMes(),
                funcionario.getDepartamento().getNumeroHorasMaximas(),
                funcionario.getStatus().name()
        );
    }

    private String gerarRecomendacoesMock(String nomeDepartamento, Integer horasMaximas,
                                          Long totalFuncionarios, Long funcionariosEmRisco) {
        return String.format("""
            **Recomendações para %s** [MOCK]
            
            **Análise:** 
            Departamento com %d funcionários, sendo %d em situação de risco.
            Limite de horas: %d
            
            **Ações Recomendadas:**
            1. Revisar distribuição de tarefas entre a equipe
            2. Implementar sistema de rodízio para cargas pesadas
            3. Estabelecer políticas de descanso obrigatório
            
            **Prevenção:**
            - Monitoramento proativo de horas extras
            - Programas de qualidade de vida no trabalho
            """,
                nomeDepartamento,
                totalFuncionarios,
                funcionariosEmRisco,
                horasMaximas
        );
    }

}
