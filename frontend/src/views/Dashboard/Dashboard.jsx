import { useState, useEffect } from "react";
import { Box, Typography, Grid2, Paper, Alert } from "@mui/material";
import {
  Assignment,
  TrendingUp,
  CalendarMonth,
  AttachMoney,
} from "@mui/icons-material";
import dashboardService from "../../services/dashboardService";
import CardResumoOrcamentos from "./components/CardResumoOrcamentos";

const StatCard = ({ title, value, icon, color }) => (
  <Paper
    sx={{
      p: 3,
      display: "flex",
      flexDirection: "column",
      height: 140,
      position: "relative",
      overflow: "hidden",
    }}
  >
    <Box
      sx={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "flex-start",
      }}
    >
      <Box>
        <Typography color="text.secondary" variant="subtitle2" gutterBottom>
          {title}
        </Typography>
        <Typography variant="h4" sx={{ fontWeight: "bold", color }}>
          {value}
        </Typography>
      </Box>
      <Box
        sx={{
          backgroundColor: `${color}20`,
          borderRadius: 2,
          p: 1.5,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        {icon}
      </Box>
    </Box>
  </Paper>
);

export default function Dashboard() {
  const [resumo, setResumo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    carregarResumo();
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

  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 4, fontWeight: 600 }}>
        Bem-vindo ao Madeirart
      </Typography>

      {error && !loading && (
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

        {/* Cards temporários - serão substituídos */}
        <Grid2 size={{ xs: 12, md: 8 }}>
          <Grid2 container spacing={3}>
            <Grid2 size={{ xs: 12, sm: 6 }}>
              <StatCard
                title="Próximos Vencimentos"
                value="Em breve"
                icon={<CalendarMonth sx={{ fontSize: 40, color: "#ed6c02" }} />}
                color="#ed6c02"
              />
            </Grid2>

            <Grid2 size={{ xs: 12, sm: 6 }}>
              <StatCard
                title="Receita do Mês"
                value="Em breve"
                icon={<AttachMoney sx={{ fontSize: 40, color: "#2e7d32" }} />}
                color="#2e7d32"
              />
            </Grid2>
          </Grid2>
        </Grid2>
      </Grid2>
    </Box>
  );
}
