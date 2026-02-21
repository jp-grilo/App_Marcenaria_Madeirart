import { Paper, Box, Typography } from "@mui/material";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  ReferenceLine,
} from "recharts";
import { formatCurrency } from "../../../utils/formatters";

/**
 * Tooltip customizado para o gráfico
 */
const CustomTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    return (
      <Box
        sx={{
          bgcolor: "background.paper",
          p: 2,
          border: 1,
          borderColor: "divider",
          borderRadius: 1,
          boxShadow: 2,
        }}
      >
        <Typography variant="body2" fontWeight="600">
          {payload[0].payload.nome}
        </Typography>
        <Typography variant="body2" color="primary">
          Saldo: {formatCurrency(payload[0].value)}
        </Typography>
      </Box>
    );
  }
  return null;
};

/**
 * Gráfico de linha mostrando a evolução do saldo projetado
 */
export default function GraficoProjecao({ meses, saldoAtual }) {
  if (!meses || meses.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: "center" }}>
        <Typography color="text.secondary">
          Dados insuficientes para exibir o gráfico
        </Typography>
      </Paper>
    );
  }

  const mesesNomes = [
    "Jan",
    "Fev",
    "Mar",
    "Abr",
    "Mai",
    "Jun",
    "Jul",
    "Ago",
    "Set",
    "Out",
    "Nov",
    "Dez",
  ];

  const dados = [
    {
      nome: "Atual",
      saldo: saldoAtual,
      mesReferencia: 0,
    },
    ...meses.map((mes) => ({
      nome: mesesNomes[mes.mesReferencia - 1],
      saldo: mes.saldoFinalProjetado,
      mesReferencia: mes.mesReferencia,
    })),
  ];

  return (
    <Paper sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom>
        Evolução do Saldo
      </Typography>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart
          data={dados}
          margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
        >
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="nome" />
          <YAxis
            tickFormatter={(value) => `R$ ${(value / 1000).toFixed(0)}k`}
          />
          <Tooltip content={<CustomTooltip />} />
          <ReferenceLine
            y={0}
            stroke="#d32f2f"
            strokeDasharray="3 3"
            label={{ value: "Zero", position: "right" }}
          />
          <Line
            type="monotone"
            dataKey="saldo"
            stroke="#D2691E"
            strokeWidth={3}
            dot={{ fill: "#D2691E", r: 6 }}
            activeDot={{ r: 8 }}
          />
        </LineChart>
      </ResponsiveContainer>
    </Paper>
  );
}
