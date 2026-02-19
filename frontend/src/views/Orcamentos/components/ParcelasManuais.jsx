import {
  Box,
  TextField,
  Typography,
  IconButton,
  Button,
  Alert,
} from "@mui/material";
import { Add, Delete } from "@mui/icons-material";
import { formatCurrency } from "../../../utils/formatters";

export default function ParcelasManuais({
  parcelas,
  onParcelasChange,
  valorRestante,
}) {
  const totalAlocado = parcelas.reduce(
    (sum, p) => sum + (parseFloat(p.valor) || 0),
    0,
  );

  const disponivel = valorRestante - totalAlocado;
  const ultrapassouLimite = disponivel < 0;

  const adicionarParcela = () => {
    onParcelasChange([...parcelas, { valor: "", dataVencimento: "" }]);
  };

  const removerParcela = (index) => {
    onParcelasChange(parcelas.filter((_, i) => i !== index));
  };

  const atualizarParcela = (index, campo, valor) => {
    const novasParcelas = [...parcelas];

    if (campo === "valor") {
      const valorAtual = parseFloat(novasParcelas[index].valor) || 0;
      const novoValor = parseFloat(valor) || 0;
      const outros = totalAlocado - valorAtual;
      const limite = valorRestante - outros;

      if (novoValor > limite) {
        novasParcelas[index][campo] = limite.toFixed(2);
      } else {
        novasParcelas[index][campo] = valor;
      }
    } else {
      novasParcelas[index][campo] = valor;
    }

    onParcelasChange(novasParcelas);
  };

  return (
    <Box>
      {/* Feedback de valor disponível */}
      <Box sx={{ mb: 2 }}>
        <Typography variant="body2" color="text.secondary" sx={{ mb: 1 }}>
          Configure manualmente o valor e data de cada parcela
        </Typography>
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <Typography variant="body2" sx={{ fontWeight: 500 }}>
            Valor disponível:{" "}
            <span style={{ color: ultrapassouLimite ? "#d32f2f" : "#2e7d32" }}>
              {formatCurrency(disponivel)}
            </span>
          </Typography>
          <Button
            variant="outlined"
            size="small"
            startIcon={<Add />}
            onClick={adicionarParcela}
            disabled={disponivel <= 0 && parcelas.length > 0}
          >
            Adicionar
          </Button>
        </Box>
      </Box>

      {ultrapassouLimite && (
        <Alert severity="error" sx={{ mb: 2 }}>
          A soma das parcelas ({formatCurrency(totalAlocado)}) ultrapassou o
          valor restante ({formatCurrency(valorRestante)})
        </Alert>
      )}

      {parcelas.length === 0 ? (
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{ mb: 2, textAlign: "center", py: 2 }}
        >
          Nenhuma parcela adicionada. Clique em "Adicionar" para criar parcelas
          personalizadas.
        </Typography>
      ) : (
        <Box sx={{ maxHeight: 300, overflowY: "auto" }}>
          {parcelas.map((parcela, index) => (
            <Box
              key={index}
              sx={{
                display: "flex",
                gap: 2,
                mb: 2,
                alignItems: "center",
              }}
            >
              <Typography sx={{ minWidth: 80 }}>
                Parcela {index + 1}:
              </Typography>
              <TextField
                label="Valor"
                type="number"
                value={parcela.valor}
                onChange={(e) =>
                  atualizarParcela(index, "valor", e.target.value)
                }
                size="small"
                inputProps={{
                  min: 0,
                  step: 100,
                  max: valorRestante,
                }}
                error={ultrapassouLimite}
                sx={{ flex: 1 }}
              />
              <TextField
                label="Data de Vencimento"
                type="date"
                value={parcela.dataVencimento}
                onChange={(e) =>
                  atualizarParcela(index, "dataVencimento", e.target.value)
                }
                size="small"
                InputLabelProps={{ shrink: true }}
                sx={{ flex: 1 }}
              />
              <IconButton
                color="error"
                size="small"
                onClick={() => removerParcela(index)}
              >
                <Delete />
              </IconButton>
            </Box>
          ))}
        </Box>
      )}
    </Box>
  );
}
