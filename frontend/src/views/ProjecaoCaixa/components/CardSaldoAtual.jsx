import {
  Card,
  CardContent,
  Typography,
  Box,
  Button,
} from "@mui/material";
import {
  AccountBalance,
  TrendingUp,
  TrendingDown,
  Add,
} from "@mui/icons-material";
import { formatCurrency } from "../../../utils/formatters";

/**
 * Card que exibe o saldo atual do caixa
 */
export default function CardSaldoAtual({
  saldoAtual,
  saldoInicial,
  onDefinirSaldoInicial,
}) {
  const saldoPositivo = saldoAtual >= 0;

  return (
    <Card
      sx={{
        background: saldoPositivo
          ? "linear-gradient(135deg, #2e7d32 0%, #1b5e20 100%)"
          : "linear-gradient(135deg, #d32f2f 0%, #c62828 100%)",
        color: "white",
        boxShadow: 3,
      }}
    >
      <CardContent>
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "flex-start",
            mb: 2,
          }}
        >
          <Box>
            <Typography variant="subtitle2" sx={{ opacity: 0.9, mb: 1 }}>
              Saldo Atual do Caixa
            </Typography>
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              {saldoPositivo ? (
                <TrendingUp fontSize="large" />
              ) : (
                <TrendingDown fontSize="large" />
              )}
              <Typography variant="h3" sx={{ fontWeight: "bold" }}>
                {formatCurrency(saldoAtual)}
              </Typography>
            </Box>
          </Box>
          <AccountBalance sx={{ fontSize: 48, opacity: 0.3 }} />
        </Box>

        {saldoInicial !== null && saldoInicial !== undefined && (
          <Box
            sx={{ mt: 3, pt: 2, borderTop: "1px solid rgba(255,255,255,0.2)" }}
          >
            <Typography variant="caption" sx={{ opacity: 0.9 }}>
              Saldo Inicial Cadastrado
            </Typography>
            <Typography variant="h6" sx={{ fontWeight: "600", mt: 0.5 }}>
              {formatCurrency(saldoInicial)}
            </Typography>
          </Box>
        )}

        <Box sx={{ mt: 3 }}>
          <Button
            variant="outlined"
            size="small"
            startIcon={saldoInicial !== null ? null : <Add />}
            onClick={onDefinirSaldoInicial}
            sx={{
              color: "white",
              borderColor: "rgba(255,255,255,0.5)",
              "&:hover": {
                borderColor: "white",
                backgroundColor: "rgba(255,255,255,0.1)",
              },
            }}
          >
            {saldoInicial !== null
              ? "Alterar Saldo Inicial"
              : "Definir Saldo Inicial"}
          </Button>
        </Box>
      </CardContent>
    </Card>
  );
}
