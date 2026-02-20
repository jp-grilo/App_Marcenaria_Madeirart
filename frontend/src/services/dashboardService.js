import api from "./api";

/**
 * Service para operações relacionadas ao dashboard
 */
const dashboardService = {
  /**
   * Busca o resumo de orçamentos para o dashboard
   */
  getResumo: async () => {
    const response = await api.get("/dashboard/resumo");
    return response.data;
  },

  /**
   * Busca a projeção financeira de um mês específico
   * @param {number} mes - Mês (1-12)
   * @param {number} ano - Ano
   */
  getProjecao: async (mes, ano) => {
    const response = await api.get("/dashboard/projecao", {
      params: { mes, ano },
    });
    return response.data;
  },
};

export default dashboardService;
