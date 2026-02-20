import { useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Chip,
  Typography,
} from "@mui/material";
import { STATUS_LABELS, STATUS_CORES } from "../../../utils/constants";

export default function AlterarStatusDialog({
  open,
  onClose,
  orcamento,
  onConfirm,
}) {
  const [statusSelecionado, setStatusSelecionado] = useState(null);

  // Status disponíveis (excluindo AGUARDANDO)
  const statusDisponiveis = ["INICIADA", "FINALIZADA", "CANCELADA"];

  const handleConfirmar = () => {
    if (statusSelecionado && statusSelecionado !== orcamento?.status) {
      onConfirm(statusSelecionado);
    }
  };

  const handleFechar = () => {
    setStatusSelecionado(null);
    onClose();
  };

  if (!orcamento) return null;

  // Define o status selecionado inicial se ainda não foi definido
  const statusAtual = statusSelecionado || orcamento.status;

  return (
    <Dialog open={open} onClose={handleFechar} maxWidth="sm" fullWidth>
      <DialogTitle>Alterar Status do Orçamento</DialogTitle>
      <DialogContent>
        <Box sx={{ mb: 2 }}>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Cliente: <strong>{orcamento.cliente}</strong>
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Móveis: <strong>{orcamento.moveis}</strong>
          </Typography>
        </Box>

        <Typography variant="subtitle2" sx={{ mb: 2, fontWeight: 600 }}>
          Selecione o novo status:
        </Typography>

        <Box sx={{ display: "flex", gap: 2, flexWrap: "wrap" }}>
          {statusDisponiveis.map((status) => (
            <Chip
              key={status}
              label={STATUS_LABELS[status]}
              color={STATUS_CORES[status]}
              variant={statusAtual === status ? "filled" : "outlined"}
              onClick={() => setStatusSelecionado(status)}
              sx={{
                cursor: "pointer",
                fontSize: "0.875rem",
                fontWeight: statusAtual === status ? 600 : 400,
                "&:hover": {
                  transform: "scale(1.05)",
                  transition: "transform 0.2s",
                },
              }}
            />
          ))}
        </Box>

        {statusSelecionado && statusSelecionado !== orcamento.status && (
          <Box
            sx={{
              mt: 3,
              p: 2,
              backgroundColor: "#f5f5f5",
              borderRadius: 1,
            }}
          >
            <Typography variant="body2" color="text.secondary">
              Status será alterado de{" "}
              <Chip
                label={STATUS_LABELS[orcamento.status]}
                color={STATUS_CORES[orcamento.status]}
                size="small"
              />{" "}
              para{" "}
              <Chip
                label={STATUS_LABELS[statusSelecionado]}
                color={STATUS_CORES[statusSelecionado]}
                size="small"
              />
            </Typography>
          </Box>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={handleFechar} color="inherit">
          Cancelar
        </Button>
        <Button
          onClick={handleConfirmar}
          variant="contained"
          color="primary"
          disabled={
            !statusSelecionado || statusSelecionado === orcamento.status
          }
        >
          Confirmar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
