import {
  Card,
  CardContent,
  Typography,
  Box,
  Stack,
  Divider,
  CircularProgress,
  Alert,
  Chip,
} from "@mui/material";
import {
  TrendingUp,
  TrendingDown,
  AccountBalance,
  CalendarMonth,
} from "@mui/icons-material";
import { formatCurrency } from "../../../utils/formatters";

/**
 * Componente que exibe a projeção financeira mensal
 * Mostra receita prevista, despesa prevista e saldo projetado
 */
export default function CardProjecaoFinanceira({ projecao, loading, error }) {
  if (loading) {
    return (
      <Card sx={{ height: "100%" }}>
        <CardContent>
          <Box
            display="flex"
            justifyContent="center"
            alignItems="center"
            minHeight={300}
          >
            <CircularProgress />
          </Box>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card sx={{ height: "100%" }}>
        <CardContent>
          <Alert severity="error">Erro ao carregar projeção financeira</Alert>
        </CardContent>
      </Card>
    );
  }

  if (!projecao) {
    return null;
  }

  const {
    receitaPrevista = 0,
    despesaPrevista = 0,
    saldoProjetado = 0,
    mesReferencia = "",
  } = projecao;

  const saldoPositivo = saldoProjetado >= 0;
  const corSaldo = saldoPositivo ? "#2e7d32" : "#d32f2f";

  const percentualMargem =
    receitaPrevista > 0
      ? ((saldoProjetado / receitaPrevista) * 100).toFixed(1)
      : 0;

  return (
    <Card sx={{ height: "100%" }}>
      <CardContent>
        <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 3 }}>
          <CalendarMonth sx={{ color: "text.secondary" }} />
          <Typography variant="h6" sx={{ fontWeight: 600 }}>
            Projeção Financeira
          </Typography>
        </Box>

        <Typography
          variant="subtitle2"
          color="text.secondary"
          sx={{ mb: 3, textAlign: "center" }}
        >
          {mesReferencia}
        </Typography>

        {/* Receita Prevista */}
        <Box
          sx={{
            p: 2,
            backgroundColor: "rgba(46, 125, 50, 0.1)",
            borderRadius: 2,
            mb: 2,
          }}
        >
          <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 1 }}>
            <TrendingUp sx={{ fontSize: 20, color: "#2e7d32" }} />
            <Typography variant="body2" color="text.secondary">
              Receita Prevista
            </Typography>
          </Box>
          <Typography
            variant="h5"
            sx={{ fontWeight: "bold", color: "#2e7d32" }}
          >
            {formatCurrency(receitaPrevista)}
          </Typography>
        </Box>

        {/* Despesa Prevista */}
        <Box
          sx={{
            p: 2,
            backgroundColor: "rgba(211, 47, 47, 0.1)",
            borderRadius: 2,
            mb: 2,
          }}
        >
          <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 1 }}>
            <TrendingDown sx={{ fontSize: 20, color: "#d32f2f" }} />
            <Typography variant="body2" color="text.secondary">
              Despesa Prevista
            </Typography>
          </Box>
          <Typography
            variant="h5"
            sx={{ fontWeight: "bold", color: "#d32f2f" }}
          >
            {formatCurrency(despesaPrevista)}
          </Typography>
        </Box>

        <Divider sx={{ my: 2 }} />

        {/* Saldo Projetado */}
        <Box
          sx={{
            p: 3,
            backgroundColor: saldoPositivo
              ? "rgba(46, 125, 50, 0.15)"
              : "rgba(211, 47, 47, 0.15)",
            borderRadius: 2,
            border: `2px solid ${corSaldo}`,
          }}
        >
          <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 1 }}>
            <AccountBalance sx={{ fontSize: 24, color: corSaldo }} />
            <Typography variant="body2" color="text.secondary">
              Saldo Projetado
            </Typography>
          </Box>
          <Typography
            variant="h4"
            sx={{ fontWeight: "bold", color: corSaldo, mb: 2 }}
          >
            {formatCurrency(saldoProjetado)}
          </Typography>

          {/* Indicador de margem */}
          {receitaPrevista > 0 && (
            <Stack direction="row" spacing={1} alignItems="center">
              <Typography variant="caption" color="text.secondary">
                Margem:
              </Typography>
              <Chip
                size="small"
                label={`${percentualMargem}%`}
                color={saldoPositivo ? "success" : "error"}
                sx={{ fontWeight: "bold" }}
              />
            </Stack>
          )}
        </Box>
      </CardContent>
    </Card>
  );
}
