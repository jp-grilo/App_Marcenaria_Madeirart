import { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Box,
  Typography,
  Divider,
  Alert,
  ToggleButtonGroup,
  ToggleButton,
} from "@mui/material";
import { AutoAwesome, Edit } from "@mui/icons-material";
import { formatCurrency } from "../../../utils/formatters";
import ParcelasAutomaticas from "../components/ParcelasAutomaticas";
import ParcelasManuais from "../components/ParcelasManuais";

export default function IniciarProducaoDialog({
  open,
  onClose,
  orcamento,
  onConfirm,
}) {
  const [valorEntrada, setValorEntrada] = useState("");
  const [dataEntrada, setDataEntrada] = useState(
    new Date().toISOString().split("T")[0],
  );
  const [modoParcelas, setModoParcelas] = useState("automatico");
  const [parcelas, setParcelas] = useState([]);
  const [numeroParcelas, setNumeroParcelas] = useState(1);
  const [diaVencimento, setDiaVencimento] = useState(10);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (open) {
      setError("");
      setLoading(false);
    }
  }, [open]);

  const valorTotal = orcamento?.valorTotal || 0;
  const valorRestante = valorTotal - (parseFloat(valorEntrada) || 0);

  const calcularTotalParcelas = () => {
    const entrada = parseFloat(valorEntrada) || 0;

    if (modoParcelas === "automatico") {
      return valorTotal;
    } else {
      const somaParcelas = parcelas.reduce(
        (sum, p) => sum + (parseFloat(p.valor) || 0),
        0,
      );
      return entrada + somaParcelas;
    }
  };

  const calcularRestante = () => {
    return valorTotal - calcularTotalParcelas();
  };

  const gerarParcelasAutomaticas = () => {
    if (valorRestante <= 0 || numeroParcelas <= 0) {
      return [];
    }

    const hoje = new Date();
    const parcelas = [];

    const valorPorParcela =
      Math.round((valorRestante / numeroParcelas) * 100) / 100;

    for (let i = 0; i < numeroParcelas; i++) {
      const dataVencimento = new Date(
        hoje.getFullYear(),
        hoje.getMonth() + i + 1,
        Math.min(diaVencimento, 28),
      );

      parcelas.push({
        valor: valorPorParcela,
        dataVencimento: dataVencimento.toISOString().split("T")[0],
      });
    }

    const somaAtual = valorPorParcela * (numeroParcelas - 1);
    const ultimaParcela = Math.round((valorRestante - somaAtual) * 100) / 100;

    if (parcelas.length > 0) {
      parcelas[parcelas.length - 1].valor = ultimaParcela;
    }

    return parcelas;
  };

  const validarEConfirmar = async () => {
    setError("");
    setLoading(true);

    try {
      if (!valorEntrada || parseFloat(valorEntrada) <= 0) {
        setError("Valor de entrada é obrigatório e deve ser positivo");
        return;
      }

      if (!dataEntrada) {
        setError("Data de entrada é obrigatória");
        return;
      }

      let parcelasParaEnviar = [];

      if (modoParcelas === "automatico") {
        if (valorRestante > 0 && numeroParcelas <= 0) {
          setError("Número de parcelas deve ser maior que zero");
          return;
        }
        if (valorRestante > 0 && (diaVencimento < 1 || diaVencimento > 28)) {
          setError("Dia de vencimento deve estar entre 1 e 28");
          return;
        }

        parcelasParaEnviar =
          valorRestante > 0 ? gerarParcelasAutomaticas() : [];
      } else {
        const total = calcularTotalParcelas();
        if (Math.abs(total - valorTotal) > 0.01) {
          setError(
            `A soma da entrada e parcelas (${formatCurrency(
              total,
            )}) deve ser igual ao valor total do orçamento (${formatCurrency(
              valorTotal,
            )})`,
          );
          return;
        }

        for (let i = 0; i < parcelas.length; i++) {
          if (!parcelas[i].valor || parseFloat(parcelas[i].valor) <= 0) {
            setError(
              `Parcela ${i + 1}: valor é obrigatório e deve ser positivo`,
            );
            return;
          }
          if (!parcelas[i].dataVencimento) {
            setError(`Parcela ${i + 1}: data de vencimento é obrigatória`);
            return;
          }
        }

        parcelasParaEnviar = parcelas.map((p) => ({
          valor: parseFloat(p.valor),
          dataVencimento: p.dataVencimento,
        }));
      }

      const dados = {
        valorEntrada: parseFloat(valorEntrada),
        dataEntrada,
        parcelas: parcelasParaEnviar,
      };

      await onConfirm(dados);
    } catch (error) {
      console.error("Erro na validação:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setValorEntrada("");
    setDataEntrada(new Date().toISOString().split("T")[0]);
    setModoParcelas("automatico");
    setParcelas([]);
    setNumeroParcelas(1);
    setDiaVencimento(10);
    setError("");
    onClose();
  };

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle>Iniciar Produção - {orcamento?.cliente}</DialogTitle>

      <DialogContent>
        <Box sx={{ mt: 2 }}>
          <Typography variant="h6" gutterBottom>
            Valor Total: {formatCurrency(valorTotal)}
          </Typography>

          <Divider sx={{ my: 2 }} />

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          {/* Entrada */}
          <Typography variant="subtitle1" sx={{ mb: 1, fontWeight: 600 }}>
            Entrada (Pagamento Inicial)
          </Typography>
          <Box sx={{ display: "flex", gap: 2, mb: 3 }}>
            <TextField
              label="Valor da Entrada"
              type="number"
              value={valorEntrada}
              onChange={(e) => setValorEntrada(e.target.value)}
              fullWidth
              inputProps={{ min: 0, step: 100 }}
              required
            />
            <TextField
              label="Data de Pagamento"
              type="date"
              value={dataEntrada}
              onChange={(e) => setDataEntrada(e.target.value)}
              fullWidth
              InputLabelProps={{ shrink: true }}
              required
            />
          </Box>

          <Divider sx={{ my: 2 }} />

          {/* Parcelas */}
          <Box sx={{ mb: 2 }}>
            <Typography variant="subtitle1" sx={{ fontWeight: 600, mb: 2 }}>
              Parcelas Subsequentes
            </Typography>

            <ToggleButtonGroup
              value={modoParcelas}
              exclusive
              onChange={(e, value) => {
                if (value !== null) {
                  setModoParcelas(value);
                  setError("");
                }
              }}
              sx={{ mb: 3 }}
              fullWidth
            >
              <ToggleButton value="automatico">
                <AutoAwesome sx={{ mr: 1 }} />
                Recorrente
              </ToggleButton>
              <ToggleButton value="manual">
                <Edit sx={{ mr: 1 }} />
                Manual
              </ToggleButton>
            </ToggleButtonGroup>

            {modoParcelas === "automatico" ? (
              <ParcelasAutomaticas
                valorRestante={valorRestante}
                numeroParcelas={numeroParcelas}
                diaVencimento={diaVencimento}
                onNumeroParcelasChange={setNumeroParcelas}
                onDiaVencimentoChange={setDiaVencimento}
              />
            ) : (
              <ParcelasManuais
                parcelas={parcelas}
                onParcelasChange={setParcelas}
                valorRestante={valorRestante}
              />
            )}
          </Box>

          <Divider sx={{ my: 2 }} />

          {/* Resumo */}
          <Box sx={{ backgroundColor: "#f5f5f5", p: 2, borderRadius: 1 }}>
            <Typography variant="body2">
              <strong>Total de Entrada e Parcelas:</strong>{" "}
              {formatCurrency(calcularTotalParcelas())}
            </Typography>
            <Typography
              variant="body2"
              sx={{
                color:
                  Math.abs(calcularRestante()) < 0.01
                    ? "success.main"
                    : "error.main",
              }}
            >
              <strong>Restante:</strong> {formatCurrency(calcularRestante())}
              {Math.abs(calcularRestante()) < 0.01 && " ✓"}
            </Typography>
          </Box>
        </Box>
      </DialogContent>

      <DialogActions>
        <Button onClick={handleClose} disabled={loading}>
          Cancelar
        </Button>
        <Button
          onClick={validarEConfirmar}
          variant="contained"
          disabled={Math.abs(calcularRestante()) > 0.01 || loading}
        >
          {loading ? "Processando..." : "Confirmar e Iniciar Produção"}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
