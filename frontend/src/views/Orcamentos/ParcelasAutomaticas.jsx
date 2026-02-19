import { Box, TextField, Typography, Grid2 } from "@mui/material";
import { formatCurrency } from "../../utils/formatters";

export default function ParcelasAutomaticas({
  valorRestante,
  numeroParcelas,
  diaVencimento,
  onNumeroParcelasChange,
  onDiaVencimentoChange,
}) {
  const valorPorParcela =
    numeroParcelas > 0
      ? Math.round((valorRestante / numeroParcelas) * 100) / 100
      : 0;

  const calcularDataVencimento = (mes) => {
    const hoje = new Date();
    const dataVencimento = new Date(
      hoje.getFullYear(),
      hoje.getMonth() + mes,
      Math.min(diaVencimento, 28),
    );
    return dataVencimento.toISOString().split("T")[0];
  };

  return (
    <Box>
      <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
        Divida o valor restante em parcelas iguais com vencimento recorrente
        todo dia {diaVencimento || "_"} de cada mês.
      </Typography>

      <Grid2 container spacing={2} sx={{ mb: 3 }}>
        <Grid2 item xs={12} sm={6}>
          <TextField
            label="Número de Parcelas"
            type="number"
            value={numeroParcelas}
            onChange={(e) =>
              onNumeroParcelasChange(parseInt(e.target.value) || 0)
            }
            fullWidth
            size="small"
            inputProps={{ min: 1, max: 36, step: 1 }}
            helperText={`Valor por parcela: ${formatCurrency(valorPorParcela)} (última parcela pode ter pequeno ajuste)`}
          />
        </Grid2>
        <Grid2 item xs={12} sm={6}>
          <TextField
            label="Dia de Vencimento"
            type="number"
            value={diaVencimento}
            onChange={(e) =>
              onDiaVencimentoChange(parseInt(e.target.value) || 1)
            }
            fullWidth
            size="small"
            inputProps={{ min: 1, max: 28 }}
            helperText="Dia do mês (1-28)"
          />
        </Grid2>
      </Grid2>

      {numeroParcelas > 0 && valorRestante > 0 && (
        <Box
          sx={{
            backgroundColor: "#f9f9f9",
            p: 2,
            borderRadius: 1,
            border: "1px solid #e0e0e0",
          }}
        >
          <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600 }}>
            Prévia das Parcelas:
          </Typography>
          <Box sx={{ maxHeight: 200, overflowY: "auto" }}>
            {Array.from({ length: numeroParcelas }, (_, i) => (
              <Box
                key={i}
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  py: 0.5,
                  borderBottom:
                    i < numeroParcelas - 1 ? "1px solid #f0f0f0" : "none",
                }}
              >
                <Typography variant="body2">Parcela {i + 1}</Typography>
                <Typography variant="body2" sx={{ fontWeight: 500 }}>
                  {formatCurrency(valorPorParcela)} -{" "}
                  {calcularDataVencimento(i + 1)}
                </Typography>
              </Box>
            ))}
          </Box>
        </Box>
      )}
    </Box>
  );
}
