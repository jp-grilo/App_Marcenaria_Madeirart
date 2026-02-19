import { Box, TextField, Typography, IconButton, Button } from "@mui/material";
import { Add, Delete } from "@mui/icons-material";

export default function ParcelasManuais({ parcelas, onParcelasChange }) {
  const adicionarParcela = () => {
    onParcelasChange([...parcelas, { valor: "", dataVencimento: "" }]);
  };

  const removerParcela = (index) => {
    onParcelasChange(parcelas.filter((_, i) => i !== index));
  };

  const atualizarParcela = (index, campo, valor) => {
    const novasParcelas = [...parcelas];
    novasParcelas[index][campo] = valor;
    onParcelasChange(novasParcelas);
  };

  return (
    <Box>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 2,
        }}
      >
        <Typography variant="body2" color="text.secondary">
          Configure manualmente o valor e data de cada parcela
        </Typography>
        <Button
          variant="outlined"
          size="small"
          startIcon={<Add />}
          onClick={adicionarParcela}
        >
          Adicionar
        </Button>
      </Box>

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
                inputProps={{ min: 0, step: 100 }}
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
