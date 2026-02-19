import {
  Card,
  CardContent,
  Typography,
  Box,
  LinearProgress,
  Divider,
  Stack,
} from "@mui/material";
import {
  CheckCircle,
  PendingActions,
  MonetizationOn,
} from "@mui/icons-material";
import { formatCurrency } from "../utils/formatters";

/**
 * Componente que exibe o status de recebimento de um orçamento
 * Mostra totais, percentual recebido e barra de progresso
 */
export default function StatusRecebimentoCard({ statusRecebimento }) {
  if (!statusRecebimento) {
    return null;
  }

  const {
    valorTotalOrcamento,
    totalJaConfirmado,
    totalPendente,
    percentualRecebido,
  } = statusRecebimento;

  return (
    <Card
      sx={{
        mb: 3,
        background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
        color: "white",
      }}
    >
      <CardContent>
        <Typography variant="h6" sx={{ mb: 3, fontWeight: 600 }}>
          Status de Recebimento
        </Typography>

        {/* Barra de Progresso */}
        <Box sx={{ mb: 3 }}>
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 1,
            }}
          >
            <Typography variant="body2" sx={{ fontWeight: 500 }}>
              Progresso de Pagamento
            </Typography>
            <Typography variant="h6" sx={{ fontWeight: 700 }}>
              {percentualRecebido?.toFixed(1)}%
            </Typography>
          </Box>
          <LinearProgress
            variant="determinate"
            value={percentualRecebido || 0}
            sx={{
              height: 12,
              borderRadius: 2,
              backgroundColor: "rgba(255, 255, 255, 0.3)",
              "& .MuiLinearProgress-bar": {
                borderRadius: 2,
                backgroundColor: "#4caf50",
              },
            }}
          />
        </Box>

        <Divider sx={{ borderColor: "rgba(255, 255, 255, 0.2)", mb: 2 }} />

        {/* Informações Financeiras */}
        <Stack spacing={2}>
          {/* Total do Orçamento */}
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              p: 2,
              backgroundColor: "rgba(255, 255, 255, 0.1)",
              borderRadius: 2,
            }}
          >
            <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
              <MonetizationOn sx={{ fontSize: 28 }} />
              <Box>
                <Typography variant="caption" sx={{ opacity: 0.9 }}>
                  Total do Orçamento
                </Typography>
                <Typography variant="h6" sx={{ fontWeight: 700 }}>
                  {formatCurrency(valorTotalOrcamento)}
                </Typography>
              </Box>
            </Box>
          </Box>

          <Box sx={{ display: "flex", gap: 2 }}>
            {/* Total Já Confirmado */}
            <Box
              sx={{
                flex: 1,
                p: 2,
                backgroundColor: "rgba(76, 175, 80, 0.2)",
                borderRadius: 2,
                border: "1px solid rgba(76, 175, 80, 0.3)",
              }}
            >
              <Box
                sx={{ display: "flex", alignItems: "center", gap: 1, mb: 0.5 }}
              >
                <CheckCircle sx={{ fontSize: 20, color: "#4caf50" }} />
                <Typography variant="caption" sx={{ opacity: 0.9 }}>
                  Já Confirmado
                </Typography>
              </Box>
              <Typography variant="h6" sx={{ fontWeight: 700 }}>
                {formatCurrency(totalJaConfirmado)}
              </Typography>
            </Box>

            {/* Total Pendente */}
            <Box
              sx={{
                flex: 1,
                p: 2,
                backgroundColor: "rgba(255, 152, 0, 0.2)",
                borderRadius: 2,
                border: "1px solid rgba(255, 152, 0, 0.3)",
              }}
            >
              <Box
                sx={{ display: "flex", alignItems: "center", gap: 1, mb: 0.5 }}
              >
                <PendingActions sx={{ fontSize: 20, color: "#ff9800" }} />
                <Typography variant="caption" sx={{ opacity: 0.9 }}>
                  Pendente
                </Typography>
              </Box>
              <Typography variant="h6" sx={{ fontWeight: 700 }}>
                {formatCurrency(totalPendente)}
              </Typography>
            </Box>
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );
}
