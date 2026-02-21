import { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Box,
  InputAdornment,
} from "@mui/material";

/**
 * Modal para cadastrar/editar o saldo inicial do sistema
 */
export default function ModalSaldoInicial({
  open,
  onClose,
  onSalvar,
  saldoAtual,
}) {
  const [valor, setValor] = useState("");
  const [observacao, setObservacao] = useState("");

  const formatarValor = (value) => {
    // Remove tudo que não é número
    let numero = value.replace(/\D/g, "");

    if (!numero) return "";

    // Converte para centavos
    numero = (parseInt(numero) / 100).toFixed(2);

    // Formata com separadores
    return numero.replace(".", ",");
  };

  // Inicializar valores quando o modal abrir
  useEffect(() => {
    if (!open) return;

    if (saldoAtual) {
      // Formatar o valor existente corretamente
      const valorFormatado = saldoAtual.valor
        ? formatarValor((saldoAtual.valor * 100).toString())
        : "";
      setValor(valorFormatado);
      setObservacao(saldoAtual.observacao || "");
    } else {
      // Limpar quando abrir sem saldo
      setValor("");
      setObservacao("");
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open]);

  const handleSalvar = () => {
    const valorNumerico = parseFloat(
      valor.replace(/\./g, "").replace(",", "."),
    );

    if (isNaN(valorNumerico)) {
      return;
    }

    onSalvar(valorNumerico, observacao);
    handleClose();
  };

  const handleClose = () => {
    setValor("");
    setObservacao("");
    onClose();
  };

  const handleValorChange = (e) => {
    const valorFormatado = formatarValor(e.target.value);
    setValor(valorFormatado);
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        {saldoAtual ? "Alterar Saldo Inicial" : "Definir Saldo Inicial"}
      </DialogTitle>
      <DialogContent>
        <Box sx={{ pt: 2, display: "flex", flexDirection: "column", gap: 3 }}>
          <TextField
            label="Valor do Saldo Inicial"
            value={valor}
            onChange={handleValorChange}
            fullWidth
            required
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">R$</InputAdornment>
              ),
            }}
            helperText="Digite o valor do saldo que você tinha ao começar a usar o sistema"
          />

          <TextField
            label="Observação"
            value={observacao}
            onChange={(e) => setObservacao(e.target.value)}
            fullWidth
            multiline
            rows={3}
            helperText="Opcional: adicione uma observação sobre este saldo"
          />
        </Box>
      </DialogContent>
      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleClose} color="inherit">
          Cancelar
        </Button>
        <Button onClick={handleSalvar} variant="contained" disabled={!valor}>
          Salvar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
