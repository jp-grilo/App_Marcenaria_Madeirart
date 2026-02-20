import { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Paper,
  TextField,
  Button,
  Grid2,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Alert,
  CircularProgress,
  InputAdornment,
} from "@mui/material";
import { Save, ArrowBack } from "@mui/icons-material";
import { useNavigate, useParams } from "react-router-dom";
import { useSnackbar } from "../../hooks/useSnackbar";
import custoService from "../../services/custoService";

export default function CustoForm() {
  const navigate = useNavigate();
  const { id, tipo } = useParams();
  const { showSuccess, showError } = useSnackbar();
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(false);
  const isEditMode = !!id;

  const [tipoCusto, setTipoCusto] = useState(tipo || "fixo");

  // Estados separados para cada tipo de custo
  const [custoFixoData, setCustoFixoData] = useState({
    nome: "",
    valor: "",
    descricao: "",
    diaVencimento: "",
  });

  const [custoVariavelData, setCustoVariavelData] = useState({
    nome: "",
    valor: "",
    descricao: "",
    dataLancamento: new Date().toISOString().split("T")[0],
    quantidadeParcelas: "",
  });

  // Determina qual estado usar baseado no tipo
  const formData = tipoCusto === "fixo" ? custoFixoData : custoVariavelData;
  const setFormData =
    tipoCusto === "fixo" ? setCustoFixoData : setCustoVariavelData;

  const [errors, setErrors] = useState({});

  // Carrega dados do custo em modo de edição
  useEffect(() => {
    const carregarCusto = async () => {
      if (isEditMode) {
        try {
          setLoadingData(true);
          let dados;

          if (tipoCusto === "fixo") {
            dados = await custoService.buscarCustoFixoPorId(id);
            setCustoFixoData({
              nome: dados.nome,
              valor: dados.valor.toString(),
              descricao: dados.descricao || "",
              diaVencimento: dados.diaVencimento.toString(),
            });
          } else {
            dados = await custoService.buscarCustoVariavelPorId(id);
            setCustoVariavelData({
              nome: dados.nome,
              valor: dados.valor.toString(),
              descricao: dados.descricao || "",
              dataLancamento: dados.dataLancamento,
            });
          }
        } catch (err) {
          showError("Erro ao carregar custo");
          console.error(err);
          navigate("/custos");
        } finally {
          setLoadingData(false);
        }
      }
    };

    carregarCusto();
  }, [id, tipoCusto, isEditMode, navigate, showError]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    if (errors[name]) {
      setErrors({ ...errors, [name]: null });
    }
  };

  const handleTipoCustoChange = (e) => {
    const novoTipo = e.target.value;
    setTipoCusto(novoTipo);
    setErrors({});
  };

  const validarFormulario = () => {
    const novosErros = {};

    if (!formData.nome.trim()) {
      novosErros.nome = "Nome é obrigatório";
    }

    if (!formData.valor || parseFloat(formData.valor) <= 0) {
      novosErros.valor = "Valor deve ser maior que zero";
    }

    if (tipoCusto === "fixo") {
      const diaVencimento = parseInt(formData.diaVencimento);
      if (!formData.diaVencimento) {
        novosErros.diaVencimento = "Dia de vencimento é obrigatório";
      } else if (diaVencimento < 1 || diaVencimento > 31) {
        novosErros.diaVencimento = "Dia deve estar entre 1 e 31";
      }
    } else {
      if (!formData.dataLancamento) {
        novosErros.dataLancamento = "Data de lançamento é obrigatória";
      }

      // Validar quantidade de parcelas se fornecida
      if (formData.quantidadeParcelas) {
        const qtdParcelas = parseInt(formData.quantidadeParcelas);
        if (qtdParcelas < 1 || qtdParcelas > 120) {
          novosErros.quantidadeParcelas = "Quantidade deve estar entre 1 e 120";
        }
      }
    }

    setErrors(novosErros);
    return Object.keys(novosErros).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validarFormulario()) {
      showError("Por favor, preencha todos os campos obrigatórios");
      return;
    }

    try {
      setLoading(true);

      const dados = {
        nome: formData.nome,
        valor: parseFloat(formData.valor),
        descricao: formData.descricao || null,
      };

      if (tipoCusto === "fixo") {
        dados.diaVencimento = parseInt(formData.diaVencimento);

        if (isEditMode) {
          await custoService.atualizarCustoFixo(id, dados);
          showSuccess("Custo fixo atualizado com sucesso");
        } else {
          await custoService.criarCustoFixo(dados);
          showSuccess("Custo fixo criado com sucesso");
        }
      } else {
        dados.dataLancamento = formData.dataLancamento;
        if (
          formData.quantidadeParcelas &&
          parseInt(formData.quantidadeParcelas) > 0
        ) {
          dados.quantidadeParcelas = parseInt(formData.quantidadeParcelas);
        }

        if (isEditMode) {
          await custoService.atualizarCustoVariavel(id, dados);
          showSuccess("Custo variável atualizado com sucesso");
        } else {
          await custoService.criarCustoVariavel(dados);
          if (dados.quantidadeParcelas && dados.quantidadeParcelas > 1) {
            showSuccess(
              `${dados.quantidadeParcelas} parcelas criadas com sucesso`,
            );
          } else {
            showSuccess("Custo variável criado com sucesso");
          }
        }
      }

      navigate("/custos");
    } catch (err) {
      showError("Erro ao salvar custo");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loadingData) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="400px"
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      {/* Cabeçalho */}
      <Box sx={{ mb: 3 }}>
        <Button
          startIcon={<ArrowBack />}
          onClick={() => navigate("/custos")}
          sx={{ mb: 2 }}
        >
          Voltar
        </Button>
        <Typography variant="h4" fontWeight="bold">
          {isEditMode ? "Editar Custo" : "Novo Custo"}
        </Typography>
      </Box>

      <Paper sx={{ p: 3 }}>
        <form onSubmit={handleSubmit}>
          <Grid2 container spacing={3}>
            {/* Tipo de Custo */}
            {!isEditMode && (
              <Grid2 size={12}>
                <FormControl fullWidth>
                  <InputLabel>Tipo de Custo</InputLabel>
                  <Select
                    value={tipoCusto}
                    onChange={handleTipoCustoChange}
                    label="Tipo de Custo"
                  >
                    <MenuItem value="fixo">Custo Fixo (Recorrente)</MenuItem>
                    <MenuItem value="variavel">
                      Custo Variável (Pontual)
                    </MenuItem>
                  </Select>
                </FormControl>
              </Grid2>
            )}

            {/* Nome */}
            <Grid2 size={{ xs: 12, md: 6 }}>
              <TextField
                fullWidth
                label="Nome do Custo"
                name="nome"
                value={formData.nome}
                onChange={handleChange}
                error={!!errors.nome}
                helperText={errors.nome}
                required
              />
            </Grid2>

            {/* Valor */}
            <Grid2 size={{ xs: 12, md: 6 }}>
              <TextField
                fullWidth
                label="Valor"
                name="valor"
                type="number"
                value={formData.valor}
                onChange={handleChange}
                error={!!errors.valor}
                helperText={errors.valor}
                required
                inputProps={{
                  min: "0.01",
                  step: "0.01",
                }}
                InputProps={{
                  startAdornment: (
                    <InputAdornment position="start">R$</InputAdornment>
                  ),
                }}
              />
            </Grid2>

            {/* Dia de Vencimento (somente para custo fixo) */}
            {tipoCusto === "fixo" && (
              <Grid2 size={{ xs: 12, md: 6 }}>
                <TextField
                  fullWidth
                  label="Dia de Vencimento"
                  name="diaVencimento"
                  type="number"
                  value={formData.diaVencimento}
                  onChange={handleChange}
                  error={!!errors.diaVencimento}
                  helperText={errors.diaVencimento || "Dia do mês (1-31)"}
                  required
                  inputProps={{
                    min: "1",
                    max: "31",
                  }}
                />
              </Grid2>
            )}

            {/* Data de Lançamento (somente para custo variável) */}
            {tipoCusto === "variavel" && (
              <>
                <Grid2 size={{ xs: 12, md: 6 }}>
                  <TextField
                    fullWidth
                    label="Data de Lançamento"
                    name="dataLancamento"
                    type="date"
                    value={formData.dataLancamento}
                    onChange={handleChange}
                    error={!!errors.dataLancamento}
                    helperText={errors.dataLancamento}
                    required
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                </Grid2>

                {/* Quantidade de Parcelas (somente para custo variável não editando) */}
                {!isEditMode && (
                  <Grid2 size={{ xs: 12, md: 6 }}>
                    <TextField
                      fullWidth
                      label="Quantidade de Parcelas"
                      name="quantidadeParcelas"
                      type="number"
                      value={formData.quantidadeParcelas}
                      onChange={handleChange}
                      error={!!errors.quantidadeParcelas}
                      helperText={
                        errors.quantidadeParcelas ||
                        "Deixe vazio ou 1 para sem parcelamento"
                      }
                      inputProps={{
                        min: "1",
                        max: "120",
                      }}
                    />
                  </Grid2>
                )}
              </>
            )}

            {/* Descrição */}
            <Grid2 size={12}>
              <TextField
                fullWidth
                label="Descrição"
                name="descricao"
                value={formData.descricao}
                onChange={handleChange}
                multiline
                rows={3}
                inputProps={{ maxLength: 500 }}
                helperText={`${formData.descricao.length}/500 caracteres`}
              />
            </Grid2>

            {/* Botões */}
            <Grid2 size={12}>
              <Box sx={{ display: "flex", gap: 2, justifyContent: "flex-end" }}>
                <Button
                  variant="outlined"
                  onClick={() => navigate("/custos")}
                  disabled={loading}
                >
                  Cancelar
                </Button>
                <Button
                  type="submit"
                  variant="contained"
                  startIcon={
                    loading ? <CircularProgress size={20} /> : <Save />
                  }
                  disabled={loading}
                >
                  {loading ? "Salvando..." : "Salvar"}
                </Button>
              </Box>
            </Grid2>
          </Grid2>
        </form>
      </Paper>
    </Box>
  );
}
