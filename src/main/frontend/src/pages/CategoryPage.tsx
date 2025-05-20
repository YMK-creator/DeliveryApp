import React, { useEffect, useState } from 'react';
import axios from 'axios';
import {
    Box,
    Typography,
    Button,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Paper,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    IconButton,
    Alert,
    CircularProgress,
} from '@mui/material';
import { Delete, Edit, Add } from '@mui/icons-material';

interface Category {
    id: number;
    name: string;
}

const API_URL = 'http://localhost:8080';

const CategoryPage: React.FC = () => {
    const [categories, setCategories] = useState<Category[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [currentCategory, setCurrentCategory] = useState<Partial<Category>>({});

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        setLoading(true);
        try {
            const response = await axios.get<Category[]>(`${API_URL}/category`);
            setCategories(response.data);
            setError(null);
        } catch (err) {
            console.error(err);
            setError('Не удалось загрузить категории');
        } finally {
            setLoading(false);
        }
    };

    const handleOpenDialog = (category?: Category) => {
        setCurrentCategory(category ? { ...category } : {});
        setDialogOpen(true);
    };

    const handleCloseDialog = () => {
        setDialogOpen(false);
        setCurrentCategory({});
        setError(null);
    };

    const handleSave = async () => {
        if (!currentCategory.name || !currentCategory.name.trim()) {
            setError('Название категории не может быть пустым');
            return;
        }

        try {
            if (currentCategory.id) {
                await axios.put(`${API_URL}/category/${currentCategory.id}`, currentCategory);
            } else {
                const response = await axios.post(`${API_URL}/category`, currentCategory);
                setCategories((prev) => [...prev, response.data]);
            }
            fetchCategories();
            handleCloseDialog();
        } catch (err) {
            console.error(err);
            setError('Ошибка при сохранении категории');
        }
    };

    const handleDelete = async (id: number) => {
        try {
            await axios.delete(`${API_URL}/category/${id}`);
            fetchCategories();
        } catch (err) {
            console.error(err);
            setError('Ошибка при удалении категории');
        }
    };

    return (
        <Box sx={{ p: 3 }}>
            <Typography variant="h4" gutterBottom>
                🍽 Категории
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <Button
                variant="contained"
                startIcon={<Add />}
                onClick={() => handleOpenDialog()}
                sx={{ mb: 2 }}
            >
                Добавить категорию
            </Button>

            {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', py: 4 }}>
                    <CircularProgress />
                </Box>
            ) : (
                <TableContainer component={Paper}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                <TableCell>Название</TableCell>
                                <TableCell align="right">Действия</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {categories.map((category) => (
                                <TableRow key={category.id}>
                                    <TableCell>{category.name}</TableCell>
                                    <TableCell align="right">
                                        <IconButton onClick={() => handleOpenDialog(category)}>
                                            <Edit />
                                        </IconButton>
                                        <IconButton onClick={() => handleDelete(category.id)}>
                                            <Delete />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            )}

            <Dialog open={dialogOpen} onClose={handleCloseDialog} fullWidth maxWidth="sm">
                <DialogTitle>
                    {currentCategory.id ? 'Редактировать категорию' : 'Добавить категорию'}
                </DialogTitle>
                <DialogContent sx={{ px: 3, pt: 2 }}>
                    <TextField
                        autoFocus
                        margin="dense"
                        label="Название"
                        fullWidth
                        value={currentCategory.name || ''}
                        onChange={(e) =>
                            setCurrentCategory({ ...currentCategory, name: e.target.value })
                        }
                        disabled={loading}
                    />
                </DialogContent>
                <DialogActions sx={{ px: 3, pb: 2 }}>
                    <Button onClick={handleCloseDialog} disabled={loading}>
                        Отмена
                    </Button>
                    <Button
                        onClick={handleSave}
                        variant="contained"
                        disabled={loading}
                    >
                        Сохранить
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default CategoryPage;
