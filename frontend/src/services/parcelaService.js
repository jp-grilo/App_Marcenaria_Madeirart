import api from "./api";

/**
 * Service para operações com parcelas
 */
const parcelaService = {
  /**
   * Lista todas as parcelas de um orçamento
   */
  async listarPorOrcamento(orcamentoId) {
    const response = await api.get(`/parcelas/orcamento/${orcamentoId}`);
    return response.data;
  },

  /**
   * Busca uma parcela por ID
   */
  async buscarPorId(id) {
    const response = await api.get(`/parcelas/${id}`);
    return response.data;
  },

  /**
   * Confirma o pagamento de uma parcela
   */
  async confirmarPagamento(id) {
    const response = await api.patch(`/parcelas/${id}/confirmar`);
    return response.data;
  },
};

export default parcelaService;
