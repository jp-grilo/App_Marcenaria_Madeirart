import { useState } from 'react';
import { Snackbar, Alert } from '@mui/material';
import { SnackbarContext } from '../contexts/SnackbarContext';

/**
 * Provider global para Snackbar (notificações toast)
 * Envolve a aplicação no App.jsx
 */
export default function SnackbarProvider({ children }) {
  const [snackbar, setSnackbar] = useState({
    open: false,
    message: '',
    severity: 'info', // 'success' | 'error' | 'warning' | 'info'
  });

  const showSnackbar = (message, severity = 'info') => {
    setSnackbar({
      open: true,
      message,
      severity,
    });
  };

  const handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackbar({ ...snackbar, open: false });
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
      <Snackbar
        open={snackbar.open}
        autoHideDuration={6000}
        onClose={handleClose}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert
          onClose={handleClose}
          severity={snackbar.severity}
          variant="filled"
          sx={{ width: '100%' }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </SnackbarContext.Provider>
  );
};
