package br.com.empowerlearn.empowerlearn_api.config;

import br.com.empowerlearn.empowerlearn_api.model.Plano;
import br.com.empowerlearn.empowerlearn_api.repository.PlanoRepository;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DadosIniciaisConfig {

    @Bean
    CommandLineRunner carregarPlanosPadrao(PlanoRepository planoRepository) {
        return args -> {
            salvarOuAtualizarPlano(
                planoRepository,
                "GRATUITO",
                "Gratuito",
                "Acesso inicial para alunos, responsáveis, instituições e professores. Professores usam gratuitamente.",
                new BigDecimal("0.00"),
                "sem cobrança",
                "Cadastro e perfil ativo|Busca e filtros básicos|Acesso inicial à EmpowerLearn.class|1 contratação por mês para contratantes|1 curtida/favorito|Notificações essenciais",
                1
            );

            salvarOuAtualizarPlano(
                planoRepository,
                "BASICO",
                "Básico+",
                "Plano para alunos e responsáveis que precisam ampliar o uso da plataforma.",
                new BigDecimal("19.90"),
                "mensal",
                "Tudo do Gratuito|Até 5 contratações por mês|Até 10 curtidas/favoritos|Filtros avançados por localização e experiência|Histórico completo de perfis",
                5
            );

            salvarOuAtualizarPlano(
                planoRepository,
                "PRO",
                "Pro Institucional",
                "Plano recomendado para escolas, cursinhos e instituições com demanda frequente de contratação.",
                new BigDecimal("59.90"),
                "mensal",
                "Tudo do Básico+|Até 20 contratações por mês|Painel de contratados|Notificações em tempo real|Filtros avançados completos|Prioridade no matching pedagógico",
                20
            );

            salvarOuAtualizarPlano(
                planoRepository,
                "PREMIUM",
                "Premium Institucional",
                "Plano completo para instituições, equipes pedagógicas e uso intensivo da EmpowerLearn.",
                new BigDecimal("99.90"),
                "mensal",
                "Tudo do Pro Institucional|Contratações ilimitadas|Relatórios e acompanhamento ampliado|Suporte prioritário|Recursos avançados da EmpowerLearn.class|Gestão ampliada para equipes",
                999999
            );
        };
    }

    private void salvarOuAtualizarPlano(
        PlanoRepository repository,
        String codigo,
        String nome,
        String descricao,
        BigDecimal valor,
        String periodicidade,
        String recursos,
        Integer limiteContratacoes
    ) {
        Plano plano = repository.findByCodigo(codigo).orElseGet(Plano::new);
        plano.setCodigo(codigo);
        plano.setNome(nome);
        plano.setDescricao(descricao);
        plano.setValor(valor);
        plano.setPeriodicidade(periodicidade);
        plano.setRecursos(recursos);
        plano.setLimiteContratacoes(limiteContratacoes);
        plano.setAtivo(true);
        repository.save(plano);
    }
}
