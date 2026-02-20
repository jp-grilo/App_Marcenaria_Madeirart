import { useState, useEffect } from "react";
import { Box, Typography, Grid2, Paper, Alert } from "@mui/material";
import dashboardService from "../../services/dashboardService";
import CardResumoOrcamentos from "./components/CardResumoOrcamentos";
import CardProjecaoFinanceira from "./components/CardProjecaoFinanceira";

export default function Dashboard() {
  const [resumo, setResumo] = useState(null);
  const [projecao, setProjecao] = useState(null);
  const [loading, setLoading] = useState(true);
  const [loadingProjecao, setLoadingProjecao] = useState(true);
  const [error, setError] = useState(null);
  const [errorProjecao, setErrorProjecao] = useState(null);

  useEffect(() => {
    carregarResumo();
    carregarProjecao();
  }, []);

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

  const carregarProjecao = async () => {
    try {
      setLoadingProjecao(true);
      setErrorProjecao(null);

      // Obter mês e ano atuais
      const hoje = new Date();
      const mes = hoje.getMonth() + 1; // getMonth() retorna 0-11
      const ano = hoje.getFullYear();

      const dados = await dashboardService.getProjecao(mes, ano);
      setProjecao(dados);
    } catch (err) {
      console.error("Erro ao carregar projeção:", err);
      setErrorProjecao(err);
    } finally {
      setLoadingProjecao(false);
    }
  };

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 4, fontWeight: 600 }}>
        Bem-vindo ao Madeirart
      </Typography>

      {(error || errorProjecao) && !loading && !loadingProjecao && (
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

        {/* Cards temporários - serão substituídos pelo calendário */}
        <Grid2 size={{ xs: 12, md: 4 }}>
          <Paper sx={{ p: 3, height: "100%" }}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              Calendário Financeiro
            </Typography>
            <Typography color="text.secondary">
              Em breve: calendário com indicadores de entradas e saídas
            </Typography>
          </Paper>
        </Grid2>
      </Grid2>
    </Box>
  );
}
