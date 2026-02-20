import { useState, useEffect } from "react";
import { Box, CircularProgress, Alert } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useSnackbar } from "../../../hooks/useSnackbar";
import custoService from "../../../services/custoService";
import FiltrosCustos from "./FiltrosCustos";
import TabelaCustos from "./TabelaCustos";
import ConfirmacaoDialog from "../../../components/ConfirmacaoDialog";

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

  const handleEditar = (custo) => {
    const rota = custo.tipo === "FIXO" ? "fixo" : "variavel";
    navigate(`/custos/${rota}/${custo.id}/editar`);
  };

  return (
    <Box sx={{ p: 3 }}>
      <FiltrosCustos
        filtro={filtro}
        setFiltro={setFiltro}
        getDataInicial={getDataInicial}
        getDataFinal={getDataFinal}
        onLimpar={limparFiltros}
      />

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
        <TabelaCustos
          custos={custos}
          onEditar={handleEditar}
          onDesativarFixo={handleDesativarCustoFixo}
          onReativarFixo={handleReativarCustoFixo}
          onExcluirFixo={handleExcluirCustoFixo}
          onExcluirVariavel={handleExcluirCustoVariavel}
          onAlterarStatus={handleAlterarStatusCusto}
        />
      )}

      <ConfirmacaoDialog
        aberto={dialogConfirmacao.aberto}
        titulo={dialogConfirmacao.titulo}
        mensagem={dialogConfirmacao.mensagem}
        onConfirmar={confirmarAcao}
        onCancelar={fecharDialogConfirmacao}
      />
    </Box>
  );
}
