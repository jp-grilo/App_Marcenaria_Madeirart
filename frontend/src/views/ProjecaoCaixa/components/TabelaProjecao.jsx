import { useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  IconButton,
  Collapse,
  Box,
  Typography,
  Chip,
  Stack,
  Divider,
} from "@mui/material";
import { KeyboardArrowDown, KeyboardArrowUp } from "@mui/icons-material";
import { formatCurrency } from "../../../utils/formatters";

/**
 * Linha expansível da tabela
 */
function LinhaProjecao({ mes }) {
  const [expandido, setExpandido] = useState(false);

  const getSaldoColor = (valor) => {
    if (valor > 0) return "success.main";
    if (valor < 0) return "error.main";
    return "warning.main";
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

  return (
    <>
      <TableRow sx={{ "& > *": { borderBottom: "unset" } }}>
        <TableCell>
          <IconButton size="small" onClick={() => setExpandido(!expandido)}>
            {expandido ? <KeyboardArrowUp /> : <KeyboardArrowDown />}
          </IconButton>
        </TableCell>
        <TableCell component="th" scope="row">
          <Typography variant="body2" fontWeight="600">
            {meses[mes.mesReferencia - 1]} {mes.anoReferencia}
          </Typography>
        </TableCell>
        <TableCell align="right">{formatCurrency(mes.saldoInicial)}</TableCell>
        <TableCell
          align="right"
          sx={{ color: "success.main", fontWeight: 600 }}
        >
          {formatCurrency(mes.totalEntradasPrevistas)}
        </TableCell>
        <TableCell align="right" sx={{ color: "error.main", fontWeight: 600 }}>
          {formatCurrency(mes.totalSaidasPrevistas)}
        </TableCell>
        <TableCell align="right">
          <Chip
            label={formatCurrency(mes.saldoFinalProjetado)}
            size="small"
            sx={{
              bgcolor: getSaldoColor(mes.saldoFinalProjetado),
              color: "white",
              fontWeight: "bold",
            }}
          />
        </TableCell>
      </TableRow>
      <TableRow>
        <TableCell style={{ paddingBottom: 0, paddingTop: 0 }} colSpan={6}>
          <Collapse in={expandido} timeout="auto" unmountOnExit>
            <Box sx={{ margin: 2 }}>
              <Stack direction="row" spacing={4}>
                {/* Entradas */}
                <Box sx={{ flex: 1 }}>
                  <Typography
                    variant="subtitle2"
                    gutterBottom
                    color="success.main"
                  >
                    Entradas Previstas ({mes.detalhesEntradas?.length || 0})
                  </Typography>
                  <Divider sx={{ mb: 2 }} />
                  {mes.detalhesEntradas && mes.detalhesEntradas.length > 0 ? (
                    <Stack spacing={1}>
                      {mes.detalhesEntradas.map((item, idx) => (
                        <Box
                          key={idx}
                          sx={{
                            p: 1.5,
                            bgcolor: "success.lighter",
                            borderRadius: 1,
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                          }}
                        >
                          <Box>
                            <Typography variant="body2" fontWeight="500">
                              {item.descricao}
                            </Typography>
                            <Typography
                              variant="caption"
                              color="text.secondary"
                            >
                              {new Date(item.data).toLocaleDateString("pt-BR")}{" "}
                              • {item.origem}
                            </Typography>
                          </Box>
                          <Typography
                            variant="body2"
                            fontWeight="600"
                            color="success.main"
                          >
                            {formatCurrency(item.valor)}
                          </Typography>
                        </Box>
                      ))}
                    </Stack>
                  ) : (
                    <Typography variant="body2" color="text.secondary">
                      Nenhuma entrada prevista
                    </Typography>
                  )}
                </Box>

                {/* Saídas */}
                <Box sx={{ flex: 1 }}>
                  <Typography
                    variant="subtitle2"
                    gutterBottom
                    color="error.main"
                  >
                    Saídas Previstas ({mes.detalhesSaidas?.length || 0})
                  </Typography>
                  <Divider sx={{ mb: 2 }} />
                  {mes.detalhesSaidas && mes.detalhesSaidas.length > 0 ? (
                    <Stack spacing={1}>
                      {mes.detalhesSaidas.map((item, idx) => (
                        <Box
                          key={idx}
                          sx={{
                            p: 1.5,
                            bgcolor: "error.lighter",
                            borderRadius: 1,
                            display: "flex",
                            justifyContent: "space-between",
                            alignItems: "center",
                          }}
                        >
                          <Box>
                            <Typography variant="body2" fontWeight="500">
                              {item.descricao}
                            </Typography>
                            <Typography
                              variant="caption"
                              color="text.secondary"
                            >
                              {new Date(item.data).toLocaleDateString("pt-BR")}{" "}
                              • {item.origem}
                            </Typography>
                          </Box>
                          <Typography
                            variant="body2"
                            fontWeight="600"
                            color="error.main"
                          >
                            {formatCurrency(item.valor)}
                          </Typography>
                        </Box>
                      ))}
                    </Stack>
                  ) : (
                    <Typography variant="body2" color="text.secondary">
                      Nenhuma saída prevista
                    </Typography>
                  )}
                </Box>
              </Stack>
            </Box>
          </Collapse>
        </TableCell>
      </TableRow>
    </>
  );
}

/**
 * Tabela com projeção dos próximos meses
 */
export default function TabelaProjecao({ meses }) {
  if (!meses || meses.length === 0) {
    return (
      <Paper sx={{ p: 3, textAlign: "center" }}>
        <Typography color="text.secondary">
          Nenhuma projeção disponível
        </Typography>
      </Paper>
    );
  }

  return (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell width="50px" />
            <TableCell>Mês</TableCell>
            <TableCell align="right">Saldo Inicial</TableCell>
            <TableCell align="right">Entradas</TableCell>
            <TableCell align="right">Saídas</TableCell>
            <TableCell align="right">Saldo Final</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {meses.map((mes, idx) => (
            <LinhaProjecao key={idx} mes={mes} />
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
