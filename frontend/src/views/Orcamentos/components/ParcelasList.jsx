import { useState } from "react";
import {
  Box,
  Card,
  Typography,
  Chip,
  Button,
  Stack,
} from "@mui/material";
import { CheckCircle } from "@mui/icons-material";
import { formatCurrency } from "../../../utils/formatters";

export default function ParcelasList({
  parcelas,
  onConfirmar,
  loading = false,
}) {
  const [confirmandoId, setConfirmandoId] = useState(null);

  const getStatusColor = (status) => {
    switch (status) {
      case "PAGO":
        return "success";
      case "PENDENTE":
        return "warning";
      case "ATRASADO":
        return "error";
      default:
        return "default";
    }
  };

  const getStatusLabel = (status) => {
    switch (status) {
      case "PAGO":
        return "Pago";
      case "PENDENTE":
        return "Pendente";
      case "ATRASADO":
        return "Atrasado";
      default:
        return status;
    }
  };

  const formatarData = (data) => {
    if (!data) return "-";
    return new Date(data + "T00:00:00").toLocaleDateString("pt-BR");
  };

  const handleConfirmar = async (id) => {
    setConfirmandoId(id);
    try {
      await onConfirmar(id);
    } finally {
      setConfirmandoId(null);
    }
  };

  if (!parcelas || parcelas.length === 0) {
    return (
      <Box sx={{ p: 3, textAlign: "center" }}>
        <Typography variant="body2" color="text.secondary">
          Nenhuma parcela cadastrada para este orçamento.
        </Typography>
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h6" sx={{ mb: 2 }}>
        Plano de Pagamento
      </Typography>

      <Stack spacing={2}>
        {parcelas.map((parcela) => (
          <Card
            key={parcela.id}
            variant="outlined"
            sx={{
              borderLeft: 4,
              borderLeftColor:
                parcela.status === "PAGO" ? "success.main" : "warning.main",
            }}
          >
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                p: 2,
                gap: 2,
              }}
            >
              {/* Informações da parcela */}
              <Box sx={{ display: "flex", alignItems: "center", gap: 3, flex: 1 }}>
                <Box sx={{ minWidth: 100 }}>
                  <Typography variant="subtitle2" fontWeight="bold">
                    {parcela.numeroParcela === 1
                      ? "Entrada"
                      : `Parcela ${parcela.numeroParcela - 1}`}
                  </Typography>
                </Box>

                <Box sx={{ minWidth: 120 }}>
                  <Typography variant="h6" color="primary">
                    {formatCurrency(parcela.valor)}
                  </Typography>
                </Box>

                <Box sx={{ minWidth: 150 }}>
                  <Typography variant="caption" color="text.secondary" display="block">
                    Vencimento
                  </Typography>
                  <Typography variant="body2">
                    {formatarData(parcela.dataVencimento)}
                  </Typography>
                </Box>

                {parcela.dataPagamento && (
                  <Box sx={{ minWidth: 150 }}>
                    <Typography variant="caption" color="text.secondary" display="block">
                      Pago em
                    </Typography>
                    <Typography variant="body2">
                      {formatarData(parcela.dataPagamento)}
                    </Typography>
                  </Box>
                )}

                <Box>
                  <Chip
                    label={getStatusLabel(parcela.status)}
                    color={getStatusColor(parcela.status)}
                    size="small"
                  />
                </Box>
              </Box>

              {/* Botão de confirmação */}
              {(parcela.status === "PENDENTE" || parcela.status === "ATRASADO") && (
                <Button
                  variant="outlined"
                  color="inherit"
                  size="small"
                  startIcon={<CheckCircle />}
                  onClick={() => handleConfirmar(parcela.id)}
                  disabled={loading || confirmandoId === parcela.id}
                  sx={{ minWidth: 200 }}
                >
                  Confirmar Recebimento
                </Button>
              )}
            </Box>
          </Card>
        ))}
      </Stack>
    </Box>
  );
}
