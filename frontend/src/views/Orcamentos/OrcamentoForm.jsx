import { useState, useEffect } from "react";
import {
  Box,
  Typography,
  Paper,
  TextField,
  Button,
  IconButton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Grid2,
  Alert,
  CircularProgress,
} from "@mui/material";
import { Add, Delete, Save, ArrowBack } from "@mui/icons-material";
import { useNavigate, useParams } from "react-router-dom";
import { useSnackbar } from "../../hooks/useSnackbar";
import orcamentoService from "../../services/orcamentoService";
import { formatCurrency } from "../../utils/formatters";

export default function OrcamentoForm() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { showSuccess, showError } = useSnackbar();
  const [loading, setLoading] = useState(false);
  const [loadingData, setLoadingData] = useState(false);
  const isEditMode = !!id;

  const [formData, setFormData] = useState({
    cliente: "",
    moveis: "",
    data: new Date().toISOString().split("T")[0],
    previsaoEntrega: "",
    fatorMaoDeObra: "",
    custosExtras: "",
    cpc: "",
  });

  const [itens, setItens] = useState([
    { quantidade: "", descricao: "", valorUnitario: "" },
  ]);

  const [errors, setErrors] = useState({});

  // Carrega dados do orçamento em modo de edição
  useEffect(() => {
    const carregarOrcamento = async () => {
      if (isEditMode) {
        try {
          setLoadingData(true);
          const dados = await orcamentoService.buscarPorId(id);

          setFormData({
            cliente: dados.cliente,
            moveis: dados.moveis,
            data: dados.data,
            previsaoEntrega: dados.previsaoEntrega || "",
            fatorMaoDeObra: dados.fatorMaoDeObra.toString(),
            custosExtras: dados.custosExtras
              ? dados.custosExtras.toString()
              : "",
            cpc: dados.cpc ? dados.cpc.toString() : "",
          });

          setItens(
            dados.itens.map((item) => ({
              quantidade: item.quantidade.toString(),
              descricao: item.descricao,
              valorUnitario: item.valorUnitario.toString(),
            })),
          );
        } catch (err) {
          showError("Erro ao carregar orçamento");
          console.error(err);
          navigate("/orcamentos");
        } finally {
          setLoadingData(false);
        }
      }
    };

    carregarOrcamento();
  }, [id, isEditMode, navigate, showError]);

  const adicionarItem = () => {
    setItens([...itens, { quantidade: "", descricao: "", valorUnitario: "" }]);
  };

  const removerItem = (index) => {
    if (itens.length > 1) {
      setItens(itens.filter((_, i) => i !== index));
    }
  };

  const atualizarItem = (index, campo, valor) => {
    const novosItens = [...itens];
    novosItens[index][campo] = valor;
    setItens(novosItens);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    if (errors[name]) {
      setErrors({ ...errors, [name]: null });
    }
  };

  const calcularSubtotalMateriais = () => {
    return itens.reduce((total, item) => {
      const quantidade = parseFloat(item.quantidade) || 0;
      const valorUnitario = parseFloat(item.valorUnitario) || 0;
      return total + quantidade * valorUnitario;
    }, 0);
  };

  const calcularValorMaoDeObra = () => {
    const subtotal = calcularSubtotalMateriais();
    const fator = parseFloat(formData.fatorMaoDeObra) || 0;
    return subtotal * fator;
  };

  const calcularValorTotal = () => {
    const custoObra = calcularValorMaoDeObra();
    const custosExtras = parseFloat(formData.custosExtras) || 0;
    const cpc = parseFloat(formData.cpc) || 0;
    return custoObra + custosExtras + cpc;
  };

  const validarFormulario = () => {
    const novosErros = {};

    if (!formData.cliente.trim()) {
      novosErros.cliente = "Cliente é obrigatório";
    }
    if (!formData.moveis.trim()) {
      novosErros.moveis = "Descrição dos móveis é obrigatória";
    }
    if (!formData.data) {
      novosErros.data = "Data é obrigatória";
    }
    if (!formData.previsaoEntrega) {
      novosErros.previsaoEntrega = "Previsão de entrega é obrigatória";
    }
    if (!formData.fatorMaoDeObra || parseFloat(formData.fatorMaoDeObra) < 1) {
      novosErros.fatorMaoDeObra =
        "Fator de mão de obra deve ser maior ou igual a 1";
    }

    const itensValidos = itens.every(
      (item) =>
        item.quantidade &&
        parseFloat(item.quantidade) > 0 &&
        item.descricao.trim() &&
        item.valorUnitario &&
        parseFloat(item.valorUnitario) >= 0,
    );

    if (!itensValidos) {
      novosErros.itens =
        "Todos os itens devem ter quantidade, descrição e valor preenchidos";
    }

    setErrors(novosErros);
    return Object.keys(novosErros).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validarFormulario()) {
      showError("Por favor, corrija os erros no formulário");
      return;
    }

    try {
      setLoading(true);

      // Monta o payload conforme o DTO do backend
      const payload = {
        cliente: formData.cliente,
        moveis: formData.moveis,
        data: formData.data,
        previsaoEntrega: formData.previsaoEntrega,
        fatorMaoDeObra: parseFloat(formData.fatorMaoDeObra),
        custosExtras: parseFloat(formData.custosExtras) || 0,
        cpc: parseFloat(formData.cpc) || 0,
        itens: itens.map((item) => ({
          quantidade: parseFloat(item.quantidade),
          descricao: item.descricao,
          valorUnitario: parseFloat(item.valorUnitario),
        })),
      };

      if (isEditMode) {
        await orcamentoService.atualizar(id, payload);
        showSuccess("Orçamento atualizado com sucesso!");
      } else {
        await orcamentoService.criar(payload);
        showSuccess("Orçamento criado com sucesso!");
      }

      navigate("/orcamentos");
    } catch (error) {
      console.error("Erro ao salvar orçamento:", error);
      showError(error.response?.data?.message || "Erro ao salvar orçamento");
    } finally {
      setLoading(false);
    }
  };

  const subtotalMateriais = calcularSubtotalMateriais();
  const valorMaoDeObra = calcularValorMaoDeObra();
  const valorTotal = calcularValorTotal();

  if (loadingData) {
    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          minHeight: 400,
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ mb: 3, display: "flex", alignItems: "center", gap: 2 }}>
        <IconButton onClick={() => navigate("/orcamentos")} color="primary">
          <ArrowBack />
        </IconButton>
        <Typography variant="h4" sx={{ fontWeight: 600 }}>
          {isEditMode ? "Editar Orçamento" : "Novo Orçamento"}
        </Typography>
      </Box>

      <form onSubmit={handleSubmit}>
        <Paper sx={{ p: 3, mb: 3 }}>
          <Typography variant="h6" sx={{ mb: 3 }}>
            Informações Gerais
          </Typography>

          <Grid2 container spacing={3}>
            <Grid2 item xs={12} md={6}>
              <TextField
                fullWidth
                label="Cliente"
                name="cliente"
                value={formData.cliente}
                onChange={handleChange}
                error={!!errors.cliente}
                helperText={errors.cliente}
                required
              />
            </Grid2>

            <Grid2 item xs={12} md={6}>
              <TextField
                fullWidth
                label="Móveis / Descrição"
                name="moveis"
                value={formData.moveis}
                onChange={handleChange}
                error={!!errors.moveis}
                helperText={errors.moveis}
                required
              />
            </Grid2>

            <Grid2 item xs={12} md={6}>
              <TextField
                fullWidth
                type="date"
                label="Data"
                name="data"
                value={formData.data}
                onChange={handleChange}
                error={!!errors.data}
                helperText={errors.data}
                InputLabelProps={{ shrink: true }}
                required
              />
            </Grid2>

            <Grid2 item xs={12} md={6}>
              <TextField
                fullWidth
                type="date"
                label="Previsão de Entrega"
                name="previsaoEntrega"
                value={formData.previsaoEntrega}
                onChange={handleChange}
                error={!!errors.previsaoEntrega}
                helperText={errors.previsaoEntrega}
                InputLabelProps={{ shrink: true }}
                required
              />
            </Grid2>

            <Grid2 item xs={12} md={4}>
              <TextField
                fullWidth
                type="number"
                label="Fator de Mão de Obra"
                name="fatorMaoDeObra"
                value={formData.fatorMaoDeObra}
                onChange={handleChange}
                error={!!errors.fatorMaoDeObra}
                helperText={
                  errors.fatorMaoDeObra ||
                  "Ex: 1.5 (150% do custo dos materiais)"
                }
                inputProps={{ step: "0.1", min: "0" }}
                required
              />
            </Grid2>

            <Grid2 item xs={12} md={4}>
              <TextField
                fullWidth
                type="number"
                label="Custos Extras"
                name="custosExtras"
                value={formData.custosExtras}
                onChange={handleChange}
                inputProps={{ step: "10", min: "0" }}
                helperText="Valores adicionais"
              />
            </Grid2>

            <Grid2 item xs={12} md={4}>
              <TextField
                fullWidth
                type="number"
                label="CPC"
                name="cpc"
                value={formData.cpc}
                onChange={handleChange}
                inputProps={{ step: "10", min: "0" }}
                helperText="Cola, parafuso, carreto"
              />
            </Grid2>
          </Grid2>
        </Paper>

        <Paper sx={{ p: 3, mb: 3 }}>
          <Box
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              mb: 3,
            }}
          >
            <Typography variant="h6">Materiais</Typography>
            <Button
              startIcon={<Add />}
              onClick={adicionarItem}
              variant="outlined"
            >
              Adicionar Item
            </Button>
          </Box>

          {errors.itens && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {errors.itens}
            </Alert>
          )}

          <TableContainer>
            <Table>
              <TableHead>
                <TableRow sx={{ backgroundColor: "#f5f5f5" }}>
                  <TableCell sx={{ fontWeight: "bold", width: "15%" }}>
                    Quantidade
                  </TableCell>
                  <TableCell sx={{ fontWeight: "bold", width: "45%" }}>
                    Descrição
                  </TableCell>
                  <TableCell sx={{ fontWeight: "bold", width: "20%" }}>
                    Valor Unitário
                  </TableCell>
                  <TableCell sx={{ fontWeight: "bold", width: "15%" }}>
                    Subtotal
                  </TableCell>
                  <TableCell
                    sx={{ fontWeight: "bold", width: "5%" }}
                  ></TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {itens.map((item, index) => {
                  const subtotal =
                    (parseFloat(item.quantidade) || 0) *
                    (parseFloat(item.valorUnitario) || 0);

                  return (
                    <TableRow key={index}>
                      <TableCell>
                        <TextField
                          fullWidth
                          type="number"
                          value={item.quantidade}
                          onChange={(e) =>
                            atualizarItem(index, "quantidade", e.target.value)
                          }
                          inputProps={{ min: "0" }}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>
                        <TextField
                          fullWidth
                          value={item.descricao}
                          onChange={(e) =>
                            atualizarItem(index, "descricao", e.target.value)
                          }
                          placeholder="Ex: Tábua de pinus 2m"
                          size="small"
                        />
                      </TableCell>
                      <TableCell>
                        <TextField
                          fullWidth
                          type="number"
                          value={item.valorUnitario}
                          onChange={(e) =>
                            atualizarItem(
                              index,
                              "valorUnitario",
                              e.target.value,
                            )
                          }
                          inputProps={{ min: "0" }}
                          size="small"
                        />
                      </TableCell>
                      <TableCell>
                        <Typography variant="body2" sx={{ fontWeight: "bold" }}>
                          {formatCurrency(subtotal)}
                        </Typography>
                      </TableCell>
                      <TableCell>
                        <IconButton
                          onClick={() => removerItem(index)}
                          disabled={itens.length === 1}
                          color="error"
                          size="small"
                        >
                          <Delete />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          </TableContainer>
        </Paper>

        <Paper sx={{ p: 3, mb: 3 }}>
          <Typography variant="h6" sx={{ mb: 3 }}>
            Cálculos Automáticos
          </Typography>

          <Grid2 container spacing={2}>
            <Grid2 item xs={12} md={4}>
              <Box sx={{ p: 2, backgroundColor: "#f5f5f5", borderRadius: 1 }}>
                <Typography variant="caption" color="text.secondary">
                  Subtotal de Materiais
                </Typography>
                <Typography
                  variant="h6"
                  sx={{ fontWeight: "bold", color: "#1976d2" }}
                >
                  {formatCurrency(subtotalMateriais)}
                </Typography>
              </Box>
            </Grid2>

            <Grid2 item xs={12} md={4}>
              <Box sx={{ p: 2, backgroundColor: "#f5f5f5", borderRadius: 1 }}>
                <Typography variant="caption" color="text.secondary">
                  Valor Mão de Obra
                </Typography>
                <Typography
                  variant="h6"
                  sx={{ fontWeight: "bold", color: "#ed6c02" }}
                >
                  {formatCurrency(valorMaoDeObra)}
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  (Materiais × Fator)
                </Typography>
              </Box>
            </Grid2>

            <Grid2 item xs={12} md={4}>
              <Box sx={{ p: 2, backgroundColor: "#D2691E", borderRadius: 1 }}>
                <Typography variant="caption" sx={{ color: "white" }}>
                  Valor Total do Orçamento
                </Typography>
                <Typography
                  variant="h5"
                  sx={{ fontWeight: "bold", color: "white" }}
                >
                  {formatCurrency(valorTotal)}
                </Typography>
                <Typography
                  variant="caption"
                  sx={{ color: "rgba(255,255,255,0.8)" }}
                >
                  (Mão de Obra + Extras + CPC)
                </Typography>
              </Box>
            </Grid2>
          </Grid2>
        </Paper>

        <Box sx={{ display: "flex", gap: 2, justifyContent: "flex-end" }}>
          <Button
            variant="outlined"
            onClick={() => navigate("/orcamentos")}
            disabled={loading}
          >
            Cancelar
          </Button>
          <Button
            type="submit"
            variant="contained"
            startIcon={<Save />}
            disabled={loading}
            sx={{
              backgroundColor: "#D2691E",
              "&:hover": {
                backgroundColor: "#B8551A",
              },
            }}
          >
            {loading ? "Salvando..." : "Salvar Orçamento"}
          </Button>
        </Box>
      </form>
    </Box>
  );
}
