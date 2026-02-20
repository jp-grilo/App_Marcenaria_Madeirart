import { useState, useEffect } from "react";
import { Box, Typography, Grid2, Alert } from "@mui/material";
import dashboardService from "../../services/dashboardService";
import CardResumoOrcamentos from "./components/CardResumoOrcamentos";
import CardProjecaoFinanceira from "./components/CardProjecaoFinanceira";
import CalendarioFinanceiro from "./components/CalendarioFinanceiro";

export default function Dashboard() {
  const [resumo, setResumo] = useState(null);
  const [projecao, setProjecao] = useState(null);
  const [calendario, setCalendario] = useState(null);
  const [loading, setLoading] = useState(true);
  const [loadingProjecao, setLoadingProjecao] = useState(true);
  const [loadingCalendario, setLoadingCalendario] = useState(true);
  const [error, setError] = useState(null);
  const [errorProjecao, setErrorProjecao] = useState(null);
  const [errorCalendario, setErrorCalendario] = useState(null);

  // Estado para controle de mês/ano do calendário
  const hoje = new Date();
  const [mesCalendario, setMesCalendario] = useState(hoje.getMonth() + 1);
  const [anoCalendario, setAnoCalendario] = useState(hoje.getFullYear());

  useEffect(() => {
    carregarResumo();
  }, []);

  useEffect(() => {
    carregarProjecao(mesCalendario, anoCalendario);
    carregarCalendario(mesCalendario, anoCalendario);
  }, [mesCalendario, anoCalendario]);

  const carregarResumo = async () => {
    try {
      setLoading(true);
      setError(null);
      const dados = await dashboardService.getResumo();
      setResumo(dados);
    } catch (err) {
      console.error("Erro ao carregar resumo:", err);
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  const carregarProjecao = async (mes, ano) => {
    try {
      setLoadingProjecao(true);
      setErrorProjecao(null);
      const dados = await dashboardService.getProjecao(mes, ano);
      setProjecao(dados);
    } catch (err) {
      console.error("Erro ao carregar projeção:", err);
      setErrorProjecao(err);
    } finally {
      setLoadingProjecao(false);
    }
  };

  const carregarCalendario = async (mes, ano) => {
    try {
      setLoadingCalendario(true);
      setErrorCalendario(null);
      const dados = await dashboardService.getCalendario(mes, ano);
      setCalendario(dados);
    } catch (err) {
      console.error("Erro ao carregar calendário:", err);
      setErrorCalendario(err);
    } finally {
      setLoadingCalendario(false);
    }
  };

  const handleMesChange = (novoMes, novoAno) => {
    setMesCalendario(novoMes);
    setAnoCalendario(novoAno);
  };

  const temErro = error || errorProjecao || errorCalendario;
  const estaCarregando = loading || loadingProjecao || loadingCalendario;

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 4, fontWeight: 600 }}>
        Bem-vindo ao Madeirart
      </Typography>

      {temErro && !estaCarregando && (
        <Alert severity="error" sx={{ mb: 3 }}>
          Erro ao carregar dados do dashboard. Verifique se o backend está
          rodando.
        </Alert>
      )}

      <Grid2 container spacing={3}>
        {/* Card de Resumo de Orçamentos */}
        <Grid2 size={{ xs: 12, md: 4 }}>
          <CardResumoOrcamentos
            resumo={resumo}
            loading={loading}
            error={error}
          />
        </Grid2>

        {/* Card de Projeção Financeira */}
        <Grid2 size={{ xs: 12, md: 4 }}>
          <CardProjecaoFinanceira
            projecao={projecao}
            loading={loadingProjecao}
            error={errorProjecao}
          />
        </Grid2>

        {/* Calendário Financeiro */}
        <Grid2 size={{ xs: 12, md: 4 }}>
          <CalendarioFinanceiro
            calendario={calendario}
            loading={loadingCalendario}
            error={errorCalendario}
            onMesChange={handleMesChange}
          />
        </Grid2>
      </Grid2>
    </Box>
  );
}
