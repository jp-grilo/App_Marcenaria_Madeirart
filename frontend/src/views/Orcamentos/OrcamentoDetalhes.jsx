import { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Paper,
  Button,
  IconButton,
  CircularProgress,
  Alert,
  Grid2,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  Tabs,
  Tab,
} from "@mui/material";
import { ArrowBack, Edit, PlayArrow } from "@mui/icons-material";
import { useNavigate, useParams } from "react-router-dom";
import { useSnackbar } from "../../hooks/useSnackbar";
import orcamentoService from "../../services/orcamentoService";
import { formatCurrency, formatDate } from "../../utils/formatters";
import { STATUS_LABELS, STATUS_CORES } from "../../utils/constants";
import IniciarProducaoDialog from "./IniciarProducaoDialog";

export default function OrcamentoDetalhes() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { showError, showSuccess } = useSnackbar();
  const [loading, setLoading] = useState(true);
  const [orcamento, setOrcamento] = useState(null);
  const [historico, setHistorico] = useState([]);
  const [tabAtiva, setTabAtiva] = useState(0);
  const [loadingHistorico, setLoadingHistorico] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);

  useEffect(() => {
    const carregarDados = async () => {
      try {
        setLoading(true);
        const dados = await orcamentoService.buscarPorId(id);
        setOrcamento(dados);
      } catch (err) {
        showError("Erro ao carregar orçamento");
        console.error(err);
        navigate("/orcamentos");
      } finally {
        setLoading(false);
      }
    };

    carregarDados();
  }, [id, navigate, showError]);

  const carregarHistorico = async () => {
    if (historico.length > 0) return;

    try {
      setLoadingHistorico(true);
      const dados = await orcamentoService.buscarHistorico(id);
      setHistorico(dados);
    } catch (err) {
      showError("Erro ao carregar histórico");
      console.error(err);
    } finally {
      setLoadingHistorico(false);
    }
  };

  const handleTabChange = (event, newValue) => {
    setTabAtiva(newValue);
    if (newValue === 1) {
      carregarHistorico();
    }
  };

  const handleIniciarProducao = async (dados) => {
    try {
      const orcamentoAtualizado = await orcamentoService.iniciarProducao(
        id,
        dados,
      );
      setOrcamento(orcamentoAtualizado);
      setDialogOpen(false);
      showSuccess("Produção iniciada com sucesso!");
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

  if (!orcamento) {
    return <Alert severity="error">Orçamento não encontrado</Alert>;
  }

  return (
    <Box>
      <Box
        sx={{
          mb: 3,
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
          <IconButton onClick={() => navigate("/orcamentos")} color="primary">
            <ArrowBack />
          </IconButton>
          <Typography variant="h4" sx={{ fontWeight: 600 }}>
            Detalhes do Orçamento #{orcamento.id}
          </Typography>
        </Box>
        <Box sx={{ display: "flex", gap: 2 }}>
          {orcamento.status === "AGUARDANDO" && (
            <Button
              variant="contained"
              color="success"
              startIcon={<PlayArrow />}
              onClick={() => setDialogOpen(true)}
            >
              Iniciar Produção
            </Button>
          )}
          <Button
            variant="contained"
            startIcon={<Edit />}
            onClick={() => navigate(`/orcamentos/${id}/editar`)}
            sx={{
              backgroundColor: "#D2691E",
              "&:hover": {
                backgroundColor: "#B8551A",
              },
            }}
          >
            Editar
          </Button>
        </Box>
      </Box>

      <Tabs value={tabAtiva} onChange={handleTabChange} sx={{ mb: 3 }}>
        <Tab label="Informações" />
        <Tab label="Histórico" />
      </Tabs>

      {tabAtiva === 0 && (
        <>
          <Paper sx={{ p: 3, mb: 3 }}>
            <Typography variant="h6" sx={{ mb: 3 }}>
              Informações Gerais
            </Typography>

            <Grid2 container spacing={3}>
              <Grid2 item xs={12} md={6}>
                <Typography variant="caption" color="text.secondary">
                  Cliente
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 500 }}>
                  {orcamento.cliente}
                </Typography>
              </Grid2>

              <Grid2 item xs={12} md={6}>
                <Typography variant="caption" color="text.secondary">
                  Status
                </Typography>
                <Box sx={{ mt: 0.5 }}>
                  <Chip
                    label={STATUS_LABELS[orcamento.status]}
                    color={STATUS_CORES[orcamento.status]}
                    size="small"
                  />
                </Box>
              </Grid2>

              <Grid2 item xs={12}>
                <Typography variant="caption" color="text.secondary">
                  Móveis / Descrição
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 500 }}>
                  {orcamento.moveis}
                </Typography>
              </Grid2>

              <Grid2 item xs={12} md={6}>
                <Typography variant="caption" color="text.secondary">
                  Data
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 500 }}>
                  {formatDate(orcamento.data)}
                </Typography>
              </Grid2>

              <Grid2 item xs={12} md={6}>
                <Typography variant="caption" color="text.secondary">
                  Previsão de Entrega
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 500 }}>
                  {orcamento.previsaoEntrega
                    ? formatDate(orcamento.previsaoEntrega)
                    : "-"}
                </Typography>
              </Grid2>

              <Grid2 item xs={12} md={4}>
                <Typography variant="caption" color="text.secondary">
                  Fator de Mão de Obra
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 500 }}>
                  {orcamento.fatorMaoDeObra}
                </Typography>
              </Grid2>

              <Grid2 item xs={12} md={4}>
                <Typography variant="caption" color="text.secondary">
                  Custos Extras
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 500 }}>
                  {formatCurrency(orcamento.custosExtras)}
                </Typography>
              </Grid2>

              <Grid2 item xs={12} md={4}>
                <Typography variant="caption" color="text.secondary">
                  CPC
                </Typography>
                <Typography variant="body1" sx={{ fontWeight: 500 }}>
                  {formatCurrency(orcamento.cpc)}
                </Typography>
              </Grid2>
            </Grid2>
          </Paper>

          <Paper sx={{ p: 3, mb: 3 }}>
            <Typography variant="h6" sx={{ mb: 3 }}>
              Materiais
            </Typography>

            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Quantidade
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Descrição</TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>
                      Valor Unitário
                    </TableCell>
                    <TableCell sx={{ fontWeight: "bold" }}>Subtotal</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {orcamento.itens.map((item, index) => (
                    <TableRow key={index}>
                      <TableCell>{item.quantidade}</TableCell>
                      <TableCell>{item.descricao}</TableCell>
                      <TableCell>
                        {formatCurrency(item.valorUnitario)}
                      </TableCell>
                      <TableCell sx={{ fontWeight: "bold" }}>
                        {formatCurrency(item.subtotal)}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </Paper>

          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" sx={{ mb: 3 }}>
              Resumo Financeiro
            </Typography>

            <Grid2 container spacing={2}>
              <Grid2 item xs={12} md={4}>
                <Box sx={{ p: 2, backgroundColor: "#f5f5f5", borderRadius: 1 }}>
                  <Typography variant="caption" color="text.secondary">
                    Subtotal de Materiais
                  </Typography>
                  <Typography
                    variant="h6"
                    sx={{ fontWeight: "bold", color: "#1976d2" }}
                  >
                    {formatCurrency(orcamento.subtotalMateriais)}
                  </Typography>
                </Box>
              </Grid2>

              <Grid2 item xs={12} md={4}>
                <Box sx={{ p: 2, backgroundColor: "#f5f5f5", borderRadius: 1 }}>
                  <Typography variant="caption" color="text.secondary">
                    Valor Mão de Obra
                  </Typography>
                  <Typography
                    variant="h6"
                    sx={{ fontWeight: "bold", color: "#ed6c02" }}
                  >
                    {formatCurrency(orcamento.valorMaoDeObra)}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    (Materiais × Fator)
                  </Typography>
                </Box>
              </Grid2>

              <Grid2 item xs={12} md={4}>
                <Box sx={{ p: 2, backgroundColor: "#D2691E", borderRadius: 1 }}>
                  <Typography variant="caption" sx={{ color: "white" }}>
                    Valor Total do Orçamento
                  </Typography>
                  <Typography
                    variant="h5"
                    sx={{ fontWeight: "bold", color: "white" }}
                  >
                    {formatCurrency(orcamento.valorTotal)}
                  </Typography>
                  <Typography
                    variant="caption"
                    sx={{ color: "rgba(255,255,255,0.8)" }}
                  >
                    (Mão de Obra + Extras + CPC)
                  </Typography>
                </Box>
              </Grid2>
            </Grid2>
          </Paper>
        </>
      )}

      {tabAtiva === 1 && (
        <Paper sx={{ p: 3 }}>
          <Typography variant="h6" sx={{ mb: 3 }}>
            Histórico de Alterações
          </Typography>

          {loadingHistorico ? (
            <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
              <CircularProgress />
            </Box>
          ) : historico.length === 0 ? (
            <Alert severity="info">
              Nenhuma alteração registrada para este orçamento.
            </Alert>
          ) : (
            <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
              {historico.map((item, index) => {
                let dadosAntigos;
                try {
                  dadosAntigos = JSON.parse(item.snapshotJson);
                } catch {
                  dadosAntigos = null;
                }

                return (
                  <Paper key={item.id} variant="outlined" sx={{ p: 2 }}>
                    <Box
                      sx={{
                        display: "flex",
                        justifyContent: "space-between",
                        mb: 2,
                      }}
                    >
                      <Typography variant="subtitle2" color="primary">
                        Versão #{historico.length - index}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {new Date(item.dataAlteracao).toLocaleString("pt-BR")}
                      </Typography>
                    </Box>

                    {dadosAntigos ? (
                      <Grid2 container spacing={2}>
                        <Grid2 item xs={6} md={3}>
                          <Typography variant="caption" color="text.secondary">
                            Cliente
                          </Typography>
                          <Typography variant="body2">
                            {dadosAntigos.cliente}
                          </Typography>
                        </Grid2>
                        <Grid2 item xs={6} md={3}>
                          <Typography variant="caption" color="text.secondary">
                            Valor Total
                          </Typography>
                          <Typography variant="body2">
                            {formatCurrency(dadosAntigos.valorTotal)}
                          </Typography>
                        </Grid2>
                        <Grid2 item xs={6} md={3}>
                          <Typography variant="caption" color="text.secondary">
                            Status
                          </Typography>
                          <Typography variant="body2">
                            {STATUS_LABELS[dadosAntigos.status] ||
                              dadosAntigos.status}
                          </Typography>
                        </Grid2>
                        <Grid2 item xs={6} md={3}>
                          <Typography variant="caption" color="text.secondary">
                            Itens
                          </Typography>
                          <Typography variant="body2">
                            {dadosAntigos.itens?.length || 0} materiais
                          </Typography>
                        </Grid2>
                      </Grid2>
                    ) : (
                      <Typography variant="body2" color="text.secondary">
                        Dados não disponíveis
                      </Typography>
                    )}
                  </Paper>
                );
              })}
            </Box>
          )}
        </Paper>
      )}

      <IniciarProducaoDialog
        open={dialogOpen}
        onClose={() => setDialogOpen(false)}
        orcamento={orcamento}
        onConfirm={handleIniciarProducao}
      />
    </Box>
  );
}
