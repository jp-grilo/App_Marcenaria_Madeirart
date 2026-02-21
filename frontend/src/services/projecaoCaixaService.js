import api from "./api";

/**
 * Service para operações relacionadas à projeção de caixa
 */
const projecaoCaixaService = {
  /**
   * Retorna a projeção completa de caixa
   * Inclui saldo atual e projeção dos próximos 2 meses
   */
  getProjecaoCaixa: async () => {
    const response = await api.get("/financeiro/projecao-caixa");
    return response.data;
  },

  /**
   * Cadastra ou atualiza o saldo inicial do sistema
   * @param {number} valor - Valor do saldo inicial
   * @param {string} observacao - Observação sobre o saldo inicial
   */
  setSaldoInicial: async (valor, observacao) => {
    const response = await api.post("/financeiro/saldo-inicial", {
      valor,
      observacao,
    });
    return response.data;
  },

  /**
   * Busca o saldo inicial cadastrado
   */
  getSaldoInicial: async () => {
    try {
      const response = await api.get("/financeiro/saldo-inicial");
      return response.data;
    } catch (error) {
      if (error.response?.status === 404) {
        return null;
      }
      throw error;
    }
  },
};

export default projecaoCaixaService;
