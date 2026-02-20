import api from "./api";

/**
 * Service para operações relacionadas a orçamentos
 */
const orcamentoService = {
  /**
   * Lista todos os orçamentos
   * @param {string} status - Filtro opcional por status
   */
  listar: async (status = null) => {
    const params = status ? { status } : {};
    const response = await api.get("/orcamentos", { params });
    return response.data;
  },

  /**
   * Busca um orçamento por ID
   * @param {number} id - ID do orçamento
   */
  buscarPorId: async (id) => {
    const response = await api.get(`/orcamentos/${id}`);
    return response.data;
  },

  /**
   * Cria um novo orçamento
   * @param {object} dados - Dados do orçamento
   */
  criar: async (dados) => {
    const response = await api.post("/orcamentos", dados);
    return response.data;
  },

  /**
   * Atualiza um orçamento existente
   * @param {number} id - ID do orçamento
   * @param {object} dados - Dados atualizados
   */
  atualizar: async (id, dados) => {
    const response = await api.put(`/orcamentos/${id}`, dados);
    return response.data;
  },

  /**
   * Deleta um orçamento
   * @param {number} id - ID do orçamento
   */
  deletar: async (id) => {
    await api.delete(`/orcamentos/${id}`);
  },

  /**
   * Busca o histórico de auditoria de um orçamento
   * @param {number} id - ID do orçamento
   */
  buscarHistorico: async (id) => {
    const response = await api.get(`/orcamentos/${id}/historico`);
    return response.data;
  },

  /**
   * Inicia a produção de um orçamento com plano de parcelas
   * @param {number} id - ID do orçamento
   * @param {object} dados - { valorEntrada, dataEntrada, parcelas: [{valor, dataVencimento}] }
   */
  iniciarProducao: async (id, dados) => {
    const response = await api.patch(`/orcamentos/${id}/iniciar`, dados);
    return response.data;
  },

  /**
   * Altera o status de um orçamento
   * @param {number} id - ID do orçamento
   * @param {string} novoStatus - Novo status (INICIADA, FINALIZADA, CANCELADA)
   */
  alterarStatus: async (id, novoStatus) => {
    const response = await api.patch(`/orcamentos/${id}/status`, null, {
      params: { novoStatus },
    });
    return response.data;
  },
};

export default orcamentoService;
