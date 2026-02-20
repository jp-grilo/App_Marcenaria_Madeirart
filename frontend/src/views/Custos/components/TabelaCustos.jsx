import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  IconButton,
  Tooltip,
  Paper,
} from "@mui/material";
import { Edit, Delete, ToggleOff, ToggleOn } from "@mui/icons-material";
import { formatCurrency, formatDate } from "../../../utils/formatters";
import {
  STATUS_CUSTO_LABELS,
  STATUS_CUSTO_CORES,
} from "../../../utils/constants";

export default function TabelaCustos({
  custos,
  onEditar,
  onDesativarFixo,
  onReativarFixo,
  onExcluirFixo,
  onExcluirVariavel,
  onAlterarStatus,
}) {
  return (
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
              <TableCell align="right">{formatCurrency(custo.valor)}</TableCell>
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
                    onClick={() => onAlterarStatus(custo)}
                    sx={{ cursor: "pointer" }}
                  />
                </Tooltip>
              </TableCell>
              <TableCell align="center">
                <Tooltip title="Editar">
                  <IconButton size="small" onClick={() => onEditar(custo)}>
                    <Edit fontSize="small" />
                  </IconButton>
                </Tooltip>

                {custo.tipo === "FIXO" ? (
                  <>
                    {custo.ativo ? (
                      <Tooltip title="Desativar">
                        <IconButton
                          size="small"
                          onClick={() => onDesativarFixo(custo.id)}
                        >
                          <ToggleOff fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    ) : (
                      <Tooltip title="Reativar">
                        <IconButton
                          size="small"
                          color="success"
                          onClick={() => onReativarFixo(custo.id)}
                        >
                          <ToggleOn fontSize="small" />
                        </IconButton>
                      </Tooltip>
                    )}
                    <Tooltip title="Excluir Permanentemente">
                      <IconButton
                        size="small"
                        color="error"
                        onClick={() => onExcluirFixo(custo.id)}
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
                      onClick={() => onExcluirVariavel(custo.id)}
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
  );
}
