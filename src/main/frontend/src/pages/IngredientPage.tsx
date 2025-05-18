import React, { useEffect, useState } from 'react';
import {
    Box,
    Button,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    IconButton,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    TextField,
    Typography,
    Alert,
    CircularProgress,
} from '@mui/material';
import { Delete, Edit, Add } from '@mui/icons-material';
import axios from 'axios';

interface Ingredient {
    id: number;
    name: string;
}

const API_URL = 'http://localhost:8080';

const IngredientPage: React.FC = () => {
    const [ingredients, setIngredients] = useState<Ingredient[]>([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [currentIngredient, setCurrentIngredient] = useState<Partial<Ingredient>>({});
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetchIngredients();
    }, []);

    const fetchIngredients = async () => {
        setLoading(true);
        try {
            const response = await axios.get<Ingredient[]>(`${API_URL}/ingredient`);
            setIngredients(response.data);
            setError(null);
        } catch (err) {
            console.error('Ошибка при получении ингредиентов:', err);
            setError('Не удалось загрузить ингредиенты');
        } finally {
            setLoading(false);
        }
    };

    const handleOpenDialog = (ingredient?: Ingredient) => {
        setCurrentIngredient(ingredient ? { ...ingredient } : {});
        setOpenDialog(true);
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
        setCurrentIngredient({});
        setError(null);
    };

    const handleSave = async () => {
        if (!currentIngredient.name || !currentIngredient.name.trim()) {
            setError('Название ингредиента не может быть пустым');
            return;
        }
        setLoading(true);
        try {
            if (currentIngredient.id) {
                await axios.put(`${API_URL}/ingredient/${currentIngredient.id}`, currentIngredient);
            } else {
                await axios.post(`${API_URL}/ingredient`, currentIngredient);
            }
            await fetchIngredients();
            handleCloseDialog();
        } catch (err) {
            console.error('Ошибка при сохранении ингредиента:', err);
            setError('Ошибка при сохранении ингредиента');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id: number) => {
        setLoading(true);
        try {
            await axios.delete(`${API_URL}/ingredient/${id}`);
            await fetchIngredients();
        } catch (err) {
            console.error('Ошибка при удалении ингредиента:', err);
            setError('Ошибка при удалении ингредиента');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box>
            <Typography variant="h4" gutterBottom>
                🧂 Ингредиенты
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <Button variant="contained" startIcon={<Add />} onClick={() => handleOpenDialog()}>
                Добавить ингредиент
            </Button>

            {loading && (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                    <CircularProgress />
                </Box>
            )}

            {!loading && (
                <TableContainer component={Paper} sx={{ marginTop: 2 }}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Название</TableCell>
                                <TableCell align="right">Действия</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {ingredients.map((ingredient) => (
                                <TableRow key={ingredient.id}>
                                    <TableCell>{ingredient.name}</TableCell>
                                    <TableCell align="right">
                                        <IconButton onClick={() => handleOpenDialog(ingredient)}>
                                            <Edit />
                                        </IconButton>
                                        <IconButton onClick={() => handleDelete(ingredient.id)}>
                                            <Delete />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            <Dialog open={openDialog} onClose={handleCloseDialog}>
                <DialogTitle>{currentIngredient.id ? 'Редактировать ингредиент' : 'Добавить ингредиент'}</DialogTitle>
                <DialogContent>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Название"
                        fullWidth
                        value={currentIngredient.name || ''}
                        onChange={(e) => setCurrentIngredient({ ...currentIngredient, name: e.target.value })}
                        disabled={loading}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog} disabled={loading}>
                        Отмена
                    </Button>
                    <Button onClick={handleSave} variant="contained" disabled={loading}>
                        Сохранить
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default IngredientPage;
