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

const getBreadcrumbLabel = (path, value, pathnames, index) => {
  if (breadcrumbNameMap[path]) {
    return breadcrumbNameMap[path];
  }

  if (pathnames[0] === "orcamentos") {
    if (!isNaN(value)) {
      const nextValue = pathnames[index + 1];
      if (nextValue === "editar") {
        return value;
      }
      return "Visualizar";
    }

    if (value === "editar") {
      return "Editar";
    }
  }

  return value;
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
        const label = getBreadcrumbLabel(to, value, pathnames, index);

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
