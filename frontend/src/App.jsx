import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ThemeProvider, createTheme } from "@mui/material/styles";
import CssBaseline from "@mui/material/CssBaseline";
import SnackbarProvider from "./components/SnackbarProvider";
import MainLayout from "./components/Layout/MainLayout";
import Dashboard from "./views/Dashboard/Dashboard";
import OrcamentosList from "./views/Orcamentos/OrcamentosList";

const theme = createTheme({
  palette: {
    primary: {
      main: "#D2691E",
      dark: "#B8551A",
    },
    secondary: {
      main: "#8B4513",
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <SnackbarProvider>
        <BrowserRouter>
          <MainLayout>
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/orcamentos" element={<OrcamentosList />} />
              <Route
                path="/calendario"
                element={<div>Calendário em desenvolvimento</div>}
              />
              <Route
                path="/relatorios"
                element={<div>Relatórios em desenvolvimento</div>}
              />
              <Route
                path="/configuracoes"
                element={<div>Configurações em desenvolvimento</div>}
              />
            </Routes>
          </MainLayout>
        </BrowserRouter>
      </SnackbarProvider>
    </ThemeProvider>
  );
}

export default App;
