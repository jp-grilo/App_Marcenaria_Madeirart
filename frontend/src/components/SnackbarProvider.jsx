import { useState } from 'react';
import { Snackbar, Alert } from '@mui/material';
import { SnackbarContext } from '../contexts/SnackbarContext';

const MAX_SNACKBARS = 5;

/**
 * Provider global para Snackbar (notificações toast)
 * Envolve a aplicação no App.jsx
 */
export default function SnackbarProvider({ children }) {
  const [snackbars, setSnackbars] = useState([]);

  const showSnackbar = (message, severity = 'info') => {
    const newSnackbar = {
      id: Date.now() + Math.random(), // ID único
      message,
      severity,
      open: true,
    };

    setSnackbars((prev) => {
      const updated = prev.length >= MAX_SNACKBARS ? prev.slice(1) : prev;
      return [...updated, newSnackbar];
    });
  };

  const handleClose = (id) => (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    
    setSnackbars((prev) =>
      prev.map((snackbar) =>
        snackbar.id === id ? { ...snackbar, open: false } : snackbar
      )
    );

    setTimeout(() => {
      setSnackbars((prev) => prev.filter((snackbar) => snackbar.id !== id));
    }, 300);
  };

  const value = {
    showSuccess: (message) => showSnackbar(message, 'success'),
    showError: (message) => showSnackbar(message, 'error'),
    showWarning: (message) => showSnackbar(message, 'warning'),
    showInfo: (message) => showSnackbar(message, 'info'),
  };

  return (
    <SnackbarContext.Provider value={value}>
      {children}
      {snackbars.map((snackbar, index) => (
        <Snackbar
          key={snackbar.id}
          open={snackbar.open}
          autoHideDuration={6000}
          onClose={handleClose(snackbar.id)}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
          sx={{
            bottom: `${24 + index * 70}px !important`, // Empilha verticalmente
          }}
        >
          <Alert
            onClose={handleClose(snackbar.id)}
            severity={snackbar.severity}
            variant="filled"
            sx={{ width: '100%' }}
          >
            {snackbar.message}
          </Alert>
        </Snackbar>
      ))}
    </SnackbarContext.Provider>
  );
};
