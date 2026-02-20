import {
  Card,
  CardContent,
  Typography,
  Box,
  List,
  ListItem,
  Chip,
  Stack,
  Divider,
  CircularProgress,
  Alert,
} from "@mui/material";
import {
  Assignment,
  TrendingUp,
  CalendarMonth,
  Warning,
} from "@mui/icons-material";
import { useNavigate } from "react-router-dom";

/**
 * Componente que exibe o resumo de orçamentos no dashboard
 * Mostra totais de orçamentos ativos, em produção e próximos da entrega
 */
export default function CardResumoOrcamentos({ resumo, loading, error }) {
  const navigate = useNavigate();

  if (loading) {
    return (
      <Card sx={{ height: "100%" }}>
        <CardContent>
          <Box
            display="flex"
            justifyContent="center"
            alignItems="center"
            minHeight={300}
          >
            <CircularProgress />
          </Box>
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card sx={{ height: "100%" }}>
        <CardContent>
          <Alert severity="error">Erro ao carregar resumo de orçamentos</Alert>
        </CardContent>
      </Card>
    );
  }

  if (!resumo) {
    return null;
  }

  const {
    totalOrcamentosAtivos = 0,
    totalEmProducao = 0,
    orcamentosProximosEntrega = [],
  } = resumo;

  /**
   * Calcula quantos dias faltam para a entrega
   */
  const getDiasParaEntrega = (dataEntrega) => {
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const entrega = new Date(dataEntrega);
    entrega.setHours(0, 0, 0, 0);
    const diffTime = entrega - hoje;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  /**
   * Formata data para dd/MM/yyyy
   */
  const formatarData = (data) => {
    const date = new Date(data);
    const dia = String(date.getDate()).padStart(2, "0");
    const mes = String(date.getMonth() + 1).padStart(2, "0");
    const ano = date.getFullYear();
    return `${dia}/${mes}/${ano}`;
  };

  /**
   * Retorna a cor do chip baseado nos dias restantes
   */
  const getCorUrgencia = (dias) => {
    if (dias < 0) return "error";
    if (dias <= 2) return "error";
    if (dias <= 5) return "warning";
    return "success";
  };

  return (
    <Card sx={{ height: "100%" }}>
      <CardContent>
        <Typography variant="h6" gutterBottom sx={{ fontWeight: 600 }}>
          Resumo de Orçamentos
        </Typography>

        {/* Estatísticas principais */}
        <Stack spacing={2} sx={{ mb: 3 }}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              gap: 2,
              p: 2,
              backgroundColor: "rgba(210, 105, 30, 0.1)",
              borderRadius: 2,
            }}
          >
            <Assignment sx={{ fontSize: 32, color: "#D2691E" }} />
            <Box>
              <Typography variant="body2" color="text.secondary">
                Orçamentos em Espera
              </Typography>
              <Typography variant="h4" sx={{ fontWeight: "bold" }}>
                {totalOrcamentosAtivos}
              </Typography>
            </Box>
          </Box>

          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              gap: 2,
              p: 2,
              backgroundColor: "rgba(25, 118, 210, 0.1)",
              borderRadius: 2,
            }}
          >
            <TrendingUp sx={{ fontSize: 32, color: "#1976d2" }} />
            <Box>
              <Typography variant="body2" color="text.secondary">
                Em Produção
              </Typography>
              <Typography variant="h4" sx={{ fontWeight: "bold" }}>
                {totalEmProducao}
              </Typography>
            </Box>
          </Box>
        </Stack>

        <Divider sx={{ my: 2 }} />

        {/* Lista de orçamentos próximos da entrega */}
        <Box>
          <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 2 }}>
            <CalendarMonth sx={{ color: "text.secondary" }} />
            <Typography variant="subtitle2" color="text.secondary">
              Próximos da Entrega (5 dias)
            </Typography>
          </Box>

          {orcamentosProximosEntrega.length === 0 ? (
            <Alert severity="info" sx={{ mt: 1 }}>
              Nenhum orçamento próximo da entrega nos próximos 5 dias
            </Alert>
          ) : (
            <List sx={{ p: 0 }}>
              {orcamentosProximosEntrega.map((orcamento, index) => {
                const diasRestantes = getDiasParaEntrega(
                  orcamento.previsaoEntrega,
                );
                const corUrgencia = getCorUrgencia(diasRestantes);
                const dataFormatada = formatarData(orcamento.previsaoEntrega);

                return (
                  <Box key={orcamento.id}>
                    {index > 0 && <Divider sx={{ my: 1 }} />}
                    <ListItem
                      onClick={() => navigate(`/orcamentos/${orcamento.id}`)}
                      sx={{
                        px: 0,
                        py: 1,
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "flex-start",
                        cursor: "pointer",
                        borderRadius: 1,
                        transition: "all 0.2s",
                        "&:hover": {
                          backgroundColor: "action.hover",
                          transform: "translateX(4px)",
                        },
                      }}
                    >
                      <Box
                        sx={{
                          display: "flex",
                          justifyContent: "space-between",
                          width: "100%",
                          mb: 1,
                        }}
                      >
                        <Typography
                          variant="body2"
                          sx={{ fontWeight: 600, flex: 1 }}
                        >
                          {orcamento.cliente}
                        </Typography>
                        <Chip
                          size="small"
                          label={
                            diasRestantes < 0
                              ? "Atrasado"
                              : diasRestantes === 0
                                ? "Hoje"
                                : diasRestantes === 1
                                  ? "Amanhã"
                                  : `${diasRestantes} dias`
                          }
                          color={corUrgencia}
                          icon={
                            diasRestantes <= 2 ? (
                              <Warning sx={{ fontSize: 16 }} />
                            ) : undefined
                          }
                        />
                      </Box>
                      <Typography
                        variant="caption"
                        color="text.secondary"
                        sx={{ mb: 0.5 }}
                      >
                        {orcamento.moveis}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        Entrega: {dataFormatada}
                      </Typography>
                    </ListItem>
                  </Box>
                );
              })}
            </List>
          )}
        </Box>
      </CardContent>
    </Card>
  );
}
