import { useState } from "react";
import {
  Card,
  CardContent,
  Typography,
  Box,
  IconButton,
  Grid2,
  CircularProgress,
  Alert,
  Tooltip,
} from "@mui/material";
import { ChevronLeft, ChevronRight, CalendarMonth } from "@mui/icons-material";
import ModalDetalheDia from "./ModalDetalheDia";

/**
 * Componente de calendário financeiro
 * Exibe um calendário mensal com indicadores de entradas e saídas por dia
 */
export default function CalendarioFinanceiro({
  calendario,
  loading,
  error,
  onMesChange,
}) {
  const [modalAberto, setModalAberto] = useState(false);
  const [diaSelecionado, setDiaSelecionado] = useState(null);

  if (error) {
    return (
      <Card sx={{ height: "100%" }}>
        <CardContent>
          <Alert severity="error">Erro ao carregar calendário financeiro</Alert>
        </CardContent>
      </Card>
    );
  }

  if (!calendario && !loading) {
    return null;
  }

  const { ano, mes, dias = {} } = calendario || {
    ano: new Date().getFullYear(),
    mes: new Date().getMonth() + 1,
    dias: {},
  };

  const meses = [
    "Janeiro",
    "Fevereiro",
    "Março",
    "Abril",
    "Maio",
    "Junho",
    "Julho",
    "Agosto",
    "Setembro",
    "Outubro",
    "Novembro",
    "Dezembro",
  ];
  const nomeMes = meses[mes - 1] || "";

  const primeiroDia = new Date(ano, mes - 1, 1).getDay();
  const diasNoMes = new Date(ano, mes, 0).getDate();

  const diasSemana = ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"];

  const calendarioDias = [];
  for (let i = 0; i < primeiroDia; i++) {
    calendarioDias.push(null);
  }
  for (let dia = 1; dia <= diasNoMes; dia++) {
    calendarioDias.push(dia);
  }

  const handleMesAnterior = () => {
    let novoMes = mes - 1;
    let novoAno = ano;
    if (novoMes < 1) {
      novoMes = 12;
      novoAno = ano - 1;
    }
    onMesChange(novoMes, novoAno);
  };

  const handleProximoMes = () => {
    let novoMes = mes + 1;
    let novoAno = ano;
    if (novoMes > 12) {
      novoMes = 1;
      novoAno = ano + 1;
    }
    onMesChange(novoMes, novoAno);
  };

  const handleDiaClick = (dia) => {
    const diaDados = dias[dia];
    if (diaDados && (diaDados.temEntradas || diaDados.temSaidas)) {
      setDiaSelecionado({ dia, dados: diaDados });
      setModalAberto(true);
    }
  };

  const handleFecharModal = () => {
    setModalAberto(false);
    setDiaSelecionado(null);
  };

  const renderDia = (dia) => {
    if (!dia) {
      return <Box sx={{ height: 70 }} />;
    }

    const diaDados = dias[dia];
    const temEntradas = diaDados?.temEntradas || false;
    const temSaidas = diaDados?.temSaidas || false;
    const temTransacoes = temEntradas || temSaidas;

    const hoje = new Date();
    const ehHoje =
      dia === hoje.getDate() &&
      mes === hoje.getMonth() + 1 &&
      ano === hoje.getFullYear();

    return (
      <Tooltip
        title={temTransacoes ? `Clique para ver detalhes` : "Nenhuma transação"}
        arrow
      >
        <Box
          onClick={() => temTransacoes && handleDiaClick(dia)}
          sx={{
            height: 70,
            border: "1px solid",
            borderColor: ehHoje ? "primary.main" : "divider",
            borderRadius: 1,
            p: 1,
            cursor: temTransacoes ? "pointer" : "default",
            backgroundColor: ehHoje
              ? "rgba(25, 118, 210, 0.05)"
              : "background.paper",
            transition: "all 0.2s",
            "&:hover": temTransacoes
              ? {
                  backgroundColor: "action.hover",
                  transform: "scale(1.02)",
                  boxShadow: 1,
                }
              : {},
          }}
        >
          <Typography
            variant="caption"
            sx={{
              display: "block",
              fontWeight: ehHoje ? "bold" : "normal",
              color: ehHoje ? "primary.main" : "text.primary",
            }}
          >
            {dia}
          </Typography>

          {/* Indicadores de transações */}
          {temTransacoes && (
            <Box
              sx={{
                display: "flex",
                gap: 0.5,
                mt: 0.5,
                flexWrap: "wrap",
              }}
            >
              {temEntradas && (
                <Box
                  sx={{
                    width: 8,
                    height: 8,
                    borderRadius: "50%",
                    backgroundColor: "#2e7d32",
                  }}
                />
              )}
              {temSaidas && (
                <Box
                  sx={{
                    width: 8,
                    height: 8,
                    borderRadius: "50%",
                    backgroundColor: "#d32f2f",
                  }}
                />
              )}
            </Box>
          )}
        </Box>
      </Tooltip>
    );
  };

  return (
    <>
      <Card sx={{ height: "100%", position: "relative" }}>
        <CardContent>
          {/* Cabeçalho com navegação */}
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 3,
            }}
          >
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <CalendarMonth sx={{ color: "text.secondary" }} />
              <Typography variant="h6" sx={{ fontWeight: 600 }}>
                Calendário Financeiro
              </Typography>
            </Box>

            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <IconButton 
                onClick={handleMesAnterior} 
                size="small"
                disabled={loading}
              >
                <ChevronLeft />
              </IconButton>
              <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                <Typography
                  variant="subtitle1"
                  sx={{ minWidth: 150, textAlign: "center", fontWeight: 500 }}
                >
                  {nomeMes} {ano}
                </Typography>
                {loading && <CircularProgress size={16} />}
              </Box>
              <IconButton 
                onClick={handleProximoMes} 
                size="small"
                disabled={loading}
              >
                <ChevronRight />
              </IconButton>
            </Box>
          </Box>

          {/* Legenda */}
          <Box
            sx={{
              display: "flex",
              gap: 2,
              mb: 2,
              justifyContent: "center",
              flexWrap: "wrap",
            }}
          >
            <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
              <Box
                sx={{
                  width: 12,
                  height: 12,
                  borderRadius: "50%",
                  backgroundColor: "#2e7d32",
                }}
              />
              <Typography variant="caption">Entradas</Typography>
            </Box>
            <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
              <Box
                sx={{
                  width: 12,
                  height: 12,
                  borderRadius: "50%",
                  backgroundColor: "#d32f2f",
                }}
              />
              <Typography variant="caption">Saídas</Typography>
            </Box>
          </Box>

          {/* Grid do calendário */}
          <Grid2 container spacing={0.5}>
            {/* Cabeçalho dos dias da semana */}
            {diasSemana.map((dia, index) => (
              <Grid2 key={`header-${index}`} size={{ xs: 12 / 7 }}>
                <Typography
                  variant="caption"
                  sx={{
                    display: "block",
                    textAlign: "center",
                    fontWeight: "bold",
                    color: "text.secondary",
                    mb: 1,
                  }}
                >
                  {dia}
                </Typography>
              </Grid2>
            ))}

            {/* Dias do mês */}
            {calendarioDias.map((dia, index) => (
              <Grid2 key={`dia-${index}`} size={{ xs: 12 / 7 }}>
                {renderDia(dia)}
              </Grid2>
            ))}
          </Grid2>
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
      </Card>

      {/* Modal de detalhes do dia */}
      {diaSelecionado && (
        <ModalDetalheDia
          open={modalAberto}
          onClose={handleFecharModal}
          diaDados={diaSelecionado.dados}
          data={`${diaSelecionado.dia}/${mes}/${ano}`}
        />
      )}
    </>
  );
}
