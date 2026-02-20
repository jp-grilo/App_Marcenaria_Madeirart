import { Box, Typography, Button } from "@mui/material";
import { Add } from "@mui/icons-material";
import { useNavigate } from "react-router-dom";
import CustosUnificadosList from "./components/CustosUnificadosList";

export default function CustosList() {
  const navigate = useNavigate();

  return (
    <Box>
      {/* Cabeçalho */}
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 3,
        }}
      >
        <Typography variant="h4" fontWeight="bold">
          Gestão de Custos
        </Typography>
        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={() => navigate("/custos/novo")}
        >
          Novo Custo
        </Button>
      </Box>

      {/* Lista Unificada */}
      <CustosUnificadosList />
    </Box>
  );
}
