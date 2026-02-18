import { Breadcrumbs, Link, Typography } from "@mui/material";
import { useLocation, Link as RouterLink } from "react-router-dom";
import { NavigateNext } from "@mui/icons-material";

const breadcrumbNameMap = {
  "/": "Dashboard",
  "/orcamentos": "Orçamentos",
  "/orcamentos/novo": "Novo Orçamento",
  "/calendario": "Calendário Financeiro",
  "/relatorios": "Relatórios",
  "/configuracoes": "Configurações",
};

export default function AppBreadcrumbs() {
  const location = useLocation();
  const pathnames = location.pathname.split("/").filter((x) => x);

  // Se estiver na home, não mostrar breadcrumbs
  if (pathnames.length === 0) {
    return null;
  }

  return (
    <Breadcrumbs separator={<NavigateNext fontSize="small" />} sx={{ mb: 3 }}>
      <Link
        component={RouterLink}
        to="/"
        underline="hover"
        color="inherit"
        sx={{ display: "flex", alignItems: "center" }}
      >
        Dashboard
      </Link>

      {pathnames.map((value, index) => {
        const last = index === pathnames.length - 1;
        const to = `/${pathnames.slice(0, index + 1).join("/")}`;
        const label = breadcrumbNameMap[to] || value;

        return last ? (
          <Typography key={to} color="text.primary" sx={{ fontWeight: 500 }}>
            {label}
          </Typography>
        ) : (
          <Link
            key={to}
            component={RouterLink}
            to={to}
            underline="hover"
            color="inherit"
          >
            {label}
          </Link>
        );
      })}
    </Breadcrumbs>
  );
}
