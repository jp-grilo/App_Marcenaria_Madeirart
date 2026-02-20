import {
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Divider,
  Box,
  Typography,
} from "@mui/material";
import {
  Dashboard,
  Assignment,
  AccountBalanceWallet,
  Settings,
} from "@mui/icons-material";
import { useNavigate, useLocation } from "react-router-dom";

const DRAWER_WIDTH = 260;

const menuItems = [
  { text: "Dashboard", icon: <Dashboard />, path: "/" },
  { text: "Orçamentos", icon: <Assignment />, path: "/orcamentos" },
  { text: "Custos", icon: <AccountBalanceWallet />, path: "/custos" },
];

export default function Sidebar() {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: DRAWER_WIDTH,
        flexShrink: 0,
        "& .MuiDrawer-paper": {
          width: DRAWER_WIDTH,
          boxSizing: "border-box",
          backgroundColor: "#1e1e1e",
          color: "#fff",
        },
      }}
    >
      <Toolbar
        sx={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          py: 2,
        }}
      >
        <Box sx={{ textAlign: "center" }}>
          <Typography
            variant="h5"
            sx={{ fontWeight: "bold", color: "#D2691E" }}
          >
            Madeirart
          </Typography>
          <Typography variant="caption" sx={{ color: "#999" }}>
            Gestão Financeira
          </Typography>
        </Box>
      </Toolbar>

      <Divider sx={{ borderColor: "#333" }} />

      <List sx={{ px: 1, pt: 2 }}>
        {menuItems.map((item) => (
          <ListItem key={item.text} disablePadding sx={{ mb: 0.5 }}>
            <ListItemButton
              onClick={() => navigate(item.path)}
              selected={location.pathname === item.path}
              sx={{
                borderRadius: 1,
                "&.Mui-selected": {
                  backgroundColor: "#D2691E",
                  "&:hover": {
                    backgroundColor: "#B8551A",
                  },
                },
                "&:hover": {
                  backgroundColor: "#333",
                },
              }}
            >
              <ListItemIcon sx={{ color: "inherit", minWidth: 40 }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>

      <Box sx={{ flexGrow: 1 }} />

      <Divider sx={{ borderColor: "#333" }} />

      <List sx={{ px: 1, py: 2 }}>
        <ListItem disablePadding>
          <ListItemButton
            onClick={() => navigate("/configuracoes")}
            sx={{
              borderRadius: 1,
              "&:hover": {
                backgroundColor: "#333",
              },
            }}
          >
            <ListItemIcon sx={{ color: "#999", minWidth: 40 }}>
              <Settings />
            </ListItemIcon>
            <ListItemText primary="Configurações" sx={{ color: "#999" }} />
          </ListItemButton>
        </ListItem>
      </List>
    </Drawer>
  );
}
