import { useState, useEffect } from "react";
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
  IconButton,
  Tooltip,
  CircularProgress,
  Alert,
} from "@mui/material";
import { Add, Edit, Visibility, PlayArrow } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useSnackbar } from "../../hooks/useSnackbar";
import orcamentoService from "../../services/orcamentoService";
import { formatCurrency, formatDate } from "../../utils/formatters";
import { STATUS_LABELS, STATUS_CORES } from "../../utils/constants";
import IniciarProducaoDialog from "./dialogs/IniciarProducaoDialog";

export default function OrcamentosList() {
  const navigate = useNavigate();
  const { showError, showSuccess } = useSnackbar();
  const [orcamentos, setOrcamentos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [orcamentoSelecionado, setOrcamentoSelecionado] = useState(null);

  useEffect(() => {
    const carregarOrcamentos = async () => {
      try {
        setLoading(true);
        setError(null);
        const dados = await orcamentoService.listar();
        setOrcamentos(dados);
      } catch (err) {
        const mensagem =
          "Erro ao carregar orçamentos. Verifique se o backend está rodando.";
        setError(mensagem);
        showError(mensagem);
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    carregarOrcamentos();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleAbrirDialogIniciar = (orcamento) => {
    setOrcamentoSelecionado(orcamento);
    setDialogOpen(true);
  };

  const handleFecharDialog = () => {
    setDialogOpen(false);
    setOrcamentoSelecionado(null);
  };

  const handleIniciarProducao = async (dados) => {
    try {
      await orcamentoService.iniciarProducao(orcamentoSelecionado.id, dados);
      showSuccess("Produção iniciada com sucesso!");
      handleFecharDialog();

      // Recarregar a lista
      const orcamentosAtualizados = await orcamentoService.listar();
      setOrcamentos(orcamentosAtualizados);
    } catch (err) {
      console.error("Erro ao iniciar produção:", err);

      let mensagem = "Erro ao iniciar produção";

      if (err.response) {
        const { status, data } = err.response;

        if (status === 500) {
          mensagem =
            "Erro interno no servidor. Verifique os dados e tente novamente.";
        } else if (status === 400) {
          mensagem =
            typeof data === "string"
              ? data
              : data?.message || "Dados inválidos";
        } else if (data) {
          mensagem =
            typeof data === "string"
              ? data
              : data?.message || data?.error || mensagem;
        }
      } else if (err.request) {
        mensagem = "Servidor não respondeu. Verifique sua conexão.";
      } else {
        mensagem = err.message || mensagem;
      }

      showError(mensagem);
    }
  };

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
              <TableCell sx={{ fontWeight: "bold", width: "120px" }}>
                Ações
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {orcamentos.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} align="center" sx={{ py: 4 }}>
                  <Typography color="text.secondary">
                    Nenhum orçamento cadastrado. Clique em "Novo Orçamento" para
                    começar.
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              orcamentos.map((orcamento) => (
                <TableRow key={orcamento.id} hover>
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
                  <TableCell>
                    <Box sx={{ display: "flex", gap: 1 }}>
                      <Tooltip title="Visualizar">
                        <IconButton
                          size="small"
                          color="primary"
                          onClick={() =>
                            navigate(`/orcamentos/${orcamento.id}`)
                          }
                        >
                          <Visibility fontSize="small" />
                        </IconButton>
                      </Tooltip>
                      {orcamento.status === "AGUARDANDO" && (
                        <>
                          <Tooltip title="Editar">
                            <IconButton
                              size="small"
                              color="primary"
                              onClick={() =>
                                navigate(`/orcamentos/${orcamento.id}/editar`)
                              }
                            >
                              <Edit fontSize="small" />
                            </IconButton>
                          </Tooltip>
                          <Tooltip title="Iniciar Produção">
                            <IconButton
                              size="small"
                              color="success"
                              onClick={() =>
                                handleAbrirDialogIniciar(orcamento)
                              }
                            >
                              <PlayArrow fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        </>
                      )}
                      {orcamento.status !== "AGUARDANDO" && (
                        <Tooltip title="Editar">
                          <IconButton
                            size="small"
                            color="primary"
                            onClick={() =>
                              navigate(`/orcamentos/${orcamento.id}/editar`)
                            }
                          >
                            <Edit fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      )}
                    </Box>
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <IniciarProducaoDialog
        open={dialogOpen}
        onClose={handleFecharDialog}
        orcamento={orcamentoSelecionado}
        onConfirm={handleIniciarProducao}
      />
    </Box>
  );
}
