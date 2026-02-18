import { Box, Typography, Grid2, Paper } from "@mui/material";
import {
  Assignment,
  TrendingUp,
  CalendarMonth,
  AttachMoney,
} from "@mui/icons-material";

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
  return (
    <Box>
      <Typography variant="h4" sx={{ mb: 4, fontWeight: 600 }}>
        Bem-vindo ao Madeirart
      </Typography>

      <Grid2 container spacing={3}>
        <Grid2 item xs={12} sm={6} md={3}>
          <StatCard
            title="Orçamentos Ativos"
            value="12"
            icon={<Assignment sx={{ fontSize: 40, color: "#D2691E" }} />}
            color="#D2691E"
          />
        </Grid2>

        <Grid2 item xs={12} sm={6} md={3}>
          <StatCard
            title="Em Produção"
            value="5"
            icon={<TrendingUp sx={{ fontSize: 40, color: "#1976d2" }} />}
            color="#1976d2"
          />
        </Grid2>

        <Grid2 item xs={12} sm={6} md={3}>
          <StatCard
            title="Próximos Vencimentos"
            value="3"
            icon={<CalendarMonth sx={{ fontSize: 40, color: "#ed6c02" }} />}
            color="#ed6c02"
          />
        </Grid2>

        <Grid2 item xs={12} sm={6} md={3}>
          <StatCard
            title="Receita do Mês"
            value="R$ 15.420"
            icon={<AttachMoney sx={{ fontSize: 40, color: "#2e7d32" }} />}
            color="#2e7d32"
          />
        </Grid2>
      </Grid2>

      <Box sx={{ mt: 4 }}>
        <Paper sx={{ p: 3 }}>
          <Typography variant="h6" sx={{ mb: 2 }}>
            Visão Geral
          </Typography>
          <Typography color="text.secondary">
            Aqui você terá acesso rápido aos principais indicadores do seu
            negócio. Use o menu lateral para navegar entre as funcionalidades.
          </Typography>
        </Paper>
      </Box>
    </Box>
  );
}
