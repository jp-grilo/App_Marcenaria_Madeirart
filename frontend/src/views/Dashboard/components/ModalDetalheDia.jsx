import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Box,
  Typography,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Chip,
  Divider,
} from "@mui/material";
import {
  TrendingUp,
  TrendingDown,
  Close as CloseIcon,
} from "@mui/icons-material";
import { formatCurrency } from "../../../utils/formatters";

/**
 * Modal que exibe os detalhes das transações de um dia específico
 */
export default function ModalDetalheDia({ open, onClose, diaDados, data }) {
  if (!diaDados) {
    return null;
  }

  const { entradas = [], saidas = [] } = diaDados;

  const totalEntradas = entradas.reduce((sum, t) => sum + (t.valor || 0), 0);
  const totalSaidas = saidas.reduce((sum, t) => sum + (t.valor || 0), 0);
  const saldoDia = totalEntradas - totalSaidas;

  const getStatusColor = (status) => {
    switch (status?.toUpperCase()) {
      case "PAGO":
      case "RECEBIDO":
        return "success";
      case "PENDENTE":
        return "warning";
      case "ATRASADO":
        return "error";
      default:
        return "default";
    }
  };

  const getOrigemLabel = (origem) => {
    switch (origem) {
      case "PARCELA":
        return "Parcela";
      case "CUSTO_FIXO":
        return "Custo Fixo";
      case "CUSTO_VARIAVEL":
        return "Custo Variável";
      default:
        return origem;
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="md" fullWidth>
      <DialogTitle>
        <Box
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <Typography variant="h6">{data}</Typography>
        </Box>
      </DialogTitle>

      <DialogContent>
        {/* Resumo do Dia */}
        <Box
          sx={{
            p: 2,
            mb: 3,
            backgroundColor: "background.default",
            borderRadius: 2,
          }}
        >
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-around",
              flexWrap: "wrap",
              gap: 2,
            }}
          >
            <Box sx={{ textAlign: "center" }}>
              <Typography variant="caption" color="text.secondary">
                Entradas
              </Typography>
              <Typography
                variant="h6"
                sx={{ color: "#2e7d32", fontWeight: "bold" }}
              >
                {formatCurrency(totalEntradas)}
              </Typography>
            </Box>
            <Box sx={{ textAlign: "center" }}>
              <Typography variant="caption" color="text.secondary">
                Saídas
              </Typography>
              <Typography
                variant="h6"
                sx={{ color: "#d32f2f", fontWeight: "bold" }}
              >
                {formatCurrency(totalSaidas)}
              </Typography>
            </Box>
            <Box sx={{ textAlign: "center" }}>
              <Typography variant="caption" color="text.secondary">
                Saldo
              </Typography>
              <Typography
                variant="h6"
                sx={{
                  color: saldoDia >= 0 ? "#2e7d32" : "#d32f2f",
                  fontWeight: "bold",
                }}
              >
                {formatCurrency(saldoDia)}
              </Typography>
            </Box>
          </Box>
        </Box>

        {/* Lista de Entradas */}
        {entradas.length > 0 && (
          <>
            <Typography
              variant="subtitle1"
              sx={{ fontWeight: 600, mb: 1, display: "flex", gap: 1 }}
            >
              <TrendingUp sx={{ color: "#2e7d32" }} />
              Entradas ({entradas.length})
            </Typography>
            <List dense>
              {entradas.map((transacao, index) => (
                <ListItem
                  key={`entrada-${index}`}
                  sx={{
                    backgroundColor: "rgba(46, 125, 50, 0.05)",
                    borderRadius: 1,
                    mb: 1,
                  }}
                >
                  <ListItemIcon>
                    <TrendingUp sx={{ color: "#2e7d32" }} />
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Box
                        sx={{
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "center",
                          flexWrap: "wrap",
                          gap: 1,
                        }}
                      >
                        <Typography variant="body2" sx={{ fontWeight: 500 }}>
                          {transacao.descricao}
                        </Typography>
                        <Typography
                          variant="body2"
                          sx={{ color: "#2e7d32", fontWeight: "bold" }}
                        >
                          {formatCurrency(transacao.valor)}
                        </Typography>
                      </Box>
                    }
                    secondary={
                      <Box sx={{ display: "flex", gap: 1, mt: 0.5 }}>
                        <Chip
                          label={getOrigemLabel(transacao.origem)}
                          size="small"
                          variant="outlined"
                        />
                        <Chip
                          label={transacao.status}
                          size="small"
                          color={getStatusColor(transacao.status)}
                        />
                      </Box>
                    }
                  />
                </ListItem>
              ))}
            </List>
          </>
        )}

        {/* Divider entre entradas e saídas */}
        {entradas.length > 0 && saidas.length > 0 && <Divider sx={{ my: 3 }} />}

        {/* Lista de Saídas */}
        {saidas.length > 0 && (
          <>
            <Typography
              variant="subtitle1"
              sx={{ fontWeight: 600, mb: 1, display: "flex", gap: 1 }}
            >
              <TrendingDown sx={{ color: "#d32f2f" }} />
              Saídas ({saidas.length})
            </Typography>
            <List dense>
              {saidas.map((transacao, index) => (
                <ListItem
                  key={`saida-${index}`}
                  sx={{
                    backgroundColor: "rgba(211, 47, 47, 0.05)",
                    borderRadius: 1,
                    mb: 1,
                  }}
                >
                  <ListItemIcon>
                    <TrendingDown sx={{ color: "#d32f2f" }} />
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Box
                        sx={{
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "center",
                          flexWrap: "wrap",
                          gap: 1,
                        }}
                      >
                        <Typography variant="body2" sx={{ fontWeight: 500 }}>
                          {transacao.descricao}
                        </Typography>
                        <Typography
                          variant="body2"
                          sx={{ color: "#d32f2f", fontWeight: "bold" }}
                        >
                          {formatCurrency(transacao.valor)}
                        </Typography>
                      </Box>
                    }
                    secondary={
                      <Box sx={{ display: "flex", gap: 1, mt: 0.5 }}>
                        <Chip
                          label={getOrigemLabel(transacao.origem)}
                          size="small"
                          variant="outlined"
                        />
                        <Chip
                          label={transacao.status}
                          size="small"
                          color={getStatusColor(transacao.status)}
                        />
                      </Box>
                    }
                  />
                </ListItem>
              ))}
            </List>
          </>
        )}

        {/* Mensagem quando não há transações */}
        {entradas.length === 0 && saidas.length === 0 && (
          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ textAlign: "center", py: 3 }}
          >
            Nenhuma transação registrada para este dia.
          </Typography>
        )}
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose} variant="outlined">
          Fechar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
