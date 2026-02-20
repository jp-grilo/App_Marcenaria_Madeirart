import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
} from "@mui/material";

export default function ConfirmacaoDialog({
  aberto,
  titulo,
  mensagem,
  onConfirmar,
  onCancelar,
}) {
  return (
    <Dialog
      open={aberto}
      onClose={onCancelar}
      aria-labelledby="dialog-titulo"
      aria-describedby="dialog-descricao"
    >
      <DialogTitle id="dialog-titulo">{titulo}</DialogTitle>
      <DialogContent>
        <DialogContentText id="dialog-descricao">{mensagem}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onCancelar} color="inherit">
          Cancelar
        </Button>
        <Button
          onClick={onConfirmar}
          color="primary"
          variant="contained"
          autoFocus
        >
          Confirmar
        </Button>
      </DialogActions>
    </Dialog>
  );
}
