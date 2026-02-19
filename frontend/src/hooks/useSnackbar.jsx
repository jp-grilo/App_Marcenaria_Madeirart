import { useContext } from 'react';
import { SnackbarContext } from '../contexts/SnackbarContext';

/**
 * Hook para usar o Snackbar em qualquer componente
 * @returns {{ showSuccess, showError, showInfo, showWarning }}
 * 
 * @example
 * const { showSuccess, showError } = useSnackbar();
 * 
 * try {
 *   await api.salvar(dados);
 *   showSuccess('Salvo com sucesso!');
 * } catch (error) {
 *   showError('Erro ao salvar');
 * }
 */
export const useSnackbar = () => {
  const context = useContext(SnackbarContext);
  if (!context) {
    throw new Error('useSnackbar deve ser usado dentro de SnackbarProvider');
  }
  return context;
};
