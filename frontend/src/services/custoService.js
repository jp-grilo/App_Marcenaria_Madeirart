import api from "./api";

/**
 * Service para operações relacionadas a custos fixos e variáveis
 */
const custoService = {
  // ========== Custos Fixos ==========

  /**
   * Lista todos os custos fixos
   * @param {object} params - Parâmetros de filtro { apenasAtivos, orderByDiaVencimento, diaInicio, diaFim }
   */
  listarCustosFixos: async (params = {}) => {
    const response = await api.get("/custos-fixos", { params });
    return response.data;
  },

  /**
   * Busca um custo fixo por ID
   * @param {number} id - ID do custo fixo
   */
  buscarCustoFixoPorId: async (id) => {
    const response = await api.get(`/custos-fixos/${id}`);
    return response.data;
  },

  /**
   * Cria um novo custo fixo
   * @param {object} dados - Dados do custo fixo
   */
  criarCustoFixo: async (dados) => {
    const response = await api.post("/custos-fixos", dados);
    return response.data;
  },

  /**
   * Atualiza um custo fixo existente
   * @param {number} id - ID do custo fixo
   * @param {object} dados - Dados atualizados
   */
  atualizarCustoFixo: async (id, dados) => {
    const response = await api.put(`/custos-fixos/${id}`, dados);
    return response.data;
  },

  /**
   * Desativa um custo fixo (soft delete)
   * @param {number} id - ID do custo fixo
   */
  desativarCustoFixo: async (id) => {
    const response = await api.patch(`/custos-fixos/${id}/desativar`);
    return response.data;
  },

  /**
   * Reativa um custo fixo
   * @param {number} id - ID do custo fixo
   */
  reativarCustoFixo: async (id) => {
    const response = await api.patch(`/custos-fixos/${id}/reativar`);
    return response.data;
  },

  /**
   * Exclui permanentemente um custo fixo
   * @param {number} id - ID do custo fixo
   */
  excluirCustoFixo: async (id) => {
    await api.delete(`/custos-fixos/${id}`);
  },

  /**
   * Marca um custo fixo como pago
   * @param {number} id - ID do custo fixo
   */
  marcarCustoFixoComoPago: async (id) => {
    const response = await api.patch(`/custos-fixos/${id}/marcar-pago`);
    return response.data;
  },

  /**
   * Marca um custo fixo como pendente
   * @param {number} id - ID do custo fixo
   */
  marcarCustoFixoComoPendente: async (id) => {
    const response = await api.patch(`/custos-fixos/${id}/marcar-pendente`);
    return response.data;
  },

  // ========== Custos Variáveis ==========

  /**
   * Lista todos os custos variáveis
   * @param {object} params - Parâmetros de filtro { dataInicio, dataFim }
   */
  listarCustosVariaveis: async (params = {}) => {
    const response = await api.get("/custos-variaveis", { params });
    return response.data;
  },

  /**
   * Busca um custo variável por ID
   * @param {number} id - ID do custo variável
   */
  buscarCustoVariavelPorId: async (id) => {
    const response = await api.get(`/custos-variaveis/${id}`);
    return response.data;
  },

  /**
   * Cria um novo custo variável
   * @param {object} dados - Dados do custo variável
   */
  criarCustoVariavel: async (dados) => {
    const response = await api.post("/custos-variaveis", dados);
    return response.data;
  },

  /**
   * Atualiza um custo variável existente
   * @param {number} id - ID do custo variável
   * @param {object} dados - Dados atualizados
   */
  atualizarCustoVariavel: async (id, dados) => {
    const response = await api.put(`/custos-variaveis/${id}`, dados);
    return response.data;
  },

  /**
   * Exclui um custo variável
   * @param {number} id - ID do custo variável
   */
  excluirCustoVariavel: async (id) => {
    await api.delete(`/custos-variaveis/${id}`);
  },

  /**
   * Marca um custo variável como pago
   * @param {number} id - ID do custo variável
   */
  marcarCustoVariavelComoPago: async (id) => {
    const response = await api.patch(`/custos-variaveis/${id}/marcar-pago`);
    return response.data;
  },

  /**
   * Marca um custo variável como pendente
   * @param {number} id - ID do custo variável
   */
  marcarCustoVariavelComoPendente: async (id) => {
    const response = await api.patch(`/custos-variaveis/${id}/marcar-pendente`);
    return response.data;
  },
};

export default custoService;
