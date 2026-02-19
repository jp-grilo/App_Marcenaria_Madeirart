/**
 * Constantes da aplicação
 */

export const STATUS_ORCAMENTO = {
  AGUARDANDO: "AGUARDANDO",
  INICIADA: "INICIADA",
  FINALIZADA: "FINALIZADA",
  CANCELADA: "CANCELADA",
};

export const STATUS_LABELS = {
  AGUARDANDO: "Aguardando",
  INICIADA: "Em Produção",
  FINALIZADA: "Finalizada",
  CANCELADA: "Cancelada",
};

export const STATUS_CORES = {
  AGUARDANDO: "warning",
  INICIADA: "info",
  FINALIZADA: "success",
  CANCELADA: "error",
};

export const STATUS_PARCELA = {
  PENDENTE: "PENDENTE",
  PAGO: "PAGO",
};

export const FORMAS_PAGAMENTO = [
  "PIX",
  "Dinheiro",
  "Débito",
  "Crédito",
  "Boleto",
];
