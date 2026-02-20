import {
  Box,
  Typography,
  Paper,
  TextField,
  Grid2,
  Button,
  FormControlLabel,
  Checkbox,
  FormGroup,
} from "@mui/material";
import { FilterList } from "@mui/icons-material";

export default function FiltrosCustos({
  filtro,
  setFiltro,
  getDataInicial,
  getDataFinal,
  onLimpar,
}) {
  return (
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
            onClick={onLimpar}
            sx={{ height: "40px", fontSize: "0.8rem" }}
          >
            Limpar Filtros
          </Button>
        </Grid2>
      </Grid2>
    </Paper>
  );
}
