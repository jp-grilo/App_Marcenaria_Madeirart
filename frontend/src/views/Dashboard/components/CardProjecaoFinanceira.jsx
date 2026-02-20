import { useState } from "react";
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
  CheckCircle,
} from "@mui/icons-material";
import { formatCurrency } from "../../../utils/formatters";
import ModalDetalheDia from "./ModalDetalheDia";

/**
 * Componente que exibe a projeção financeira mensal
 * Mostra receita prevista, receita recebida, despesa prevista e saldo projetado
 */
export default function CardProjecaoFinanceira({
  projecao,
  loading,
  error,
  calendario,
}) {
  const [modalAberto, setModalAberto] = useState(false);
  const [tipoTransacao, setTipoTransacao] = useState(null);

  const agregarTransacoes = (tipo) => {
    if (!calendario || !calendario.dias) {
      return { entradas: [], saidas: [] };
    }

    const todasEntradas = [];
    const todasSaidas = [];

    Object.values(calendario.dias).forEach((dia) => {
      if (dia.entradas) {
        todasEntradas.push(...dia.entradas);
      }
      if (dia.saidas) {
        todasSaidas.push(...dia.saidas);
      }
    });

    if (tipo === "receitaRecebida") {
      const receitasRecebidas = todasEntradas.filter(
        (t) =>
          t.status?.toUpperCase() === "PAGO" ||
          t.status?.toUpperCase() === "RECEBIDO",
      );
      return { entradas: receitasRecebidas, saidas: [] };
    } else if (tipo === "receitaPrevista") {
      const receitasPrevistas = todasEntradas.filter(
        (t) => t.status?.toUpperCase() === "PENDENTE",
      );
      return { entradas: receitasPrevistas, saidas: [] };
    } else if (tipo === "despesaPrevista") {
      return { entradas: [], saidas: todasSaidas };
    }

    return { entradas: [], saidas: [] };
  };

  const handleAbrirModal = (tipo) => {
    setTipoTransacao(tipo);
    setModalAberto(true);
  };

  const handleFecharModal = () => {
    setModalAberto(false);
    setTipoTransacao(null);
  };
  if (error) {
    return (
      <Card sx={{ height: "100%" }}>
        <CardContent>
          <Alert severity="error">Erro ao carregar projeção financeira</Alert>
        </CardContent>
      </Card>
    );
  }

  if (!projecao && !loading) {
    return null;
  }

  const {
    receitaPrevista = 0,
    receitaRecebida = 0,
    despesaPrevista = 0,
    saldoProjetado = 0,
    mesReferencia = "",
  } = projecao || {};

  const saldoPositivo = saldoProjetado >= 0;
  const corSaldo = saldoPositivo ? "#2e7d32" : "#d32f2f";

  const receitaTotal = receitaPrevista + receitaRecebida;

  const percentualMargem =
    receitaTotal > 0 ? ((saldoProjetado / receitaTotal) * 100).toFixed(1) : 0;

  return (
    <Card sx={{ height: "100%", position: "relative" }}>
      <CardContent>
        <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 3 }}>
          <CalendarMonth sx={{ color: "text.secondary" }} />
          <Typography variant="h6" sx={{ fontWeight: 600 }}>
            Projeção Financeira
          </Typography>
          {loading && <CircularProgress size={16} />}
        </Box>

        <Typography
          variant="subtitle2"
          color="text.secondary"
          sx={{ mb: 3, textAlign: "center" }}
        >
          {mesReferencia}
        </Typography>

        {/* Receita Recebida */}
        <Box
          onClick={() => handleAbrirModal("receitaRecebida")}
          sx={{
            p: 2,
            backgroundColor: "rgba(46, 125, 50, 0.15)",
            borderRadius: 2,
            mb: 2,
            cursor: "pointer",
            transition: "all 0.2s",
            "&:hover": {
              backgroundColor: "rgba(46, 125, 50, 0.25)",
              transform: "scale(1.02)",
              boxShadow: 2,
            },
          }}
        >
          <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 1 }}>
            <CheckCircle sx={{ fontSize: 20, color: "#2e7d32" }} />
            <Typography variant="body2" color="text.secondary">
              Receita Recebida
            </Typography>
          </Box>
          <Typography
            variant="h5"
            sx={{ fontWeight: "bold", color: "#2e7d32" }}
          >
            {formatCurrency(receitaRecebida)}
          </Typography>
        </Box>

        {/* Receita Prevista */}
        <Box
          onClick={() => handleAbrirModal("receitaPrevista")}
          sx={{
            p: 2,
            backgroundColor: "rgba(25, 118, 210, 0.1)",
            borderRadius: 2,
            mb: 2,
            cursor: "pointer",
            transition: "all 0.2s",
            "&:hover": {
              backgroundColor: "rgba(25, 118, 210, 0.2)",
              transform: "scale(1.02)",
              boxShadow: 2,
            },
          }}
        >
          <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 1 }}>
            <TrendingUp sx={{ fontSize: 20, color: "#1976d2" }} />
            <Typography variant="body2" color="text.secondary">
              Receita Prevista
            </Typography>
          </Box>
          <Typography
            variant="h5"
            sx={{ fontWeight: "bold", color: "#1976d2" }}
          >
            {formatCurrency(receitaPrevista)}
          </Typography>
        </Box>

        {/* Despesa Prevista */}
        <Box
          onClick={() => handleAbrirModal("despesaPrevista")}
          sx={{
            p: 2,
            backgroundColor: "rgba(211, 47, 47, 0.1)",
            borderRadius: 2,
            mb: 2,
            cursor: "pointer",
            transition: "all 0.2s",
            "&:hover": {
              backgroundColor: "rgba(211, 47, 47, 0.2)",
              transform: "scale(1.02)",
              boxShadow: 2,
            },
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

          {/* Indicador de margem e receita total */}
          <Stack spacing={1}>
            {receitaTotal > 0 && (
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
            <Typography variant="caption" color="text.secondary">
              Receita Total: {formatCurrency(receitaTotal)}
            </Typography>
          </Stack>
        </Box>
      </CardContent>

      {/* Overlay de loading */}
      {loading && (
        <Box
          sx={{
            position: "absolute",
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: (theme) =>
              theme.palette.mode === "dark"
                ? "rgba(0, 0, 0, 0.7)"
                : "rgba(255, 255, 255, 0.8)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            borderRadius: 1,
            zIndex: 10,
          }}
        >
          <CircularProgress />
        </Box>
      )}

      {/* Modal de detalhes das transações */}
      {modalAberto && tipoTransacao && (
        <ModalDetalheDia
          open={modalAberto}
          onClose={handleFecharModal}
          diaDados={agregarTransacoes(tipoTransacao)}
          data={
            tipoTransacao === "receitaRecebida"
              ? `Receita Recebida - ${mesReferencia}`
              : tipoTransacao === "receitaPrevista"
                ? `Receita Prevista - ${mesReferencia}`
                : `Despesa Prevista - ${mesReferencia}`
          }
        />
      )}
    </Card>
  );
}
