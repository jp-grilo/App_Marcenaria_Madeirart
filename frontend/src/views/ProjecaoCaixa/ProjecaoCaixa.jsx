import { useState, useEffect } from "react";
import {
  Box,
  Container,
  Typography,
  Stack,
  CircularProgress,
  Alert,
  Button,
  Paper,
} from "@mui/material";
import { Refresh } from "@mui/icons-material";
import { useSnackbar } from "../../hooks/useSnackbar";
import projecaoCaixaService from "../../services/projecaoCaixaService";
import CardSaldoAtual from "./components/CardSaldoAtual";
import ModalSaldoInicial from "./components/ModalSaldoInicial";
import TabelaProjecao from "./components/TabelaProjecao";
import GraficoProjecao from "./components/GraficoProjecao";

/**
 * Página de Projeção de Caixa
 * Exibe o saldo atual e a projeção para os próximos 2 meses
 */
export default function ProjecaoCaixa() {
  const { showSnackbar } = useSnackbar();
  const [loading, setLoading] = useState(true);
  const [projecao, setProjecao] = useState(null);
  const [saldoInicial, setSaldoInicial] = useState(null);
  const [modalAberto, setModalAberto] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    carregarDados();
  }, []);

  const carregarDados = async () => {
    try {
      setLoading(true);
      setError(null);

      const [projecaoData, saldoInicialData] = await Promise.all([
        projecaoCaixaService.getProjecaoCaixa(),
        projecaoCaixaService.getSaldoInicial(),
      ]);

      setProjecao(projecaoData);
      setSaldoInicial(saldoInicialData);
    } catch (err) {
      console.error("Erro ao carregar projeção:", err);
      setError("Erro ao carregar dados da projeção de caixa");
      showSnackbar("Erro ao carregar dados da projeção", "error");
    } finally {
      setLoading(false);
    }
  };

  const handleSalvarSaldoInicial = async (valor, observacao) => {
    try {
      const resultado = await projecaoCaixaService.setSaldoInicial(
        valor,
        observacao
      );
      setSaldoInicial(resultado);
      showSnackbar("Saldo inicial salvo com sucesso!", "success");
      
      // Recarregar projeção para refletir o novo saldo
      carregarDados();
    } catch (err) {
      console.error("Erro ao salvar saldo inicial:", err);
      showSnackbar("Erro ao salvar saldo inicial", "error");
    }
  };

  if (loading) {
    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          minHeight: "60vh",
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4 }}>
        <Alert
          severity="error"
          action={
            <Button color="inherit" size="small" onClick={carregarDados}>
              Tentar Novamente
            </Button>
          }
        >
          {error}
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Stack spacing={3}>
        {/* Cabeçalho */}
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <Box>
            <Typography variant="h4" fontWeight="bold" gutterBottom>
              Projeção de Caixa
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Visualize o saldo atual e a projeção para os próximos 2 meses
            </Typography>
          </Box>
          <Button
            variant="outlined"
            startIcon={<Refresh />}
            onClick={carregarDados}
          >
            Atualizar
          </Button>
        </Box>

        {/* Card Saldo Atual */}
        <CardSaldoAtual
          saldoAtual={projecao?.saldoAtual || 0}
          saldoInicial={projecao?.saldoInicialCadastrado}
          onDefinirSaldoInicial={() => setModalAberto(true)}
        />

        {/* Informação sobre última atualização */}
        {projecao?.dataCalculo && (
          <Paper sx={{ p: 2, bgcolor: "info.lighter" }}>
            <Typography variant="caption" color="text.secondary">
              Última atualização:{" "}
              {new Date(projecao.dataCalculo).toLocaleString("pt-BR")}
            </Typography>
          </Paper>
        )}

        {/* Gráfico */}
        <GraficoProjecao
          meses={projecao?.mesesProjetados || []}
          saldoAtual={projecao?.saldoAtual || 0}
        />

        {/* Tabela de Projeção */}
        <Box>
          <Typography variant="h6" gutterBottom sx={{ mb: 2 }}>
            Detalhamento Mensal
          </Typography>
          <TabelaProjecao meses={projecao?.mesesProjetados || []} />
        </Box>

        {/* Aviso se não houver saldo inicial */}
        {!saldoInicial && (
          <Alert severity="info">
            <Typography variant="body2">
              <strong>Dica:</strong> Defina um saldo inicial para ter uma
              projeção mais precisa do seu caixa. Clique no botão "Definir Saldo
              Inicial" no card acima.
            </Typography>
          </Alert>
        )}
      </Stack>

      {/* Modal Saldo Inicial */}
      <ModalSaldoInicial
        open={modalAberto}
        onClose={() => setModalAberto(false)}
        onSalvar={handleSalvarSaldoInicial}
        saldoAtual={saldoInicial}
      />
    </Container>
  );
}
