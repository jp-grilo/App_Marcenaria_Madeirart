import axios from "axios";

/**
 * Instância do Axios configurada para comunicação com o backend
 * Base URL aponta para o Spring Boot rodando localmente
 */
const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * Interceptor para tratamento global de erros
 */
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      // Erro retornado pelo servidor
      console.error("Erro na requisição:", error.response.data);
    } else if (error.request) {
      // Requisição feita mas sem resposta
      console.error("Servidor não respondeu:", error.request);
    } else {
      // Erro na configuração da requisição
      console.error("Erro ao configurar requisição:", error.message);
    }
    return Promise.reject(error);
  },
);

export default api;
