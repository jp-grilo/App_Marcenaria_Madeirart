import { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Paper,
  TextField,
  Grid2,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  IconButton,
  Tooltip,
  CircularProgress,
  Alert,
  Button,
  FormControlLabel,
  Checkbox,
  FormGroup,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
} from "@mui/material";
import {
  Edit,
  Delete,
  ToggleOff,
  ToggleOn,
  FilterList,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import { useSnackbar } from "../../../hooks/useSnackbar";
import custoService from "../../../services/custoService";
import { formatCurrency, formatDate } from "../../../utils/formatters";
import {
  STATUS_CUSTO_LABELS,
  STATUS_CUSTO_CORES,
} from "../../../utils/constants";

export default function CustosUnificadosList() {
  const navigate = useNavigate();
  const { showError, showSuccess } = useSnackbar();

  const [custos, setCustos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [dialogConfirmacao, setDialogConfirmacao] = useState({
    aberto: false,
    titulo: "",
    mensagem: "",
    onConfirmar: null,
  });

  const abrirDialogConfirmacao = (titulo, mensagem, onConfirmar) => {
    setDialogConfirmacao({
      aberto: true,
      titulo,
      mensagem,
      onConfirmar,
    });
  };

  const fecharDialogConfirmacao = () => {
    setDialogConfirmacao({
      aberto: false,
      titulo: "",
      mensagem: "",
      onConfirmar: null,
    });
  };

  const confirmarAcao = () => {
    if (dialogConfirmacao.onConfirmar) {
      dialogConfirmacao.onConfirmar();
    }
    fecharDialogConfirmacao();
  };

  const getDataInicial = () => {
    const data = new Date();
    data.setDate(data.getDate() - 30);
    return data.toISOString().split("T")[0];
  };

  const getDataFinal = () => {
    return new Date().toISOString().split("T")[0];
  };

  const [filtro, setFiltro] = useState({
    mostrarFixos: true,
    mostrarVariaveis: true,
    dataInicio: getDataInicial(),
    dataFim: getDataFinal(),
    statusPendente: true,
    statusPago: true,
    statusAtrasado: true,
  });

  useEffect(() => {
    carregarCustos();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filtro]);

  const calcularDataReferenciaCustoFixo = (diaVencimento) => {
    const hoje = new Date();
    const ano = hoje.getFullYear();
    const mes = String(hoje.getMonth() + 1).padStart(2, "0"); // Mês de 1-12
    const dia = String(diaVencimento).padStart(2, "0");
    return `${ano}-${mes}-${dia}`;
  };

  const carregarCustos = async () => {
    try {
      setLoading(true);
      setError(null);

      let todosOsCustos = [];

      if (filtro.mostrarFixos) {
        const paramsFixos = { apenasAtivos: true };

        const custosFixos = await custoService.listarCustosFixos(paramsFixos);

        let custosFixosFiltrados = custosFixos;
        if (filtro.dataInicio && filtro.dataFim) {
          custosFixosFiltrados = filtrarCustosFixosPorPeriodo(
            custosFixos,
            filtro.dataInicio,
            filtro.dataFim,
          );
        }

        const custosFixosFormatados = custosFixosFiltrados.map((c) => ({
          ...c,
          tipo: "FIXO",
          dataReferencia: calcularDataReferenciaCustoFixo(c.diaVencimento),
        }));
        todosOsCustos = [...todosOsCustos, ...custosFixosFormatados];
      }

      if (filtro.mostrarVariaveis) {
        const paramsVariaveis = {};
        if (filtro.dataInicio) paramsVariaveis.dataInicio = filtro.dataInicio;
        if (filtro.dataFim) paramsVariaveis.dataFim = filtro.dataFim;

        const custosVariaveis =
          await custoService.listarCustosVariaveis(paramsVariaveis);
        const custosVariaveisFormatados = custosVariaveis.map((c) => ({
          ...c,
          tipo: "VARIAVEL",
          dataReferencia: c.dataLancamento,
        }));
        todosOsCustos = [...todosOsCustos, ...custosVariaveisFormatados];
      }

      todosOsCustos.sort((a, b) => {
        return new Date(b.dataReferencia) - new Date(a.dataReferencia);
      });

      const statusSelecionados = [];
      if (filtro.statusPendente) statusSelecionados.push("PENDENTE");
      if (filtro.statusPago) statusSelecionados.push("PAGO");
      if (filtro.statusAtrasado) statusSelecionados.push("ATRASADO");

      if (statusSelecionados.length > 0) {
        todosOsCustos = todosOsCustos.filter((custo) =>
          statusSelecionados.includes(custo.status),
        );
      }

      setCustos(todosOsCustos);
    } catch (err) {
      const mensagem = "Erro ao carregar custos.";
      setError(mensagem);
      showError(mensagem);
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  /**
   * Filtra custos fixos baseado no período de datas
   * Verifica se o diaVencimento está dentro dos dias do período
   */
  const filtrarCustosFixosPorPeriodo = (custos, dataInicio, dataFim) => {
    const inicio = new Date(dataInicio);
    const fim = new Date(dataFim);

    const diasDoMesNoPeriodo = new Set();

    const dataAtual = new Date(inicio);
    while (dataAtual <= fim) {
      diasDoMesNoPeriodo.add(dataAtual.getDate());
      dataAtual.setDate(dataAtual.getDate() + 1);
    }

    return custos.filter((custo) =>
      diasDoMesNoPeriodo.has(custo.diaVencimento),
    );
  };

  const handleDesativarCustoFixo = (id) => {
    abrirDialogConfirmacao(
      "Desativar Custo Fixo",
      "Deseja desativar este custo fixo?",
      async () => {
        try {
          await custoService.desativarCustoFixo(id);
          showSuccess("Custo fixo desativado com sucesso!");
          carregarCustos();
        } catch (err) {
          showError("Erro ao desativar custo fixo.");
          console.error(err);
        }
      },
    );
  };

  const handleReativarCustoFixo = async (id) => {
    try {
      await custoService.reativarCustoFixo(id);
      showSuccess("Custo fixo reativado com sucesso!");
      carregarCustos();
    } catch (err) {
      showError("Erro ao reativar custo fixo.");
      console.error(err);
    }
  };

  const handleExcluirCustoFixo = (id) => {
    abrirDialogConfirmacao(
      "Excluir Custo Fixo",
      "Deseja excluir permanentemente este custo fixo?",
      async () => {
        try {
          await custoService.excluirCustoFixo(id);
          showSuccess("Custo fixo excluído com sucesso!");
          carregarCustos();
        } catch (err) {
          showError("Erro ao excluir custo fixo.");
          console.error(err);
        }
      },
    );
  };

  const handleExcluirCustoVariavel = (id) => {
    abrirDialogConfirmacao(
      "Excluir Custo Variável",
      "Deseja excluir este custo variável?",
      async () => {
        try {
          await custoService.excluirCustoVariavel(id);
          showSuccess("Custo variável excluído com sucesso!");
          carregarCustos();
        } catch (err) {
          showError("Erro ao excluir custo variável.");
          console.error(err);
        }
      },
    );
  };

  const handleAlterarStatusCusto = (custo) => {
    const estaPago = custo.status === "PAGO";
    const novoStatus = estaPago ? "PENDENTE" : "PAGO";
    const mensagem = estaPago
      ? "Desmarcar custo como pago?"
      : "Marcar custo como pago?";
    const titulo = estaPago ? "Desmarcar como Pago" : "Marcar como Pago";

    abrirDialogConfirmacao(titulo, mensagem, async () => {
      try {
        if (custo.tipo === "FIXO") {
          if (novoStatus === "PAGO") {
            await custoService.marcarCustoFixoComoPago(custo.id);
            showSuccess("Custo fixo marcado como PAGO!");
          } else {
            await custoService.marcarCustoFixoComoPendente(custo.id);
            showSuccess("Custo fixo desmarcado como PAGO!");
          }
        } else {
          if (novoStatus === "PAGO") {
            await custoService.marcarCustoVariavelComoPago(custo.id);
            showSuccess("Custo variável marcado como PAGO!");
          } else {
            await custoService.marcarCustoVariavelComoPendente(custo.id);
            showSuccess("Custo variável desmarcado como PAGO!");
          }
        }

        carregarCustos();
      } catch (err) {
        showError("Erro ao alterar status do custo.");
        console.error(err);
      }
    });
  };

  const limparFiltros = () => {
    setFiltro({
      mostrarFixos: true,
      mostrarVariaveis: true,
      dataInicio: getDataInicial(),
      dataFim: getDataFinal(),
      statusPendente: true,
      statusPago: true,
      statusAtrasado: true,
    });
  };

  return (
    <Box sx={{ p: 3 }}>
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
          {/* Tipo de Custo */}
          <Grid2 size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600 }}>
              Tipo de Custo
            </Typography>
            <FormGroup>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={filtro.mostrarFixos}
                    onChange={(e) =>
                      setFiltro({
                        ...filtro,
                        mostrarFixos: e.target.checked,
                      })
                    }
                  />
                }
                label="Custos Fixos"
              />
              <FormControlLabel
                control={
                  <Checkbox
                    checked={filtro.mostrarVariaveis}
                    onChange={(e) => {
                      const novoValor = e.target.checked;
                      setFiltro({
                        ...filtro,
                        mostrarVariaveis: novoValor,
                        dataInicio: novoValor
                          ? filtro.dataInicio
                          : getDataInicial(),
                        dataFim: novoValor ? filtro.dataFim : getDataFinal(),
                      });
                    }}
                  />
                }
                label="Custos Variáveis"
              />
            </FormGroup>
          </Grid2>

          {/* Status de Pagamento */}
          <Grid2 size={{ xs: 12, sm: 6, md: 3 }}>
            <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600 }}>
              Status de Pagamento
            </Typography>
            <FormGroup>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={filtro.statusPendente}
                    onChange={(e) =>
                      setFiltro({
                        ...filtro,
                        statusPendente: e.target.checked,
                      })
                    }
                  />
                }
                label="Pendente"
              />
              <FormControlLabel
                control={
                  <Checkbox
                    checked={filtro.statusPago}
                    onChange={(e) =>
                      setFiltro({
                        ...filtro,
                        statusPago: e.target.checked,
                      })
                    }
                  />
                }
                label="Pago"
              />
              <FormControlLabel
                control={
                  <Checkbox
                    checked={filtro.statusAtrasado}
                    onChange={(e) =>
                      setFiltro({
                        ...filtro,
                        statusAtrasado: e.target.checked,
                      })
                    }
                  />
                }
                label="Atrasado"
              />
            </FormGroup>
          </Grid2>

          {/* Período */}
          <Grid2 size={{ xs: 12, sm: 6, md: 4 }}>
            <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 600 }}>
              Período
            </Typography>
            <TextField
              fullWidth
              size="small"
              type="date"
              label="Data Início"
              InputLabelProps={{ shrink: true }}
              value={filtro.dataInicio}
              onChange={(e) =>
                setFiltro({
                  ...filtro,
                  dataInicio: e.target.value,
                })
              }
              sx={{ mb: 1 }}
            />
            <TextField
              fullWidth
              size="small"
              type="date"
              label="Data Fim"
              InputLabelProps={{ shrink: true }}
              value={filtro.dataFim}
              onChange={(e) =>
                setFiltro({
                  ...filtro,
                  dataFim: e.target.value,
                })
              }
            />
          </Grid2>

          {/* Botão Limpar */}
          <Grid2
            size={{ md: 2 }}
            sx={{
              display: "flex",
              paddingTop: 3.7,
            }}
          >
            <Button
              fullWidth
              variant="outlined"
              onClick={limparFiltros}
              sx={{ height: "40px", fontSize: "0.8rem" }}
            >
              Limpar Filtros
            </Button>
          </Grid2>
        </Grid2>
      </Paper>

      {/* Tabela Unificada */}
      {loading ? (
        <Box sx={{ display: "flex", justifyContent: "center", py: 4 }}>
          <CircularProgress />
        </Box>
      ) : error ? (
        <Alert severity="error">{error}</Alert>
      ) : custos.length === 0 ? (
        <Alert severity="info">Nenhum custo encontrado.</Alert>
      ) : (
        <TableContainer component={Paper}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>Tipo</TableCell>
                <TableCell>Nome</TableCell>
                <TableCell>Data</TableCell>
                <TableCell align="right">Valor</TableCell>
                <TableCell align="center">Status</TableCell>
                <TableCell align="center">Ações</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {custos.map((custo) => (
                <TableRow key={`${custo.tipo}-${custo.id}`}>
                  <TableCell>
                    <Chip
                      label={custo.tipo === "FIXO" ? "Fixo" : "Variável"}
                      color={custo.tipo === "FIXO" ? "primary" : "secondary"}
                      size="small"
                      variant="outlined"
                    />
                  </TableCell>
                  <TableCell>{custo.nome}</TableCell>
                  <TableCell>{formatDate(custo.dataReferencia)}</TableCell>
                  <TableCell align="right">
                    {formatCurrency(custo.valor)}
                  </TableCell>
                  <TableCell align="center">
                    <Tooltip
                      title={
                        custo.status === "PAGO"
                          ? "Clique para desmarcar como PAGO"
                          : "Clique para marcar como PAGO"
                      }
                    >
                      <Chip
                        label={STATUS_CUSTO_LABELS[custo.status]}
                        color={STATUS_CUSTO_CORES[custo.status]}
                        size="small"
                        onClick={() => handleAlterarStatusCusto(custo)}
                        sx={{ cursor: "pointer" }}
                      />
                    </Tooltip>
                  </TableCell>
                  <TableCell align="center">
                    <Tooltip title="Editar">
                      <IconButton
                        size="small"
                        onClick={() =>
                          navigate(
                            `/custos/${custo.tipo === "FIXO" ? "fixo" : "variavel"}/${custo.id}/editar`,
                          )
                        }
                      >
                        <Edit fontSize="small" />
                      </IconButton>
                    </Tooltip>

                    {custo.tipo === "FIXO" ? (
                      <>
                        {custo.ativo ? (
                          <Tooltip title="Desativar">
                            <IconButton
                              size="small"
                              onClick={() => handleDesativarCustoFixo(custo.id)}
                            >
                              <ToggleOff fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        ) : (
                          <Tooltip title="Reativar">
                            <IconButton
                              size="small"
                              color="success"
                              onClick={() => handleReativarCustoFixo(custo.id)}
                            >
                              <ToggleOn fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        )}
                        <Tooltip title="Excluir Permanentemente">
                          <IconButton
                            size="small"
                            color="error"
                            onClick={() => handleExcluirCustoFixo(custo.id)}
                          >
                            <Delete fontSize="small" />
                          </IconButton>
                        </Tooltip>
                      </>
                    ) : (
                      <Tooltip title="Excluir">
                        <IconButton
                          size="small"
                          color="error"
                          onClick={() => handleExcluirCustoVariavel(custo.id)}
                        >
                          <Delete fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    )}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* Dialog de Confirmação */}
      <Dialog
        open={dialogConfirmacao.aberto}
        onClose={fecharDialogConfirmacao}
        aria-labelledby="dialog-titulo"
        aria-describedby="dialog-descricao"
      >
        <DialogTitle id="dialog-titulo">{dialogConfirmacao.titulo}</DialogTitle>
        <DialogContent>
          <DialogContentText id="dialog-descricao">
            {dialogConfirmacao.mensagem}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={fecharDialogConfirmacao} color="inherit">
            Cancelar
          </Button>
          <Button
            onClick={confirmarAcao}
            color="primary"
            variant="contained"
            autoFocus
          >
            Confirmar
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
