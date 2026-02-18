import { useState, useEffect, useCallback } from "react";
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Button,
  CircularProgress,
  Alert,
} from "@mui/material";
import { Add } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useSnackbar } from "../../hooks/useSnackbar";
import orcamentoService from "../../services/orcamentoService";
import { formatCurrency, formatDate } from "../../utils/formatters";
import { STATUS_LABELS, STATUS_CORES } from "../../utils/constants";

export default function OrcamentosList() {
  const navigate = useNavigate();
  const { showSuccess, showError } = useSnackbar();
  const [orcamentos, setOrcamentos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const carregarOrcamentos = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const dados = await orcamentoService.listar();
      setOrcamentos(dados);
      if (dados.length > 0) {
        showSuccess(`${dados.length} orçamento(s) carregado(s) com sucesso`);
      }
    } catch (err) {
      const mensagem = "Erro ao carregar orçamentos. Verifique se o backend está rodando.";
      setError(mensagem);
      showError(mensagem);
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [showSuccess, showError]);

  useEffect(() => {
    carregarOrcamentos();
  }, [carregarOrcamentos]);

  if (loading) {
    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          minHeight: 400,
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 3,
        }}
      >
        <Typography variant="h4" sx={{ fontWeight: 600 }}>
          Orçamentos
        </Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => navigate("/orcamentos/novo")}
          sx={{
            backgroundColor: "#D2691E",
            "&:hover": {
              backgroundColor: "#B8551A",
            },
          }}
        >
          Novo Orçamento
        </Button>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
              <TableCell sx={{ fontWeight: "bold" }}>Cliente</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>Móveis</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>Data</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>Valor Total</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>Status</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {orcamentos.length === 0 ? (
              <TableRow>
                <TableCell colSpan={5} align="center" sx={{ py: 4 }}>
                  <Typography color="text.secondary">
                    Nenhum orçamento cadastrado. Clique em "Novo Orçamento" para
                    começar.
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              orcamentos.map((orcamento) => (
                <TableRow
                  key={orcamento.id}
                  hover
                  sx={{ cursor: "pointer" }}
                  onClick={() => navigate(`/orcamentos/${orcamento.id}`)}
                >
                  <TableCell>{orcamento.cliente}</TableCell>
                  <TableCell>{orcamento.moveis}</TableCell>
                  <TableCell>{formatDate(orcamento.data)}</TableCell>
                  <TableCell sx={{ fontWeight: "bold" }}>
                    {formatCurrency(orcamento.valorTotal)}
                  </TableCell>
                  <TableCell>
                    <Chip
                      label={STATUS_LABELS[orcamento.status]}
                      color={STATUS_CORES[orcamento.status]}
                      size="small"
                    />
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
