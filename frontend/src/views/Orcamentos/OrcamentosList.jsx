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
  TextField,
  FormControlLabel,
  Checkbox,
  FormGroup,
  Grid2,
} from "@mui/material";
import {
  Add,
  Edit,
  Visibility,
  PlayArrow,
  FilterList,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useSnackbar } from "../../hooks/useSnackbar";
import orcamentoService from "../../services/orcamentoService";
import { formatCurrency, formatDate } from "../../utils/formatters";
import { STATUS_LABELS, STATUS_CORES } from "../../utils/constants";
import IniciarProducaoDialog from "./dialogs/IniciarProducaoDialog";
import AlterarStatusDialog from "./dialogs/AlterarStatusDialog";

export default function OrcamentosList() {
  const navigate = useNavigate();
  const { showError, showSuccess } = useSnackbar();
  const [orcamentos, setOrcamentos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [orcamentoSelecionado, setOrcamentoSelecionado] = useState(null);
  const [dialogStatusOpen, setDialogStatusOpen] = useState(false);

  const [filtro, setFiltro] = useState({
    nomeCliente: "",
    statusAguardando: true,
    statusIniciada: true,
    statusFinalizada: true,
    statusCancelada: true,
  });

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

  const handleAbrirDialogStatus = (orcamento) => {
    setOrcamentoSelecionado(orcamento);
    setDialogStatusOpen(true);
  };

  const handleFecharDialogStatus = () => {
    setDialogStatusOpen(false);
    setOrcamentoSelecionado(null);
  };

  const handleAlterarStatus = async (novoStatus) => {
    try {
      await orcamentoService.alterarStatus(orcamentoSelecionado.id, novoStatus);
      showSuccess("Status alterado com sucesso!");
      handleFecharDialogStatus();

      // Recarregar a lista
      const orcamentosAtualizados = await orcamentoService.listar();
      setOrcamentos(orcamentosAtualizados);
    } catch (err) {
      console.error("Erro ao alterar status:", err);
      showError("Erro ao alterar status do orçamento");
    }
  };

  const limparFiltros = () => {
    setFiltro({
      nomeCliente: "",
      statusAguardando: true,
      statusIniciada: true,
      statusFinalizada: true,
      statusCancelada: true,
    });
  };

  const orcamentosFiltrados = orcamentos.filter((orcamento) => {
    if (
      filtro.nomeCliente &&
      !orcamento.cliente
        .toLowerCase()
        .includes(filtro.nomeCliente.toLowerCase())
    ) {
      return false;
    }

    const statusSelecionados = [];
    if (filtro.statusAguardando) statusSelecionados.push("AGUARDANDO");
    if (filtro.statusIniciada) statusSelecionados.push("INICIADA");
    if (filtro.statusFinalizada) statusSelecionados.push("FINALIZADA");
    if (filtro.statusCancelada) statusSelecionados.push("CANCELADA");

    if (
      statusSelecionados.length > 0 &&
      !statusSelecionados.includes(orcamento.status)
    ) {
      return false;
    }

    return true;
  });

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

      {/* Filtros */}
      <Paper sx={{ p: 2, mb: 3, backgroundColor: "#f5f5f5" }}>
        <Box
          sx={{
            display: "flex",
            alignItems: "center",
            mb: 2,
            gap: 1,
          }}
        >
          <FilterList />
          <Typography variant="h6">Filtros</Typography>
        </Box>

        <Grid2 container spacing={3}>
          {/* Nome do Cliente */}
          <Grid2 size={{ xs: 12, sm: 6, md: 4 }}>
            <Typography
              variant="subtitle2"
              sx={{ mb: 1, fontWeight: 600, paddingBottom: 1.5 }}
            >
              Nome do Cliente
            </Typography>
            <TextField
              fullWidth
              size="small"
              label="Buscar por Cliente"
              placeholder="Digite o nome do cliente..."
              value={filtro.nomeCliente}
              onChange={(e) =>
                setFiltro({
                  ...filtro,
                  nomeCliente: e.target.value,
                })
              }
            />
          </Grid2>

          {/* Status */}
          <Grid2 size={{ xs: 12, sm: 6, md: 5 }}>
            <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600 }}>
              Status
            </Typography>
            <Box sx={{ display: "flex", gap: 3 }}>
              <FormGroup>
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={filtro.statusAguardando}
                      onChange={(e) =>
                        setFiltro({
                          ...filtro,
                          statusAguardando: e.target.checked,
                        })
                      }
                    />
                  }
                  label="Aguardando"
                />
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={filtro.statusIniciada}
                      onChange={(e) =>
                        setFiltro({
                          ...filtro,
                          statusIniciada: e.target.checked,
                        })
                      }
                    />
                  }
                  label="Iniciada"
                />
              </FormGroup>
              <FormGroup>
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={filtro.statusFinalizada}
                      onChange={(e) =>
                        setFiltro({
                          ...filtro,
                          statusFinalizada: e.target.checked,
                        })
                      }
                    />
                  }
                  label="Finalizada"
                />
                <FormControlLabel
                  control={
                    <Checkbox
                      checked={filtro.statusCancelada}
                      onChange={(e) =>
                        setFiltro({
                          ...filtro,
                          statusCancelada: e.target.checked,
                        })
                      }
                    />
                  }
                  label="Cancelada"
                />
              </FormGroup>
            </Box>
          </Grid2>

          {/* Botão Limpar */}
          <Grid2
            size={{ xs: 12, md: 3 }}
            sx={{
              display: "flex",
            }}
          >
            <Button
              fullWidth
              variant="outlined"
              onClick={limparFiltros}
              sx={{ height: "40px", marginTop: 5 }}
            >
              Limpar Filtros
            </Button>
          </Grid2>
        </Grid2>
      </Paper>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
              <TableCell sx={{ fontWeight: "bold" }}>Cliente</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>Móveis</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>Data</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>Valor Total</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>Status</TableCell>
              <TableCell sx={{ fontWeight: "bold" }}>% Pago</TableCell>
              <TableCell sx={{ fontWeight: "bold", width: "120px" }}>
                Ações
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {orcamentosFiltrados.length === 0 ? (
              <TableRow>
                <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                  <Typography color="text.secondary">
                    Nenhum orçamento encontrado com os filtros aplicados.
                  </Typography>
                </TableCell>
              </TableRow>
            ) : (
              orcamentosFiltrados.map((orcamento) => (
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
                      onClick={() => handleAbrirDialogStatus(orcamento)}
                      sx={{ cursor: "pointer" }}
                    />
                  </TableCell>
                  <TableCell>
                    <Typography
                      variant="body2"
                      sx={{
                        fontWeight: 600,
                        color:
                          orcamento.statusRecebimento?.percentualRecebido >= 100
                            ? "success.main"
                            : orcamento.statusRecebimento?.percentualRecebido >=
                                50
                              ? "warning.main"
                              : "error.main",
                      }}
                    >
                      {orcamento.statusRecebimento?.percentualRecebido?.toFixed(
                        1,
                      ) || "0.0"}
                      %
                    </Typography>
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

      <AlterarStatusDialog
        open={dialogStatusOpen}
        onClose={handleFecharDialogStatus}
        orcamento={orcamentoSelecionado}
        onConfirm={handleAlterarStatus}
      />
    </Box>
  );
}
