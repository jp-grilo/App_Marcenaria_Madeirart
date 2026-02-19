import { Box, Container, Toolbar } from "@mui/material";
import Sidebar from "./Sidebar";
import AppBreadcrumbs from "./AppBreadcrumbs";

const DRAWER_WIDTH = 260;

export default function MainLayout({ children }) {
  return (
    <Box
      sx={{ display: "flex", minHeight: "100vh", backgroundColor: "#f5f5f5" }}
    >
      <Sidebar />

      <Box
        component="main"
        sx={{
          flexGrow: 1,
          width: `calc(100% - ${DRAWER_WIDTH}px)`,
        }}
      >
        <Toolbar />

        <Container maxWidth="xl" sx={{ mb: 4 }}>
          <AppBreadcrumbs />
          {children}
        </Container>
      </Box>
    </Box>
  );
}
